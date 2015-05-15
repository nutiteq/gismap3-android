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
			

		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);

			// force Java to load PROJ.4 library. Needed as we don't call it directly, but 
			// OGR datasource reading may need it.

			//Java.Lang.JavaSystem.LoadLibrary("proj");

			// Register license
			Nutiteq.Utils.Log.ShowError = true;
			Nutiteq.Utils.Log.ShowWarn = true;
			//Nutiteq.Utils.Log.ShowDebug = true;

			//var licenseOk = MapView.RegisterLicense("XTUMwQ0ZRQ0ZRc2FRYnZWNGhNNEkxbFhjY3IvbGRSdHNLd0lVQjM4S3ZtNG44ZnIxR0tnWS9PYkdWL2llYlNFPQoKcHJvZHVjdHM9c2RrLXhhbWFyaW4taW9zLTMuKixzZGsteGFtYXJpbi1hbmRyb2lkLTMuKixzZGstZ2lzZXh0ZW5zaW9uCnBhY2thZ2VOYW1lPWNvbS5udXRpdGVxLmhlbGxvbWFwLnhhbWFyaW4KYnVuZGxlSWRlbnRpZmllcj1jb20ubnV0aXRlcS5oZWxsb21hcC54YW1hcmluCndhdGVybWFyaz1udXRpdGVxCnZhbGlkVW50aWw9MjAxNS0wNi0wMQp1c2VyS2V5PTJhOWU5Zjc0NjJjZWY0ODFiZTJhOGMxMjYxZmU2Y2JkCg==", ApplicationContext);
			//Log.Info ("License ok = " + licenseOk);
			// Set our view from the "main" layout resource
			SetContentView (Resource.Layout.Main);
			var mapView = (MapView)FindViewById (Resource.Id.mapView);

			mapView.Zoom = 2;

			mapView.Layers.Add(new RasterTileLayer(new HTTPTileDataSource(1,18,"http://ecn.t3.tiles.virtualearth.net/tiles/r{quadkey}.png?g=1&mkt=en-US&shading=hill&n=z")));

			// Now can add vector map as layer
			// define styling for vector map
			UnsignedCharVector styleBytes = AssetUtils.LoadBytes("osmbright.zip");
			MBVectorTileDecoder vectorTileDecoder = null;
			if (styleBytes != null) {
				// Create style set
				var vectorTileStyleSet = new MBVectorTileStyleSet (styleBytes);
				vectorTileDecoder = new MBVectorTileDecoder (vectorTileStyleSet);
			} else {
				Log.Error ("Failed to load style data");
			}


