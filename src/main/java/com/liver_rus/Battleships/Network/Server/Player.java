package com.liver_rus.Battleships.Network.Server;

import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;

import java.nio.channels.SocketChannel;

public class Player {
    private SocketChannel channel;
    private final GameField gameField;
    private boolean readyForDeployment;
    private boolean readyForGame;
    private boolean wantRematch;
    private String name;

    public Player(SocketChannel channel, GameField gameField) {
        this.channel = channel;
        this.gameField = gameField;
        this.name = "Player";
        readyForDeployment = false;
        readyForGame = false;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public GameField getGameField() {
        return gameField;
    }

    public boolean isReadyForDeployment() {
        return readyForDeployment;
    }

    public void setReadyForDeployment(boolean readyForDeployment) {
        this.readyForDeployment = readyForDeployment;
    }

    public boolean isReadyForGame() {
        return readyForGame;
    }

    public void setReadyForGame(boolean readyForGame) {
        if (readyForGame) {
            readyForDeployment = false;
        }
        this.readyForGame = readyForGame;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWantRematch() {
        return wantRematch;
    }

    public void setWantRematch(boolean wantRematch) {
        this.wantRematch = wantRematch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (readyForGame != player.readyForGame) return false;
        if (!channel.equals(player.channel)) return false;
        if (!gameField.equals(player.gameField)) return false;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        int result = channel.hashCode();
        result = 31 * result + gameField.hashCode();
        result = 31 * result + (readyForGame ? 1 : 0);
        result = 31 * result + name.hashCode();
        return result;
    }
}