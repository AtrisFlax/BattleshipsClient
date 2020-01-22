package com.liver_rus.Battleships.Client.GUI;

//Class describes virtual ship for draw

import com.liver_rus.Battleships.Client.GamePrimitives.FieldCoord;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;

public class CurrentGUIState {
    private FieldCoord fieldCoord;
    private Ship.Type shipType;
    private boolean isHorizontalShipOrientation;

    public CurrentGUIState(){
        setFieldCoord(new FieldCoord());
        setShipType(Ship.Type.UNKNOWN);;
        isHorizontalShipOrientation = true;
    }

    public FieldCoord getFieldCoord() {
        return fieldCoord;
    }

    public void setFieldCoord(FieldCoord fieldCoord) {
        this.fieldCoord = fieldCoord;
    }

    public Ship.Type getShipType() {
        return shipType;
    }

    public void setShipType(Ship.Type shipType) {
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