package com.liver_rus.Battleships.Client;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class Fleet {
    private FleetCounter fleetCounter;
    private LinkedHashSet<Ship> shipsList;

    Fleet() {
        shipsList = new LinkedHashSet<>(FleetCounter.NUM_MAX_SHIPS);
        fleetCounter = new FleetCounter();
    }

    void clear() {
        shipsList.clear();
    }

    void remove(Ship ship) {
        shipsList.remove(ship);
    }

    public void add(Ship ship) {
        if (shipsList.size() < FleetCounter.NUM_MAX_SHIPS) {
            shipsList.add(ship);
        } else {
            throw new ArrayIndexOutOfBoundsException("Trying to add too many ships on field");
        }
    }

    HashSet<Ship> getShipsOnField() {
        return shipsList;
    }

    public Ship findShip(FieldCoord shipCoord) {
        Ship findedShip = null;
        label:
        {
            for (Ship ship : shipsList) {
                for (FieldCoord coord : ship.getShipCoords()) {
                    if (coord.getX() == shipCoord.getX() && coord.getY() == shipCoord.getY()) {
                        findedShip = ship;
                        break label;
                    }
                }
            }
        }
        return findedShip;
    }

    int getShipsLeft() {
        return fleetCounter.getShipsLeft();
    }

    int popShip(Ship.Type shipType) {
        return fleetCounter.popShip(shipType);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Ship ship : shipsList) {
            result.append(ship).append(Constants.NetworkMessage.SPLIT_SYMBOL.getTypeValue());
        }
        return result.toString();
    }

    boolean isEmpty() {
        return shipsList.isEmpty();
    }
}
