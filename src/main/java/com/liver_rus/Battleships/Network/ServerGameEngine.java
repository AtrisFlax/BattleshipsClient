package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Client.GameEngine;

public class ServerGameEngine extends GameEngine {
    private static final int MAX_PLAYERS = 2;
    private Phase gamePhase;
    private boolean isReadyForBroadcast = false;

    enum Phase {
        INIT, END_GAME;
    }

    ServerGameEngine() {
        super(); setGamePhase(Phase.INIT);
    }

    boolean isBroadcastEnabled() {
        return isReadyForBroadcast;
    }

    void setReadyForBroadcast(boolean readyForBroadcast) {
        isReadyForBroadcast = readyForBroadcast;
    }

    public Phase getGamePhase() {
        return gamePhase;
    }

    void setGamePhase(Phase gamePhase) {
        this.gamePhase = gamePhase;
    }

    static int max_players() {
        return MAX_PLAYERS;
    }
}
