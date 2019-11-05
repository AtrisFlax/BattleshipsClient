package com.liver_rus.Battleships.Client;

/**
 * Класс игрового поле с фикированным размером размером 10x10
 */

public class GameField {

    ///Поле 10*10 окружено кольцом клеток типа CellStatus.BORDER -> Поле 12*12

    private final static int FIELD_SIZE = 12;

    private Cell[][] field;

    private Fleet fleet;

    private enum Cell {
        CLEAR, MISS, SHIP, NEAR_WITH_SHIP, BORDER, DAMAGED_SHIP
    }

    public boolean isEmpty() {
        return fleet.isEmpty();
    }

    public GameField() {
        fleet = new Fleet();

        field = new Cell[FIELD_SIZE][FIELD_SIZE];
        for (int i = 1; i < FIELD_SIZE - 1; i++) {
            for (int j = 1; j < FIELD_SIZE - 1; j++) {
                field[j][i] = Cell.CLEAR;
            }
        }
        //верхняя часть кольца
        for (int j = 0; j < FIELD_SIZE; j++) {
            field[0][j] = Cell.BORDER;
        }
        //нижняя часть кольца
        for (int j = 0; j < FIELD_SIZE; j++) {
            field[FIELD_SIZE - 1][j] = Cell.BORDER;
        }
        //левая часть кольца
        for (int i = 0; i < FIELD_SIZE; i++) {
            field[i][0] = Cell.BORDER;
        }
        //правая часть кольца
        for (int i = 0; i < FIELD_SIZE; i++) {
            field[i][FIELD_SIZE - 1] = Cell.BORDER;
        }
    }

    public Fleet getFleet() {
        return fleet;
    }

    private void markShipCells(int x, int y, int shipType, boolean shipOrientation) {
        //horizontal
        if (shipOrientation) {
            for (int i = 0; i < shipType + 1; i++) {
                setCellAsShip(x + i, y);
            }
        }
        //vertical
        else {
            for (int i = 0; i < shipType + 1; i++) {
                setCellAsShip(x, y + i);
            }
        }
    }

    /*
     * Отметка клеток корабля и ближлежайших клеток
     */

    public void markFieldByShip(Ship ship) {
        markFieldByShip(ship.getShipStartCoord(), ship.getType(), ship.getOrientation());
    }

    void markFieldByShip(FieldCoord shipCoord, Ship.Type shipType, Ship.Orientation orientation) {
        int x = shipCoord.getX();
        int y = shipCoord.getY();
        int type = Ship.Type.shipTypeToInt(shipType);
        boolean shipOrientation = orientation.getBoolean();
        markFieldByShip(x, y, type, shipOrientation);
    }

    private void markFieldByShip(int x, int y, int type, boolean shipOrientation) {
        markShipCells(x, y, type, shipOrientation);
        //horizontal
        if (shipOrientation) {
            setCellAsNearWithShip(x - 1, y);
            setCellAsNearWithShip(x + type + 1, y);
            for (int i = x - 1; i <= x + type + 1; i++) {
                setCellAsNearWithShip(i, y + 1);
                setCellAsNearWithShip(i, y - 1);
            }
        }
        //vertical
        else {
            setCellAsNearWithShip(x, y - 1);
            setCellAsNearWithShip(x, y + type + 1);
            for (int i = y - 1; i <= y + type + 1; i++) {
                setCellAsNearWithShip(x + 1, i);
                setCellAsNearWithShip(x - 1, i);
            }
        }
    }


    //Отметка попадания в корабль
    private void tagShipsByCoord(int x, int y) {
        FieldCoord findedCellForTag = null;
        for (Ship ship : fleet.getShipsOnField()) {
            for (FieldCoord shipCoord : ship.getShipCoords()) {
                if (shipCoord.getX() == x && shipCoord.getY() == y) {
                    findedCellForTag = shipCoord;
                    break;
                }
            }
        }
        if (findedCellForTag != null)
            findedCellForTag.setTag();
    }

    /*
     * Возвращает ture, если все корабли уничтожены(игра закончена)
     */

    public boolean isAllShipsDestroyed() {
        boolean someoneAlive = false;
        Ship ship_for_remove = null;
        for (Ship ship : fleet.getShipsOnField()) {
            if (ship.isAlive()) {
                someoneAlive = true;
                break;
            } else {
                ship_for_remove = ship;
            }
        }
        if (ship_for_remove != null)
            fleet.remove(ship_for_remove);
        return !someoneAlive;
    }

    public boolean setCellAsDamaged(FieldCoord fieldCoord) {
        int x = fieldCoord.getX();
        int y = fieldCoord.getY();
        if (field[x][y] == Cell.SHIP) {
            tagShipsByCoord(x, y);
            field[x][y] = Cell.DAMAGED_SHIP;
            return true;
        } else {
            field[x][y] = Cell.MISS;
            return false;
        }
    }

    private void setCellAsShip(int x, int y) {
        field[x][y] = Cell.SHIP;
    }

    private void setCellAsNearWithShip(int x, int y) {
        if (field[x][y] != Cell.BORDER) {
            field[x][y] = Cell.NEAR_WITH_SHIP;
        }
    }

    boolean isPossibleLocateShip(CurrentGUIState currentGUIState) {
        FieldCoord coord = currentGUIState.getFieldCoord();
        Ship.Type shipType = currentGUIState.getShipType();
        Ship.Orientation shipOrientation = currentGUIState.getShipOrientation();

        boolean isPossibleLocateShipFlag = true;
        int x = coord.getX() + 1;
        int y = coord.getY() + 1;
        int shipTypeInt = Ship.Type.shipTypeToInt(shipType);
        if (shipOrientation == Ship.Orientation.HORIZONTAL) {
            for (int i = x; i < x + shipTypeInt + 1; i++) {
                if (field[i][y] == Cell.SHIP ||
                        field[i][y] == Cell.NEAR_WITH_SHIP) {
                    isPossibleLocateShipFlag = false;
                    break;
                }
            }
        }
        if (shipOrientation == Ship.Orientation.VERTICAL) {
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

    boolean isNotIntersectShipWithBorder(CurrentGUIState currentGUIState) {
        FieldCoord coord = currentGUIState.getFieldCoord();
        Ship.Type shipType = currentGUIState.getShipType();
        Ship.Orientation shipOrientation = currentGUIState.getShipOrientation();


        boolean isPossibleLocateShipFlag = true;
        int shipTypeInt = Ship.Type.shipTypeToInt(shipType);
        int x = coord.getX() + 1;
        int y = coord.getY() + 1;
        if (shipOrientation == Ship.Orientation.HORIZONTAL) {
            for (int i = x; i < x + shipTypeInt + 1; i++) {
                if (field[i][y] == Cell.BORDER) {
                    isPossibleLocateShipFlag = false;
                    break;
                }
            }
        }
        if (shipOrientation == Ship.Orientation.VERTICAL) {
            for (int i = y; i < y + shipTypeInt + 1; i++) {
                if (field[x][i] == Cell.BORDER) {
                    isPossibleLocateShipFlag = false;
                    break;
                }
            }
        }
        return isPossibleLocateShipFlag;
    }


    public void printOnConsole() {
        //TODO
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
                if (field[j][i] == Cell.BORDER) {
                    System.out.print(" # ");
                    continue;
                }
                if (field[j][i] == Cell.DAMAGED_SHIP) {
                    System.out.print(" x ");
                    continue;
                }

            }
            System.out.println();
        }
    }
}