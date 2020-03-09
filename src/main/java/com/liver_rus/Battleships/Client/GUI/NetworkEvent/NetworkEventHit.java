package com.liver_rus.Battleships.Client.GUI.NetworkEvent;

public class NetworkEventHit implements NetworkEvent, XYGettable  {
    final int x;
    final int y;

    NetworkEventHit(int x, int y) {
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
