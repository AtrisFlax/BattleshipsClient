package com.liver_rus.Battleships.Client;

/**
 * Класс Ships отвечающий за оставшиеся корабли
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

    ///возвращает количество кораблей

    byte getShipsLeft() {
        return left;
    }

    //извлекает корабль определенного типа из пула кораблей*/

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