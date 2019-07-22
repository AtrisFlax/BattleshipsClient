package com.liver_rus.Battleships.Client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class Draw {
    static void CrossMyPlayer(GraphicsContext graphicContext, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY() - 2;
        double width = Constant.WIDTH_FIRST_PLAYER_CELL;
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + Constant.LEFT_EDGE_PIXEL_FIRST_PLAYER_X,
                y * width + Constant.TOP_EDGE_PIXEL_FIRST_PLAYER_Y,
                x * width + Constant.LEFT_EDGE_PIXEL_FIRST_PLAYER_X + width,
                y * width + Constant.TOP_EDGE_PIXEL_FIRST_PLAYER_Y + width);
        graphicContext.strokeLine(x * width + Constant.LEFT_EDGE_PIXEL_FIRST_PLAYER_X,
                y * width + Constant.TOP_EDGE_PIXEL_FIRST_PLAYER_Y + width,
                x * width + Constant.LEFT_EDGE_PIXEL_FIRST_PLAYER_X + width,
                y * width + Constant.TOP_EDGE_PIXEL_FIRST_PLAYER_Y);
    }

    static void LineMyPlayer(GraphicsContext graphicContext, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY() - 2;
        double width = Constant.WIDTH_FIRST_PLAYER_CELL;
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + Constant.LEFT_EDGE_PIXEL_FIRST_PLAYER_X,
                y * width + Constant.TOP_EDGE_PIXEL_FIRST_PLAYER_Y + width,
                x * width + Constant.LEFT_EDGE_PIXEL_FIRST_PLAYER_X + width,
                y * width + Constant.TOP_EDGE_PIXEL_FIRST_PLAYER_Y);
    }

    static void CrossEnemyPlayer(GraphicsContext graphicContext, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        double width = Constant.WIDTH_SECOND_PLAYER_CELL;
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + Constant.LEFT_EDGE_PIXEL_SECOND_PLAYER_X,
                y * width + Constant.TOP_EDGE_PIXEL_SECOND_PLAYER_Y,
                x * width + Constant.LEFT_EDGE_PIXEL_SECOND_PLAYER_X + width,
                y * width + Constant.TOP_EDGE_PIXEL_SECOND_PLAYER_Y + width);
        graphicContext.strokeLine(x * width + Constant.LEFT_EDGE_PIXEL_SECOND_PLAYER_X,
                y * width + Constant.TOP_EDGE_PIXEL_SECOND_PLAYER_Y + width,
                x * width + Constant.LEFT_EDGE_PIXEL_SECOND_PLAYER_X + width,
                y * width + Constant.TOP_EDGE_PIXEL_SECOND_PLAYER_Y);
    }

    static void LineEnemyPlayer(GraphicsContext graphicContext, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        double width = Constant.WIDTH_SECOND_PLAYER_CELL;
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + Constant.LEFT_EDGE_PIXEL_SECOND_PLAYER_X,
                y * width + Constant.TOP_EDGE_PIXEL_SECOND_PLAYER_Y + width,
                x * width + Constant.LEFT_EDGE_PIXEL_SECOND_PLAYER_X + width,
                y * width + Constant.TOP_EDGE_PIXEL_SECOND_PLAYER_Y);
    }

    static void ShipMyField(GraphicsContext gc, FieldCoord fieldCoord, Ship.Type shipType, Ship.Orientation shipOrientation) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY() - 1;
        int intShipType = Ship.Type.shipTypeToInt(shipType);
        if (shipOrientation == Ship.Orientation.HORIZONTAL) {
            gc.strokeRect(
                    Constant.LEFT_EDGE_PIXEL_FIRST_PLAYER_X + x * Constant.WIDTH_FIRST_PLAYER_CELL,
                    Constant.TOP_EDGE_PIXEL_FIRST_PLAYER_Y + y * Constant.WIDTH_FIRST_PLAYER_CELL,
                    Constant.WIDTH_FIRST_PLAYER_CELL * (intShipType + 1),
                    Constant.WIDTH_FIRST_PLAYER_CELL
            );
        } else {
            gc.strokeRect(
                    Constant.LEFT_EDGE_PIXEL_FIRST_PLAYER_X + x * Constant.WIDTH_FIRST_PLAYER_CELL,
                    Constant.TOP_EDGE_PIXEL_FIRST_PLAYER_Y + y * Constant.WIDTH_FIRST_PLAYER_CELL,
                    Constant.WIDTH_FIRST_PLAYER_CELL,
                    Constant.WIDTH_FIRST_PLAYER_CELL * (intShipType + 1)
            );
        }
    }

    static void ShipEnemyField(GraphicsContext gc, FieldCoord fieldCoord, Ship.Type shipType, Ship.Orientation orientation) {
        int x = fieldCoord.getX() - 1;
        int y = fieldCoord.getY() - 1;
        int intShipType = Ship.Type.shipTypeToInt(shipType);
        if (orientation == Ship.Orientation.HORIZONTAL) {
            gc.strokeRect(
                    Constant.LEFT_EDGE_PIXEL_SECOND_PLAYER_X + x * Constant.WIDTH_SECOND_PLAYER_CELL,
                    Constant.TOP_EDGE_PIXEL_SECOND_PLAYER_Y + y * Constant.WIDTH_SECOND_PLAYER_CELL,
                    Constant.WIDTH_SECOND_PLAYER_CELL * (intShipType + 1),
                    Constant.WIDTH_SECOND_PLAYER_CELL);
        } else {
            gc.strokeRect(
                    Constant.LEFT_EDGE_PIXEL_SECOND_PLAYER_X + x * Constant.WIDTH_SECOND_PLAYER_CELL,
                    Constant.TOP_EDGE_PIXEL_SECOND_PLAYER_Y + y * Constant.WIDTH_SECOND_PLAYER_CELL,
                    Constant.WIDTH_SECOND_PLAYER_CELL,
                    Constant.WIDTH_SECOND_PLAYER_CELL * (intShipType + 1));
        }
    }
}
