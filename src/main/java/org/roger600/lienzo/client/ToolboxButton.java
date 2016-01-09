package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.image.PictureLoadedHandler;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.types.Point2D;

public class ToolboxButton {

    private final Toolbox toolbox;
    private final String icon;
    private final ToolboxButtonClickHandler toolboxButtonClickHandler;

    public ToolboxButton(final Toolbox toolbox, String icon, final ToolboxButtonClickHandler toolboxButtonClickHandler) {
        this.toolbox = toolbox;
        this.icon = icon;
        this.toolboxButtonClickHandler = toolboxButtonClickHandler;
        new Picture(icon).onLoaded(new PictureLoadedHandler() {
            @Override
            public void onPictureLoaded(Picture picture) {
                Point2D position = toolbox.addButton(ToolboxButton.this);
                picture.setX(position.getX());
                picture.setY(position.getY());
                picture.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(1)), 500, new AnimationCallback());
                picture.addNodeMouseClickHandler(new NodeMouseClickHandler() {
                    @Override
                    public void onNodeMouseClick(NodeMouseClickEvent event) {
                        toolboxButtonClickHandler.onClick(ToolboxButton.this, event);
                    }
                });
                toolbox.getLayer().add(picture);
            }
        });

    }
}