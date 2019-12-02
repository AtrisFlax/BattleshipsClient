package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.Constants.Constants;

import java.io.IOException;

/**
 * Класс Ship содержащий инормацию о расположении коробля(коррдинатах, ориентации и типе) в игровом поле GameField
 */

public class Ship {
    private FieldCoord[] shipCoord;
    private boolean isHorizontalOrientation;
    private Type type;

    static Ship createShip(FieldCoord fieldCoord, Ship.Type shipType, boolean isHorizontal) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        int type = Ship.Type.shipTypeToInt(shipType);
        return new Ship(x, y, type, isHorizontal);
    }

    static Ship createShip(String shipInfo) throws IOException {
        if (shipInfo.length() == Constants.ShipInfoLength) {
            int x = Character.getNumericValue(shipInfo.charAt(0));
            int y = Character.getNumericValue(shipInfo.charAt(1));
            int type = Character.getNumericValue(shipInfo.charAt(2));
            boolean isHorizontal = charToIsHorizontal(shipInfo.charAt(3));
            return new Ship(x, y, type, isHorizontal);
        } else {
            //TODO custom exception
            throw new IOException("Not enough symbols in ship. Can't create ship");
        }
    }

    static Ship createShip(CurrentGUIState currentGUIState) {
        return createShip(
                currentGUIState.getFieldCoord(),
                currentGUIState.getShipType(),
                currentGUIState.isHorizontalOrientation()
        );
    }

    public static Ship[] createShips(String[] shipsInfo) throws IOException {
        if (shipsInfo.length != FleetCounter.getNumMaxShip()) {
            throw new IllegalArgumentException("Not enough symbols in ship. Can't create fleet");
        } else {
            Ship[] ships = new Ship[shipsInfo.length];
            for (int i = 0; i < shipsInfo.length; i++) {
                ships[i] = Ship.createShip(shipsInfo[i]);
            }
            return ships;
        }
    }

    //TODO make private inner realization
    public Ship(int x_coord, int y_coord, int shipType, boolean isHorizontal) {
        //shift coord. field(12*12) border
        int x = x_coord + 1;
        int y = y_coord + 1;
        shipCoord = new FieldCoord[shipType + 1];
        if (isHorizontal) {
            for (int i = 0; i < shipCoord.length; i++) {
                shipCoord[i] = new FieldCoord(x + i, y);
            }
        } else {
            for (int i = 0; i < shipCoord.length; i++) {
                shipCoord[i] = new FieldCoord(x, y + i);
            }
        }
        isHorizontalOrientation = isHorizontal;
        type = Type.shipIntToType(shipType);
    }

    //TODO метод длины корабля в зависимости от типа

    //TODO сделана двойная работа
    //
    public enum Type {
        AIRCRAFT_CARRIER(4),
        BATTLESHIP(3),
        CRUISER(2),
        DESTROYER(1),
        SUBMARINE(0),
        UNKNOWN(-1);


        private final int value;

        Type(int value) {
            this.value = value;
        }

        static int shipTypeToInt(Type type) {
            return type.value;
        }

        static Type shipIntToType(int intType) {
            switch (intType) {
                case 4:
                    return AIRCRAFT_CARRIER;
                case 3:
                    return BATTLESHIP;
                case 2:
                    return CRUISER;
                case 1:
                    return DESTROYER;
                case 0:
                    return SUBMARINE;
                default:
                    return UNKNOWN;
                //TODO generate exception
            }
        }
    }

    FieldCoord[] getShipCoords() {
        return shipCoord;
    }

    FieldCoord getShipStartCoord() {
        return shipCoord[0];
    }

    public void tagShipCell(FieldCoord coord) {
        if (isHorizontalOrientation) {
            this.shipCoord[coord.getX() - shipCoord[0].getX()].setTag();
        } else {
            this.shipCoord[coord.getY() - shipCoord[0].getY()].setTag();
        }
    }

    public boolean isAlive() {
        for (FieldCoord shipSector : shipCoord) {
            if (!shipSector.getTag()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return Integer.toString((shipCoord[0].getX() - 1)) +
                (shipCoord[0].getY() - 1) +
                Type.shipTypeToInt(type) +
                orientationToChar(isHorizontalOrientation);
    }

    boolean isHorizontal() {
        return isHorizontalOrientation;
    }

    public Ship.Type getType() {
        return type;
    }

    private static boolean charToIsHorizontal(char c) throws IOException {
        if (c == 'H') {
            return true;
        } else {
            if (c == 'V') {
                return false;
            } else {
                throw new IOException();
            }
        }
    }

    private static char orientationToChar(boolean isHorizontal) {
        if (isHorizontal) {
            return 'H';
        } else {
            return 'V';
        }
    }

    void printOnConsole() {
        System.out.println(type);
        for (FieldCoord cell : shipCoord) {
            if (!cell.getTag()) {
                System.out.print("|#|");
            } else {
                System.out.print("|X|");
            }
            System.out.print(" ");
        }
        System.out.println();
    }
}
