package com.liver_rus.Battleships.Client;

import java.util.ArrayList;
import java.util.List;

class ShipsOnField {
    private List<Ship> shipsOnField = new ArrayList<>(ArrangeFleetHolder.TOTAL_SHIPS_AMOUNT);

    void clear() {
        shipsOnField.clear();
    }

    void remove(Ship ship) {
        shipsOnField.remove(ship);
    }

    void add(Ship ship) {
        shipsOnField.add(ship);
    }

    List<Ship> getShipsOnField() {
        return shipsOnField;
    }

    Ship findShip(FieldCoord shipCoord) {
        Ship findedShip = null;
        label:
        {
            for (Ship ship : shipsOnField) {
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
