package com.liver_rus.Battleships.Client.GamePrimitives;

import java.util.Arrays;

/**
 * Класс FleetCounter отвечающий за подсчет количества оставшихся кораблей
 */

public class FleetCounter {
    static private final int NUM_MAX_SHIPS = Arrays.stream(shipsBuilder()).sum();

    private int[] ships;
    private int left;

    public FleetCounter() {
        ships = shipsBuilder();
        left = NUM_MAX_SHIPS;
    }

    public int[] getShipsLeftByType() {
        return ships;
    }

    public int popShip(Ship.Type shipType) {
        int type = Ship.Type.shipTypeToInt(shipType);
        return popShip(type);
    }

    static public int getNumMaxShip() {
        return NUM_MAX_SHIPS;
    }

    public int getShipsLeft() {
        return left;
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
}