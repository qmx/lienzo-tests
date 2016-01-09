package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.Direction;

import java.util.HashMap;
import java.util.Map;

public class Toolbox {

    private final MultiPath owner;
    private final Direction direction;
    private final Map<Direction, Integer> stack = new HashMap<Direction, Integer>() {{
        for (Direction direction : Direction.values()) {
            put(direction, 0);
        }
    }};

    public Toolbox(MultiPath owner, Direction direction) {
        this.owner = owner;
        this.direction = direction;

    }

    public Point2D addButton(ToolboxButton toolboxButton) {
        return getNextStackedButtonPosition();
    }

    private Point2D getNextStackedButtonPosition() {
        Point2D anchor = anchorFor(this.direction);
        double iconHeight = 16;
        double iconWidth = 16;
        double padding = 2;

        double x;
        double y;
        if (direction.equals(Direction.NORTH_EAST)) {
            x = anchor.getX() + padding;
            y = anchor.getY() + (stack.get(direction) * (iconHeight + padding));
        } else if (direction.equals(Direction.NORTH_WEST)) {
            x = anchor.getX() - padding - iconWidth;
            y = anchor.getY() + (stack.get(direction) * (iconHeight + padding));
        } else if (direction.equals(Direction.SOUTH_EAST)) {
            x = anchor.getX() + padding;
            y = anchor.getY() - (stack.get(direction) * (iconHeight + padding)) - (iconHeight);
        } else if (direction.equals(Direction.SOUTH_WEST)) {
            x = anchor.getX() - padding - iconWidth;
            y = anchor.getY() - (stack.get(direction) * (iconHeight + padding)) - (iconHeight);
        } else {
            throw new RuntimeException();
        }
        stack.put(direction, stack.get(direction) + 1);
        return new Point2D(x, y);
    }


    private Point2D anchorFor(Direction direction) {
        Point2DArray cardinals = Geometry.getCardinals(this.owner.getBoundingPoints().getBoundingBox());
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

    public Layer getLayer() {
        return this.owner.getLayer();
    }

}
