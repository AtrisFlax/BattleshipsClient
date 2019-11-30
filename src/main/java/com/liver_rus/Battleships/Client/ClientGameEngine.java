package com.liver_rus.Battleships.Client;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

//TODO numTurn tracking, incrementing and reseting
class ClientGameEngine extends GameEngine {

    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private GameField gameField;
    private Phase gamePhase;
    private boolean shipSelected;
    private FieldCoord shootCoord;
    private CurrentGUIState currentGUIState;

    private FieldCoord lastMyFieldCoord;
    private FieldCoord lastEnemyFieldCoord;

    enum Phase {
        INIT, DEPLOYING_FLEET, FLEET_IS_DEPLOYED, WAITING_ANSWER, TAKE_SHOT, MAKE_SHOT, END_GAME;
    }

    ClientGameEngine() {
        super();
        gameField = new GameField();
        setGamePhase(ClientGameEngine.Phase.INIT);
        shipSelected = false;
        shootCoord = null;
        currentGUIState = new CurrentGUIState();

        //TODO lastMyFieldCoord lastEnemyFieldCoord move on GameEngine class
        lastMyFieldCoord = new FieldCoord((byte) Constants.NONE_SELECTED_FIELD_COORD, (byte) Constants.NONE_SELECTED_FIELD_COORD);
        lastEnemyFieldCoord = new FieldCoord((byte) Constants.NONE_SELECTED_FIELD_COORD, (byte) Constants.NONE_SELECTED_FIELD_COORD);
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
            int popShipResult = gameField.getFleet().popShip(type);
            if (popShipResult != -1) {
                setShipSelected(true);
                currentGUIState.shipType = type;
                return popShipResult;
            } else {
                setShipSelected(false);
                return 0;
            }
        } else {
            return 0;
        }
    }

    void addShipOnField(Ship ship) {
        gameField.getFleet().add(ship);
        gameField.markFieldByShip(ship);
    }

    void proceedMessage(String message) {
        //HITXX
        if (MessageProcessor.isHit(message)) {
            //if you turn
            if (getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                //shoot coord is set by gui handler before
                //setShootCoord(MessageProcessor.getShootCoordFromMessage(message));
            }
            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                setShootCoord(MessageProcessor.getShootCoordFromMessage(message));
            }
        }

        //MISSXX
        if (MessageProcessor.isMiss(message)) {
            //Miss [/] auto placed by gui handler
            //if (getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
            //    log.info("Client: Server give message to Early");
            //}
            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                setShootCoord(MessageProcessor.getShootCoordFromMessage(message));
            }
            return;
        }

        if (MessageProcessor.isDestroyed(message)) {
            if (getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
                log.info("Client: Server give message to Early");
            }
            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                setShootCoord(MessageProcessor.getShootCoordFromMessage(message));
            }
            return;
        }

        if (MessageProcessor.isYouTurn(message)) {
            setGamePhase(ClientGameEngine.Phase.MAKE_SHOT);
            return;
        }

        if (MessageProcessor.isEnemyTurn(message)) {
            setGamePhase(ClientGameEngine.Phase.TAKE_SHOT);
            return;
        }

        if (MessageProcessor.isYouWin(message)) {
            setGamePhase(ClientGameEngine.Phase.END_GAME);
            return;
        }

        if (MessageProcessor.isYouLose(message)) {
            setGamePhase(ClientGameEngine.Phase.END_GAME);
        }
    }

    public FieldCoord getLastMyFieldCoord() {
        return lastMyFieldCoord;
    }

    public void setLastMyFieldCoord(FieldCoord lastMyFieldCoord) {
        this.lastMyFieldCoord = lastMyFieldCoord;
    }

    public FieldCoord getLastEnemyFieldCoord() {
        return lastEnemyFieldCoord;
    }

    public void setLastEnemyFieldCoord(FieldCoord lastEnemyFieldCoord) {
        this.lastEnemyFieldCoord = lastEnemyFieldCoord;
    }
}
