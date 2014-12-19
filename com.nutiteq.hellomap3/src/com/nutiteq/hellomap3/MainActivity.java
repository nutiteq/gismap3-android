package com.nutiteq.hellomap3;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.nutiteq.core.MapBounds;
import com.nutiteq.core.MapPos;
import com.nutiteq.core.MapRange;
import com.nutiteq.datasources.LocalVectorDataSource;
import com.nutiteq.datasources.OGRVectorDataSource;
import com.nutiteq.graphics.Color;
import com.nutiteq.layers.NutiteqOnlineVectorTileLayer;
import com.nutiteq.layers.VectorLayer;
import com.nutiteq.layers.VectorTileLayer;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.styles.BalloonPopupStyleBuilder;
import com.nutiteq.styles.MarkerStyle;
import com.nutiteq.styles.MarkerStyleBuilder;
import com.nutiteq.styles.PolygonStyle;
import com.nutiteq.styles.PolygonStyleBuilder;
import com.nutiteq.styles.StyleSelector;
import com.nutiteq.styles.StyleSelectorBuilder;
import com.nutiteq.ui.MapClickInfo;
import com.nutiteq.ui.MapEventListener;
import com.nutiteq.ui.MapView;
import com.nutiteq.ui.VectorElementClickInfo;
import com.nutiteq.ui.VectorElementsClickInfo;
import com.nutiteq.utils.BitmapUtils;
import com.nutiteq.vectorelements.BalloonPopup;
import com.nutiteq.vectorelements.Marker;
import com.nutiteq.wrappedcommons.StringMap;

public class MainActivity extends Activity {
	
	// Listener that displays vector element meta data as popups
	class ActivityMapEventListener extends MapEventListener {
		@Override
		public void onMapMoved() {
		}

		@Override
		public void onMapClicked(MapClickInfo mapClickInfo) {	
		}

		@Override
		public void onVectorElementClicked(VectorElementsClickInfo vectorElementsClickInfo) {
			popupDataSource.removeAll();
			VectorElementClickInfo mapClickInfo = vectorElementsClickInfo.getVectorElementClickInfos().get(0);
			StringMap stringMap = mapClickInfo.getVectorElement().getMetaData();
			if (stringMap.size() > 0) {
				StringBuilder msgBuilder = new StringBuilder();
				for (int i = 0; i < stringMap.size(); i++) {
					msgBuilder.append(stringMap.get_key(i));
					msgBuilder.append("=");
					msgBuilder.append(stringMap.get(stringMap.get_key(i)));
					msgBuilder.append("\n");
				}
				BalloonPopupStyleBuilder styleBuilder = new BalloonPopupStyleBuilder();
				BalloonPopup clickPopup = new BalloonPopup(
						mapClickInfo.getClickPos(),
						styleBuilder.buildStyle(),
						"",
						msgBuilder.toString());
				popupDataSource.add(clickPopup);
			}
		}
	}
	
	private LocalVectorDataSource popupDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Basic map setup
        // Create map view 
        MapView mapView = (MapView) this.findViewById(R.id.map_view);
        
        // Set the base projection, that will be used for most MapView, MapEventListener and Options methods
        EPSG3857 proj = new EPSG3857();
        mapView.getOptions().setBaseProjection(proj); // note: EPSG3857 is the default, so this is actually not required
        
        // General options
        mapView.getOptions().setRotatable(true); // make map rotatable (this is also the default)
        mapView.getOptions().setTileThreadPoolSize(2); // use 2 download threads for tile downloading

        // Create base layer. Use registered Nutiteq API key and vector style from assets (osmbright.zip)
        VectorTileLayer baseLayer = new NutiteqOnlineVectorTileLayer("15cd9131072d6df68b8a54feda5b0496", "osmbright.zip");
        mapView.getLayers().add(baseLayer);
                
        // 2. Add a pin marker to map
        // Initialize a local vector data source
        LocalVectorDataSource vectorDataSource1 = new LocalVectorDataSource(proj);
        
        // Create marker style, by first loading marker bitmap
        Bitmap androidMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        com.nutiteq.graphics.Bitmap markerBitmap = BitmapUtils.CreateBitmapFromAndroidBitmap(androidMarkerBitmap);        
        MarkerStyleBuilder markerStyleBuilder = new MarkerStyleBuilder();
        markerStyleBuilder.setBitmap(markerBitmap);
        markerStyleBuilder.setSize(30);
        MarkerStyle sharedMarkerStyle = markerStyleBuilder.buildStyle();
        // Add marker to the local data source
        Marker marker1 = new Marker(proj.fromWgs84(new MapPos(13.38933, 52.51704)), sharedMarkerStyle);
        vectorDataSource1.add(marker1);
        
