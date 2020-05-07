package com.liver_rus.Battleships.Network.Server.GamePrimitives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fleet {
    private int[] ships;
    //amount ships by type <------------>  {4, 3, 2, 1, 0} type 4 has possible locate 2 ship, etc.
    private final static int[] initShipsByType = {2, 2, 1, 1, 1};
    private int leftForDeployment;
    private int leftAlive;
    private final List<Ship> shipsList;

    public static final int NUM_MAX_SHIPS = Arrays.stream(initShipsByType).sum();
    public static final int NUM_TYPE = initShipsByType.length;

    public Fleet() {
        this.shipsList = new ArrayList<>();
        this.ships = shipsBuilder();
        this.leftForDeployment = NUM_MAX_SHIPS;
        this.leftAlive = 0;
    }

    public void add(Ship ship) {
        shipsList.add(ship);
        int type = ship.getType();
        if (ships[type] > 0) {
            ships[type]--;
        }
        leftAlive++;
        leftForDeployment--;
    }

    public List<Ship> getAllShipsOnField() {
        return shipsList;
    }

    public Ship findAliveShip(int x, int y) {
        for (Ship ship : shipsList) {
            for (FieldCoord coord : ship.getShipCoords()) {
                if (coord.getX() == x && coord.getY() == y) {
                    if (ship.isAlive()) {
                        return ship;
                    }
                }
            }
        }
        return null;
    }

    public int getShipsLeftForDeploy() {
        return leftForDeployment;
    }

    public int getLeftAlive() {
        return leftAlive;
    }

    public void clear() {
        this.shipsList.clear();
        this.ships = shipsBuilder();
        this.leftForDeployment = NUM_MAX_SHIPS;
        this.leftAlive = 0;
    }

    public int[] getShipsLeftByType() {
        return ships;
    }

    public List<Ship> getShips() {
        return shipsList;
    }

    private static int[] shipsBuilder() {
        return Arrays.copyOf(initShipsByType, initShipsByType.length);
    }

    public static int[] initShipsLeftByType() {
        return initShipsByType;
    }

    public static int getNumMaxShip() {
        return NUM_MAX_SHIPS;
    }

    public void updateAlive(Ship ship) {
        boolean isAlive = false;
        for (FieldCoord shipCell : ship.getShipCoords()) {
            if (!shipCell.getTag()) {
                isAlive = true;
            }
        }
        if (!isAlive) {
            leftAlive--;
        }
        ship.setAlive(isAlive);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Ship ship : shipsList) {
            result.append(ship).append("|");
        }
        return result.toString() + "\n" +
                "leftForDeployment=" + leftForDeployment + "\n" +
                "leftAlive=" + leftAlive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fleet fleet = (Fleet) o;

        if (leftForDeployment != fleet.leftForDeployment) return false;
        if (!Arrays.equals(ships, fleet.ships)) return false;
        return shipsList.equals(fleet.shipsList);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(ships);
        result = 31 * result + leftForDeployment;
        result = 31 * result + shipsList.hashCode();
        return result;
    }

}
