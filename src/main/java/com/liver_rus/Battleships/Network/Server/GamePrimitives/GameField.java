package com.liver_rus.Battleships.Network.Server.GamePrimitives;

import com.liver_rus.Battleships.Network.Server.FieldCell;

import java.util.Arrays;
import java.util.List;

/**
 * Класс игрового поле с фикированным размером размером 10x10
 */


public class GameField {
    public static final int FIELD_SIZE = 10;

    private FieldCoord[][] field;
    private Fleet fleet;

    public GameField() {
        fleet = new Fleet();
        field = createField();
    }

    public void reset() {
        fleet = new Fleet();
        field = new FieldCoord[FIELD_SIZE][FIELD_SIZE];
    }

    //return true if ship had been created; false if not
    public boolean addShip(int x, int y, int type, boolean isHorizontal) throws TryingAddTooManyShipsOnFieldException {
        Ship ship = null;
        if (fleet.getShipsLeftForDeploy() > 0) {
            ship = Ship.create(x, y, type, isHorizontal, this);
        } else {
            throw new TryingAddTooManyShipsOnFieldException();
        }

        if (ship == null) {
            return false;
        } else {
            markFieldFieldCellsByShip(ship);
            fleet.add(ship);
            return true;
        }
    }

    public FieldCoord getFieldCell(int x, int y) {
        return field[x][y];
    }

    public int getShipsLeftForDeploy() {
        return fleet.getShipsLeftForDeploy();
    }

    public int[] getShipsLeftByTypeForDeploy() {
        return fleet.getShipsLeftByType();
    }

    //Отметка клеток корабля и ближлежайших клеток
    public void markFieldFieldCellsByShip(Ship ship) {
        FieldCoord shipCoord = ship.getShipStartCoord();
        int x = shipCoord.getX();
        int y = shipCoord.getY();
        int type = ship.getType();
        boolean isHorizontal = ship.isHorizontal();

        markShipFieldCells(x, y, type, isHorizontal);
        if (isHorizontal) {
            setFieldCellAsNearWithShip(x - 1, y);
            setFieldCellAsNearWithShip(x + type + 1, y);
            for (int i = x - 1; i <= x + type + 1; i++) {
                setFieldCellAsNearWithShip(i, y + 1);
                setFieldCellAsNearWithShip(i, y - 1);
            }
        } else {
            setFieldCellAsNearWithShip(x, y - 1);
            setFieldCellAsNearWithShip(x, y + type + 1);
            for (int i = y - 1; i <= y + type + 1; i++) {
                setFieldCellAsNearWithShip(x + 1, i);
                setFieldCellAsNearWithShip(x - 1, i);
            }
        }
    }


    //Возвращает true, если все корабли уничтожены(игра закончена)
    public boolean isAllShipsDestroyed() {
        return fleet.getLeftAlive() == 0;
    }

    public boolean isAllShipsDeployed() {
        return fleet.getShipsLeftForDeploy() == 0;
    }

    public List<Ship> getShips() {
        return fleet.getAllShipsOnField();
    }

    public int getShipLeftAlive() {
        return fleet.getLeftAlive();
    }

    // return destroyed ship
    public Ship shoot(int x, int y) {
        Ship ship = fleet.findShip(x, y);
        //if no ship on (x,y)
        if (ship == null) {
            field[x][y].setType(FieldCell.MISS);
            return null;
        } else {
            field[x][y].setType(FieldCell.DAMAGED_SHIP);
            fleet.updateAlive(ship);
        }
        if (ship.isAlive()) {
            return null;
        } else {
            return ship;
        }
    }


    public boolean isFieldCellDamaged(int x, int y) {
        return field[x][y].getType() == FieldCell.DAMAGED_SHIP;
    }

    public boolean isNotIntersectionWithShips(int x, int y, int shipType, boolean isHorizontal) {
        if (isHorizontal) {
            for (int i = x; i < x + shipType; i++) {
                if (field[i][y].getType() == FieldCell.SHIP || field[i][y].getType() == FieldCell.NEAR_WITH_SHIP) {
                    return false;

                }
            }
        } else {
            for (int i = y; i < y + shipType; i++) {
                if (field[x][i].getType() == FieldCell.SHIP || field[x][i].getType() == FieldCell.NEAR_WITH_SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isNotIntersectionShipWithBorder(int x, int y, int shipType, boolean isHorizontal) {
        if (x < 0 || x >= FIELD_SIZE) return false;
        if (y < 0 || y >= FIELD_SIZE) return false;
        if (isHorizontal) {
            return x + shipType < FIELD_SIZE;
        } else {
            return y + shipType < FIELD_SIZE;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (field[j][i].getType() == FieldCell.CLEAR) {
                    result.append("   ");
                    continue;
                }
                if (field[j][i].getType() == FieldCell.MISS) {
                    result.append(" o ");
                    continue;
                }
                if (field[j][i].getType() == FieldCell.SHIP) {
                    result.append(" + ");
                    continue;
                }
                if (field[j][i].getType() == FieldCell.NEAR_WITH_SHIP) {
                    result.append(" * ");
                    continue;
                }
                if (field[j][i].getType() == FieldCell.DAMAGED_SHIP) {
                    result.append(" x ");
                }
            }
            result.append("\n");
        }
        result.append(fleet.toString());
        return result.toString();
    }

    private static FieldCoord[][] createField() {
        FieldCoord[][] gameField = new FieldCoord[FIELD_SIZE][];
        for (int i = 0; i < FIELD_SIZE; i++) {
            gameField[i] = new FieldCoord[FIELD_SIZE];
        }
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                gameField[i][j] = new FieldCoord(i, j);
                gameField[i][j].setType(FieldCell.CLEAR);
            }
        }
        return gameField;
    }


    private void setFieldCellAsShip(int x, int y) {
        field[x][y].setType(FieldCell.SHIP);
    }

    private void setFieldCellAsNearWithShip(int x, int y) {
        if (x >= 0 && x < FIELD_SIZE && y >= 0 && y < FIELD_SIZE) {
            field[x][y].setType(FieldCell.NEAR_WITH_SHIP);
        }
    }

    private void markShipFieldCells(int x, int y, int shipType, boolean isHorizontal) {
        if (isHorizontal) {
            for (int i = 0; i < shipType + 1; i++) {
                setFieldCellAsShip(x + i, y);
            }
        } else {
            for (int i = 0; i < shipType + 1; i++) {
                setFieldCellAsShip(x, y + i);
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
