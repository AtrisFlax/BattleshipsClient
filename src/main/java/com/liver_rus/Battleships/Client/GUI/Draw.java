package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.Constants.GUIConstants;
import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

//TODO
//служебный класс

//MissCellOnField HitCellOnField ShipOnField оставить
//остальные удалить
class Draw {
    static void MissCellOnMyField(GraphicsContext gc, int x, int y) {
        MissCellOnField(gc, FirstPlayerGUIConstants.getGUIConstant(), x, y);
    }

    static void MissCellOnEnemyField(GraphicsContext gc, int x, int y) {
        MissCellOnField(gc, SecondPlayerGUIConstants.getGUIConstant(), x, y);
    }

    static void HitCellOnMyField(GraphicsContext gc, int x, int y) {
        HitCellOnField(gc, FirstPlayerGUIConstants.getGUIConstant(), x, y);
    }

    static void HitCellOnEnemyField(GraphicsContext gc, int x, int y) {
        HitCellOnField(gc, SecondPlayerGUIConstants.getGUIConstant(), x, y);
    }

    static void ShipOnMyField(GraphicsContext gc, Ship ship) {
        ShipOnField(gc, FirstPlayerGUIConstants.getGUIConstant(), ship);
    }

    static void ShipOnMyField(GraphicsContext gc, GUIState currentGUIState) {
        int x = currentGUIState.getX();
        int y = currentGUIState.getY();
        int shipLength = convertTypeToShipLength(currentGUIState.getShipType());
        boolean isHorizontal = currentGUIState.isHorizontalOrientation();
        ShipOnField(gc, FirstPlayerGUIConstants.getGUIConstant(), x, y, shipLength, isHorizontal);
    }

    static void ShipOnEnemyField(GraphicsContext gc, GUIState currentGUIState) {
        int x = currentGUIState.getX();
        int y = currentGUIState.getY();
        int shipLength = convertTypeToShipLength(currentGUIState.getShipType());
        boolean isHorizontal = currentGUIState.isHorizontalOrientation();
        ShipOnField(gc, SecondPlayerGUIConstants.getGUIConstant(), x, y, shipLength, isHorizontal);
    }

    //draw ship frame
    static void ShipOnEnemyField(GraphicsContext gc, Ship ship) {
        ShipOnField(gc, SecondPlayerGUIConstants.getGUIConstant(), ship);
    }

    private static void MissCellOnField(GraphicsContext graphicContext, GUIConstants constants, int x, int y) {
        double width = constants.getWidthCell();
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + constants.getLeftX(),
                y * width + constants.getTopY() + width,
                x * width + constants.getLeftX() + width,
                y * width + constants.getTopY());
    }

    private static void HitCellOnField(GraphicsContext gc, GUIConstants constants, int x, int y) {
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

    private static void ShipOnField(GraphicsContext gc, GUIConstants constants, Ship ship) {
        int x = ship.getShipStartCoord().getX();
        int y = ship.getShipStartCoord().getY();
        int shipLength = convertTypeToShipLength(ship.getType());
        boolean isHorizontal = ship.isHorizontal();
        ShipOnField(gc, constants, x, y, shipLength, isHorizontal);
    }

    private static void ShipOnField(GraphicsContext gc, GUIConstants constants, int x, int y, int shipLength, boolean isHorizontal) {
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


