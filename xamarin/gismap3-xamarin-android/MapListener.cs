using Nutiteq.Core;
using Nutiteq.Ui;
using Nutiteq.DataSources;
using Nutiteq.VectorElements;
using Nutiteq.Styles;
using Nutiteq.WrappedCommons;
using Nutiteq.Utils;
using Nutiteq.Graphics;
using System.Text;

namespace HelloMap
{

	public class MapListener : MapEventListener
	{
		private LocalVectorDataSource _dataSource;
		private BalloonPopup _oldClickLabel;
		private IMapView _mapView;

		public MapListener(LocalVectorDataSource dataSource, IMapView mapView)
		{
			_dataSource = dataSource;
			_mapView = mapView;
		}

		public override void OnMapClicked (MapClickInfo mapClickInfo)
		{
			// Remove old click label
			if (_oldClickLabel != null) {
				_dataSource.Remove(_oldClickLabel);
				_oldClickLabel = null;
			}
		}

		public override void OnMapMoved()
		{

			var mapPos1 = _mapView.ScreenToMap(new ScreenPos(0,0));
		//	var mapPos2 = _mapView.ScreenToMap(new ScreenPos(_mapView.Width,_mapView.Height));
				
			Log.Debug ("bounds: " + mapPos1.X + " "+ mapPos1.Y); 

		}

		public override void OnVectorElementClicked(VectorElementsClickInfo vectorElementsClickInfo)
		{
			// A note about iOS: DISABLE 'Optimize PNG files for iOS' option in iOS build settings,
			// otherwise icons can not be loaded using AssetUtils/Bitmap constructor as Xamarin converts
			// PNGs to unsupported custom format.

			// Remove old click label
			if (_oldClickLabel != null) {
				_dataSource.Remove(_oldClickLabel);
				_oldClickLabel = null;
			}

			var clickInfo = vectorElementsClickInfo.VectorElementClickInfos[0];

			var styleBuilder = new BalloonPopupStyleBuilder();
			// Configure simple style
			styleBuilder.LeftMargins = new BalloonPopupMargins (0, 3, 0, 6);
			styleBuilder.RightMargins = new BalloonPopupMargins (0, 3, 0, 6);

			// Make sure this label is shown on top all other labels
			styleBuilder.PlacementPriority = 10;

			var vectorElement = clickInfo.VectorElement;

			if (vectorElement is BalloonPopup) {
				return;
			}

			var stringMap = vectorElement.GetMetaData();
			StringBuilder msgBuilder = new StringBuilder ();

			if (stringMap.Count > 0) {
				
				foreach (string key in stringMap.Keys) {
					string value = "";
					var success = stringMap.TryGetValue(key, out value);

					Log.Debug(""+key+" = "+value);
					msgBuilder.Append (key + " = " + value + "\n");
				}
				msgBuilder.Remove (msgBuilder.Length-1, 1);
			}
			var clickText = msgBuilder.ToString();
			var clickPopup = new BalloonPopup(clickInfo.ElementClickPos, 
				styleBuilder.BuildStyle(),
				"Attributes:", 
				clickText);

			_dataSource.Add(clickPopup);
			_oldClickLabel = clickPopup;


		}
	}
}
