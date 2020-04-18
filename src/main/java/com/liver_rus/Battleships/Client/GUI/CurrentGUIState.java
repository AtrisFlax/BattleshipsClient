package com.liver_rus.Battleships.Client.GUI;

//Class describes virtual ship for draw

import com.liver_rus.Battleships.Network.Server.GamePrimitives.FieldCoord;

public class CurrentGUIState {
    private static final int UNKNOWN_TYPE = -1;
    private FieldCoord fieldCoord;
    private int shipType;
    private boolean isHorizontalShipOrientation;

    public CurrentGUIState(){
        fieldCoord = new FieldCoord(0, 0);
        shipType = UNKNOWN_TYPE;
        isHorizontalShipOrientation = true;
    }

    public FieldCoord getFieldCoord() {
        return fieldCoord;
    }

    public void setFieldCoord(FieldCoord fieldCoord) {
        this.fieldCoord = fieldCoord;
    }

    public int  getShipType() {
        return shipType;
    }

    public void setShipType(int  shipType) {
        this.shipType = shipType;
    }

    public final boolean isHorizontalOrientation() {
        return isHorizontalShipOrientation;
    }

    public void setOrientation(boolean isHorizontal) {
        isHorizontalShipOrientation = isHorizontal;
    }

    public void changeShipOrientation() {
        isHorizontalShipOrientation = !isHorizontalShipOrientation;
    }

    @Override
    public String toString() {
        return "CurrentGUIState: " + fieldCoord + " " + shipType + " " + isHorizontalShipOrientation;
    }
}