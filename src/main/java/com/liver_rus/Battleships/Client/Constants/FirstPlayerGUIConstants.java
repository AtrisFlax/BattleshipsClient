package com.liver_rus.Battleships.Client.Constants;

public class FirstPlayerGUIConstants implements GUIConstants {
    private final static int LEFT_X = 65;
    private final static int RIGHT_X = 299; //+1
    private final static int TOP_Y = 130;
    private final static int BOTTOM_Y = 364; //+1
    private final static double WIDTH_CELL = 23.5;

    private static FirstPlayerGUIConstants instance;

    private FirstPlayerGUIConstants() {}

    public static FirstPlayerGUIConstants getGUIConstant() {
        if (instance == null) {
            instance = new FirstPlayerGUIConstants();
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
