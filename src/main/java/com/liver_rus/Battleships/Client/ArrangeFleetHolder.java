package com.liver_rus.Battleships.Client;

/**
 * Класс Ships отвечающий за подсчет количества оставшихся кораблей
 */

class ArrangeFleetHolder {
    static final int TOTAL_SHIPS_AMOUNT = 2 + 2 + 1 + 1 + 1;

    private int[] ships;

    private byte left = TOTAL_SHIPS_AMOUNT;

    ArrangeFleetHolder() {
        reset();
    }

    private void reset() {
        left = TOTAL_SHIPS_AMOUNT;
        ships = new int[TOTAL_SHIPS_AMOUNT];
        ships[4] = 1;
        ships[3] = 1;
        ships[2] = 1;
        ships[1] = 2;
        ships[0] = 2;
    }

    byte getShipsLeft() {
        return left;
    }

    int popShip(int type) {
        if (ships[type] > 0) {
            --left;
        }
        if (ships[type] >= 0) {
            return --ships[type];
        } else {
            return ships[type];
        }
    }
}