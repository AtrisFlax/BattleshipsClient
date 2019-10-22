package com.liver_rus.Battleships.Client;

import java.util.HashSet;
import java.util.LinkedHashSet;

class ShipsOnField  {
    private FleetCounter fleetCounter;
    private LinkedHashSet<Ship> shipsList;

    ShipsOnField() {
        shipsList = new LinkedHashSet<>(FleetCounter.NUM_MAX_SHIPS);
        fleetCounter = new FleetCounter();
    }

    void clear() {
        shipsList.clear();
    }

    void remove(Ship ship) {
        shipsList.remove(ship);
    }

    void add(Ship ship) {
        if (shipsList.size() < FleetCounter.NUM_MAX_SHIPS) {
            shipsList.add(ship);
        }
        else {
            throw new ArrayIndexOutOfBoundsException("Trying to add too many ships on field");
        }
    }

    HashSet<Ship> getShipsOnField() {
        return shipsList;
    }

    Ship findShip(FieldCoord shipCoord) {
        Ship findedShip = null;
        label: {
            for (Ship ship : shipsList) {
                for (FieldCoord coord : ship.getShipCoord()) {
                    if (coord.getX() - 1 == shipCoord.getX() && coord.getY() == shipCoord.getY()) {
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
}
