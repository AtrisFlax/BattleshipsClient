package com.liver_rus.Battleships.Client;

class CurrentState {
    FieldCoord fieldCoord;
    Ship.Type shipType;
    Ship.Orientation shipOrientation;

    CurrentState(){
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

    @Override
    public String toString() {
        return fieldCoord + " " + shipType + " " + shipOrientation;
    }
}