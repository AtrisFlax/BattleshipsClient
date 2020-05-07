package com.liver_rus.Battleships.Network.Server.GamePrimitives;

import com.liver_rus.Battleships.Client.GUI.Constants.GUIConstants;
import com.liver_rus.Battleships.Client.GUI.SceneCoord;
import com.liver_rus.Battleships.Network.Server.FieldCell;
import javafx.scene.input.MouseEvent;

public class FieldCoord {
    private final int x;
    private final int y;
    private FieldCell type;

    public FieldCoord(int x, int y) {
        if (x < 0 || x >= GameField.FIELD_SIZE)
            throw new IllegalArgumentException("x should be x>=0 and x<"+ GameField.FIELD_SIZE + " Real x=" + x);
        if (y < 0 || y >= GameField.FIELD_SIZE)
            throw new IllegalArgumentException("y should be y>=0 and y<"+ GameField.FIELD_SIZE + " Real y=" + y);
        this.x = x;
        this.y = y;
        type = FieldCell.CLEAR;
    }

    public FieldCoord(MouseEvent event, GUIConstants constants) {
        this.x = SceneCoord.transformToFieldX(event.getSceneX(), constants);
        this.y = SceneCoord.transformToFieldY(event.getSceneY(), constants);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean getTag() {
        return type == FieldCell.DAMAGED_SHIP ||type == FieldCell.DOUBLE_DAMAGED;
    }

    public void setTag() {
        type = FieldCell.DAMAGED_SHIP;
    }

    public FieldCell getType() {
        return type;
    }

    public void setType(FieldCell type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return Integer.toString(x) + y;
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


