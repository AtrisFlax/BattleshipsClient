package com.liver_rus.Battleships.Client;

/**
 * Класс игрового поле с фикированным размером размером 10x10
 */

class GameField {

    ///Поле 10*10 окружено кольцом клеток типа CellStatus.BORDER -> Поле 12*12

    private final static int REAL_FIELD_SIZE = 12;

    private enum CellStatus {
        CLEAR, MISS, SHIP, NEAR_WITH_SHIP, BORDER, DAMAGED_SHIP
    }

    private ShipsOnField shipsOnField = new ShipsOnField();

    private CellStatus[][] field;

    GameField() {
        reset();
    }

    private void reset() {
        field = new CellStatus[REAL_FIELD_SIZE][REAL_FIELD_SIZE];
        for (int i = 1; i < REAL_FIELD_SIZE - 1; i++) {
            for (int j = 1; j < REAL_FIELD_SIZE - 1; j++) {
                field[i][j] = CellStatus.CLEAR;
            }
        }
        //верхняя часть кольца
        for (int j = 0; j < REAL_FIELD_SIZE; j++) {
            field[0][j] = CellStatus.BORDER;
        }
        //нижняя часть кольца
        for (int j = 0; j < REAL_FIELD_SIZE; j++) {
            field[REAL_FIELD_SIZE - 1][j] = CellStatus.BORDER;
        }
        //левая часть кольца
        for (int i = 0; i < REAL_FIELD_SIZE; i++) {
            field[i][0] = CellStatus.BORDER;
        }
        //правая часть кольца
        for (int i = 0; i < REAL_FIELD_SIZE; i++) {
            field[i][REAL_FIELD_SIZE - 1] = CellStatus.BORDER;
        }
        shipsOnField.clear();
    }

    private void markShipsCell(int x, int y, int shipType, Ship.Orientation shipOrientation) {
        if (shipOrientation == Ship.Orientation.HORIZONTAL) {
            for (int i = 0; i < shipType + 1; i++) {
                setCellAsShip(x + i, y);
            }
        }
        if (shipOrientation == Ship.Orientation.VERTICAL) {
            for (int i = 0; i < shipType + 1; i++) {
                setCellAsShip(x, y + i);
            }
        }
    }

    /*
     * Отметка клеток корабля и ближлежайших клеток
     */

    void markFieldByShip(FieldCoord fieldCoord, Ship.Type type, Ship.Orientation shipOrientation) {
        int intShipType = Ship.Type.shipTypeToInt(type);
        shipsOnField.add(new Ship(fieldCoord, type, shipOrientation));
        int x = fieldCoord.getX() + 1;
        int y = fieldCoord.getY() + 1;
        markShipsCell(x, y, intShipType, shipOrientation);
        if (shipOrientation == Ship.Orientation.HORIZONTAL) {
            setCellAsNearWithShip(x - 1, y);
            setCellAsNearWithShip(x + intShipType + 1, y);
            for (int i = x - 1; i <= x + intShipType + 1; i++) {
                if (field[i][y + 1] != CellStatus.BORDER) {
                    setCellAsNearWithShip(i, y + 1);
                }
                if (field[i][y - 1] != CellStatus.BORDER) {
                    setCellAsNearWithShip(i, y - 1);
                }
            }
        }
        if (shipOrientation == Ship.Orientation.VERTICAL) {
            field[x][y - 1] = CellStatus.NEAR_WITH_SHIP;
            field[x][y + intShipType + 1] = CellStatus.NEAR_WITH_SHIP;
            for (int i = y - 1; i <= y + intShipType + 1; i++) {
                field[x + 1][i] = CellStatus.NEAR_WITH_SHIP;
                field[x - 1][i] = CellStatus.NEAR_WITH_SHIP;
            }
        }
    }

    /*
     *  Отметка попадания в корабль
     */

    private void tagShipsByCoord(int x, int y) {
        FieldCoord findedCellForTag = null;
        for (Ship ship : shipsOnField.getShipsOnField()) {
            for (FieldCoord shipCoord : ship.getShipCoord()) {
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

    boolean isShipsOnFieldAlive() {
        boolean someoneAlive = false;
        Ship ship_for_remove = null;
        for (Ship ship : shipsOnField.getShipsOnField()) {
            if (ship.isAlive()) {
                someoneAlive = true;
                break;
            } else {
                ship_for_remove = ship;
            }
        }
        if (ship_for_remove != null)
            shipsOnField.remove(ship_for_remove);
        return someoneAlive;
    }

    boolean setCellAsDamaged(FieldCoord fieldCoord) {
        int x = fieldCoord.getX() + 1;
        int y = fieldCoord.getY();
        if (field[x][y] == CellStatus.SHIP) {
            tagShipsByCoord(x, y);
            field[x][y] = CellStatus.DAMAGED_SHIP;
            return true;
        } else {
            field[x][y] = CellStatus.MISS;
            return false;
        }
    }

    private void setCellAsShip(int x, int y) {
        field[x][y] = CellStatus.SHIP;
    }

    private void setCellAsNearWithShip(int x, int y) {
        if (field[x][y] != CellStatus.BORDER) {
            field[x][y] = CellStatus.NEAR_WITH_SHIP;
        }
    }

    boolean isPossibleLocateShip(FieldCoord coord, Ship.Type shipType, Ship.Orientation shipOrientation) {
        boolean isPossibleLocateShipFlag = true;
        int x = coord.getX() + 1;
        int y = coord.getY() + 1;
        int shipTypeInt = Ship.Type.shipTypeToInt(shipType);
        if (shipOrientation == Ship.Orientation.HORIZONTAL) {
            for (int i = x; i < x + shipTypeInt + 1; i++) {
                if (field[i][y] == CellStatus.SHIP ||
                        field[i][y] == CellStatus.NEAR_WITH_SHIP) {
                    isPossibleLocateShipFlag = false;
                    break;
                }
            }
        }
        if (shipOrientation == Ship.Orientation.VERTICAL) {
            for (int i = y; i < y + shipTypeInt + 1; i++) {
                if (field[x][i] == CellStatus.SHIP ||
                        field[x][i] == CellStatus.NEAR_WITH_SHIP) {
                    isPossibleLocateShipFlag = false;
                    break;
                }
            }
        }
        return isPossibleLocateShipFlag;
    }

    boolean isNotIntersectShipWithBorder(FieldCoord coord, Ship.Type shipType, Ship.Orientation shipOrientation) {
        boolean isPossibleLocateShipFlag = true;
        int shipTypeInt = Ship.Type.shipTypeToInt(shipType);
        int x = coord.getX() + 1;
        int y = coord.getY() + 1;
        if (shipOrientation == Ship.Orientation.HORIZONTAL) {
            for (int i = x; i < x + shipTypeInt + 1; i++) {
                if (field[i][y] == CellStatus.BORDER) {
                    isPossibleLocateShipFlag = false;
                    break;
                }
            }
        }
        if (shipOrientation == Ship.Orientation.VERTICAL) {
            for (int i = y; i < y + shipTypeInt + 1; i++) {
                if (field[x][i] == CellStatus.BORDER) {
                    isPossibleLocateShipFlag = false;
                    break;
                }
            }
        }
        return isPossibleLocateShipFlag;
    }
}