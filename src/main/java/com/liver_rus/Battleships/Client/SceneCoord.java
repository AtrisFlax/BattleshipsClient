package com.liver_rus.Battleships.Client;

/**
 * Класс для преобразования координат экрана в коориданты поля игры.
 */

class SceneCoord {
    static int transformToFieldX(double x, GUIConstant constants) {
        return (int) (Math.floor((x - constants.getLeftX()) / constants.getWidthCell()));
    }

    static int transformToFieldY(double y, GUIConstant constants) {
        return (int) (Math.floor((y - constants.getTopY()) / constants.getWidthCell()));
    }

    static boolean isFromFirstPlayerField(double x, double y) {
        return checkBorders(x, y, new FirstPlayerGUIConstants());
    }

    static boolean isFromSecondPlayerField(double x, double y) {
        return checkBorders(x, y, new SecondPlayerGUIConstants());
    }

    static private boolean checkBorders(double x, double y, GUIConstant constants) {
        return x >= constants.getLeftX() && x <= constants.getRightX() &&
                y >= constants.getTopY() && y <= constants.getBottomY();
    }
}