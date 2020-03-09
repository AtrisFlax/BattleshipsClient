package com.liver_rus.Battleships.Client.GamePrimitives;

import com.liver_rus.Battleships.Client.GUI.GUIState;

import java.util.Arrays;

/**
 * Класс игрового поле с фикированным размером размером 10x10
 */


//TODO ClientGameEngine удалить
    //TODO GameField хранится только локально
public class GameField {
    public static final int FIELD_SIZE = 10;

    private Cell[][] field;
    private Fleet fleet;

    private enum Cell {
        CLEAR, MISS, SHIP, NEAR_WITH_SHIP, DAMAGED_SHIP
    }

    public GameField() {
        fleet = new Fleet();
        field = createField();
    }

    public void reset() {
        fleet = new Fleet();
        field = new Cell[FIELD_SIZE][FIELD_SIZE];
    }

    public void addShip(Ship ship) {
        markFieldCellsByShip(ship);
        try {
            getFleet().add(ship);
        } catch (TryingAddTooManyShipsOnFieldException e) {
            e.printStackTrace();
        }
    }

    public Fleet getFleet() {
        return fleet;
    }

    public static int[] getShipLeftByTypeInit() {
        return Fleet.initShipsLeftByType();
    }

    //Отметка клеток корабля и ближлежайших клеток
    public void markFieldCellsByShip(Ship ship) {
        FieldCoord shipCoord = ship.getShipStartCoord();
        int x = shipCoord.getX();
        int y = shipCoord.getY();
        int type = Ship.Type.shipTypeToInt(ship.getType());
        boolean isHorizontal = ship.isHorizontal();

        markShipCells(x, y, type, isHorizontal);
        if (isHorizontal) {
            setCellAsNearWithShip(x - 1, y);
            setCellAsNearWithShip(x + type + 1, y);
            for (int i = x - 1; i <= x + type + 1; i++) {
                setCellAsNearWithShip(i, y + 1);
                setCellAsNearWithShip(i, y - 1);
            }
        } else {
            setCellAsNearWithShip(x, y - 1);
            setCellAsNearWithShip(x, y + type + 1);
            for (int i = y - 1; i <= y + type + 1; i++) {
                setCellAsNearWithShip(x + 1, i);
                setCellAsNearWithShip(x - 1, i);
            }
        }
    }

    //Возвращает ture, если все корабли уничтожены(игра закончена)
    public boolean allShipsDestroyed() {
        for (Ship ship : fleet.getShipsOnField()) {
            if (ship.isAlive()) {
                return false;
            }
        }
        return true;
    }

    public void shoot(int x, int y) {
        markField(x, y);
        markShip(x, y);
    }

    private void markShip(int x, int y) {
        Ship ship = fleet.findShip(x, y);
        if (isCellDamaged(x, y)) {
            ship.tagShipCell(x, y);
        }
        if (ship!= null && !ship.isAlive()) {
            fleet.remove(ship);
        }
    }

    private void markField(int x, int y) {
        if (field[x][y] == Cell.SHIP) {
            tagShipsByCoord(x, y);
            field[x][y] = Cell.DAMAGED_SHIP;
        } else {
            field[x][y] = Cell.MISS;
        }
    }

    public boolean isCellDamaged(int x, int y) {
        return field[x][y] == Cell.DAMAGED_SHIP;
    }

    public boolean isNotIntersetionWithShips(GUIState currentGUIState) {
        Ship.Type shipType = currentGUIState.getShipType();
        boolean isHorizontal = currentGUIState.isHorizontalOrientation();
        int x = currentGUIState.getX();
        int y = currentGUIState.getY();
        int shipTypeInt = Ship.Type.shipTypeToInt(shipType);
        if (isHorizontal) {
            for (int i = x; i < x + shipTypeInt; i++) {
                if (field[i][y] == Cell.SHIP || field[i][y] == Cell.NEAR_WITH_SHIP) {
                    return false;

                }
            }
        } else {
            for (int i = y; i < y + shipTypeInt; i++) {
                if (field[x][i] == Cell.SHIP || field[x][i] == Cell.NEAR_WITH_SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isNotIntersectionShipWithBorder(GUIState currentGUIState) {
        int x = currentGUIState.getX();
        int y = currentGUIState.getY();
        if (x < 0 || x >= FIELD_SIZE) return false;
        if (y < 0 || y >= FIELD_SIZE) return false;
        int shipTypeInt = Ship.Type.shipTypeToInt(currentGUIState.getShipType());
        boolean isHorizontal = currentGUIState.isHorizontalOrientation();
        if (isHorizontal) {
            return x + shipTypeInt < FIELD_SIZE;
        } else {
            return y + shipTypeInt < FIELD_SIZE;
        }
    }

    public void printOnConsole() {
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (field[j][i] == Cell.CLEAR) {
                    System.out.print("   ");
                    continue;
                }
                if (field[j][i] == Cell.MISS) {
                    System.out.print(" o ");
                    continue;
                }
                if (field[j][i] == Cell.SHIP) {
                    System.out.print(" + ");
                    continue;
                }
                if (field[j][i] == Cell.NEAR_WITH_SHIP) {
                    System.out.print(" * ");
                    continue;
                }
                if (field[j][i] == Cell.DAMAGED_SHIP) {
                    System.out.print(" x ");
                }
            }
            System.out.println();
        }
        getFleet().printOnConsole();
    }

    private static Cell[][] createField() {
        Cell[][] gameField = new Cell[FIELD_SIZE][FIELD_SIZE];
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                gameField[j][i] = Cell.CLEAR;
            }
        }
        return gameField;
    }


    private void setCellAsShip(int x, int y) {
        field[x][y] = Cell.SHIP;
    }

    private void setCellAsNearWithShip(int x, int y) {
        if (x >= 0 && x < FIELD_SIZE && y >= 0 && y < FIELD_SIZE) {
            field[x][y] = Cell.NEAR_WITH_SHIP;
        }
    }

    private void markShipCells(int x, int y, int shipType, boolean isHorizontal) {
        if (isHorizontal) {
            for (int i = 0; i < shipType + 1; i++) {
                setCellAsShip(x + i, y);
            }
        }
        else {
            for (int i = 0; i < shipType + 1; i++) {
                setCellAsShip(x, y + i);
            }
        }
    }

    //Отметка попадания в корабль
    private void tagShipsByCoord(int x, int y) {
        for (Ship ship : fleet.getShipsOnField()) {
            for (FieldCoord shipCoord : ship.getShipCoords()) {
                if (shipCoord.getX() == x && shipCoord.getY() == y) {
                    shipCoord.setTag();
                    return;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameField gameField = (GameField) o;

        if (!Arrays.deepEquals(field, gameField.field)) return false;
        return fleet.equals(gameField.fleet);
    }

    @Override
    public int hashCode() {
        int result = Arrays.deepHashCode(field);
        result = 31 * result + fleet.hashCode();
        return result;
    }
}
