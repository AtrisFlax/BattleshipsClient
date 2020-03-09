package com.liver_rus.Battleships.Network.Server;

import com.liver_rus.Battleships.Client.GamePrimitives.Ship;

class ServerGameEngine {
    private static final int MAX_PLAYERS = 2;

    private boolean isReadyForBroadcast;
    private boolean isFirstTurn;
    Ship destroyedShip;

    ServerGameEngine() {
        isReadyForBroadcast = false;
        isFirstTurn = true;
        destroyedShip = null;
    }

    boolean isBroadcastEnabled() {
        return isReadyForBroadcast;
    }

    void setReadyForBroadcast(boolean readyForBroadcast) {
        isReadyForBroadcast = readyForBroadcast;
    }

    static int maxPlayers() {
        return MAX_PLAYERS;
    }

    final boolean isFirstTurn() {
        return isFirstTurn;
    }

    void setFirstTurn(boolean firstTurn) {
        isFirstTurn = firstTurn;
    }

    public Ship getDestroyedShip() {
        return destroyedShip;
    }

    public void setDestroyedShip(Ship destroyedShip) {
        this.destroyedShip = destroyedShip;
    }
}