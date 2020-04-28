package com.liver_rus.Battleships.Network.NetworkEvent;

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