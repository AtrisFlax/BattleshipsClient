package com.liver_rus.Battleships.Network.Server;

public class GamePreferences {
    private boolean adjacentShips;
    private boolean salvoMode;

    public GamePreferences(boolean adjacentShips, boolean salvoMode) {
        this.adjacentShips = adjacentShips;
        this.salvoMode = salvoMode;
    }

    public boolean isAdjacentShips() {
        return adjacentShips;
    }

    public void setAdjacentShips(boolean adjacentShips) {
        this.adjacentShips = adjacentShips;
    }

    public boolean isSalvoMode() {
        return salvoMode;
    }

    public void setSalvoMode(boolean salvoMode) {
        this.salvoMode = salvoMode;
    }
}
