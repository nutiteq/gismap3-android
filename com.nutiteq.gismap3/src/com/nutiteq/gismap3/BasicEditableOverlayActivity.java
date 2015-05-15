package com.nutiteq.gismap3;

import android.app.Activity;
import android.os.Bundle;

import com.nutiteq.core.MapPos;
import com.nutiteq.geometry.Geometry;
import com.nutiteq.geometry.LineGeometry;
import com.nutiteq.geometry.PointGeometry;
import com.nutiteq.geometry.PolygonGeometry;
import com.nutiteq.datasources.LocalVectorDataSource;
import com.nutiteq.gismap3.R;
import com.nutiteq.graphics.Color;
import com.nutiteq.layers.EditableVectorLayer;
import com.nutiteq.layers.NutiteqOnlineVectorTileLayer;
import com.nutiteq.ui.VectorElementDragPointStyle;
import com.nutiteq.ui.VectorElementDragResult;
import com.nutiteq.ui.VectorEditEventListener;
import com.nutiteq.layers.VectorTileLayer;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.projections.Projection;
import com.nutiteq.styles.LineStyleBuilder;
import com.nutiteq.styles.PointStyle;
import com.nutiteq.styles.PointStyleBuilder;
import com.nutiteq.styles.PolygonStyleBuilder;
import com.nutiteq.ui.MapClickInfo;
import com.nutiteq.ui.MapEventListener;
import com.nutiteq.ui.MapView;
import com.nutiteq.ui.VectorElementClickInfo;
import com.nutiteq.ui.VectorElementDragInfo;
import com.nutiteq.ui.VectorElementsClickInfo;
import com.nutiteq.utils.Log;
import com.nutiteq.vectorelements.Line;
import com.nutiteq.vectorelements.Point;
import com.nutiteq.vectorelements.Polygon;
import com.nutiteq.vectorelements.VectorElement;
import com.nutiteq.wrappedcommons.MapPosVector;

/**
 * A minimal sample displaying EditableVectorLayer usage.
 */
public class BasicEditableOverlayActivity extends Activity {
		
	class MyEditEventListener extends VectorEditEventListener {
    	private PointStyle styleNormal;
    	private PointStyle styleVirtual;
    	private PointStyle styleSelected;
    	private final LocalVectorDataSource vectorDataSource;
    	
    	public MyEditEventListener(LocalVectorDataSource vectorDataSource) {
    		this.vectorDataSource = vectorDataSource;
    	}
    	
    	@Override
    	public boolean onElementSelect(VectorElement element) {
    		Log.debug("elementSelected");
    		return true;
    	}
    	
    	@Override
    	public void onElementDeselected(VectorElement element) {
    		Log.debug("elementDeselected");
    	}
    	
    	@Override
    	public void onElementModify(VectorElement element, Geometry geometry) {
    		if (element instanceof Point && geometry instanceof PointGeometry) {
    			((Point) element).setGeometry((PointGeometry) geometry);
    		}
    		if (element instanceof Line && geometry instanceof LineGeometry) {
    			((Line) element).setGeometry((LineGeometry) geometry);
    		}
    		if (element instanceof Polygon && geometry instanceof PolygonGeometry) {
    			((Polygon) element).setGeometry((PolygonGeometry) geometry);
    		}
    		Log.debug("modifyElement");
    	}
        
    	@Override
		public void onElementDelete(VectorElement element) {
    		Log.debug("deleteElement");
    		vectorDataSource.remove(element);
    	}

    	@Override
		public VectorElementDragResult onDragStart(VectorElementDragInfo dragInfo) {
    		Log.debug("dragStart");
    		return VectorElementDragResult.VECTOR_ELEMENT_DRAG_RESULT_MODIFY;
    	}

    	@Override
		public VectorElementDragResult onDragMove(VectorElementDragInfo dragInfo) {
    		return VectorElementDragResult.VECTOR_ELEMENT_DRAG_RESULT_MODIFY;
    	}

    	@Override
		public VectorElementDragResult onDragEnd(VectorElementDragInfo dragInfo) {
    		Log.debug("dragEnd");
    		return VectorElementDragResult.VECTOR_ELEMENT_DRAG_RESULT_MODIFY;
    	}

