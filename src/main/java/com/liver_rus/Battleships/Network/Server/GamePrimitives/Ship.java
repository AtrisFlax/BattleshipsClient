package com.liver_rus.Battleships.Network.Server.GamePrimitives;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Network.Server.FieldCell;

import java.io.IOException;
import java.util.Arrays;

/**
 * Класс Ship содержащий инормацию о расположении коробля(коррдинатах, ориентации и типе) в игровом поле GameField
 */

public class Ship {
    private final FieldCoord[] shipCoords;
    private final boolean isHorizontal;
    private final int type;
    private boolean alive;


    //TODO delete comment
    /* type <-> int type
      AIRCRAFT_CARRIER(4),
        BATTLESHIP(3),
        CRUISER(2),
        DESTROYER(1),
        SUBMARINE(0),
        UNKNOWN(-1);
     */
    static Ship create(int x, int y, int type, boolean isHorizontal, GameField field) {
        if (checkField(x, y, type, isHorizontal, field)) {
            return new Ship(x, y, type, isHorizontal, field);
        } else {
            return null;
        }
    }

    //shipInfo format - XYZO
    //XY - x,y coordiate from 0 to 9
    //Z - type from 0 to 4
    //O - letter 'H' or 'V'
    public static Ship create(String shipInfo, GameField field) throws IOException, WrongShipInfoSizeException {
        if (shipInfo.length() == Constants.ShipInfoLength) {
            int x = Character.getNumericValue(shipInfo.charAt(0));
            int y = Character.getNumericValue(shipInfo.charAt(1));
            int type = Character.getNumericValue(shipInfo.charAt(2));
            boolean isHorizontal = charToIsHorizontal(shipInfo.charAt(3));
            return new Ship(x, y, type, isHorizontal, field);
        } else {
            throw new WrongShipInfoSizeException(shipInfo);
        }
    }

//    public static Ship create(GUIState state) {
//        return create(state.getX(), state.getY(), state.getShipType(), state.isHorizontalOrientation());
//    }

    public static Ship[] createShips(String[] shipsInfo, GameField field) throws IOException, WrongShipInfoSizeException {
        if (shipsInfo.length != Fleet.getNumMaxShip()) {
            throw new IllegalArgumentException("Not enough symbols in ship. Can't create fleet");
        } else {
            Ship[] ships = new Ship[shipsInfo.length];
            for (int i = 0; i < shipsInfo.length; i++) {
                ships[i] = Ship.create(shipsInfo[i], field);
            }
            return ships;
        }
    }

    FieldCoord[] getShipCoords() {
        return shipCoords;
    }

    public FieldCoord getShipStartCoord() {
        return shipCoords[0];
    }

    public int getX() {
        return shipCoords[0].getX();
    }

    public int getY() {
        return shipCoords[0].getY();
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public String toString() {
        return Integer.toString((shipCoords[0].getX())) +
                (shipCoords[0].getY()) +
                type +
                orientationToChar(isHorizontal);
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public int getType() {
        return type;
    }

    private Ship(int x, int y, int type, boolean isHorizontal, GameField field) {
        //TODO check shipCoord array with null references
        shipCoords = new FieldCoord[type + 1];
        for (int i = 0; i < type + 1; i++) {
            if (isHorizontal) {
                shipCoords[i] = field.getFieldCell(x + i, y);
            } else {
                shipCoords[i] = field.getFieldCell(x, y + i);
            }
            shipCoords[i].setType(FieldCell.SHIP);
        }
        this.isHorizontal = isHorizontal;
        this.type = type;
        this.alive = true;
    }

    private static boolean checkField(int x, int y, int shipType, boolean isHorizontal, GameField field) {
        return field.isNotIntersectionShipWithBorder(x, y, shipType, isHorizontal) &&
                field.isNotIntersectionWithShips(x, y, shipType, isHorizontal);
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
        for (FieldCoord cell : shipCoords) {
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

        if (!Arrays.deepEquals(shipCoords, ship.shipCoords)) return false;
        if (isHorizontal != ship.isHorizontal) return false;
        if (type != ship.type) return false;
        return alive == ship.alive;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(shipCoords);
        result = 31 * result + (isHorizontal ? 1 : 0);
        result = 31 * result + type;
        result = 31 * result + (alive ? 1 : 0);
        return result;
    }
}