        // Create a vector layer with the previously created data source
        VectorLayer vectorLayer1 = new VectorLayer(vectorDataSource1);
        // Add the previous vector layer to the map
        mapView.getLayers().add(vectorLayer1);
        // Set visible zoom range for the vector layer
        vectorLayer1.setVisibleZoomRange(new MapRange(0, 24)); // this is optional, by default layer is visible for all zoom levels
        
        // Copy sample shape file from assets folder to SDCard
        String localDir = getFilesDir().toString();
        try {
//			AssetCopy.copyAssetToSDCard(getAssets(), "points.shp", localDir);
//			AssetCopy.copyAssetToSDCard(getAssets(), "points.dbf", localDir);
//			AssetCopy.copyAssetToSDCard(getAssets(), "points.prj", localDir);
//			AssetCopy.copyAssetToSDCard(getAssets(), "points.shx", localDir);

			AssetCopy.copyAssetToSDCard(getAssets(), "bina_polyon.shp", localDir);
			AssetCopy.copyAssetToSDCard(getAssets(), "bina_polyon.dbf", localDir);
			AssetCopy.copyAssetToSDCard(getAssets(), "bina_polyon.prj", localDir);
			AssetCopy.copyAssetToSDCard(getAssets(), "bina_polyon.shx", localDir);
			
            AssetCopy.copyAssetToSDCard(getAssets(), "maakond_20130401.tab", localDir);
            AssetCopy.copyAssetToSDCard(getAssets(), "maakond_20130401.DAT", localDir);
            AssetCopy.copyAssetToSDCard(getAssets(), "maakond_20130401.ID", localDir);
            AssetCopy.copyAssetToSDCard(getAssets(), "maakond_20130401.MAP", localDir);
            			
			
        } catch (IOException e) {
			e.printStackTrace();
		}

        /*
        // Create sample point styles, one for cafes/restaurants, the other for all other POIs
        PointStyleBuilder pointStyleBuilder = new PointStyleBuilder();
        pointStyleBuilder.setColor(new Color(0xffff0000)); // fully opaque, red
        pointStyleBuilder.setSize(5.0f);
        PointStyle pointStyleBig = pointStyleBuilder.buildStyle();
        pointStyleBuilder.setColor(new Color(0x7f7f0000)); // half-transparent, red
        pointStyleBuilder.setSize(3.0f);
        PointStyle pointStyleSmall = pointStyleBuilder.buildStyle();
        */

        // Create polygon style
        PolygonStyleBuilder polygonStyleBuilder = new PolygonStyleBuilder();
        polygonStyleBuilder.setColor(new Color(0xff00ff00));
        PolygonStyle polygonStyle = polygonStyleBuilder.buildStyle();
        
        // Create style selector.
        // Style selectors allow to assign styles based on element attributes and view parameters (zoom, for example)
        // Style filter expressions are given in a simple SQL-like language.
        StyleSelectorBuilder styleSelectorBuilder = new StyleSelectorBuilder()
        		//.addRule("type='cafe' OR type='restaurant'", pointStyleBig) // 'type' is a member of geometry meta data
        		//.addRule(pointStyleSmall);
        		.addRule(polygonStyle);
        StyleSelector styleSelector = styleSelectorBuilder.buildSelector();
        
        // Create data source. Use constructed style selector and copied shape file containing points.
        //OGRVectorDataSource.SetConfigOption("SHAPE_ENCODING", "CP1254");
        OGRVectorDataSource ogrDataSource = new OGRVectorDataSource(proj, styleSelector, localDir + "/maakond_20130401.tab");
        ogrDataSource.setCodePage("CP1254");
        MapBounds bounds = ogrDataSource.getDataExtent();
        Log.d("nutiteq","bounds:"+bounds.toString());
        mapView.setFocusPos(bounds.getCenter(), 0.0f);
        mapView.setZoom(5.0f, 0.0f);

        // Create vector layer using OGR data source
        VectorLayer ogrLayer = new VectorLayer(ogrDataSource);
        mapView.getLayers().add(ogrLayer);

        // Create layer for popups and attach event listener
        popupDataSource = new LocalVectorDataSource(proj);
        VectorLayer popupLayer = new VectorLayer(popupDataSource);
        mapView.getLayers().add(popupLayer);
        mapView.setMapEventListener(new ActivityMapEventListener());
    }
}