    	@Override
		public PointStyle getDragPointStyle(VectorElement element, VectorElementDragPointStyle dragPointStyle) {
    		if (styleNormal == null) {
    			PointStyleBuilder builder = new PointStyleBuilder();
    			builder.setColor(new Color(0xa0ffffff));
    			builder.setSize(20.0f);
    			styleNormal = builder.buildStyle();
    			builder.setSize(15.0f);
    			styleVirtual = builder.buildStyle();
    			builder.setColor(new Color(0xc0ffffff));
    			builder.setSize(30.0f);
    			styleSelected = builder.buildStyle();
    		}
    		
    		switch (dragPointStyle) {
    		case VECTOR_ELEMENT_DRAG_POINT_STYLE_SELECTED:
    			return styleSelected;
    		case VECTOR_ELEMENT_DRAG_POINT_STYLE_VIRTUAL:
    			return styleVirtual;
    		default:
    			return styleNormal;
    		}
		}
		
	}
	
	class MyMapEventListener extends MapEventListener {
		private EditableVectorLayer vectorLayer;
		
		public MyMapEventListener(EditableVectorLayer vectorLayer) {
			this.vectorLayer = vectorLayer;
		}
		
		@Override
		public void onMapMoved() {
		}

		@Override
		public void onMapClicked(MapClickInfo mapClickInfo) {
			vectorLayer.setSelectedVectorElement(null);
		}

		@Override
		public void onVectorElementClicked(VectorElementsClickInfo vectorElementsClickInfo) {
			VectorElementClickInfo mapClickInfo = vectorElementsClickInfo.getVectorElementClickInfos().get(0);
			vectorLayer.setSelectedVectorElement(mapClickInfo.getVectorElement());
		}		
	}
	
	void testEditable(MapView mapView) {
		Projection proj = new EPSG3857();
        
        final LocalVectorDataSource vectorDataSource = new LocalVectorDataSource(proj);
        MapPosVector mapPoses = new MapPosVector();

        mapPoses.add(new MapPos(-5000000, -900000));
        PointStyleBuilder pointStyleBuilder = new PointStyleBuilder();
        pointStyleBuilder.setColor(new Color(0xffff0000));
        Point point = new Point(mapPoses.get(0), pointStyleBuilder.buildStyle());
        vectorDataSource.add(point);

        mapPoses.clear();
        mapPoses.add(new MapPos(-6000000, -500000));
        mapPoses.add(new MapPos(-9000000, -500000));
        LineStyleBuilder lineStyleBuilder = new LineStyleBuilder();
        lineStyleBuilder.setColor(new Color(0xff0000ff));
        Line line = new Line(mapPoses, lineStyleBuilder.buildStyle());
        vectorDataSource.add(line);

        mapPoses.clear();
        mapPoses.add(new MapPos(-5000000, -5000000));
        mapPoses.add(new MapPos( 5000000, -5000000));
        mapPoses.add(new MapPos(       0, 10000000));
        PolygonStyleBuilder polygonStyleBuilder = new PolygonStyleBuilder();
        polygonStyleBuilder.setColor(new Color(0xff00ff00));
        Polygon polygon = new Polygon(mapPoses, polygonStyleBuilder.buildStyle());
        vectorDataSource.add(polygon);

        EditableVectorLayer editLayer = new EditableVectorLayer(vectorDataSource);
		mapView.getLayers().add(editLayer);
		
		editLayer.setVectorEditEventListener(new MyEditEventListener(vectorDataSource));
		
		mapView.setMapEventListener(new MyMapEventListener(editLayer));
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.setShowInfo(true);
        Log.setShowError(true);
        
        // Get your own license from developer.nutiteq.com
        MapView.registerLicense("XTUN3Q0ZBd2NtcmFxbUJtT1h4QnlIZ2F2ZXR0Mi9TY2JBaFJoZDNtTjUvSjJLay9aNUdSVjdnMnJwVXduQnc9PQoKcHJvZHVjdHM9c2RrLWlvcy0zLiosc2RrLWFuZHJvaWQtMy4qCnBhY2thZ2VOYW1lPWNvbS5udXRpdGVxLioKYnVuZGxlSWRlbnRpZmllcj1jb20ubnV0aXRlcS4qCndhdGVybWFyaz1ldmFsdWF0aW9uCnVzZXJLZXk9MTVjZDkxMzEwNzJkNmRmNjhiOGE1NGZlZGE1YjA0OTYK", getApplicationContext());

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

        // Create editable layer with listeners
        testEditable(mapView);
		
		mapView.setZoom(2, 0);
    }
}
