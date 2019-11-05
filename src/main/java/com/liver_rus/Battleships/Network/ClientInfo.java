package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Client.GameField;

public class ClientInfo {

    private boolean ready;
    private GameField gameField;

    ClientInfo() {
        ready = false;
        gameField = new GameField();
    }



    public GameField getGameField() {
        return gameField;
    }

    public void setGameField(GameField gameField) {
        this.gameField = gameField;
    }
}





