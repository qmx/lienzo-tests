package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WiresTests extends FlowPanel {

    private Layer layer;
    private IControlHandleList m_ctrls;

    public WiresTests(Layer layer) {
        this.layer = layer;
    }


    public void testWires() {

        double width = 100, height = 100;
        final double length = 100;

        WiresManager manager = WiresManager.get(layer);
        WiresShape shape = manager.createShape(new MultiPath().rect(200, 200, 300, 300).setStrokeColor("#00CC00"));
        shape.setContainmentAcceptor(null);
        WiresShape circle = manager.createShape(new MultiPath().circle(20).setFillColor("#00CC44").setDraggable(true));
        circle.getGroup().setX(50).setY(200);
        WiresLayer wiresLayer = manager.getLayer();

        wiresLayer.add(shape);
        wiresLayer.add(circle);
        final MultiPath pointer = new MultiPath().circle(5).setFillColor("#00BB00");
        this.layer.add(pointer);
        final List<MultiPath> rects = new ArrayList<>();
        final Map<MultiPath, MultiPath> intersectionMarkers = new HashMap<>();

        this.layer.addNodeMouseMoveHandler(new NodeMouseMoveHandler() {
            @Override
            public void onNodeMouseMove(NodeMouseMoveEvent event) {
                double x = event.getX();
                double y = event.getY();
                pointer.setX(x);
                pointer.setY(y);
                Point2D pointerPosition = new Point2D(x, y);
                for (MultiPath rect : rects) {
//                    GWT.log("rectangle "+ rect.toJSONString());
                    Point2D center = findCenter(rect);
                    NFastArrayList<PathPartList> pathPartListArray = rect.getPathPartListArray();
                    for (int i = 0; i < pathPartListArray.size(); i++) {
                        Point2DArray listOfLines = new Point2DArray();
                        listOfLines.push(center);
                        listOfLines.push(getProjection(center, pointerPosition, length));
                        Set<Point2D>[] intersections = new Set[1];
                        Geometry.getCardinalIntersects(pathPartListArray.get(i), listOfLines, intersections);
                        if (intersections.length == 2) {
//                            GWT.log(intersections[1].toString());
                            Point2D intersection = intersections[1].iterator().next();

                            Point2D projection = getProjection(center, intersection, 100);
                            MultiPath l = null;
                            if (intersectionMarkers.containsKey(rect)) {
                                l = intersectionMarkers.get(rect);
                                l.removeFromParent();
                                intersectionMarkers.remove(rect);
                            }

                            l = new MultiPath().circle(5).setX(intersection.getX()).setY(intersection.getY() - 5).setFillColor("#DD0000");;
                            l.setStrokeWidth(1);
                            intersectionMarkers.put(rect, l);
                            WiresTests.this.layer.add(l);
                        }
                    }
                }
                WiresTests.this.layer.batch();
            }
        });

        for (int k = 1; k < 6; k++) {
            MultiPath rect = new MultiPath().rect(k * (width + 10), 0, width, height);
            rects.add(rect);
            this.layer.add(rect);
        }

        String svg = "M 0 100 L 65 115 L 65 105 L 120 125 L 120 115 L 200 180 L 140 160 L 140 170 L 85 150 L 85 160 L 0 140 Z";
        MultiPath path = new MultiPath(svg);
        rects.add(path);
        this.layer.add(path);

    }

    public Point2D findCenter(MultiPath rect) {
        Point2DArray cardinals = Geometry.getCardinals(rect.getBoundingPoints().getBoundingBox());
        return cardinals.get(0);
    }

    public Point2D getProjection(Point2D center, Point2D intersection, double length) {
        Point2D unit = intersection.sub(center).unit();
        return center.add(unit.mul(length));
    }

}
