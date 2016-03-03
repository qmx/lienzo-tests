package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class LienzoTests implements EntryPoint {

    private FlowPanel mainPanel = new FlowPanel();
    private LienzoPanel panel = new LienzoPanel(1200, 680);
    private Layer layer = new Layer();

    private Layer pickerLayer = new Layer();
    
    public void onModuleLoad()
    {
        RootPanel.get().add(mainPanel);
        mainPanel.add(panel);
        layer.setTransformable(true);
        panel.add(layer);

        drawIt();
        layer.draw();
    }
    
    private void drawIt() {
        WiresTests wiresTests = new WiresTests(layer, pickerLayer);
        wiresTests.testWires();
    }
    
}
