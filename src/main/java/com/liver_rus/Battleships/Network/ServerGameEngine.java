package com.liver_rus.Battleships.Network;

class ServerGameEngine {
    private static final int MAX_PLAYERS = 2;
    private Phase gamePhase;
    private boolean isReadyForBroadcast = false;

    private boolean isFirstTurn;

    enum Phase {
        INIT, END_GAME;
    }

    ServerGameEngine() {
        setGamePhase(Phase.INIT);
        isFirstTurn = true;
    }

    boolean isBroadcastEnabled() {
        return isReadyForBroadcast;
    }

    void setReadyForBroadcast(boolean readyForBroadcast) {
        isReadyForBroadcast = readyForBroadcast;
    }

    final public Phase getGamePhase() {
        return gamePhase;
    }

    void setGamePhase(Phase gamePhase) {
        this.gamePhase = gamePhase;
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
}