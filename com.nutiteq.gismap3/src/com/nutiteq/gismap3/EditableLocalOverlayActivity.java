package com.nutiteq.gismap3;

import com.nutiteq.core.MapPos;
import com.nutiteq.core.ScreenPos;
import com.nutiteq.datasources.LocalVectorDataSource;
import com.nutiteq.graphics.Color;
import com.nutiteq.layers.EditableVectorLayer;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.projections.Projection;
import com.nutiteq.styles.PolygonStyleBuilder;
import com.nutiteq.vectorelements.Polygon;
import com.nutiteq.vectorelements.VectorElement;
import com.nutiteq.wrappedcommons.MapPosVector;
import com.nutiteq.wrappedcommons.StringMap;

public class EditableLocalOverlayActivity extends EditableOverlayActivityBase {
	private LocalVectorDataSource editableDataSource;
    
    @Override
    protected EditableVectorLayer createEditableLayer() {
		Projection proj = new EPSG3857();        
		editableDataSource = new LocalVectorDataSource(proj);
        EditableVectorLayer editLayer = new EditableVectorLayer(editableDataSource);
		mapView.getLayers().add(editLayer);
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
    }

    @Override
    protected void discardEditableLayerChanges() {
    }

    @Override
    protected boolean hasEditableLayerChanges() {
    	return false;
    }
}
