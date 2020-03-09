package com.liver_rus.Battleships.Client.GamePrimitives;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GUI.GUIState;

import java.io.IOException;
import java.util.Arrays;

/**
 * Класс Ship содержащий инормацию о расположении коробля(коррдинатах, ориентации и типе) в игровом поле GameField
 */

public class Ship {
    private FieldCoord[] shipCoord;
    private boolean isHorizontalOrientation;
    private Type type;

    public static Ship create(int x, int y, Ship.Type shipType, boolean isHorizontal) {
        int type = Ship.Type.shipTypeToInt(shipType);
        return new Ship(x, y, type, isHorizontal);
    }

    //shipInfo format - XYZO
    //XY - x,y coordiate from 0 to 9
    //Z - type from 0 to 4
    //O - letter 'H' or 'V'
    public static Ship create(String shipInfo) throws IOException, WrongShipInfoSizeException {
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

    public static Ship create(GUIState state) {
        return create(state.getX(), state.getY(), state.getShipType(), state.isHorizontalOrientation());
    }

    public static Ship[] createShips(String[] shipsInfo) throws IOException, WrongShipInfoSizeException {
        if (shipsInfo.length != Fleet.getNumMaxShip()) {
            throw new IllegalArgumentException("Not enough symbols in ship. Can't create fleet");
        } else {
            Ship[] ships = new Ship[shipsInfo.length];
            for (int i = 0; i < shipsInfo.length; i++) {
                ships[i] = Ship.create(shipsInfo[i]);
            }
            return ships;
        }
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

        public static Type shipIntToType(int intType) {
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

    public void tagShipCell(int x, int y) {
        if (isHorizontalOrientation) {
            this.shipCoord[x - shipCoord[0].getX()].setTag();
        } else {
            this.shipCoord[y - shipCoord[0].getY()].setTag();
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

    private Ship(int x, int y, int shipType, boolean isHorizontal) {
        checkBounce(x, y, shipType, isHorizontal);

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

    private void checkBounce(int x, int y, int shipType, boolean isHorizontal) {
        if (isHorizontal) {
            if (x + shipType >= GameField.FIELD_SIZE)
                throw new IllegalArgumentException("Trying create ship out of field bounce");
        } else {
            if (y + shipType >= GameField.FIELD_SIZE)
                throw new IllegalArgumentException("Trying create ship out of field bounce");
        }
    }

    public static boolean charToIsHorizontal(char c) throws IOException {
        if (c == 'H') {
            return true;
        } else {
            if (c == 'V') {
                return false;
            } else {
                throw new IOException("Wrong Char in orientation ship string definition!");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ship ship = (Ship) o;

        if (isHorizontalOrientation != ship.isHorizontalOrientation) return false;
        if (!Arrays.equals(shipCoord, ship.shipCoord)) return false;
        return type == ship.type;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(shipCoord);
        result = 31 * result + (isHorizontalOrientation ? 1 : 0);
        result = 31 * result + type.hashCode();
        return result;
    }
}

