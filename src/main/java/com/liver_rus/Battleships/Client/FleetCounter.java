package com.liver_rus.Battleships.Client;

import java.util.Arrays;

/**
 * Класс Ships отвечающий за подсчет количества оставшихся кораблей
 */

class FleetCounter {
    static int NUM_MAX_SHIPS;

    private int[] ships;

    private int left;

    FleetCounter() {
        ships =  new int[]{2, 2, 1, 1, 1};
        NUM_MAX_SHIPS = Arrays.stream(ships).sum();
        left = NUM_MAX_SHIPS;
    }

    int getShipsLeft() {
        return left;
    }

    int popShip(int type) {
        if (ships[type] > 0) {
            --left;
            return --ships[type];
        } else {
            return 0;
        }
    }

    int popShip(Ship.Type shipType) {
        int type = Ship.Type.shipTypeToInt(shipType);
        return popShip(type);
    }

}