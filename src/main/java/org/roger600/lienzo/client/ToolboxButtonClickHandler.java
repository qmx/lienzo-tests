package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;

public interface ToolboxButtonClickHandler {
    void onClick(ToolboxButton button, NodeMouseClickEvent event);
}
