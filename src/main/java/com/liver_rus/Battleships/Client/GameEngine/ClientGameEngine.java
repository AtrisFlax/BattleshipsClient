package com.liver_rus.Battleships.Client.GameEngine;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GUI.CurrentGUIState;
import com.liver_rus.Battleships.Client.GamePrimitive.FieldCoord;
import com.liver_rus.Battleships.Client.GamePrimitive.GameField;
import com.liver_rus.Battleships.Client.GamePrimitive.Ship;
import com.liver_rus.Battleships.Client.Tools.MessageProcessor;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

//TODO numTurn tracking, incrementing and reseting
public class ClientGameEngine {

    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    public static ClientGameEngine.Phase phase;

    private GameField gameField;
    private Phase gamePhase;
    private boolean shipSelected;
    private FieldCoord shootCoord;
    private CurrentGUIState currentGUIState;

    private FieldCoord lastMyFieldCoord;
    private FieldCoord lastEnemyFieldCoord;

    public enum Phase {
        INIT, DEPLOYING_FLEET, FLEET_IS_DEPLOYED, WAITING_ANSWER, TAKE_SHOT, MAKE_SHOT, END_GAME;
    }

    public ClientGameEngine() {
        super();
        gameField = new GameField();
        setGamePhase(Phase.INIT);
        shipSelected = false;
        shootCoord = null;
        currentGUIState = new CurrentGUIState();

        lastMyFieldCoord = new FieldCoord((byte) Constants.NONE_SELECTED_FIELD_COORD, (byte) Constants.NONE_SELECTED_FIELD_COORD);
        lastEnemyFieldCoord = new FieldCoord((byte) Constants.NONE_SELECTED_FIELD_COORD, (byte) Constants.NONE_SELECTED_FIELD_COORD);
    }

    public CurrentGUIState getCurrentGUIState() {
        return currentGUIState;
    }

    public void setCurrentState(FieldCoord fieldCoord, Ship.Type shipType, boolean isHorizontal) {
        currentGUIState.setFieldCoord(fieldCoord);
        currentGUIState.setShipType(shipType);
        currentGUIState.setOrientation(isHorizontal);
    }

    public GameField getGameField() {
        return gameField;
    }

    public String getShipsInfoForSend() {
        return gameField.getFleet().toString();
    }

    public void setGamePhase(Phase phase) {
        System.out.println("Phase has been changed to " + phase);
        this.gamePhase = phase;
    }

    public final Phase getGamePhase() {
        return gamePhase;
    }

    public void setShipSelected(boolean shipSelected) {
        this.shipSelected = shipSelected;
    }

    public boolean getShipSelected() {
        return shipSelected;
    }

    public final boolean isShipSelected() {
        return shipSelected;
    }

    public FieldCoord getShootCoord() {
        return shootCoord;
    }

    public void setShootCoord(FieldCoord shootCoord) {
        this.shootCoord = shootCoord;
    }

    public void reset() {
        log.info("GameEnging.reset()");
        gameField = new GameField();
        currentGUIState = new CurrentGUIState();
        shipSelected = false;
    }

    public final boolean getShipOrientation() {
        return currentGUIState.isHorizontalOrientation();
    }

    private void setShipOrientation(boolean isHorizontal) {
        currentGUIState.setOrientation(isHorizontal);
    }

    public Ship.Type getShipType() {
        return currentGUIState.getShipType();
    }

    public int selectShip(Ship.Type type) {
        if (gameField.getFleet().getShipsLeft() > 0) {
            int popShipResult = gameField.getFleet().popShip(type);
            final int NO_MORE_SHIP_FOR_EXTRACTION = -1;
            if (popShipResult != NO_MORE_SHIP_FOR_EXTRACTION) {
                setShipSelected(true);
                currentGUIState.setShipType(type);
                return popShipResult;
            } else {
                setShipSelected(false);
                return 0;
            }
        } else {
            return 0;
        }
    }

    public void addShipOnField(Ship ship) {
        gameField.getFleet().add(ship);
        gameField.markFieldByShip(ship);
    }

    public void proceedMessage(String message) {
        //HITXX
        if (message.startsWith(Constants.NetworkMessage.HIT)) {
            //if you turn
            if (getGamePhase() == Phase.WAITING_ANSWER) {
                //shoot coord is set by gui handler before
                //setShootCoord(MessageProcessor.getShootCoordFromMessage(message));
            }
            if (getGamePhase() == Phase.TAKE_SHOT) {
                setShootCoord(MessageProcessor.getShootCoordFromMessage(message));
            }
        }

        //MISSXX
        if (message.startsWith(Constants.NetworkMessage.MISS)) {
            //Miss [/] auto placed by gui handler
            //if (getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
            //    log.info("Client: Server give message to Early");
            //}
            if (getGamePhase() == Phase.TAKE_SHOT) {
                setShootCoord(MessageProcessor.getShootCoordFromMessage(message));
            }
            return;
        }

        if (message.startsWith(Constants.NetworkMessage.DESTROYED)) {
            if (getGamePhase() == Phase.MAKE_SHOT) {
                log.info("Client: Server give message to Early");
            }
            if (getGamePhase() == Phase.TAKE_SHOT) {
                setShootCoord(MessageProcessor.getShootCoordFromMessage(message));
            }
            return;
        }

        switch (message) {
            case Constants.NetworkMessage.YOU_TURN:
                setGamePhase(Phase.MAKE_SHOT);
                return;
            case Constants.NetworkMessage.ENEMY_TURN:
                setGamePhase(Phase.TAKE_SHOT);
                return;
            case Constants.NetworkMessage.YOU_WIN:
            case Constants.NetworkMessage.YOU_LOSE:
                setGamePhase(Phase.END_GAME);
                return;
        }
    }

    public FieldCoord getLastMyFieldCoord() {
        return lastMyFieldCoord;
    }

    public void setLastMyFieldCoord(FieldCoord lastMyFieldCoord) {
        this.lastMyFieldCoord = lastMyFieldCoord;
    }

    public void setLastEnemyFieldCoord(FieldCoord lastEnemyFieldCoord) {
        this.lastEnemyFieldCoord = lastEnemyFieldCoord;
    }
}
