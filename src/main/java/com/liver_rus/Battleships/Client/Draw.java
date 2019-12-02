package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.Constants.GUIConstant;
import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class Draw {
    static void MissCellOnMyField(GraphicsContext gc, FieldCoord fieldCoord) {
        MissCellOnField(gc, FirstPlayerGUIConstants.getGUIConstant(), fieldCoord);
    }

    static void MissCellOnEnemyField(GraphicsContext gc, FieldCoord fieldCoord) {
        MissCellOnField(gc, SecondPlayerGUIConstants.getGUIConstant(), fieldCoord);
    }

    static void HitCellOnMyField(GraphicsContext gc, FieldCoord fieldCoord) {
        HitCellOnField(gc, FirstPlayerGUIConstants.getGUIConstant(), fieldCoord);
    }

    static void HitCellOnEnemyField(GraphicsContext gc, FieldCoord fieldCoord) {
        HitCellOnField(gc, SecondPlayerGUIConstants.getGUIConstant(), fieldCoord);
    }

    static void ShipOnMyField(GraphicsContext gc, Ship ship) {
        ShipOnField(gc, FirstPlayerGUIConstants.getGUIConstant(), ship);
    }

    static void ShipOnMyField(GraphicsContext gc, CurrentGUIState currentGUIState) {
        ShipOnField(gc, FirstPlayerGUIConstants.getGUIConstant(),
                currentGUIState.getFieldCoord().getX(),
                currentGUIState.getFieldCoord().getY(),
                Ship.Type.shipTypeToInt(currentGUIState.getShipType()),
                currentGUIState.isHorizontalOrientation());
    }

    //draw ship frame
    static void ShipOnEnemyField(GraphicsContext gc, Ship ship) {
        ShipOnField(gc, SecondPlayerGUIConstants.getGUIConstant(), ship);
    }

    private static void MissCellOnField(GraphicsContext graphicContext, GUIConstant constants, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        double width = constants.getWidthCell();
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + constants.getLeftX(),
                y * width + constants.getTopY() + width,
                x * width + constants.getLeftX() + width,
                y * width + constants.getTopY());
    }

    private static void HitCellOnField(GraphicsContext gc, GUIConstant constants, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        double width = constants.getWidthCell();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(x * width + constants.getLeftX(),
                y * width + constants.getTopY(),
                x * width + constants.getLeftX() + width,
                y * width + constants.getTopY() + width);
        gc.strokeLine(x * width + constants.getLeftX(),
                y * width + constants.getTopY() + width,
                x * width + constants.getLeftX() + width,
                y * width + constants.getTopY());
    }

    private static void ShipOnField(GraphicsContext gc, GUIConstant constants, Ship ship) {
        int x = ship.getShipStartCoord().getX();
        int y = ship.getShipStartCoord().getY();
        int shipLength = convertTypeToShipLength(ship.getType());
        boolean isHorizontal = ship.isHorizontal();
        ShipOnField(gc, constants, x, y, shipLength, isHorizontal);
    }

    private static void ShipOnField(GraphicsContext gc, GUIConstant constants, int x, int y, int shipLength, boolean isHorizontal) {
        if (isHorizontal) {
            //horizontal
            gc.strokeRect(
                    constants.getLeftX() + x * constants.getWidthCell(),
                    constants.getTopY() + y * constants.getWidthCell(),
                    constants.getWidthCell() * shipLength,
                    constants.getWidthCell()
            );
        } else {
            //vertical
            gc.strokeRect(
                    constants.getLeftX() + x * constants.getWidthCell(),
                    constants.getTopY() + y * constants.getWidthCell(),
                    constants.getWidthCell(),
                    constants.getWidthCell() * shipLength
            );
        }
    }

    /**
     * @param type
     * @return linear conversion type to ship length
     * ship with type N has length N + 1
     */
    private static int convertTypeToShipLength(Ship.Type type) {
        return Ship.Type.shipTypeToInt(type) + 1;
    }
}


