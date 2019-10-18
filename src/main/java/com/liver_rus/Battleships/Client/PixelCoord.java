package com.liver_rus.Battleships.Client;

/**
 * Класс координат экрана игры. Служит для преобразования координат в объекты игры.
 */

class PixelCoord {


    static int transformSceneXtoFieldX(double x, boolean isFirstPlayerCoord) {
        if (isFirstPlayerCoord) {
            return (int) (Math.floor((x - Constants.Pixel.FirstPlayer.LEFT_X) / Constants.Pixel.FirstPlayer.WIDTH_CELL));
        } else {
            return (int) (Math.floor((x - Constants.Pixel.SecondPlayer.LEFT_X) / Constants.Pixel.SecondPlayer.WIDTH_CELL));
        }
    }

    static int transformSceneYtoFieldY(double y, boolean isFirstPlayerCoord) {
        if (isFirstPlayerCoord) {
            return (int) (Math.floor((y - Constants.Pixel.FirstPlayer.TOP_Y) / Constants.Pixel.FirstPlayer.WIDTH_CELL));
        } else {
            return (int) (Math.floor((y - Constants.Pixel.SecondPlayer.TOP_Y) / Constants.Pixel.SecondPlayer.WIDTH_CELL));
        }
    }

    static boolean isFromMyPlayerField(double x, double y) {
        return x >= Constants.Pixel.FirstPlayer.LEFT_X && x <= Constants.Pixel.FirstPlayer.RIGHT_X &&
                y >= Constants.Pixel.FirstPlayer.TOP_Y && y <= Constants.Pixel.FirstPlayer.BOTTOM_Y;
    }

    static boolean isCoordFromEnemyPlayerField(double x, double y) {
        return checkBordersEnemyPlayerField(x, y);
    }

    static private boolean checkBordersEnemyPlayerField(double x, double y) {
        return x >= Constants.Pixel.SecondPlayer.LEFT_X && x <= Constants.Pixel.SecondPlayer.RIGHT_X &&
                y >= Constants.Pixel.SecondPlayer.TOP_Y && y <= Constants.Pixel.SecondPlayer.BOTTOM_Y;
    }
}