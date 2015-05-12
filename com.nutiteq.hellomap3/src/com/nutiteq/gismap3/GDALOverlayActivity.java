package com.nutiteq.gismap3;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.nutiteq.core.MapBounds;
import com.nutiteq.core.ScreenBounds;
import com.nutiteq.core.ScreenPos;
import com.nutiteq.datasources.GDALRasterTileDataSource;
import com.nutiteq.gismap3.R;
import com.nutiteq.layers.NutiteqOnlineVectorTileLayer;
import com.nutiteq.layers.RasterTileLayer;
import com.nutiteq.layers.VectorTileLayer;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.ui.MapView;
import com.nutiteq.utils.Log;

public class GDALOverlayActivity extends Activity {
	
    static {
        try {
            // force Java to load PROJ.4 library. Needed as we don't call it directly, but 
            // OGR datasource reading may need it.
            System.loadLibrary("proj");
        } catch (Throwable t) {
            System.err.println("Unable to load proj: " + t);
        }
    }
    
	void testRaster(MapView mapView) {
        String localDir = getFilesDir().toString();
        try {
            AssetCopy.copyAssetToSDCard(getAssets(), "chicago.tif", localDir);
        } catch (IOException e) {
			e.printStackTrace();
		}
        
        mapView.getOptions().setTileThreadPoolSize(2); // faster tile loading

        // Create GDAL raster tile layer
		GDALRasterTileDataSource dataSource = new GDALRasterTileDataSource(0, 23, localDir + "/chicago.tif");
		RasterTileLayer rasterLayer = new RasterTileLayer(dataSource);
		mapView.getLayers().add(rasterLayer);
		
		// Calculate zoom bias, basically this is needed to 'undo' automatic DPI scaling, we will display original raster with close to 1:1 pixel density
		double zoomLevelBias = Math.log(mapView.getOptions().getDPI() / 160) / Math.log(2);
		rasterLayer.setZoomLevelBias((float) zoomLevelBias);

		// Find GDAL layer bounds
		MapBounds bounds = dataSource.getDataExtent();

        // Fit to bounds
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        mapView.moveToFitBounds(bounds,
                new ScreenBounds(new ScreenPos(0, 0), new ScreenPos(width, height)), false, 0.0f);
	}
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.setShowInfo(true);
        Log.setShowError(true);
        
        // Get your own license from developer.nutiteq.com
        MapView.registerLicense("XTUN3Q0ZBd2NtcmFxbUJtT1h4QnlIZ2F2ZXR0Mi9TY2JBaFJoZDNtTjUvSjJLay9aNUdSVjdnMnJwVXduQnc9PQoKcHJvZHVjdHM9c2RrLWlvcy0zLiosc2RrLWFuZHJvaWQtMy4qCnBhY2thZ2VOYW1lPWNvbS5udXRpdGVxLioKYnVuZGxlSWRlbnRpZmllcj1jb20ubnV0aXRlcS4qCndhdGVybWFyaz1ldmFsdWF0aW9uCnVzZXJLZXk9MTVjZDkxMzEwNzJkNmRmNjhiOGE1NGZlZGE1YjA0OTYK", getApplicationContext());

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

        testRaster(mapView);
    }
}
