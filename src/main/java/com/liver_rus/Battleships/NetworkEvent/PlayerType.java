package com.liver_rus.Battleships.NetworkEvent;

public enum PlayerType  {
    YOU(NetworkCommandConstant.YOU),
    ENEMY(NetworkCommandConstant.ENEMY);

    private final String player;

    PlayerType(String envUrl) {
        this.player = envUrl;
    }

    public String getString() {
        return player;
    }
}