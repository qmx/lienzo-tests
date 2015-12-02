package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.Map;

public class WiresTests extends FlowPanel {

    private Layer layer;
    private IControlHandleList m_ctrls;

    public WiresTests(Layer layer) {
        this.layer = layer;
    }

    public void testWires() {
        WiresManager wires_manager = WiresManager.get(layer);

        wires_manager.setConnectionAcceptor(new IConnectionAcceptor() {
            @Override
            public boolean acceptHead(WiresConnection head, WiresMagnet magnet) {
                WiresConnection tail = head.getConnector().getTailConnection();

                WiresMagnet m = tail.getMagnet();

                if (m == null)
                {
                    return true;
                }
                return accept(magnet.getMagnets().getGroup(), tail.getMagnet().getMagnets().getGroup());
            }

            @Override
            public boolean acceptTail(WiresConnection tail, WiresMagnet magnet) {
                WiresConnection head = tail.getConnector().getHeadConnection();

                WiresMagnet m = head.getMagnet();

                if (m == null)
                {
                    return true;
                }
                return accept(head.getMagnet().getMagnets().getGroup(), magnet.getMagnets().getGroup());
            }

            @Override
            public boolean headConnectionAllowed(WiresConnection head, WiresShape shape) {
                WiresConnection tail = head.getConnector().getTailConnection();
                WiresMagnet m = tail.getMagnet();

                if (m == null)
                {
                    return true;
                }

                return accept(shape.getGroup(), tail.getMagnet().getMagnets().getGroup());
            }

            @Override
            public boolean tailConnectionAllowed(WiresConnection tail, WiresShape shape) {
                WiresConnection head = tail.getConnector().getHeadConnection();

                WiresMagnet m = head.getMagnet();

                if (m == null)
                {
                    return true;
                }
                return accept(head.getMagnet().getMagnets().getGroup(), shape.getGroup());
            }

            private boolean accept(final Group head, final Group tail)
            {
                GWT.log("Accept [head=" + head.getUserData() + "] [tail=" + tail.getUserData() + "]");
                final String headData = (String) head.getUserData();
                final String tailData = (String) tail.getUserData();
                if ( "event".equals(headData) && "event".equals(tailData) )
                {
                    return false;
                }
                return true;
            }
        });

        wires_manager.setContainmentAcceptor(new IContainmentAcceptor()
        {
            @Override
            public boolean containmentAllowed(WiresContainer parent, WiresShape child)
            {
                return acceptContainment(parent, child);
            }

            @Override
            public boolean acceptContainment(WiresContainer parent, WiresShape child)
            {
                if (parent.getParent() == null)
                {
                    return true;
                }
                return !parent.getContainer().getUserData().equals(child.getGroup().getUserData());
            }
        });

        final double startX = 200;
        final double startY = 200;
        final double radius = 50;
        final double w = 100;
        final double h = 100;

        // Toolbox shapes
        MultiPath btn1 = new MultiPath().rect( 0, 0, 20, 20 ).setFillColor( "#c0c000" );

        // Blue start event.
        MultiPath startEventMultiPath = new MultiPath().rect(0, 0, w, h).setFillColor("#000000");
        startEventMultiPath.setX( startX );
        startEventMultiPath.setY( startY );


        HoverTimer hoverTimer = new HoverTimer();
        layer.add( startEventMultiPath );

        btn1.setX( startEventMultiPath.getBoundingBox().getX() );
        btn1.setY( startEventMultiPath.getBoundingBox().getY() );

        startEventMultiPath.addNodeMouseEnterHandler( hoverTimer );
        startEventMultiPath.addNodeMouseExitHandler( hoverTimer );

        layer.add( btn1 );

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
