package com.liver_rus.Battleships.Client.GamePrimitives;

public class TryingAddTooManyShipsOnFieldException extends Throwable {
    int size;

    public TryingAddTooManyShipsOnFieldException(int size) {
        this.size = size;
    }

    public String toString() {
        return "Trying add to many ships. Max fleet size = " + Fleet.getNumMaxShip() + " Trying add " + size;
    }
}
