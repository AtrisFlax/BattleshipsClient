package com.liver_rus.Battleships.Client;

/**
 * Класс координат экрана игры. Служит для преобразования координат в объекты игры.
 */

class PixelCoord {

    //TODO залипание нижней строки в поле противника

    final private static int WRONG_COORD = -1;

    final private double x, y;

    PixelCoord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    FieldCoord transformMyFieldPixelCoordToFieldCoord() {
        if (isCoordFromMyPlayerField(x, y)) {
            //TODO LAMBDAS1
            final int cellX = (int) (Math.floor((x - Constant.LEFT_EDGE_PIXEL_FIRST_PLAYER_X) / Constant.WIDTH_FIRST_PLAYER_CELL));
            final int cellY = (int) (Math.floor((y - Constant.TOP_EDGE_PIXEL_FIRST_PLAYER_Y) / Constant.WIDTH_FIRST_PLAYER_CELL));
            return new FieldCoord(cellX, cellY);
        } else {
            //TODO LOGGER OR EXCEPTON
            return new FieldCoord(WRONG_COORD, WRONG_COORD);
        }
    }

    FieldCoord transformEnemyFieldPixelCoordToFieldCoord() {
        if (isCoordFromEnemyPlayerField(x, y)) {
            //TODO LAMBDAS1
            final int cellX = (int) (Math.floor((x - Constant.LEFT_EDGE_PIXEL_SECOND_PLAYER_X) / Constant.WIDTH_SECOND_PLAYER_CELL));
            final int cellY = (int) (Math.floor((y - Constant.TOP_EDGE_PIXEL_SECOND_PLAYER_Y) / Constant.WIDTH_SECOND_PLAYER_CELL));
            return new FieldCoord(cellX, cellY);
        } else {
            //TODO LOGGER OR EXCEPTON
            return new FieldCoord(WRONG_COORD, WRONG_COORD);
        }
    }

    static boolean isCoordFromMyPlayerField(double x, double y) {
        return checkBordersMyPlayerField(x, y);
    }

    static private boolean checkBordersMyPlayerField(double x, double y) {
        return x >= Constant.LEFT_EDGE_PIXEL_FIRST_PLAYER_X && x <= Constant.RIGHT_EDGE_PIXEL_FIRST_PLAYER_X &&
                y >= Constant.TOP_EDGE_PIXEL_FIRST_PLAYER_Y && y <= Constant.BOTTOM_EDGE_PIXEL_FIRST_PLAYER_Y;
    }

    static boolean isCoordFromEnemyPlayerField(double x, double y) {
        return checkBordersEnemyPlayerField(x, y);
    }

    static private boolean checkBordersEnemyPlayerField(double x, double y) {
        return x >= Constant.LEFT_EDGE_PIXEL_SECOND_PLAYER_X && x <= Constant.RIGHT_EDGE_PIXEL_SECOND_PLAYER_X &&
                y >= Constant.TOP_EDGE_PIXEL_SECOND_PLAYER_Y && y <= Constant.BOTTOM_EDGE_PIXEL_SECOND_PLAYER_Y;
    }
}