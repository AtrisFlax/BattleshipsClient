package com.liver_rus.Battleships.Client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class Draw {
    static void HitCellOnMyField(GraphicsContext graphicContext, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY() - 2;
        double width = Constants.Pixel.FirstPlayer.WIDTH_CELL;
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + Constants.Pixel.FirstPlayer.LEFT_X,
                y * width + Constants.Pixel.FirstPlayer.TOP_Y,
                x * width + Constants.Pixel.FirstPlayer.LEFT_X + width,
                y * width + Constants.Pixel.FirstPlayer.TOP_Y + width);
        graphicContext.strokeLine(x * width + Constants.Pixel.FirstPlayer.LEFT_X,
                y * width + Constants.Pixel.FirstPlayer.TOP_Y + width,
                x * width + Constants.Pixel.FirstPlayer.LEFT_X + width,
                y * width + Constants.Pixel.FirstPlayer.TOP_Y);
    }

    static void MissCellOnMyField(GraphicsContext graphicContext, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY() - 2;
        double width = Constants.Pixel.FirstPlayer.WIDTH_CELL;
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + Constants.Pixel.FirstPlayer.LEFT_X,
                y * width + Constants.Pixel.FirstPlayer.TOP_Y + width,
                x * width + Constants.Pixel.FirstPlayer.LEFT_X + width,
                y * width + Constants.Pixel.FirstPlayer.TOP_Y);
    }

    static void HitCellOnEnemyField(GraphicsContext graphicContext, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        double width = Constants.Pixel.SecondPlayer.WIDTH_CELL;
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + Constants.Pixel.SecondPlayer.LEFT_X,
                y * width + Constants.Pixel.SecondPlayer.TOP_Y,
                x * width + Constants.Pixel.SecondPlayer.LEFT_X + width,
                y * width + Constants.Pixel.SecondPlayer.TOP_Y + width);
        graphicContext.strokeLine(x * width + Constants.Pixel.SecondPlayer.LEFT_X,
                y * width + Constants.Pixel.SecondPlayer.TOP_Y + width,
                x * width + Constants.Pixel.SecondPlayer.LEFT_X + width,
                y * width + Constants.Pixel.SecondPlayer.TOP_Y);
    }

    static void MissCellOnEnemyField(GraphicsContext graphicContext, FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        double width = Constants.Pixel.SecondPlayer.WIDTH_CELL;
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(2);
        graphicContext.strokeLine(x * width + Constants.Pixel.SecondPlayer.LEFT_X,
                y * width + Constants.Pixel.SecondPlayer.TOP_Y + width,
                x * width + Constants.Pixel.SecondPlayer.LEFT_X + width,
                y * width + Constants.Pixel.SecondPlayer.TOP_Y);
    }

    static void ShipOnEnemyField(GraphicsContext gc, FieldCoord fieldCoord, Ship.Type shipType, Ship.Orientation orientation) {
        int x = fieldCoord.getX() - 1;
        int y = fieldCoord.getY() - 1;
        int intShipType = Ship.Type.shipTypeToInt(shipType);
        if (orientation == Ship.Orientation.HORIZONTAL) {
            gc.strokeRect(
                    Constants.Pixel.SecondPlayer.LEFT_X + x * Constants.Pixel.SecondPlayer.WIDTH_CELL,
                    Constants.Pixel.SecondPlayer.TOP_Y + y * Constants.Pixel.SecondPlayer.WIDTH_CELL,
                    Constants.Pixel.SecondPlayer.WIDTH_CELL * (intShipType + 1),
                    Constants.Pixel.SecondPlayer.WIDTH_CELL);
        } else {
            gc.strokeRect(
                    Constants.Pixel.SecondPlayer.LEFT_X + x * Constants.Pixel.SecondPlayer.WIDTH_CELL,
                    Constants.Pixel.SecondPlayer.TOP_Y + y * Constants.Pixel.SecondPlayer.WIDTH_CELL,
                    Constants.Pixel.SecondPlayer.WIDTH_CELL,
                    Constants.Pixel.SecondPlayer.WIDTH_CELL * (intShipType + 1));
        }
    }

    static void ShipOnMyField(GraphicsContext gc, FieldCoord coord, Ship.Type shipType, Ship.Orientation shipOrientation) {
        int x = coord.getX();
        int y = coord.getY() - 1;
        int intShipType = Ship.Type.shipTypeToInt(shipType);
        if (shipOrientation == Ship.Orientation.HORIZONTAL) {
            gc.strokeRect(
                    Constants.Pixel.FirstPlayer.LEFT_X + x * Constants.Pixel.FirstPlayer.WIDTH_CELL,
                    Constants.Pixel.FirstPlayer.TOP_Y + y * Constants.Pixel.FirstPlayer.WIDTH_CELL,
                    Constants.Pixel.FirstPlayer.WIDTH_CELL * (intShipType + 1),
                    Constants.Pixel.FirstPlayer.WIDTH_CELL
            );
        } else {
            gc.strokeRect(
                    Constants.Pixel.FirstPlayer.LEFT_X + x * Constants.Pixel.FirstPlayer.WIDTH_CELL,
                    Constants.Pixel.FirstPlayer.TOP_Y + y * Constants.Pixel.FirstPlayer.WIDTH_CELL,
                    Constants.Pixel.FirstPlayer.WIDTH_CELL,
                    Constants.Pixel.FirstPlayer.WIDTH_CELL * (intShipType + 1)
            );
        }
    }

    static void ShipOnMyField(GraphicsContext gc, CurrentState currentState) {
        ShipOnMyField(gc, currentState.getFieldCoord(), currentState.getShipType(), currentState.getShipOrientation());
    }
}
