package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.Direction;
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


    public void testWires() {

        final double startX = 200;
        final double startY = 200;
        final double radius = 50;
        final double w = 100;
        final double h = 100;

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

        private MultiPath stackIcon(Map<Direction, Integer> toolboxStack, Direction direction, MultiPath icon) {
            Point2D anchor = anchorFor(direction);
            double iconHeight = icon.getBoundingPoints().getBoundingBox().getHeight();
            double iconWidth = icon.getBoundingPoints().getBoundingBox().getWidth();

            if (direction.equals(Direction.NORTH_EAST)) {
                icon.setX(anchor.getX() + 2);
                icon.setY(anchor.getY() + (toolboxStack.get(direction) * (iconHeight + 2)));
            } else if (direction.equals(Direction.NORTH_WEST)) {
                icon.setX(anchor.getX() - 2 - iconWidth);
                icon.setY(anchor.getY() + (toolboxStack.get(direction) * (iconHeight + 2)));
            } else if (direction.equals(Direction.SOUTH_EAST)) {
                icon.setX(anchor.getX() + 2);
                icon.setY(anchor.getY() - (toolboxStack.get(direction) * (iconHeight + 2)) - (iconHeight));
            } else if (direction.equals(Direction.SOUTH_WEST)) {
                icon.setX(anchor.getX() - 2 - iconWidth);
                icon.setY(anchor.getY() - (toolboxStack.get(direction) * (iconHeight + 2)) - (iconHeight));
            }
            toolboxStack.put(direction, toolboxStack.get(direction) + 1);
            this.layer.add(icon);
            icon.addNodeMouseEnterHandler(this);
            icon.addNodeMouseExitHandler(this);
            icon.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(1)), 500, new AnimationCallback());
            this.layer.batch();
            return icon;
        }

        private Point2D anchorFor(Direction direction) {
            Point2DArray cardinals = Geometry.getCardinals(this.shape.getBoundingPoints().getBoundingBox());
            Point2D anchor = null;
            switch (direction) {
                case NORTH_EAST:
                    anchor = cardinals.get(2);
                    break;
                case SOUTH_EAST:
                    anchor = cardinals.get(4);
                    break;
                case SOUTH_WEST:
                    anchor = cardinals.get(6);
                    break;
                case NORTH_WEST:
                    anchor = cardinals.get(8);
                    break;
                default:
                    throw new RuntimeException("meh");
            }
            return anchor;
        }

        private List<MultiPath> createToolbox(HashMap<Direction, Integer> toolboxStack) {
            ArrayList<MultiPath> icons = new ArrayList<>();
            icons.add(stackIcon(toolboxStack, Direction.NORTH_EAST, createButton()));
            icons.add(stackIcon(toolboxStack, Direction.NORTH_EAST, createButton()));
            icons.add(stackIcon(toolboxStack, Direction.NORTH_WEST, createButton()));
            icons.add(stackIcon(toolboxStack, Direction.NORTH_WEST, createButton()));
            icons.add(stackIcon(toolboxStack, Direction.SOUTH_EAST, createButton()));
            icons.add(stackIcon(toolboxStack, Direction.SOUTH_EAST, createButton()));
            icons.add(stackIcon(toolboxStack, Direction.SOUTH_WEST, createButton()));
            icons.add(stackIcon(toolboxStack, Direction.SOUTH_WEST, createButton()));
            return icons;
        }

        private MultiPath createButton() {
            MultiPath path = new MultiPath().rect(0, 0, 20, 20).setFillColor("#c0c000").setAlpha(0);
            return path;
        }

        @Override
        public void onNodeMouseEnter( final NodeMouseEnterEvent event ) {
            if (m_timer != null)
            {
                m_timer.cancel();
                m_timer = null;
            }
            if (toolbox.isEmpty()) {
                HashMap<Direction, Integer> toolboxStack = new HashMap<Direction, Integer>() {{
                    for (Direction direction : Direction.values()) {
                        put(direction, 0);
                    }
                }};
                toolbox.addAll(createToolbox(toolboxStack));
                this.layer.batch();
            }
        }

        @Override
        public void onNodeMouseExit( final NodeMouseExitEvent event ) {
            if (!this.toolbox.isEmpty())
            {
                createHideTimer();
            }
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

                    }
                };
                m_timer.schedule(1000);
            }
        }
    }


}
