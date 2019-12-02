package com.liver_rus.Battleships.Client;

//TODO разобратся с кооридинатами и их преборазованием
public class MessageAdapterFieldCoord extends FieldCoord {
    public MessageAdapterFieldCoord(FieldCoord fieldCoord) {
        super(fieldCoord.getX() + 1, fieldCoord.getY() + 1);
    }
}

