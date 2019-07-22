package com.liver_rus.Battleships.Client;

class GameEngine {

    enum Phase {
        ARRANGE_FLEET, READY, TAKE_SHOT, MAKE_SHOT, END_GAME
    }

    private Phase gamePhase;
    private GameField myGameField;
    private ArrangeFleetHolder fleetHolder;
    private boolean isShipSelected;
    private Ship.Orientation shipOrientation;
    private ShipsOnField shipsOnField;

    private FieldCoord shootCoord;
    private Ship.Type shipType;

    GameEngine() {
        reset();
    }

    void addShipOnField(Ship ship) {
        shipsOnField.add(ship);
    }

    void setPhase(Phase phase) {
        this.gamePhase = phase;
    }

    ShipsOnField getShipsOnField() {
        return shipsOnField;
    }

    final Phase getPhase() {
        return gamePhase;
    }

    void setIsSelected(boolean isSelectted) {
        isShipSelected = isSelectted;
    }

    final boolean getIsShipSelected() {
        return isShipSelected;
    }

    final Ship.Orientation getShipOrientation() {
        return shipOrientation;
    }

    void setShipOrientation(Ship.Orientation shipOrientation) {
        this.shipOrientation = shipOrientation;
    }

    FieldCoord getShootCoord() {
        return shootCoord;
    }

    void setShootCoord(FieldCoord shootCoord) {
        this.shootCoord = shootCoord;
    }

    void reset() {
        myGameField = new GameField();
        fleetHolder = new ArrangeFleetHolder();
        isShipSelected = false;
        shipOrientation = Ship.Orientation.HORIZONTAL;
        shipType = Ship.Type.UNKNOWN;
        shipsOnField = new ShipsOnField();
    }

    Ship.Type getShipType() {
        return shipType;
    }

    void setType(Ship.Type shipType) {
        this.shipType = shipType;
    }

    GameField getMyGameField() {
        return myGameField;
    }

    ArrangeFleetHolder getFleetHolder() {
        return fleetHolder;
    }

    Phase getGamePhase() {
        return gamePhase;
    }
}
