package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WiresTests extends FlowPanel {

    private Layer layer;
    private IControlHandleList m_ctrls;

    public WiresTests(Layer layer) {
        this.layer = layer;
    }

    public enum Direction {
        NE, SE, SW, NW
    }



    public void testWires() {

        final double startX = 200;
        final double startY = 200;
        final double radius = 50;
        final double w = 100;
        final double h = 100;

        Map<Direction, Point2D> directions = new HashMap<Direction, Point2D>();

        // Blue start event.
        MultiPath box1 = new MultiPath().rect( 0, 0, w, h ).setFillColor( "#000000" );
        box1.setX( startX );
        box1.setY( startY );

        MultiPath box2 = new MultiPath().rect( 0, 0, w, h ).setFillColor( "#000000" );
        box2.setX( startX * 2 );
        box2.setY( startY * 2 );

        layer.add(box1);
        layer.add(box2);

        new HoverTimer(layer, box1);
        new HoverTimer(layer, box2);

        boolean init = false;
    }









    public static class HoverTimer implements NodeMouseEnterHandler,
                                              NodeMouseExitHandler {


        private final Layer layer;
        private final MultiPath shape;
        private List<MultiPath> toolbox = new ArrayList<>();


        private Timer m_timer;

        public HoverTimer(Layer layer, MultiPath shape){
            this.layer = layer;
            this.shape = shape;
            this.shape.addNodeMouseEnterHandler(this);
            this.shape.addNodeMouseExitHandler(this);
        }

        private Map<Direction, Point2D> getBoundingBoxAnchors( final MultiPath startEventMultiPath ) {
            Map<Direction, Point2D> boundingBox = new HashMap<>();
            Point2D[] points = startEventMultiPath.getBoundingPoints().getPoints().toArray( new Point2D[]{} );
            if (points.length == 4) {
                double max_x = points[0].getX();
                double max_y = points[0].getY();
                double min_x = points[0].getX();
                double min_y = points[0].getY();
                for ( Point2D point : points ) {
                    if ( point.getX() > max_x ) {
                        max_x = point.getX();
                    }
                    if ( point.getY() > max_y ) {
                        max_y = point.getY();
                    }
                    if ( point.getX() < min_x ) {
                        min_x = point.getX();
                    }
                    if ( point.getY() < min_y ) {
                        min_y = point.getY();
                    }
                }

                for ( Point2D point : points ) {
                    if ( point.getX() == min_x && point.getY() == min_y ) {
                        boundingBox.put( Direction.SW, point );
                    } else if ( point.getX() == max_x && point.getY() == min_y ) {
                        boundingBox.put( Direction.SE, point );
                    } else if ( point.getX() == max_x && point.getY() == max_x ) {
                        boundingBox.put( Direction.NE, point );
                    } else if ( point.getX() == min_x && point.getY() == max_x ) {
                        boundingBox.put( Direction.NW, point );
                    }
                }
            }
            return boundingBox;
        }

        private MultiPath stackIcon(Map<Direction, Integer> toolboxStack, Map<Direction, Point2D> boundingBox, MultiPath shape, Direction direction, MultiPath icon) {
            Point2D anchor = boundingBox.get(direction);
            double iconHeight = icon.getBoundingPoints().getBoundingBox().getHeight();
            double iconWidth = icon.getBoundingPoints().getBoundingBox().getWidth();
            if (direction.equals(Direction.NE)) {
                icon.setX(anchor.getX() + 2);
                icon.setY(anchor.getY() + (toolboxStack.get(direction) * (iconHeight + 2) * -1));
            } else if (direction.equals(Direction.NW)) {
                icon.setX(anchor.getX() - 2 - iconWidth);
                icon.setY(anchor.getY() + (toolboxStack.get(direction) * (iconHeight + 2) * -1));
            } else if (direction.equals(Direction.SE)) {
                icon.setX(anchor.getX() + 2);
                icon.setY(anchor.getY() - (toolboxStack.get(direction) * (iconHeight + 2)));
            } else if (direction.equals(Direction.SW)) {
                icon.setX(anchor.getX() - 2 - iconWidth);
                icon.setY(anchor.getY() - (toolboxStack.get(direction) * (iconHeight + 2) * -1));
            }
            toolboxStack.put(direction, toolboxStack.get(direction) + 1);
            this.layer.add(icon);
            return icon;
        }

        private List<MultiPath> createToolbox(MultiPath targetShape, Map<Direction, Point2D> boundingBox, HashMap<Direction, Integer> toolboxStack) {
            ArrayList<MultiPath> icons = new ArrayList<>();
            icons.add(stackIcon(toolboxStack, boundingBox, targetShape, Direction.NE, createButton()));
            icons.add(stackIcon(toolboxStack, boundingBox, targetShape, Direction.NE, createButton()));
            icons.add(stackIcon(toolboxStack, boundingBox, targetShape, Direction.NE, createButton()));
            icons.add(stackIcon(toolboxStack, boundingBox, targetShape, Direction.SE, createButton()));
            icons.add(stackIcon(toolboxStack, boundingBox, targetShape, Direction.SE, createButton()));
            icons.add(stackIcon(toolboxStack, boundingBox, targetShape, Direction.SE, createButton()));
            return icons;
        }

        private MultiPath createButton() {
            return new MultiPath().rect( 0, 0, 20, 20 ).setFillColor( "#c0c000" );
        }

        @Override
        public void onNodeMouseEnter( final NodeMouseEnterEvent event ) {
            if (m_timer != null)
            {
                m_timer.cancel();
                m_timer = null;
            }
            if (toolbox.isEmpty()) {
                GWT.log("creating shit");
                Map<Direction, Point2D> boundingBox = getBoundingBoxAnchors( this.shape );
                HashMap<Direction, Integer> toolboxStack = new HashMap<Direction, Integer>(){{
                    for (Direction direction : Direction.values()) {
                        put(direction, 0);
                    }
                }};
                toolbox.addAll(createToolbox(this.shape, boundingBox, toolboxStack));
                this.layer.batch();
            }
        }

        @Override
        public void onNodeMouseExit( final NodeMouseExitEvent event ) {
            if (!this.toolbox.isEmpty())
            {
                createHideTimer();
            }
            GWT.log( "exit" );
        }

        public void createHideTimer()
        {
            if (m_timer == null)
            {
                m_timer = new Timer()
                {
                    @Override
                    public void run()
                    {
                        for (MultiPath shape: HoverTimer.this.toolbox) {
                            layer.remove(shape);
                        }
                        HoverTimer.this.toolbox.clear();
                        HoverTimer.this.layer.batch();
                        GWT.log("cleaned up everything");

                    }
                };
                m_timer.schedule(1000);
            }
        }
    }


}
