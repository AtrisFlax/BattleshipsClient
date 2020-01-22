package com.liver_rus.Battleships.Client.GamePrimitives;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GUI.CurrentGUIState;

import java.io.IOException;

/**
 * Класс Ship содержащий инормацию о расположении коробля(коррдинатах, ориентации и типе) в игровом поле GameField
 */

public class Ship {
    private FieldCoord[] shipCoord;
    private boolean isHorizontalOrientation;
    private Type type;

    public static Ship createShip(FieldCoord fieldCoord, Ship.Type shipType, boolean isHorizontal) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        int type = Ship.Type.shipTypeToInt(shipType);
        return new Ship(x, y, type, isHorizontal);
    }

    public static Ship createShip(String shipInfo) throws IOException, WrongShipInfoSizeException {
        if (shipInfo.length() == Constants.ShipInfoLength) {
            int x = Character.getNumericValue(shipInfo.charAt(0));
            int y = Character.getNumericValue(shipInfo.charAt(1));
            int type = Character.getNumericValue(shipInfo.charAt(2));
            boolean isHorizontal = charToIsHorizontal(shipInfo.charAt(3));
            return new Ship(x, y, type, isHorizontal);
        } else {
            throw new WrongShipInfoSizeException(shipInfo);
        }
    }

    public static Ship createShip(CurrentGUIState currentGUIState) {
        return createShip(
                currentGUIState.getFieldCoord(),
                currentGUIState.getShipType(),
                currentGUIState.isHorizontalOrientation()
        );
    }

    public static Ship[] createShips(String[] shipsInfo) throws IOException, WrongShipInfoSizeException {
        if (shipsInfo.length != Fleet.getNumMaxShip()) {
            throw new IllegalArgumentException("Not enough symbols in ship. Can't create fleet");
        } else {
            Ship[] ships = new Ship[shipsInfo.length];
            for (int i = 0; i < shipsInfo.length; i++) {
                ships[i] = Ship.createShip(shipsInfo[i]);
            }
            return ships;
        }
    }

    private Ship(int x, int y, int shipType, boolean isHorizontal) {
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

        public static int shipTypeToInt(Type type) {
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
            }
        }
    }

    FieldCoord[] getShipCoords() {
        return shipCoord;
    }

    public FieldCoord getShipStartCoord() {
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
        return Integer.toString((shipCoord[0].getX())) +
                (shipCoord[0].getY()) +
                Type.shipTypeToInt(type) +
                orientationToChar(isHorizontalOrientation);
    }

    public boolean isHorizontal() {
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
        System.out.print("x=" + getShipStartCoord().getX() + " y=" + getShipStartCoord().getY() + " ");
        System.out.print(type + " ");
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
