package com.liver_rus.Battleships.Client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class Draw {

    static void MissCellOnMyField(GraphicsContext gc, FieldCoord fieldCoord) {
        MissCellOnField(gc, new FirstPlayerGUIConstants(), fieldCoord);
    }

    static void MissCellOnEnemyField(GraphicsContext gc, FieldCoord fieldCoord) {
        MissCellOnField(gc, new SecondPlayerGUIConstants(), fieldCoord);

    }

    static void HitCellOnMyField(GraphicsContext gc, FieldCoord fieldCoord) {
        HitCellOnField(gc, new FirstPlayerGUIConstants(), fieldCoord);
    }

    static void HitCellOnEnemyField(GraphicsContext gc, FieldCoord fieldCoord) {
        HitCellOnField(gc, new SecondPlayerGUIConstants(), fieldCoord);
    }

    static void ShipOnMyField(GraphicsContext gc, Ship ship) {
        int x = ship.getShipStartCoord().getX();
        int y = ship.getShipStartCoord().getX();
        int type =  Ship.Type.shipTypeToInt(ship.getType());
        boolean orientation = ship.getOrientation().getBoolean();
        ShipOnField(gc, new FirstPlayerGUIConstants(), x, y , type, orientation);
    }

    static void ShipOnMyField(GraphicsContext gc, CurrentGUIState currentGUIState) {
        ShipOnField(gc,  new FirstPlayerGUIConstants(),
                currentGUIState.getFieldCoord().getX(),
                currentGUIState.getFieldCoord().getY(),
                Ship.Type.shipTypeToInt(currentGUIState.getShipType()),
                currentGUIState.getShipOrientation().getBoolean());
    }

    static void ShipOnEnemyField(GraphicsContext gc, Ship ship) {
        int x = ship.getShipStartCoord().getX();
        int y = ship.getShipStartCoord().getX();
        int type =  Ship.Type.shipTypeToInt(ship.getType());
        boolean orientation = ship.getOrientation().getBoolean();
        ShipOnField(gc, new SecondPlayerGUIConstants(), x, y , type, orientation);
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

    private static void ShipOnField(GraphicsContext gc, GUIConstant constants, int x, int y, int type, boolean orientation) {
        if (orientation) {
            //horizontal
            gc.strokeRect(
                    constants.getLeftX() + x * constants.getWidthCell(),
                    constants.getTopY() + y * constants.getWidthCell(),
                    constants.getWidthCell() * (type + 1),
                    constants.getWidthCell()
            );
        } else {
            //vertical
            gc.strokeRect(
                    constants.getLeftX()+ x * constants.getWidthCell(),
                    constants.getTopY() + y * constants.getWidthCell(),
                    constants.getWidthCell(),
                    constants.getWidthCell() * (type + 1)
            );
        }
    }

}
