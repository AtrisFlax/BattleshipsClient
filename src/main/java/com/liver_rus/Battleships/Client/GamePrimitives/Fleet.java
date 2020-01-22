package com.liver_rus.Battleships.Client.GamePrimitives;

import com.liver_rus.Battleships.Client.Constants.Constants;

import java.util.Arrays;
import java.util.LinkedList;

public class Fleet {
    static private final int NUM_MAX_SHIPS = Arrays.stream(shipsBuilder()).sum();

    private int[] ships;
    private int left;

    private LinkedList<Ship> shipsList;

    public Fleet() {
        shipsList = new LinkedList<>();
        ships = shipsBuilder();
        left = NUM_MAX_SHIPS;
    }

    public void add(Ship ship) throws TryingAddToManyShipsOnFieldException {
        if (shipsList.size() < NUM_MAX_SHIPS) {
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
            result.append(ship).append(Constants.NetworkMessage.SPLIT_SYMBOL);
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

    //amount ships by type
    private static int[] shipsBuilder() {
        return new int[]{2, 2, 1, 1, 1};
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

}
