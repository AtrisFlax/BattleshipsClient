package com.liver_rus.Battleships.Client.GUI.NetworkEvent;

public class NetworkEventMiss implements NetworkEvent, XYGettable {
    final int x;
    final int y;

    NetworkEventMiss(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
