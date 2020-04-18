package com.liver_rus.Battleships.Network.Server.GamePrimitives;

public class TryingAddTooManyShipsOnFieldException extends Throwable {

    public TryingAddTooManyShipsOnFieldException() {
    }

    public String toString() {
        return "Trying add to many ships. Max fleet size = " + Fleet.getNumMaxShip();
    }
}