//			VectorTileLayer baseLayer = new NutiteqOnlineVectorTileLayer("osmbright.zip");

		//	var dataSource = new NutiteqOnlineTileDataSource("nutiteq.osm");
		//	var dataSource = new HTTPTileDataSource (0, 14, "http://up1.nutiteq.com/v1/nutiteq.mbstreets/{zoom}/{x}/{y}.vt?user_key=15cd9131072d6df68b8a54feda5b0496");

		//	var baseLayer = new VectorTileLayer(dataSource, vectorTileDecoder);
		//	mapView.Layers.Add(baseLayer);


			/**** offline package map ****/

			// Create/find folder for packages
			var packageFolder = new File (GetExternalFilesDir(null), "packages");
			if (!(packageFolder.Mkdirs() || packageFolder.IsDirectory)) {
				Log.Fatal("Could not create package folder!");
			}
			var packageManager = new NutiteqPackageManager("nutiteq.mbstreets", packageFolder.AbsolutePath);
			packageManager.PackageManagerListener = new PackageListener(packageManager);
			packageManager.Start ();

			// Töbi, Sweden: 17.9776,59.4019,18.1574,59.5213
			// London (30MB): bbox(-0.8164,51.2382,0.6406,51.7401)
			var bbox = "bbox(17.9776,59.4019,18.1574,59.5213)";
			if (packageManager.GetLocalPackage(bbox) == null) {
				var resultOK = packageManager.StartPackageDownload (bbox);
			}

			var baseLayer = new VectorTileLayer(new PackageManagerTileDataSource(packageManager),vectorTileDecoder);
			mapView.Layers.Add(baseLayer);

			// Copy bundled tile data to file system, so it can be imported by package manager
			var mapAsset = "maps";
			var assetFiles = Assets.List(mapAsset);
			var dir = GetExternalFilesDir(null);

			foreach (string fileName in assetFiles) {
				string importPath = new File (dir, fileName).AbsolutePath;
				try{
					using (var input = Assets.Open (mapAsset+"/"+fileName)) {
						using (var output = new System.IO.FileStream (importPath, System.IO.FileMode.Create)) {
							Log.Info ("copy " + mapAsset+"/"+fileName +" to "+importPath);
							input.CopyTo (output);
						}
					}
				}catch(IOException){
					Log.Info ("IOException " + fileName);
				}
			}

			// Set base projection
			var proj = new EPSG3857();
			mapView.Options.BaseProjection = proj; // note: EPSG3857 is the default, so this is actually not required

			var lineStyleBuilder = new LineStyleBuilder ();
			lineStyleBuilder.Color = new Color (0, 0, 0, 255);
			lineStyleBuilder.Width = 2;

			var polygonStyleBuilder = new PolygonStyleBuilder();
			polygonStyleBuilder.Color = new Color(255, 0, 255, 255);
			polygonStyleBuilder.LineStyle = lineStyleBuilder.BuildStyle ();
			var polygonStyle = polygonStyleBuilder.BuildStyle();

			var psb = new PointStyleBuilder ();
			psb.Color = new Color (Android.Graphics.Color.Fuchsia);
			psb.Size = 4.0f;
			var pointStyle = psb.BuildStyle ();

			// Create style selector.
			// Style selectors allow to assign styles based on element attributes and view parameters (zoom, for example)
			// Style filter expressions are given in a simple SQL-like language.
			StyleSelectorBuilder styleSelectorBuilder = new StyleSelectorBuilder();
			styleSelectorBuilder.AddRule(polygonStyle);
//			styleSelectorBuilder.AddRule(pointStyle);

			StyleSelector styleSelector = styleSelectorBuilder.BuildSelector();

			// Create data source. Use constructed style selector and copied shape file containing points.

			string shpPath = new File (dir, "Adm.tab").AbsolutePath;
			Log.Info ("opening " + shpPath);
			OGRVectorDataSource ogrDataSource = new OGRVectorDataSource(proj, styleSelector, shpPath);
			VectorLayer ogrLayer = new VectorLayer(ogrDataSource);
			ogrDataSource.GeometrySimplifier = new Nutiteq.Geometry.DouglasPeuckerGeometrySimplifier (0.05f);
		//	ogrLayer.VisibleZoomRange = new MapRange (18, 20);
			mapView.Layers.Add(ogrLayer);

			// Zoom in to the actual data
			MapBounds bounds = ogrDataSource.DataExtent;
			Log.Info ("data bounds: " + bounds.ToString());
			Android.Util.DisplayMetrics displaymetrics = new Android.Util.DisplayMetrics();
			WindowManager.DefaultDisplay.GetMetrics(displaymetrics);
			int height = displaymetrics.HeightPixels;
			int width = displaymetrics.WidthPixels;
			ScreenBounds screenBounds = new ScreenBounds (new ScreenPos (0, 0), new ScreenPos (width, height));
			mapView.MoveToFitBounds(bounds, screenBounds, false, 0.5f);

			// Create overlay layer for markers
			var dataSourceOverlay = new LocalVectorDataSource (proj);
			var overlayLayer = new VectorLayer (dataSourceOverlay);
			mapView.Layers.Add (overlayLayer);
		
			mapView.MapEventListener = new MapListener (dataSourceOverlay, mapView);

		}
	}
}
