package com.liver_rus.Battleships.Client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class Draw {
    static void HitCellOnMyField(GraphicsContext graphicContext, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY() - 2;
        double width = Constant.Pixel.FirstPlayer.WIDTH_CELL;
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + Constant.Pixel.FirstPlayer.LEFT_X,
                y * width + Constant.Pixel.FirstPlayer.TOP_Y,
                x * width + Constant.Pixel.FirstPlayer.LEFT_X + width,
                y * width + Constant.Pixel.FirstPlayer.TOP_Y + width);
        graphicContext.strokeLine(x * width + Constant.Pixel.FirstPlayer.LEFT_X,
                y * width + Constant.Pixel.FirstPlayer.TOP_Y + width,
                x * width + Constant.Pixel.FirstPlayer.LEFT_X + width,
                y * width + Constant.Pixel.FirstPlayer.TOP_Y);
    }

    static void MissCellOnMyField(GraphicsContext graphicContext, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY() - 2;
        double width = Constant.Pixel.FirstPlayer.WIDTH_CELL;
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + Constant.Pixel.FirstPlayer.LEFT_X,
                y * width + Constant.Pixel.FirstPlayer.TOP_Y + width,
                x * width + Constant.Pixel.FirstPlayer.LEFT_X + width,
                y * width + Constant.Pixel.FirstPlayer.TOP_Y);
    }

    static void HitCellOnEnemyField(GraphicsContext graphicContext, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        double width = Constant.Pixel.SecondPlayer.WIDTH_CELL;
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + Constant.Pixel.SecondPlayer.LEFT_X,
                y * width + Constant.Pixel.SecondPlayer.TOP_Y,
                x * width + Constant.Pixel.SecondPlayer.LEFT_X + width,
                y * width + Constant.Pixel.SecondPlayer.TOP_Y + width);
        graphicContext.strokeLine(x * width + Constant.Pixel.SecondPlayer.LEFT_X,
                y * width + Constant.Pixel.SecondPlayer.TOP_Y + width,
                x * width + Constant.Pixel.SecondPlayer.LEFT_X + width,
                y * width + Constant.Pixel.SecondPlayer.TOP_Y);
    }

    static void MissCellOnEnemyField(GraphicsContext graphicContext, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        double width = Constant.Pixel.SecondPlayer.WIDTH_CELL;
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + Constant.Pixel.SecondPlayer.LEFT_X,
                y * width + Constant.Pixel.SecondPlayer.TOP_Y + width,
                x * width + Constant.Pixel.SecondPlayer.LEFT_X + width,
                y * width + Constant.Pixel.SecondPlayer.TOP_Y);
    }

    static void ShipOnEnemyField(GraphicsContext gc, FieldCoord fieldCoord, Ship.Type shipType, Ship.Orientation orientation) {
        int x = fieldCoord.getX() - 1;
        int y = fieldCoord.getY() - 1;
        int intShipType = Ship.Type.shipTypeToInt(shipType);
        if (orientation == Ship.Orientation.HORIZONTAL) {
            gc.strokeRect(
                    Constant.Pixel.SecondPlayer.LEFT_X + x * Constant.Pixel.SecondPlayer.WIDTH_CELL,
                    Constant.Pixel.SecondPlayer.TOP_Y + y * Constant.Pixel.SecondPlayer.WIDTH_CELL,
                    Constant.Pixel.SecondPlayer.WIDTH_CELL * (intShipType + 1),
                    Constant.Pixel.SecondPlayer.WIDTH_CELL);
        } else {
            gc.strokeRect(
                    Constant.Pixel.SecondPlayer.LEFT_X + x * Constant.Pixel.SecondPlayer.WIDTH_CELL,
                    Constant.Pixel.SecondPlayer.TOP_Y + y * Constant.Pixel.SecondPlayer.WIDTH_CELL,
                    Constant.Pixel.SecondPlayer.WIDTH_CELL,
                    Constant.Pixel.SecondPlayer.WIDTH_CELL * (intShipType + 1));
        }
    }

    static void ShipOnMyField(GraphicsContext gc, FieldCoord coord, Ship.Type shipType, Ship.Orientation shipOrientation) {
        int x = coord.getX();
        int y = coord.getY() - 1;
        int intShipType = Ship.Type.shipTypeToInt(shipType);
        if (shipOrientation == Ship.Orientation.HORIZONTAL) {
            gc.strokeRect(
                    Constant.Pixel.FirstPlayer.LEFT_X + x * Constant.Pixel.FirstPlayer.WIDTH_CELL,
                    Constant.Pixel.FirstPlayer.TOP_Y + y * Constant.Pixel.FirstPlayer.WIDTH_CELL,
                    Constant.Pixel.FirstPlayer.WIDTH_CELL * (intShipType + 1),
                    Constant.Pixel.FirstPlayer.WIDTH_CELL
            );
        } else {
            gc.strokeRect(
                    Constant.Pixel.FirstPlayer.LEFT_X + x * Constant.Pixel.FirstPlayer.WIDTH_CELL,
                    Constant.Pixel.FirstPlayer.TOP_Y + y * Constant.Pixel.FirstPlayer.WIDTH_CELL,
                    Constant.Pixel.FirstPlayer.WIDTH_CELL,
                    Constant.Pixel.FirstPlayer.WIDTH_CELL * (intShipType + 1)
            );
        }
    }

    static void ShipOnMyField(GraphicsContext gc, CurrentState currentState) {
        ShipOnMyField(gc, currentState.getFieldCoord(), currentState.getShipType(), currentState.getShipOrientation());
    }
}
