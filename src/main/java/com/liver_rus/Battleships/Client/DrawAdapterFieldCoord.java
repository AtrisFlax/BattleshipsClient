package com.liver_rus.Battleships.Client;

public class DrawAdapterFieldCoord extends FieldCoord {
    public DrawAdapterFieldCoord(FieldCoord fieldCoord) {
        super(fieldCoord.getX() - 1, fieldCoord.getY() - 1);
    }
}

