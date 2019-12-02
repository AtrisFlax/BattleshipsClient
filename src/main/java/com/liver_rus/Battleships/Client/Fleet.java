package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.Constants.Constants;

import java.util.LinkedList;

public class Fleet {
    private FleetCounter fleetCounter;
    private LinkedList<Ship> shipsList;

    Fleet() {
        shipsList = new LinkedList<>();
        fleetCounter = new FleetCounter();
    }

    void add(Ship ship) {
        if (shipsList.size() < FleetCounter.getNumMaxShip()) {
            shipsList.add(ship);
        } else {
            //TODO custom exception
            throw new ArrayIndexOutOfBoundsException("Trying to add too many ships on field");
        }
    }

    LinkedList<Ship> getShipsOnField() {
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

    int getShipsLeft() {
        return fleetCounter.getShipsLeft();
    }

    int popShip(Ship.Type shipType) {
        return fleetCounter.popShip(shipType);
    }

    void clear() {
        shipsList.clear();
    }

    void remove(Ship ship) {
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

    boolean isEmpty() {
        return shipsList.isEmpty();
    }
}
