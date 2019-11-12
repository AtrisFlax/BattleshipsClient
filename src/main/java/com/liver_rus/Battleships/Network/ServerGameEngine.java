package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Client.GameEngine;
import com.liver_rus.Battleships.Client.GameField;
import com.liver_rus.Battleships.Client.Ship;

public class ServerGameEngine extends GameEngine {

    ServerGameEngine() {
        super(); setGamePhase(Phase.INIT);
    }

    private Phase gamePhase;

    enum Phase {
        INIT, END_GAME;
    }
    private boolean isReadyForBroadcast = false;

    boolean isBroadcastEnabled() {
        return isReadyForBroadcast;
    }

    void setReadyForBroadcast(boolean readyForBroadcast) {
        isReadyForBroadcast = readyForBroadcast;
    }

    static void addShipOnField(GameField gameField, Ship ship) {
        gameField.markFieldByShip(ship);
        gameField.getFleet().add(ship);
    }

    public Phase getGamePhase() {
        return gamePhase;
    }

    void setGamePhase(Phase gamePhase) {
        this.gamePhase = gamePhase;
    }

}
