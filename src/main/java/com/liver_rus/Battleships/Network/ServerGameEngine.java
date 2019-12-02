package com.liver_rus.Battleships.Network;

class ServerGameEngine {
    private static final int MAX_PLAYERS = 2;
    private Phase gamePhase;
    private boolean isReadyForBroadcast = false;

    private int numTurn;
    private boolean isFirstTurn;
    private int numRound;

    enum Phase {
        INIT, END_GAME;
    }

    ServerGameEngine() {
        setGamePhase(Phase.INIT);
        numTurn = 1;
        isFirstTurn = true;
        numRound = 1;
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