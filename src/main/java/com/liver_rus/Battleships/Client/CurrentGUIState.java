package com.liver_rus.Battleships.Client;

class CurrentGUIState {
    FieldCoord fieldCoord;
    Ship.Type shipType;
    Ship.Orientation shipOrientation;

    CurrentGUIState(){
        setFieldCoord(new FieldCoord());
        setShipOrientation(Ship.Orientation.HORIZONTAL);
        setShipType(Ship.Type.UNKNOWN);;
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

    public Ship.Orientation getShipOrientation() {
        return shipOrientation;
    }

    public void setShipOrientation(Ship.Orientation shipOrientation) {
        this.shipOrientation = shipOrientation;
    }

    public void changeShipOrientation() {
        if (getShipOrientation() == Ship.Orientation.HORIZONTAL) {
            setShipOrientation(Ship.Orientation.VERTICAL);
        } else {
            setShipOrientation(Ship.Orientation.HORIZONTAL);
        }
    }

    @Override
    public String toString() {
        return fieldCoord + " " + shipType + " " + shipOrientation;
    }
}