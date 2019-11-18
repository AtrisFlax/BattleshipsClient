package com.liver_rus.Battleships.Client;

class CurrentGUIState {
    private FieldCoord fieldCoord;
    Ship.Type shipType;
    Ship.Orientation shipOrientation;

    CurrentGUIState(){
        setFieldCoord(new FieldCoord());
        setShipOrientation(Ship.Orientation.HORIZONTAL);
        setShipType(Ship.Type.UNKNOWN);;
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

    Ship.Orientation getShipOrientation() {
        return shipOrientation;
    }

    void setShipOrientation(Ship.Orientation shipOrientation) {
        this.shipOrientation = shipOrientation;
    }

    void changeShipOrientation() {
        if (getShipOrientation() == Ship.Orientation.HORIZONTAL) {
            setShipOrientation(Ship.Orientation.VERTICAL);
        } else {
            setShipOrientation(Ship.Orientation.HORIZONTAL);
        }
    }

    @Override
    public String toString() {
        return "CurrentGUIState: " + fieldCoord + " " + shipType + " " + shipOrientation;
    }
}