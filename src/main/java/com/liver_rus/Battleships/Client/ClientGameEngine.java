package com.liver_rus.Battleships.Client;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

class ClientGameEngine extends GameEngine {

    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private GameField gameField;
    private Phase gamePhase;
    private boolean shipSelected;
    private FieldCoord shootCoord;
    private CurrentGUIState currentGUIState;

    enum Phase {
        INIT, DEPLOYING_FLEET, FLEET_IS_DEPLOYED, WAITING_ANSWER, TAKE_SHOT, MAKE_SHOT, END_GAME;
    }

    //TODO numTurn tracking, incrementing and reseting

    ClientGameEngine() {
        super();
        setGamePhase(ClientGameEngine.Phase.INIT);
        shipSelected = false;
        shootCoord = null;
        currentGUIState = new CurrentGUIState();
    }

    CurrentGUIState getCurrentGUIState() {
        return currentGUIState;
    }

    void setCurrentState(FieldCoord fieldCoord, Ship.Type shipType, Ship.Orientation shipOrientation) {
        currentGUIState.setFieldCoord(fieldCoord);
        currentGUIState.setShipType(shipType);
        currentGUIState.setShipOrientation(shipOrientation);
    }

    GameField getGameField() {
        return gameField;
    }
    String getShipsInfoForSend() {
        return gameField.getFleet().toString();
    }

    void setGamePhase(Phase phase) {
        System.out.println("Phase has been changed to " + phase);
        this.gamePhase = phase;
    }

    final Phase getGamePhase() {
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

    FieldCoord getShootCoord() {
        return shootCoord;
    }

    void setShootCoord(FieldCoord shootCoord) {
        this.shootCoord = shootCoord;
    }

    void reset() {
        log.info("GameEnging.reset()");
        gameField = new GameField();
        currentGUIState = new CurrentGUIState();
        shipSelected = false;
    }

    final Ship.Orientation getShipOrientation() {
        return currentGUIState.shipOrientation;
    }

    private void setShipOrientation(Ship.Orientation shipOrientation) {
        currentGUIState.shipOrientation = shipOrientation;
    }

    Ship.Type getShipType() {
        return currentGUIState.shipType;
    }

    void setType(Ship.Type shipType) {
        currentGUIState.shipType = shipType;
    }

    int selectShip(Ship.Type type) {
        if (gameField.getFleet().getShipsLeft() > 0) {
            setShipSelected(true);
            currentGUIState.shipType = type;
            return gameField.getFleet().popShip(type);
        } else {
            return 0;
        }
    }

    void addShipOnField(Ship ship) {
        gameField.getFleet().add(ship);
        gameField.markFieldByShip(ship);
    }
}
