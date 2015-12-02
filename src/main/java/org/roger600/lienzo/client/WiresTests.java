package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
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
        MultiPath startEventMultiPath = new MultiPath().rect( 0, 0, w, h ).setFillColor( "#000000" );
        startEventMultiPath.setX( startX );
        startEventMultiPath.setY( startY );

        HoverTimer hoverTimer = new HoverTimer();
        layer.add( startEventMultiPath );
        Map<Direction, Point2D> boundingBox = getBoundingBoxAnchors( startEventMultiPath );

        for ( Point2D point2D : boundingBox.values() ) {
            MultiPath button = createButton();
            button.setX( point2D.getX() );
            button.setY( point2D.getY() );
            layer.add( button );
        }

        GWT.log( boundingBox.toString() );



        boolean init = false;


        startEventMultiPath.addNodeMouseEnterHandler( hoverTimer );
        startEventMultiPath.addNodeMouseExitHandler( hoverTimer );

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

    private MultiPath createButton() {
        return new MultiPath().rect( 0, 0, 20, 20 ).setFillColor( "#c0c000" );
    }

    public static class HoverTimer implements NodeMouseEnterHandler,
                                              NodeMouseExitHandler {

        private HandlerRegistrationManager m_HandlerRegistrationManager;

        private Timer m_timer;

        public HoverTimer(){

        }

        @Override
        public void onNodeMouseEnter( final NodeMouseEnterEvent event ) {
            if (m_timer != null)
            {
                m_timer.cancel();
                m_timer = null;
            }
            GWT.log( "enter" );
        }

        @Override
        public void onNodeMouseExit( final NodeMouseExitEvent event ) {
            if (m_HandlerRegistrationManager != null)
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
                        if (m_HandlerRegistrationManager != null)
                        {
                            m_HandlerRegistrationManager.destroy();
                        }
                        m_HandlerRegistrationManager = null;

                    }
                };
                m_timer.schedule(1000);
            }
        }
    }


}
