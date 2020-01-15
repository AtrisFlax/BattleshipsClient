package com.liver_rus.Battleships.Client.GamePrimitives;

import com.liver_rus.Battleships.Client.Constants.Constants;

import java.util.LinkedList;

public class Fleet {
    private FleetCounter fleetCounter;
    private LinkedList<Ship> shipsList;

    public Fleet() {
        shipsList = new LinkedList<>();
        fleetCounter = new FleetCounter();
    }

    public void add(Ship ship) throws TryingAddToManyShipsOnFieldException {
        if (shipsList.size() < FleetCounter.getNumMaxShip()) {
            shipsList.add(ship);
        } else {
            throw new TryingAddToManyShipsOnFieldException(shipsList.size());
        }
    }

    public LinkedList<Ship> getShipsOnField() {
        return shipsList;
    }

    public Ship findShip(FieldCoord shipCoord) {
        for (Ship ship : shipsList) {
            for (FieldCoord coord : ship.getShipCoords()) {
                if (coord.getX() == shipCoord.getX() && coord.getY() == shipCoord.getY()) {
                    return ship;
                }
            }
        }
        return null;
    }

    public int getShipsLeft() {
        return fleetCounter.getShipsLeft();
    }

    public int popShip(Ship.Type shipType) {
        return fleetCounter.popShip(shipType);
    }

    public void clear() {
        fleetCounter = new FleetCounter();
        shipsList.clear();
    }

    public void remove(Ship ship) {
        shipsList.remove(ship);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Ship ship : shipsList) {
            result.append(ship).append(Constants.NetworkMessage.SPLIT_SYMBOL);
        }
        return result.toString();
    }

    public boolean isEmpty() {
        return shipsList.isEmpty();
    }

    public void printOnConsole() {
        for (Ship ship : shipsList) {
            ship.printOnConsole();
        }
    }

    public FleetCounter getFleetCounter() {
        return fleetCounter;
    }
}
