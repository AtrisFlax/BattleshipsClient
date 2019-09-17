package com.liver_rus.Battleships.Client;

class GameEngine {

    enum Phase {
        ARRANGE_FLEET, READY, TAKE_SHOT, MAKE_SHOT, END_GAME
    }

    private Phase gamePhase;
    private GameField gameField; //active player (my field)
    private ArrangeFleetHolder fleetHolder;
    private boolean isShipSelected;
    private ShipsOnField shipsOnField;
    private FieldCoord shootCoord;
    private CurrentState currentState;
    private int numRound;

    public CurrentState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(FieldCoord fieldCoord, Ship.Type shipType, Ship.Orientation shipOrientation) {
        currentState.setFieldCoord(fieldCoord);
        currentState.setShipType(shipType);
        currentState.setShipOrientation(shipOrientation);
    }

    public int getNumRound() {
        return numRound;
    }

    public void setNumRound(int numRound) {
        this.numRound = numRound;
    }

    public int newNumRound() {
        return numRound++;
    }

    public boolean isFirstShot() {
        return firstShot;
    }

    public void setFirstShot(boolean firstShot) {
        this.firstShot = firstShot;
    }

    boolean firstShot = true;

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

    void setShipSelected(boolean isShipSelected) {
        this.isShipSelected = isShipSelected;
    }

    final boolean getIsShipSelected() {
        return isShipSelected;
    }

    final Ship.Orientation getShipOrientation() {
        return currentState.shipOrientation;
    }

    private void setShipOrientation(Ship.Orientation shipOrientation) {
        currentState.shipOrientation = shipOrientation;
    }

    FieldCoord getShootCoord() {
        return shootCoord;
    }

    void setShootCoord(FieldCoord shootCoord) {
        this.shootCoord = shootCoord;
    }

    void reset() {
        gameField = new GameField();
        fleetHolder = new ArrangeFleetHolder();
        isShipSelected = false;
        currentState = new CurrentState();
        shipsOnField = new ShipsOnField();
        numRound = 1;
    }

    Ship.Type getShipType() {
        return currentState.shipType;
    }

    void setType(Ship.Type shipType) {
        currentState.shipType = shipType;
    }

    GameField getMyField() {
        return gameField;
    }

    ArrangeFleetHolder getFleetHolder() {
        return fleetHolder;
    }

    Phase getGamePhase() {
        return gamePhase;
    }

    Ship.Orientation changeShipOrientation() {
        if (getShipOrientation() == Ship.Orientation.HORIZONTAL) {
            setShipOrientation(Ship.Orientation.VERTICAL);
        } else {
            setShipOrientation(Ship.Orientation.HORIZONTAL);
        }
        return getShipOrientation();

    }
}
