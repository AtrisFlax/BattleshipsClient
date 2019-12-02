package com.liver_rus.Battleships.Client.Tools;

import com.liver_rus.Battleships.Client.GamePrimitive.FieldCoord;

//TODO разобратся с кооридинатами и их преборазованием
public class MessageAdapterFieldCoord extends FieldCoord {
    public MessageAdapterFieldCoord(FieldCoord fieldCoord) {
        super(fieldCoord.getX() + 1, fieldCoord.getY() + 1);
    }
}

