package com.liver_rus.Battleships.Client;

//TODO убрать свзяь между клиентом и сервером
public abstract class GameEngine {
    //TODO
    //private int numTurn;
    //private int numRound;

    private boolean isFirstTurn;

    public GameEngine() {
        //numTurn = 1;
        //numRound = 1;
        isFirstTurn = true;
    }

    public boolean isFirstTurn() {
        return isFirstTurn;
    }

    public void setFirstTurn(boolean firstTurn) {
        isFirstTurn = firstTurn;
    }
}
