package com.liver_rus.Battleships.Client.Constants;

public class SecondPlayerGUIConstants implements GUIConstants {
    private final static int LEFT_X = 304;
    private final static int RIGHT_X = 652;
    private final static int TOP_Y = 370;
    private final static int BOTTOM_Y = 718;
    private final static double WIDTH_CELL = 35.0;

    private static SecondPlayerGUIConstants instance;

    private SecondPlayerGUIConstants() {
    }

    public static SecondPlayerGUIConstants getGUIConstant() {
        if (instance == null) {
            instance = new SecondPlayerGUIConstants();
        }
        return instance;
    }

    public int getLeftX() {
        return LEFT_X;
    }

    public int getRightX() {
        return RIGHT_X;
    }

    public int getTopY() {
        return TOP_Y;
    }

    public int getBottomY() {
        return BOTTOM_Y;
    }

    public double getWidthCell() {
        return WIDTH_CELL;
    }
}

