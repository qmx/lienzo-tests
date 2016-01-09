package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.shared.core.types.Direction;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;

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
        MultiPath box1 = new MultiPath().rect(0, 0, w, h).setFillColor("#000000");
        box1.setX(startX);
        box1.setY(startY);

        MultiPath box2 = new MultiPath().rect(0, 0, w, h).setFillColor("#000000");
        box2.setX(startX * 2);
        box2.setY(startY * 2);

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

        boolean init = false;
    }
}
