package com.liver_rus.Battleships.Client;

import java.io.IOException;

/**
 * Класс Ship содержащий инормацию о расположении коробля(коррдинатах, ориентации и типе) в игровом поле GameField
 */

public class Ship {

    private FieldCoord[] shipCoord;
    private Orientation orientation;
    private int type;
    private String name;

    Ship(FieldCoord fieldCoord, Ship.Type shipType, Ship.Orientation orientation) {
        int x = fieldCoord.getX() + 1;
        int y = fieldCoord.getY() + 1;
        int type = Ship.Type.shipTypeToInt(shipType);
        shipCoord = new FieldCoord[type + 1];
        if (orientation == Ship.Orientation.HORIZONTAL) {
            for (int i = 0; i < shipCoord.length; i++) {
                shipCoord[i] = new FieldCoord(x + i, y);
            }
        } else {
            for (int i = 0; i < shipCoord.length; i++) {
                shipCoord[i] = new FieldCoord(x, y + i);
            }
        }
        this.orientation = orientation;
        this.type = type;

        switch (type) {
            case 0:
                name = "Submarine";
                break;
            case 1:
                name = "Destroyer";
                break;
            case 2:
                name = "Cruiser";
                break;
            case 3:
                name = "Battleship";
                break;
            case 4:
                name = "Aircraft Carrier";
                break;
            default:
                name = "unknown";
                break;
        }
    }

    Ship(CurrentState currentState) {
        this(currentState.getFieldCoord(), currentState.getShipType(), currentState.getShipOrientation());
    }

    public enum Orientation {
        VERTICAL, HORIZONTAL;

        static Orientation strToOrientation(String str) throws IOException {
            if (str.equals("VERTICAL")) {
                return VERTICAL;
            } else {
                if (str.equals("HORIZONTAL")) {
                    return HORIZONTAL;
                } else {
                    throw new IOException();
                }
            }
        }
    }

    enum Type {
        AIRCRAFT_CARRIED(4),
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
            if (type == AIRCRAFT_CARRIED) return 4;
            if (type == BATTLESHIP) return 3;
            if (type == CRUISER) return 2;
            if (type == DESTROYER) return 1;
            if (type == SUBMARINE) return 0;
            return -1;
        }

        static Type shipIntToType(int intType) {
            if (intType == 4) return AIRCRAFT_CARRIED;
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

    FieldCoord[] getShipCoord() {
        return shipCoord;
    }

    void tagShipCell(FieldCoord coord) {
        if (orientation == Orientation.HORIZONTAL) {
            this.shipCoord[coord.getX() - shipCoord[0].getX() + 1].setTag();
        } else {
            this.shipCoord[coord.getY() - shipCoord[0].getY()].setTag();
        }
    }

    boolean isAlive() {
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
        return shipCoord[0].getX() + " " + shipCoord[0].getY() + " " + type + " " + orientation;
    }

    void printShipOnConsole() {
        System.out.println(name);
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
