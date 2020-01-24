package com.liver_rus.Battleships.Client.GamePrimitives;

import com.liver_rus.Battleships.Client.Constants.GUIConstant;
import com.liver_rus.Battleships.Client.GUI.SceneCoord;
import javafx.scene.input.MouseEvent;

public class FieldCoord {
    private final int x, y;
    private boolean tag;

    public FieldCoord() {
        this.x = 0;
        this.y = 0;
        this.tag = false;
    }

    public FieldCoord(int x, int y) {
        if (x < 0 || x >= GameField.FIELD_SIZE)
            throw new IllegalArgumentException("x should be x>=0 and x< "+ GameField.FIELD_SIZE + " Real x=" + x);
        if (y < 0 || y >= GameField.FIELD_SIZE)
            throw new IllegalArgumentException("y should be y>=0 and y< "+ GameField.FIELD_SIZE + " Real y=" + y);

        this.x = x;
        this.y = y;
        this.tag = false;
    }

    public FieldCoord(MouseEvent event, GUIConstant constants) {
        this.x = SceneCoord.transformToFieldX(event.getSceneX(), constants);
        this.y = SceneCoord.transformToFieldY(event.getSceneY(), constants);
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public boolean getTag() {
        return tag;
    }

    public void setTag() {
        tag = true;
    }

    @Override
    public String toString() {
        return Integer.toString(x) + y;
    }

    //Char + Num format A1
    public String toGameFormat() {
        int tmpX = x + 1;
        int tmpY = y + 1;
        String strY = tmpY > 0 && tmpY < 27 ? String.valueOf((char) (tmpY + 'A' - 1)) : null;
        return strY + tmpX;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldCoord that = (FieldCoord) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}


