package com.nutiteq.gismap3;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.nutiteq.core.MapBounds;
import com.nutiteq.core.MapPos;
import com.nutiteq.core.MapRange;
import com.nutiteq.core.ScreenBounds;
import com.nutiteq.core.ScreenPos;
import com.nutiteq.datasources.LocalVectorDataSource;
import com.nutiteq.datasources.OGRVectorDataSource;
import com.nutiteq.gismap3.R;
import com.nutiteq.graphics.Color;
import com.nutiteq.layers.NutiteqOnlineVectorTileLayer;
import com.nutiteq.layers.VectorLayer;
import com.nutiteq.layers.VectorTileLayer;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.projections.Projection;
import com.nutiteq.styles.BalloonPopupStyleBuilder;
import com.nutiteq.styles.LineStyle;
import com.nutiteq.styles.LineStyleBuilder;
import com.nutiteq.styles.MarkerStyle;
import com.nutiteq.styles.MarkerStyleBuilder;
import com.nutiteq.styles.PointStyle;
import com.nutiteq.styles.PointStyleBuilder;
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
import com.nutiteq.utils.Log;
import com.nutiteq.vectorelements.BalloonPopup;
import com.nutiteq.vectorelements.Marker;
import com.nutiteq.wrappedcommons.StringMap;

/**
 * A sample showing how to use OGRVectorDataSource, style selectors and custom element meta data.
 */
public class OGROverlayActivity extends Activity {
	
    static {
        try {
            // force Java to load PROJ.4 library. Needed as we don't call it directly, but 
            // OGR datasource reading may need it.
            System.loadLibrary("proj");
        } catch (Throwable t) {
            System.err.println("Unable to load proj: " + t);
        }
    }
    
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
				    Log.debug(""+stringMap.get_key(i)+" = "+stringMap.get(stringMap.get_key(i)));
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

	void testVector(MapView mapView) {
		Projection proj = new EPSG3857();
        
        // 2. Add a pin marker to map
        // Initialize a local vector data source
        LocalVectorDataSource vectorDataSource1 = new LocalVectorDataSource(proj);
        
        // Create marker style, by first loading marker bitmap
        Bitmap androidMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        com.nutiteq.graphics.Bitmap markerBitmap = BitmapUtils.createBitmapFromAndroidBitmap(androidMarkerBitmap);        
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
            
            AssetCopy.copyAssetToSDCard(getAssets(), "bina_polyon.shp", localDir);
            AssetCopy.copyAssetToSDCard(getAssets(), "bina_polyon.dbf", localDir);
            AssetCopy.copyAssetToSDCard(getAssets(), "bina_polyon.prj", localDir);
            AssetCopy.copyAssetToSDCard(getAssets(), "bina_polyon.shx", localDir);
        } catch (IOException e) {
			e.printStackTrace();
		}

        // Create sample point styles, one for cafes/restaurants, the other for all other POIs
//      PointStyleBuilder pointStyleBuilder = new PointStyleBuilder();
//      pointStyleBuilder.setColor(new Color(0xffff0000)); // fully opaque, red
//      pointStyleBuilder.setSize(5.0f);
//      PointStyle pointStyleBig = pointStyleBuilder.buildStyle();
//      pointStyleBuilder.setColor(new Color(0x7f7f0000)); // half-transparent, red
//      pointStyleBuilder.setSize(3.0f);
//      PointStyle pointStyleSmall = pointStyleBuilder.buildStyle();
        
        // Create line style
//      LineStyleBuilder lineStyleBuilder = new LineStyleBuilder();
//      lineStyleBuilder.setColor(new Color(0xff00ff00));
//      lineStyleBuilder.setWidth(2.0f);
//      LineStyle lineStyle = lineStyleBuilder.buildStyle();
                
//      lineStyleBuilder.setColor(new Color(0xffff0000));
//      LineStyle lineStyle2 = lineStyleBuilder.buildStyle();
        
//      lineStyleBuilder.setColor(new Color(0xffffff00));
//      LineStyle lineStyle3 = lineStyleBuilder.buildStyle();
        
        // Create polygon style
        PolygonStyleBuilder polygonStyleBuilder = new PolygonStyleBuilder();
        polygonStyleBuilder.setColor(new Color(0xff00ff00));
        PolygonStyle polygonStyle = polygonStyleBuilder.buildStyle();

        // Create style selector.
        // Style selectors allow to assign styles based on element attributes and view parameters (zoom, for example)
        // Style filter expressions are given in a simple SQL-like language.
        StyleSelectorBuilder styleSelectorBuilder = new StyleSelectorBuilder();
//      styleSelectorBuilder.addRule("type='cafe' OR type='restaurant'", pointStyleBig) // 'type' is a member of geometry meta data
//      styleSelectorBuilder.addRule(pointStyleSmall)
//      styleSelectorBuilder.addRule("ROADTYPE = 1", lineStyle)
//      styleSelectorBuilder.addRule("ROADTYPE = 2", lineStyle2)
//      styleSelectorBuilder.addRule("ROADTYPE = 3", lineStyle3)
//      styleSelectorBuilder.addRule(lineStyle);
        styleSelectorBuilder.addRule(polygonStyle);
        StyleSelector styleSelector = styleSelectorBuilder.buildSelector();
        
        // Create data source. Use constructed style selector and copied shape file containing points.
        OGRVectorDataSource.setConfigOption("SHAPE_ENCODING", "ISO8859_1");
        OGRVectorDataSource ogrDataSource = new OGRVectorDataSource(proj, styleSelector, localDir + "/bina_polyon.shp");
//      ogrDataSource.setCodePage("CP1254");
        MapBounds bounds = ogrDataSource.getDataExtent();
        
        Log.debug("features:" + ogrDataSource.getFeatureCount());
        Log.debug("bounds:"+bounds.toString());
        
        
        // Fit to bounds
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        mapView.moveToFitBounds(bounds,
                new ScreenBounds(new ScreenPos(0, 0), new ScreenPos(width, height)),false, 0.5f);

        // Create vector layer using OGR data source
        VectorLayer ogrLayer = new VectorLayer(ogrDataSource);
        mapView.getLayers().add(ogrLayer);

        // Create layer for popups and attach event listener
        popupDataSource = new LocalVectorDataSource(proj);
        VectorLayer popupLayer = new VectorLayer(popupDataSource);
        mapView.getLayers().add(popupLayer);
        mapView.setMapEventListener(new ActivityMapEventListener());		
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.setShowInfo(true);
        Log.setShowError(true);
        
        // Get your own license from developer.nutiteq.com
        MapView.registerLicense(getString(R.string.license_code), getApplicationContext());

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
        VectorTileLayer baseLayer = new NutiteqOnlineVectorTileLayer("osmbright.zip");
        mapView.getLayers().add(baseLayer);

        testVector(mapView);
    }
}
