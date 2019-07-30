package com.liver_rus.Battleships.Client;

/**
 * Класс координат экрана игры. Служит для преобразования координат в объекты игры.
 */

class PixelCoord {


    static int transformSceneXtoFieldX(double x, boolean isFirstPlayerCoord) {
        if (isFirstPlayerCoord) {
            return (int) (Math.floor((x - Constant.Pixel.FirstPlayer.LEFT_X) / Constant.Pixel.FirstPlayer.WIDTH_CELL));
        } else {
            return (int) (Math.floor((x - Constant.Pixel.SecondPlayer.LEFT_X) / Constant.Pixel.SecondPlayer.WIDTH_CELL));
        }
    }

    static int transformSceneYtoFieldY(double y, boolean isFirstPlayerCoord) {
        if (isFirstPlayerCoord) {
            return (int) (Math.floor((y - Constant.Pixel.FirstPlayer.TOP_Y) / Constant.Pixel.FirstPlayer.WIDTH_CELL));
        } else {
            return (int) (Math.floor((y - Constant.Pixel.SecondPlayer.TOP_Y) / Constant.Pixel.SecondPlayer.WIDTH_CELL));
        }
    }

    static boolean isCoordFromMyPlayerField(double x, double y) {
        return x >= Constant.Pixel.FirstPlayer.LEFT_X && x <= Constant.Pixel.FirstPlayer.RIGHT_X &&
                y >= Constant.Pixel.FirstPlayer.TOP_Y && y <= Constant.Pixel.FirstPlayer.BOTTOM_Y;
    }

    static boolean isCoordFromEnemyPlayerField(double x, double y) {
        return checkBordersEnemyPlayerField(x, y);
    }

    static private boolean checkBordersEnemyPlayerField(double x, double y) {
        return x >= Constant.Pixel.SecondPlayer.LEFT_X && x <= Constant.Pixel.SecondPlayer.RIGHT_X &&
                y >= Constant.Pixel.SecondPlayer.TOP_Y && y <= Constant.Pixel.SecondPlayer.BOTTOM_Y;
    }
}