package com.liver_rus.Battleships.Client.GUI.DrawEvents;

public abstract class Redraw {
    private static final int UNDEFINED_COORD = -1;

    private static int lastX = UNDEFINED_COORD;
    private static int lastY = UNDEFINED_COORD;

    boolean isOldCoord(int x, int y) {
        if (lastX == UNDEFINED_COORD || lastY == UNDEFINED_COORD) {
            lastX = x;
            lastY = y;
        } else {
            if (enemyFieldCoordinateHadBeenChanged(x, y)) {
                lastX = x;
                lastY = y;
                return true;
            }
        }
        return false;
    }

    private boolean enemyFieldCoordinateHadBeenChanged(int x, int y) {
        return lastX != x || lastY != y;
    }

    //TODO set lastX lastY UNDEFINED_COORD while global reset
}
