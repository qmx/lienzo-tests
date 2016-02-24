package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.google.gwt.user.client.ui.FlowPanel;

import static com.ait.lienzo.client.core.shape.wires.LayoutContainer.Layout.CENTER;


public class WiresTests extends FlowPanel
{

    private Layer layer;

    private IControlHandleList m_ctrls;

    public WiresTests(Layer layer)
    {
        this.layer = layer;
    }

    public void testWires()
    {

        WiresManager wires_manager = WiresManager.get(layer);

        double w = 100;

        double h = 100;

        // A shape can only contain shapes of different letters for UserData

        wires_manager.setContainmentAcceptor(new IContainmentAcceptor()
        {
            @Override public boolean containmentAllowed(WiresContainer parent, WiresShape child)
            {
                return acceptContainment(parent, child);
            }

            @Override public boolean acceptContainment(WiresContainer parent, WiresShape child)
            {
                if (parent.getParent() == null)
                {
                    return true;
                }
                return !parent.getContainer().getUserData().equals(child.getContainer().getUserData());
            }
        });

        wires_manager.setDockingAcceptor(new IDockingAcceptor()
        {
            @Override public boolean dockingAllowed(WiresContainer parent, WiresShape child, WiresShape target)
            {
                return acceptDocking(parent, child, target);
            }

            @Override public boolean acceptDocking(WiresContainer parent, WiresShape child, WiresShape target)
            {
                if (parent.getParent() == null)
                {
                    return true;
                }
                return !parent.getContainer().getUserData().equals(child.getContainer().getUserData());

            }
        });

        WiresShape wiresShape0 = wires_manager.createShape(new MultiPath().rect(0, 0, w, h).setStrokeColor("#CC0000")).setX(400).setY(400).setDraggable(true);
        wiresShape0.getContainer().setUserData("A");
        wiresShape0.addChild(new Circle(30), CENTER);

        WiresShape wiresShape1 = wires_manager.createShape(new MultiPath().rect(0, 0, w, h).setStrokeColor("#00CC00")).setX(50).setY(50).setDraggable(true);
        wiresShape1.getContainer().setUserData("A");
        wiresShape1.addChild(new Star(5, 15, 40), CENTER);

        WiresShape wiresShape2 = wires_manager.createShape(new MultiPath().rect(0, 0, 300, 200).setStrokeColor("#0000CC")).setX(50).setY(100).setDraggable(true);
        wiresShape2.getContainer().setUserData("B");

        WiresShape wiresShape4 = wires_manager.createShape(new MultiPath().rect(20, 20, 400, 400).setStrokeColor("#CC00CC")).setX(50).setY(100).setDraggable(true);
        wiresShape4.getContainer().setUserData("C");

        // bolt
        String svg = "M 0 100 L 65 115 L 65 105 L 120 125 L 120 115 L 200 180 L 140 160 L 140 170 L 85 150 L 85 160 L 0 140 Z";
        WiresShape wiresShape3 = wires_manager.createShape(new MultiPath(svg).setStrokeColor("#0000CC")).setX(50).setY(300).setDraggable(true);
        wiresShape3.getContainer().setUserData("B");

    }

}
