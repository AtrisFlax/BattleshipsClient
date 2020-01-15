package com.liver_rus.Battleships.Client.GamePrimitives;

public class TryingAddToManyShipsOnFieldException extends Throwable {
    int fleetSize;

    public TryingAddToManyShipsOnFieldException(int fleetSize) {
        this.fleetSize = fleetSize;
    }

    public String toString() {
        return "Trying add to many ships. Max fleet size = " + FleetCounter.getNumMaxShip();
    }
}
