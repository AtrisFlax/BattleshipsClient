package com.liver_rus.Battleships.Client;

//Class describes virtual ship for draw

class CurrentGUIState {
    private FieldCoord fieldCoord;
    private Ship.Type shipType;
    private boolean isHorizontalShipOrientation;

    CurrentGUIState(){
        setFieldCoord(new FieldCoord());
        setShipType(Ship.Type.UNKNOWN);;
        isHorizontalShipOrientation = true;
    }

    FieldCoord getFieldCoord() {
        return fieldCoord;
    }

    void setFieldCoord(FieldCoord fieldCoord) {
        this.fieldCoord = fieldCoord;
    }

    Ship.Type getShipType() {
        return shipType;
    }

    void setShipType(Ship.Type shipType) {
        this.shipType = shipType;
    }

    final boolean isHorizontalOrientation() {
        return isHorizontalShipOrientation;
    }

    void setOrientation(boolean isHorizontal) {
        isHorizontalShipOrientation = isHorizontal;
    }

    void changeShipOrientation() {
        isHorizontalShipOrientation = !isHorizontalShipOrientation;
    }

    @Override
    public String toString() {
        return "CurrentGUIState: " + fieldCoord + " " + shipType + " " + isHorizontalShipOrientation;
    }
}