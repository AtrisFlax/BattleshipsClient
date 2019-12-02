package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.Constants.Constants;

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

        lastMyFieldCoord = new FieldCoord((byte) Constants.NONE_SELECTED_FIELD_COORD, (byte) Constants.NONE_SELECTED_FIELD_COORD);
        lastEnemyFieldCoord = new FieldCoord((byte) Constants.NONE_SELECTED_FIELD_COORD, (byte) Constants.NONE_SELECTED_FIELD_COORD);
    }

    CurrentGUIState getCurrentGUIState() {
        return currentGUIState;
    }

    void setCurrentState(FieldCoord fieldCoord, Ship.Type shipType, boolean isHorizontal) {
        currentGUIState.setFieldCoord(fieldCoord);
        currentGUIState.setShipType(shipType);
        currentGUIState.setOrientation(isHorizontal);
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

    final boolean getShipOrientation() {
        return currentGUIState.isHorizontalOrientation();
    }

    private void setShipOrientation(boolean isHorizontal) {
        currentGUIState.setOrientation(isHorizontal);
    }

    Ship.Type getShipType() {
        return currentGUIState.getShipType();
    }

    void setType(Ship.Type shipType) {
        currentGUIState.setShipType(shipType);
    }

    int selectShip(Ship.Type type) {
        if (gameField.getFleet().getShipsLeft() > 0) {
            int popShipResult = gameField.getFleet().popShip(type);
            //TODO используется только минус единица значение
            //завести константу и сравнивать с ней именованаю (не с -1)
            final int NO_MORE_SHIP_FOR_EXTRACTION = -1;
            if (popShipResult != NO_MORE_SHIP_FOR_EXTRACTION) {
                setShipSelected(true);
                currentGUIState.setShipType(type);
                setType(type);
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
        if (message.startsWith(Constants.NetworkMessage.HIT)) {
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
        if (message.startsWith(Constants.NetworkMessage.MISS)) {
            //Miss [/] auto placed by gui handler
            //if (getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
            //    log.info("Client: Server give message to Early");
            //}
            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                setShootCoord(MessageProcessor.getShootCoordFromMessage(message));
            }
            return;
        }

        if (message.startsWith(Constants.NetworkMessage.DESTROYED)) {
            if (getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
                log.info("Client: Server give message to Early");
            }
            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                setShootCoord(MessageProcessor.getShootCoordFromMessage(message));
            }
            return;
        }

        switch (message) {
            case Constants.NetworkMessage.YOU_TURN:
                setGamePhase(ClientGameEngine.Phase.MAKE_SHOT);
                return;
            case Constants.NetworkMessage.ENEMY_TURN:
                setGamePhase(ClientGameEngine.Phase.TAKE_SHOT);
                return;
            case Constants.NetworkMessage.YOU_WIN:
            case Constants.NetworkMessage.YOU_LOSE:
                setGamePhase(ClientGameEngine.Phase.END_GAME);
                return;
        }
    }

    FieldCoord getLastMyFieldCoord() {
        return lastMyFieldCoord;
    }

    void setLastMyFieldCoord(FieldCoord lastMyFieldCoord) {
        this.lastMyFieldCoord = lastMyFieldCoord;
    }

    void setLastEnemyFieldCoord(FieldCoord lastEnemyFieldCoord) {
        this.lastEnemyFieldCoord = lastEnemyFieldCoord;
    }
}
