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

namespace HelloMap
{
	[Activity (Label = "Hellomap", MainLauncher = true, Icon = "@drawable/icon")]
	public class MainActivity : Activity
	{
		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);

			// Register license
			Nutiteq.Utils.Log.ShowError = true;
			Nutiteq.Utils.Log.ShowWarn = true;
			MapView.RegisterLicense("XTUMwQ0ZRQzdURnJKck9HYUdhT09VNGFSN3o3Nmg3UWhjQUlVTnV4TStMMk0vemhPMXUwUnBGRlhwbmFtTklFPQoKcHJvZHVjdHM9c2RrLXhhbWFyaW4taW9zLTMuKixzZGsteGFtYXJpbi1hbmRyb2lkLTMuKgpwYWNrYWdlTmFtZT1jb20ubnV0aXRlcS5oZWxsb21hcC54YW1hcmluCmJ1bmRsZUlkZW50aWZpZXI9Y29tLm51dGl0ZXEuaGVsbG9tYXAueGFtYXJpbgp3YXRlcm1hcms9bnV0aXRlcQp2YWxpZFVudGlsPTIwMTUtMDYtMDEKdXNlcktleT0yYTllOWY3NDYyY2VmNDgxYmUyYThjMTI2MWZlNmNiZAo", ApplicationContext);

			// Set our view from the "main" layout resource
			SetContentView (Resource.Layout.Main);
			var mapView = (MapView)FindViewById (Resource.Id.mapView);

			// Create package manager folder (Platform-specific)
			var packageFolder = new File (GetExternalFilesDir(null), "gismap_temp");
			if (!(packageFolder.Mkdirs() || packageFolder.IsDirectory)) {
				Log.Fatal("Could not create package folder!");
			}

			// Copy bundled tile data to file system, so it can be imported by package manager
			foreach (string fileName in new string[] { "bina_polyon.dbf", "bina_polyon.prj", "bina_polyon.shp", "bina_polyon.shx" }) {
				string importPath = new File (GetExternalFilesDir (null), fileName).AbsolutePath;
				using (var input = Assets.Open (fileName)) {
					using (var output = new System.IO.FileStream (importPath, System.IO.FileMode.Create)) {
						input.CopyTo (output);
					}
				}
			}

			// Set base projection
			EPSG3857 proj = new EPSG3857();
			mapView.Options.BaseProjection = proj; // note: EPSG3857 is the default, so this is actually not required

			PolygonStyleBuilder polygonStyleBuilder = new PolygonStyleBuilder();
			polygonStyleBuilder.Color = new Color(255, 0, 255, 255);
			PolygonStyle polygonStyle = polygonStyleBuilder.BuildStyle();

			// Create style selector.
			// Style selectors allow to assign styles based on element attributes and view parameters (zoom, for example)
			// Style filter expressions are given in a simple SQL-like language.
			StyleSelectorBuilder styleSelectorBuilder = new StyleSelectorBuilder();
			styleSelectorBuilder.AddRule(polygonStyle);
			StyleSelector styleSelector = styleSelectorBuilder.BuildSelector();

			// Create data source. Use constructed style selector and copied shape file containing points.
			string shpPath = new File (GetExternalFilesDir (null), "bina_polyon.shp").AbsolutePath;
			OGRVectorDataSource ogrDataSource = new OGRVectorDataSource(proj, styleSelector, shpPath);
			VectorLayer ogrLayer = new VectorLayer(ogrDataSource);
			mapView.Layers.Add(ogrLayer);

			// Zoom in to the actual data
			MapBounds bounds = ogrDataSource.DataExtent;
			Android.Util.DisplayMetrics displaymetrics = new Android.Util.DisplayMetrics();
			WindowManager.DefaultDisplay.GetMetrics(displaymetrics);
			int height = displaymetrics.HeightPixels;
			int width = displaymetrics.WidthPixels;
			ScreenBounds screenBounds = new ScreenBounds (new ScreenPos (0, 0), new ScreenPos (width, height));
			mapView.MoveToFitBounds(bounds, screenBounds, false, 0.5f);
		}
	}
}
