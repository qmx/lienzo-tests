package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.image.PictureLoadedHandler;
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

        layer.add(box2);

        Toolbox toolbox = new Toolbox(box2, Direction.SOUTH_EAST);
        new ToolboxButton(toolbox, "icons/activity/task.png", new ToolboxButtonClickHandler() {
            @Override
            public void onClick(ToolboxButton button, NodeMouseClickEvent event) {
                GWT.log("task clicked");
            }
        });
        new ToolboxButton(toolbox, "icons/activity/subprocess.png", new ToolboxButtonClickHandler() {
            @Override
            public void onClick(ToolboxButton button, NodeMouseClickEvent event) {
                GWT.log("subprocess clicked");
            }
        });
        new ToolboxButton(toolbox, "icons/connector/messageflow.png", new ToolboxButtonClickHandler() {
            @Override
            public void onClick(ToolboxButton button, NodeMouseClickEvent event) {
                GWT.log("messageflow clicked");
            }
        });


//        toolbox.addIcon(new Polygon(82.863,257.986 0,175.122 10.606,164.516 82.863,236.773 283.509,36.128 294.115,46.734))
//        Group icon = new Group();
//        SVGPath outline = new SVGPath("M95.915,45.957L0,160.387l95.915,114.43h224.858V45.957H95.915z M305.773,259.816H102.914l-83.342-99.43l83.342-99.43 h202.859V259.816z");
//        outline.setStrokeWidth(4);
//        outline.setStrokeColor("#000000");
//        outline.setFillColor("#000000");
//        icon.add(outline);
//        SVGPath x = new SVGPath("m 243.22,213.038 c 3.53567,-3.53567 7.07133,-7.07133 10.607,-10.607 -14.015,-14.01467 -28.03,-28.02933 -42.045,-42.044 14.015,-14.01467 28.03,-28.02933 42.045,-42.044 -3.53567,-3.536 -7.07133,-7.072 -10.607,-10.608 -14.015,14.015 -28.03,28.03 -42.045,42.045 -14.015,-14.015 -28.03,-28.03 -42.045,-42.045 -3.53533,3.536 -7.07067,7.072 -10.606,10.608 14.01467,14.01467 28.02933,28.02933 42.044,42.044 -14.01467,14.01467 -28.02933,28.02933 -42.044,42.044 3.53533,3.53567 7.07067,7.07133 10.606,10.607 14.015,-14.01467 28.03,-28.02933 42.045,-42.044 14.015,14.01467 28.03,28.02933 42.045,42.044 z");
//        x.setStrokeWidth(4);
//        x.setStrokeColor("#000000");
//        x.setFillColor("#000000");
//        icon.add(x);
//        x.setScale()
//        layer.add(icon);
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
