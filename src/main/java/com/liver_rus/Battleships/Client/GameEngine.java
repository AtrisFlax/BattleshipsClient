package com.liver_rus.Battleships.Client;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

class GameEngine {

    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    enum Phase {
        INIT, DEPLOYING_FLEET, FLEET_IS_DEPLOYED, WAITING_CONNECTION, TAKE_SHOT, MAKE_SHOT, END_GAME;
    }

    private Phase gamePhase;
    private GameField gameField; //active player (my field)
    private boolean shipSelected;
    private FieldCoord shootCoord;
    private CurrentState currentState;
    private int numRound;
    //TODO numTurn tracking, incrementing and reseting
    private int numTurn;
    private boolean firstShot;

    GameEngine() {
        gameField = new GameField();
        shipSelected = false;
        currentState = new CurrentState();
        numRound = 1;
        numTurn = 1;
        firstShot = true;
        setPhase(GameEngine.Phase.INIT);
    }

    CurrentState getCurrentState() {
        return currentState;
    }

    void setCurrentState(FieldCoord fieldCoord, Ship.Type shipType, Ship.Orientation shipOrientation) {
        currentState.setFieldCoord(fieldCoord);
        currentState.setShipType(shipType);
        currentState.setShipOrientation(shipOrientation);
    }

    int getNumRound() {
        return numRound;
    }

    void setNumRound(int numRound) {
        this.numRound = numRound;
    }

    int newNumRound() {
        return numRound++;
    }

    boolean isFirstShot() {
        return firstShot;
    }

    void setFirstShot(boolean firstShot) {
        this.firstShot = firstShot;
    }

    void addShipOnField(Ship ship) {
        gameField.getShips().add(ship);
    }

    void setPhase(Phase phase) {
        System.out.println("Phase has been changed to " + phase);
        this.gamePhase = phase;
    }

    final Phase getPhase() {
        return gamePhase;
    }

    void setShipSelected(boolean shipSelected) {
        this.shipSelected = shipSelected;
    }

    boolean getShipSelected() {
        return shipSelected;
    }

    final boolean isShipSelected() {
        return shipSelected;
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
        log.info("GameEnging.reset()");
        gameField = new GameField();
        currentState = new CurrentState();
        shipSelected = false;
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

    int selectShip(Ship.Type type) {
        if (gameField.getShips().getShipsLeft() > 0) {
            setShipSelected(true);
            currentState.shipType = type;
            return gameField.getShips().popShip(type);
        } else {
            return 0;
        }
    }
}
