package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.image.PictureLoadedHandler;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.client.core.types.Point2D;

public class ToolboxButton {

    private final Toolbox toolbox;
    private final String icon;
    private final ToolboxButtonClickHandler toolboxButtonClickHandler;
    private final HoverTimer parentHoverTimer;
    private final Point2D position;
    private Picture picture;

    public ToolboxButton(final Toolbox toolbox, String icon, final ToolboxButtonClickHandler toolboxButtonClickHandler) {
        this.toolbox = toolbox;
        this.icon = icon;
        this.toolboxButtonClickHandler = toolboxButtonClickHandler;
        this.parentHoverTimer = toolbox.getHoverTimer();
        position = toolbox.addButton(ToolboxButton.this);
    }

    public void show() {
        if (this.picture == null) {
            this.picture = new Picture(icon).onLoaded(new PictureLoadedHandler() {
                @Override
                public void onPictureLoaded(Picture picture) {
                    picture.setX(position.getX());
                    picture.setY(position.getY());
                    picture.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(1)), 500, new AnimationCallback());
                    picture.addNodeMouseClickHandler(new NodeMouseClickHandler() {
                        @Override
                        public void onNodeMouseClick(NodeMouseClickEvent event) {
                            toolboxButtonClickHandler.onClick(ToolboxButton.this, event);
                        }
                    });
                    picture.addNodeMouseEnterHandler(parentHoverTimer);
                    picture.addNodeMouseExitHandler(parentHoverTimer);
                    toolbox.getLayer().add(picture);
                    toolbox.getLayer().batch();
                }
            });
        }
    }

    public void hide() {
        if (this.picture != null) {
            picture.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(0)), 500, new AnimationCallback() {
                @Override
                public void onClose(IAnimation animation, IAnimationHandle handle) {
                    picture.removeFromParent();
                    toolbox.getLayer().batch();
                    ToolboxButton.this.picture = null;
                }
            });

        }
    }
}
