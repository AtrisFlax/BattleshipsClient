package com.liver_rus.Battleships.Client;

/**
 * Класс пар координат x, y. tag  попадание в клетку
 */

public class FieldCoord {
    private final int x, y;

    private boolean tag;

    public FieldCoord() {
        this.x = 0;
        this.y = 0;
        this.tag = false;
    }

    public FieldCoord(int x, int y) {
        this.x = x;
        this.y = y;
        this.tag = false;
    }

    FieldCoord(String str1, String str2) {
        x = Integer.parseInt(str1);
        y = Integer.parseInt(str2);
        tag = false;
    }

    public FieldCoord(double sceneX, double sceneY, boolean isFirstPlayerCoord) {
        this.x = PixelCoord.transformSceneXtoFieldX(sceneX, isFirstPlayerCoord);
        this.y = PixelCoord.transformSceneYtoFieldY(sceneY, isFirstPlayerCoord);
    }

    final int getX() {
        return x;
    }

    final int getY() {
        return y;
    }

    boolean getTag() {
        return tag;
    }

    void setTag() {
        tag = true;
    }

    @Override
    public String toString() {
        return Integer.toString(x) + y;
    }

    //A1 B2
    public String toGameFormat() {
        int tmpX = x + 1;
        int tmpY = y + 1;
        String strY = tmpY > 0 && tmpY < 27 ? String.valueOf((char)(tmpY + 'A' - 1)) : null;
        return strY + tmpX;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldCoord that = (FieldCoord) o;

        return x != that.x || y != that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}


