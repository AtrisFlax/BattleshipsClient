package com.liver_rus.Battleships.Client;

import java.io.IOException;

/**
 * Класс Ship содержащий инормацию о расположении коробля(коррдинатах, ориентации и типе) в игровом поле GameField
 */

public class Ship {
    private FieldCoord[] shipCoord;
    private Orientation orientation;
    private Type type;

    public Ship(int x_coord, int y_coord, int shipType, boolean orientation) {
        //shift coord. field(12*12) border
        int x = x_coord + 1;
        int y = y_coord + 1;
        shipCoord = new FieldCoord[shipType + 1];
        //horizontal orientation
        if (orientation) {
            for (int i = 0; i < shipCoord.length; i++) {
                shipCoord[i] = new FieldCoord(x + i, y);
            }
            //vertical orientation
        } else {
            for (int i = 0; i < shipCoord.length; i++) {
                shipCoord[i] = new FieldCoord(x, y + i);
            }
        }
        this.orientation = (orientation) ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
        this.type = Type.shipIntToType(shipType);
    }

    static Ship createShip(FieldCoord fieldCoord, Ship.Type shipType, Ship.Orientation orientation) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        int type = Ship.Type.shipTypeToInt(shipType);
        boolean shipOrientation = Orientation.HORIZONTAL == orientation;
        return new Ship(x, y, type, shipOrientation);
    }

    static Ship createShip(CurrentGUIState currentGUIState) {
        return createShip(currentGUIState.getFieldCoord(), currentGUIState.getShipType(), currentGUIState.getShipOrientation());
    }

    public static Ship createShip(String line) throws IOException {
        int x = Character.getNumericValue(line.charAt(0));
        int y = Character.getNumericValue(line.charAt(1));
        int type = Character.getNumericValue(line.charAt(2));
        boolean shipOrientation = adaptOrientation(line.charAt(3));
        return new Ship(x, y, type, shipOrientation);
    }

    private static boolean adaptOrientation(char c) throws IOException {
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

    public enum Orientation {
        HORIZONTAL, VERTICAL;

        @Override
        public String toString() {
            if (this == Orientation.HORIZONTAL) {
                return "H";
            } else {
                return "V";
            }
        }

        public boolean getBoolean() {
            return this == Orientation.HORIZONTAL;
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

        static int shipTypeToInt(Type type) {
            if (type == AIRCRAFT_CARRIER) return 4;
            if (type == BATTLESHIP) return 3;
            if (type == CRUISER) return 2;
            if (type == DESTROYER) return 1;
            if (type == SUBMARINE) return 0;
            return -1;
        }

        static Type shipIntToType(int intType) {
            if (intType == 4) return AIRCRAFT_CARRIER;
            if (intType == 3) return BATTLESHIP;
            if (intType == 2) return CRUISER;
            if (intType == 1) return DESTROYER;
            if (intType == 0) return SUBMARINE;
            return UNKNOWN;
        }

        static Type shipStrToType(String str) {
            int intType = Integer.valueOf(str);
            return shipIntToType(intType);
        }
    }

    FieldCoord[] getShipCoords() {
        return shipCoord;
    }

    FieldCoord getShipStartCoord() {
        return shipCoord[0];
    }

    public void tagShipCell(FieldCoord coord) {
        if (orientation == Orientation.HORIZONTAL) {
            this.shipCoord[coord.getX() - shipCoord[0].getX()].setTag();
        } else {
            this.shipCoord[coord.getY() - shipCoord[0].getY()].setTag();
        }
    }

    public boolean isAlive() {
        boolean isAlive = false;
        for (FieldCoord shipSector : shipCoord) {
            if (!shipSector.getTag()) {
                isAlive = true;
                break;
            }
        }
        return isAlive;
    }

    @Override
    public String toString() {
        return Integer.toString((shipCoord[0].getX() - 1)) + (shipCoord[0].getY() - 1) + Type.shipTypeToInt(type) + orientation;
    }

    Orientation getOrientation() {
        return orientation;
    }

    public Ship.Type getType() {
        return type;
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
