package com.nutiteq.gismap3;

import java.io.IOException;

import android.util.DisplayMetrics;

import com.nutiteq.core.MapBounds;
import com.nutiteq.core.MapPos;
import com.nutiteq.core.ScreenBounds;
import com.nutiteq.core.ScreenPos;
import com.nutiteq.datasources.OGRVectorDataSource;
import com.nutiteq.graphics.Color;
import com.nutiteq.layers.EditableVectorLayer;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.projections.Projection;
import com.nutiteq.styles.PolygonStyle;
import com.nutiteq.styles.PolygonStyleBuilder;
import com.nutiteq.styles.StyleSelector;
import com.nutiteq.styles.StyleSelectorBuilder;
import com.nutiteq.utils.Log;
import com.nutiteq.vectorelements.Polygon;
import com.nutiteq.vectorelements.VectorElement;
import com.nutiteq.wrappedcommons.MapPosVector;
import com.nutiteq.wrappedcommons.StringMap;

public class EditableOGROverlayActivity extends EditableOverlayActivityBase {
	private OGRVectorDataSource editableDataSource;
    
    @Override
    protected EditableVectorLayer createEditableLayer() {
        String localDir = getFilesDir().toString();
        try {
            AssetCopy.copyAssetToSDCard(getAssets(), "sample.shp", localDir);
            AssetCopy.copyAssetToSDCard(getAssets(), "sample.prj", localDir);
            AssetCopy.copyAssetToSDCard(getAssets(), "sample.dbf", localDir);
            AssetCopy.copyAssetToSDCard(getAssets(), "sample.shx", localDir);
        } catch (IOException e) {
			e.printStackTrace();
        }
        // Create polygon style
        PolygonStyleBuilder polygonStyleBuilder = new PolygonStyleBuilder();
        polygonStyleBuilder.setColor(new Color(0xff00ff00));
        PolygonStyle polygonStyle = polygonStyleBuilder.buildStyle();

        StyleSelectorBuilder styleSelectorBuilder = new StyleSelectorBuilder();
        styleSelectorBuilder.addRule(polygonStyle);
        StyleSelector styleSelector = styleSelectorBuilder.buildSelector();
        
        Projection proj = new EPSG3857();
		editableDataSource = new OGRVectorDataSource(proj, styleSelector, localDir + "/sample.shp", true);
        EditableVectorLayer editLayer = new EditableVectorLayer(editableDataSource);
		mapView.getLayers().add(editLayer);
        
        // Fit to bounds
        MapBounds bounds = editableDataSource.getDataExtent();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        ScreenBounds screenBounds = new ScreenBounds(new ScreenPos(0, 0), new ScreenPos(displaymetrics.widthPixels, displaymetrics.heightPixels));
        mapView.moveToFitBounds(bounds, screenBounds, false, 0.5f);

        return editLayer;
    }
    
    @Override
    protected VectorElement createElement() {
    	ScreenPos[] screenPoses = new ScreenPos[] {
   			new ScreenPos(0.35f, 0.40f),
   			new ScreenPos(0.65f, 0.40f),
    		new ScreenPos(0.50f, 0.60f),
    	};

        MapPosVector mapPoses = new MapPosVector();
        for (ScreenPos pos : screenPoses) {
        	MapPos mapPos = mapView.screenToMap(new ScreenPos(pos.getX() * mapView.getWidth(), pos.getY() * mapView.getHeight()));
        	mapPoses.add(mapPos);
        }
        PolygonStyleBuilder polygonStyleBuilder = new PolygonStyleBuilder();
        polygonStyleBuilder.setColor(new Color(0xff00ff00));
        StringMap metaData = new StringMap();
        metaData.set("prop1", "XXX");
        metaData.set("prop2", "YYY");
        VectorElement element = new Polygon(mapPoses, polygonStyleBuilder.buildStyle());
        element.setMetaData(metaData);
        return element;
    }

    @Override
    protected void addElement(VectorElement element) {
        editableDataSource.add(element);    	
    }
    
    @Override
    protected void removeElement(VectorElement element) {
    	editableDataSource.remove(element);
    }
    
    @Override
    protected void saveEditableLayerChanges() {
    	editableDataSource.commit();
    }

    @Override
    protected void discardEditableLayerChanges() {
    	editableDataSource.rollback();
    }

    @Override
    protected boolean hasEditableLayerChanges() {
    	return !editableDataSource.isCommitted();
    }
}
