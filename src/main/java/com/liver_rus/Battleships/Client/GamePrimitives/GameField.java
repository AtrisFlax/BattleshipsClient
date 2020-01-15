package com.liver_rus.Battleships.Client.GamePrimitives;

import com.liver_rus.Battleships.Client.GUI.CurrentGUIState;

/**
 * Класс игрового поле с фикированным размером размером 10x10
 */

public class GameField {
    private static final int FIELD_SIZE = 10;
    private Cell[][] field;
    private Fleet fleet;

    private enum Cell {
        CLEAR, MISS, SHIP, NEAR_WITH_SHIP, DAMAGED_SHIP
    }

    public GameField() {
        fleet = new Fleet();
        field = new Cell[FIELD_SIZE][FIELD_SIZE];
        initField();
    }

    public GameField(Ship[] ships) {
        this();
        for (Ship ship : ships) {
            addShip(ship);
        }
    }

    public void addShip(Ship ship) {
        markFieldCellsByShip(ship);
        try {
            getFleet().add(ship);
        } catch (TryingAddToManyShipsOnFieldException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        fleet.clear();
        initField();
    }

    public Fleet getFleet() {
        return fleet;
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
    public void updateShipList() {
        for (Ship ship : fleet.getShipsOnField()) {
            if (!ship.isAlive()) {
                fleet.remove(ship);
                return;
            }
        }
    }

    //Возвращает ture, если все корабли уничтожены(игра закончена)
    public boolean isShipsDestroyed() {
        for (Ship ship : fleet.getShipsOnField()) {
            if (ship.isAlive()) {
                return false;
            }
        }
        return true;
    }

    public void setCellAsDamaged(FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        if (field[x][y] == Cell.SHIP) {
            tagShipsByCoord(x, y);
            field[x][y] = Cell.DAMAGED_SHIP;
        } else {
            field[x][y] = Cell.MISS;
        }
    }

    public boolean isCellDamaged(FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        return field[x][y] == Cell.DAMAGED_SHIP;
    }

    public boolean isPossibleLocateShip(CurrentGUIState currentGUIState) {
        FieldCoord coord = currentGUIState.getFieldCoord();
        Ship.Type shipType = currentGUIState.getShipType();
        boolean isHorizontal = currentGUIState.isHorizontalOrientation();
        boolean isPossibleLocateShipFlag = true;
        int x = coord.getX() + 1;
        int y = coord.getY() + 1;
        int shipTypeInt = Ship.Type.shipTypeToInt(shipType);
        if (isHorizontal) {
            for (int i = x; i < x + shipTypeInt + 1; i++) {
                if (field[i][y] == Cell.SHIP ||
                        field[i][y] == Cell.NEAR_WITH_SHIP) {
                    isPossibleLocateShipFlag = false;
                    break;
                }
            }
        } else {
            for (int i = y; i < y + shipTypeInt + 1; i++) {
                if (field[x][i] == Cell.SHIP ||
                        field[x][i] == Cell.NEAR_WITH_SHIP) {
                    isPossibleLocateShipFlag = false;
                    break;
                }
            }
        }
        return isPossibleLocateShipFlag;
    }

    public boolean isNotIntersectionShipWithBorder(CurrentGUIState currentGUIState) {
        FieldCoord coord = currentGUIState.getFieldCoord();
        Ship.Type shipType = currentGUIState.getShipType();
        boolean isHorizontal = currentGUIState.isHorizontalOrientation();
        int shipTypeInt = Ship.Type.shipTypeToInt(shipType);
        int x = coord.getX();
        int y = coord.getY();
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

    private void setCellAsShip(int x, int y) {
        field[x][y] = Cell.SHIP;
    }

    private void setCellAsNearWithShip(int x, int y) {
        if (x >= 0 && x < FIELD_SIZE && y >= 0 && y < FIELD_SIZE) {
            field[x][y] = Cell.NEAR_WITH_SHIP;
        }
    }

    private void initField(){
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                field[j][i] = Cell.CLEAR;
            }
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
}
