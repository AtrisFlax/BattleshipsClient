package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.Constants.GUIConstants;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Draw {
    private final static int CELL_LINE_WIDTH = 2;
    private final static int SHIP_LINE_WIDTH = 2;
    private final static Color MISS_CELL_COLOR = Color.BLACK;
    private final static Color HIT_CELL_COLOR = Color.BLACK;

    public static void MissCellOnField(GraphicsContext graphicContext, GUIConstants constants, int x, int y) {
        double width = constants.getWidthCell();
        graphicContext.setStroke(MISS_CELL_COLOR);
        graphicContext.setLineWidth(CELL_LINE_WIDTH);
        graphicContext.strokeLine(
                x * width + constants.getLeftX(),
                y * width + constants.getTopY() + width,
                x * width + constants.getLeftX() + width,
                y * width + constants.getTopY());
    }

    public static void HitCellOnField(GraphicsContext gc, GUIConstants constants, int x, int y) {
        double width = constants.getWidthCell();
        gc.setStroke(HIT_CELL_COLOR);
        gc.setLineWidth(CELL_LINE_WIDTH);
        gc.strokeLine(
                x * width + constants.getLeftX(),
                y * width + constants.getTopY(),
                x * width + constants.getLeftX() + width,
                y * width + constants.getTopY() + width
        );
        gc.strokeLine(
                x * width + constants.getLeftX(),
                y * width + constants.getTopY() + width,
                x * width + constants.getLeftX() + width,
                y * width + constants.getTopY()
        );
    }

    public static void ShipOnField(GraphicsContext gc, Color shipColor, GUIConstants constants,
                                   int x, int y, int shipLength, boolean isHorizontal) {
        gc.setStroke(shipColor);
        gc.setLineWidth(SHIP_LINE_WIDTH);
        if (isHorizontal) {
            gc.strokeRect(
                    constants.getLeftX() + x * constants.getWidthCell(),
                    constants.getTopY() + y * constants.getWidthCell(),
                    constants.getWidthCell() * shipLength,
                    constants.getWidthCell()
            );
        } else {
            gc.strokeRect(
                    constants.getLeftX() + x * constants.getWidthCell(),
                    constants.getTopY() + y * constants.getWidthCell(),
                    constants.getWidthCell(),
                    constants.getWidthCell() * shipLength
            );
        }
    }

    public static int convertTypeToShipLength(Ship.Type type) {
        return Ship.Type.shipTypeToInt(type) + 1;
    }

    public static void clearCanvas(GraphicsContext context) {
        context.clearRect(0, 0, Constants.Window.WIDTH, Constants.Window.HEIGHT);
    }

    public static Color setColorForDrawShip(boolean isDeployable) {
        if (isDeployable) {
            return Color.BLACK;
        } else {
            return Color.RED;
        }
    }

}


