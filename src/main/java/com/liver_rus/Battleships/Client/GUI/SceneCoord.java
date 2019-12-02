package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.Constants.GUIConstant;
import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;

/**
 * Класс для преобразования координат экрана в коориданты поля игры.
 */

//TODO добавить тест на класс

//check borders входил ли в рамки
public class SceneCoord {
    public static int transformToFieldX(double x, GUIConstant constants) {
        return (int) (Math.floor((x - constants.getLeftX()) / constants.getWidthCell()));
    }

    public static int transformToFieldY(double y, GUIConstant constants) {
        return (int) (Math.floor((y - constants.getTopY()) / constants.getWidthCell()));
    }

    static boolean isFromFirstPlayerField(double x, double y) {
        return checkBorders(x, y, FirstPlayerGUIConstants.getGUIConstant());
    }

    static boolean isFromSecondPlayerField(double x, double y) {
        return checkBorders(x, y, SecondPlayerGUIConstants.getGUIConstant());
    }

    static private boolean checkBorders(double x, double y, GUIConstant constants) {
        return x >= constants.getLeftX() && x <= constants.getRightX() &&
                y >= constants.getTopY() && y <= constants.getBottomY();
    }
}