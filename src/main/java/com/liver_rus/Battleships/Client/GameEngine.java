package com.liver_rus.Battleships.Client;

public abstract class GameEngine {
    private int numTurn;
    private boolean isFirstTurn;
    private int numRound;

    public GameEngine() {
        numTurn = 1;
        isFirstTurn = true;
        numRound = 1;
    }

    int getNumRound() {
        return numRound;
    }

    void setNumRound(int numRound) {
        this.numRound = numRound;
    }

    int newRound() {
        return numRound++;
    }

    public boolean isFirstTurn() {
        return isFirstTurn;
    }

    public void setFirstTurn(boolean firstTurn) {
        isFirstTurn = firstTurn;
    }
}
