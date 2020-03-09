package com.liver_rus.Battleships.Client.GamePrimitives;

import com.liver_rus.Battleships.Client.Constants.Constants;

import java.util.Arrays;
import java.util.LinkedList;

public class Fleet {
    private int[] ships;
    //amount ships by type                 {4, 3, 2, 1, 0}
    private final static int[] initShips = {2, 2, 1, 1, 1};

    private static final int NUM_MAX_SHIPS = Arrays.stream(initShips).sum();

    private int left;

    private LinkedList<Ship> shipsList;

    public Fleet() {
        shipsList = new LinkedList<>();
        ships = shipsBuilder();
        left = NUM_MAX_SHIPS;
    }

    public void add(Ship ship) throws TryingAddTooManyShipsOnFieldException {
        if (shipsList.size() < NUM_MAX_SHIPS) {
            shipsList.add(ship);
        } else {
            throw new TryingAddTooManyShipsOnFieldException(shipsList.size());
        }
    }

    public LinkedList<Ship> getShipsOnField() {
        return shipsList;
    }

    public Ship findShip(int x, int y) {
        for (Ship ship : shipsList) {
            for (FieldCoord coord : ship.getShipCoords()) {
                if (coord.getX() == x && coord.getY() == y) {
                    return ship;
                }
            }
        }
        return null;
    }

    public int getShipsLeft() {
        return left;
    }

    public int popShip(Ship.Type shipType) {
        int type = Ship.Type.shipTypeToInt(shipType);
        return popShip(type);
    }

    public void clear() {
        shipsList.clear();
        ships = shipsBuilder();
        left = NUM_MAX_SHIPS;
    }

    public void remove(Ship ship) {
        shipsList.remove(ship);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Ship ship : shipsList) {
            result.append(ship).append(Constants.NetworkCommand.SPLIT_SYMBOL);
        }
        return result.toString();
    }

    public void printOnConsole() {
        for (Ship ship : shipsList) {
            ship.printOnConsole();
        }
    }

    public int[] getShipsLeftByType() {
        return ships;
    }

    public LinkedList<Ship> getShips() {
        return shipsList;
    }


    private static int[] shipsBuilder() {
        return Arrays.copyOf(initShips, initShips.length);
    }

    public static int[] initShipsLeftByType() {
        return initShips;
    }

    /**
     * @param type - ship type for extraction
     * @return left ships by type. If no more ships left then return -1
     */
    //
    private int popShip(int type) {
        if (ships[type] > 0) {
            --left;
            ships[type] = ships[type] - 1;
            return ships[type];
        } else {
            return -1;
        }
    }

    public static int getNumMaxShip() {
        return NUM_MAX_SHIPS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fleet fleet = (Fleet) o;

        if (left != fleet.left) return false;
        if (!Arrays.equals(ships, fleet.ships)) return false;
        return shipsList.equals(fleet.shipsList);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(ships);
        result = 31 * result + left;
        result = 31 * result + shipsList.hashCode();
        return result;
    }
}
