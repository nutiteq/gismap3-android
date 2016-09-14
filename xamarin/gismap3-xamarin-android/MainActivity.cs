using System;

using Android.App;
using Android.Content;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.OS;
using Java.IO;

using Nutiteq.Core;
using Nutiteq.Graphics;
using Nutiteq.Ui;
using Nutiteq.Utils;
using Nutiteq.DataSources;
using Nutiteq.Layers;
using Nutiteq.WrappedCommons;
using Nutiteq.Styles;
using Nutiteq.Projections;
using Nutiteq.VectorTiles;
using Nutiteq.PackageManager;

namespace HelloMap
{
	[Activity (Label = "Hellomap", MainLauncher = true, Icon = "@drawable/icon")]
	public class MainActivity : Activity
	{
		const string mapFolder = "maps";

		const string License = "XTUMwQ0ZRQ0ZRc2FRYnZWNGhNNEkxbFhjY3IvbGRSdHNLd0lVQjM4S3ZtNG44ZnIxR0tnWS9PYkdWL" +
			"2llYlNFPQoKcHJvZHVjdHM9c2RrLXhhbWFyaW4taW9zLTMuKixzZGsteGFtYXJpbi1hbmRyb2lkLTMuKixzZGstZ2lzZXh0ZW" +
			"5zaW9uCnBhY2thZ2VOYW1lPWNvbS5udXRpdGVxLmhlbGxvbWFwLnhhbWFyaW4KYnVuZGxlSWRlbnRpZmllcj1jb20ubnV0aXR" +
			"lcS5oZWxsb21hcC54YW1hcmluCndhdGVybWFyaz1udXRpdGVxCnZhbGlkVW50aWw9MjAxNS0wNi0wMQp1c2VyS2V5PTJhOWU5" +
			"Zjc0NjJjZWY0ODFiZTJhOGMxMjYxZmU2Y2JkCg==";

		protected override void OnCreate(Bundle savedInstanceState)
		{
			base.OnCreate(savedInstanceState);

			// force Java to load PROJ.4 library. Needed as we don't call it directly, but 
			// OGR datasource reading may need it.
			//Java.Lang.JavaSystem.LoadLibrary("proj");

			// Register license
			Log.ShowError = true;
			Log.ShowWarn = true;
			Log.ShowDebug = true;

			MapView.RegisterLicense(License, ApplicationContext);

			// Set our view from the "main" layout resource
			SetContentView(Resource.Layout.Main);

			var mapView = (MapView)FindViewById(Resource.Id.mapView);

			mapView.Zoom = 2;

			string url = "http://ecn.t3.tiles.virtualearth.net/tiles/r{quadkey}.png?g=1&mkt=en-US&shading=hill&n=z";
			mapView.Layers.Add(new RasterTileLayer(new HTTPTileDataSource(1, 18, url)));

			// Now can add vector map as layer. Define styling for vector map
			UnsignedCharVector styleBytes = AssetUtils.LoadBytes("nutibright-v3.zip");
			MBVectorTileDecoder vectorTileDecoder = null;

			if (styleBytes != null)
			{
				// Create style set
				var vectorTileStyleSet = new MBVectorTileStyleSet(styleBytes);
				vectorTileDecoder = new MBVectorTileDecoder(vectorTileStyleSet);
			}
			else {
				Alert("Failed to load style data");
			}

			/**** offline package map ****/

			// Create/find folder for packages
			var packageFolder = new File(GetExternalFilesDir(null), "packages");

			if (!(packageFolder.Mkdirs() || packageFolder.IsDirectory))
			{
				Alert("Could not create package folder!");
			}

			var packageManager = new NutiteqPackageManager("nutiteq.mbstreets", packageFolder.AbsolutePath);
			packageManager.PackageManagerListener = new PackageListener(packageManager);
			packageManager.Start();

			string tobi = "bbox(17.9776,59.4019,18.1574,59.5213)";
			//string london = "bbox(-0.8164,51.2382,0.6406,51.7401)";
			var bbox = tobi;

			if (packageManager.GetLocalPackage(bbox) == null)
			{
				packageManager.StartPackageDownload(bbox);
			}

			var baseLayer = new VectorTileLayer(new PackageManagerTileDataSource(packageManager), vectorTileDecoder);
			mapView.Layers.Add(baseLayer);

			// Copy bundled tile data to file system, so it can be imported by package manager
			string[] mapAssets = Assets.List(mapFolder);
			File directory = GetExternalFilesDir(null);

			foreach (string fileName in mapAssets)
			{
				string importPath = new File(directory, fileName).AbsolutePath;

				try
				{
					using (var input = Assets.Open(mapFolder + "/" + fileName))
					{
						using (var output = new System.IO.FileStream(importPath, System.IO.FileMode.Create))
						{
							input.CopyTo(output);
						}
					}
				}
				catch (IOException)
				{
					Log.Info("IOException " + fileName);
				}
			}

			// Set base projection
			var proj = new EPSG3857();
			mapView.Options.BaseProjection = proj; // note: EPSG3857 is the default, so this is actually not required

			var lineStyleBuilder = new LineStyleBuilder();
			lineStyleBuilder.Color = new Color(0, 0, 0, 255);
			lineStyleBuilder.Width = 2;

			var polygonStyleBuilder = new PolygonStyleBuilder();
			polygonStyleBuilder.Color = new Color(255, 0, 255, 255);
			polygonStyleBuilder.LineStyle = lineStyleBuilder.BuildStyle();
			var polygonStyle = polygonStyleBuilder.BuildStyle();

			// Create style selector.
			// Style selectors allow to assign styles based on element attributes and view parameters (zoom, for example)
			// Style filter expressions are given in a simple SQL-like language.
			StyleSelectorBuilder styleSelectorBuilder = new StyleSelectorBuilder();
			styleSelectorBuilder.AddRule(polygonStyle);
						
			StyleSelector styleSelector = styleSelectorBuilder.BuildSelector();

			// Create data source. Use constructed style selector and copied shape file containing points.
			string shpPath = new File(directory, "Adm.tab").AbsolutePath;

			OGRVectorDataSource ogrDataSource = new OGRVectorDataSource(proj, styleSelector, shpPath);
			VectorLayer ogrLayer = new VectorLayer(ogrDataSource);

			ogrDataSource.GeometrySimplifier = new Nutiteq.Geometry.DouglasPeuckerGeometrySimplifier(0.05f);
			mapView.Layers.Add(ogrLayer);

			// Zoom in to the actual data
			Android.Util.DisplayMetrics metrics = new Android.Util.DisplayMetrics();
			WindowManager.DefaultDisplay.GetMetrics(metrics);

			int width = metrics.WidthPixels;
			int height = metrics.HeightPixels;

			ScreenBounds screenBounds = new ScreenBounds(new ScreenPos(0, 0), new ScreenPos(width, height));
			MapBounds mapBounds = ogrDataSource.DataExtent;

			mapView.MoveToFitBounds(mapBounds, screenBounds, false, 0.5f);

			// Create overlay layer for markers
			var dataSourceOverlay = new LocalVectorDataSource(proj);
			var overlayLayer = new VectorLayer(dataSourceOverlay);

			mapView.Layers.Add(overlayLayer);

			// Add event listener
			mapView.MapEventListener = new MapListener(dataSourceOverlay, mapView);

		}

		void Alert(string message)
		{
			Toast.MakeText(this, message, ToastLength.Short).Show();
		}

	}
}
