package com.liver_rus.Battleships.Client;

import java.util.HashSet;
import java.util.LinkedHashSet;

class ShipsOnField  {
    private LinkedHashSet<Ship> shipsList = new LinkedHashSet<>(ArrangeFleetHolder.TOTAL_SHIPS_AMOUNT);

    void clear() {
        shipsList.clear();
    }

    void remove(Ship ship) {
        shipsList.remove(ship);
    }

    void add(Ship ship) {
        shipsList.add(ship);
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
}
