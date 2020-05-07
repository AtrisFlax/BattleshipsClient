package com.liver_rus.Battleships.Network.Server.GamePrimitives;

import com.liver_rus.Battleships.Network.Server.FieldCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Игрового поле размером размером FIELD_SIZE x FIELD_SIZE
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
        Ship ship;
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
        List<FieldCoord> nearShipCoord = getNearCoords(ship);
        for (FieldCoord coord: nearShipCoord) {
            coord.setType(FieldCell.NEAR_WITH_SHIP);
        }
        ship.setNearCoords(nearShipCoord);
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
        Ship ship = fleet.findAliveShip(x, y);
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

    public Ship saveShoot(int x, int y) {
        if (field[x][y].getType() == FieldCell.MISS) {
            field[x][y].setType(FieldCell.DOUBLE_MISS);
            return null;
        }
        if (field[x][y].getType() == FieldCell.DOUBLE_DAMAGED) {
            return null;
        }
        if (field[x][y].getType() == FieldCell.DAMAGED_SHIP) {
            field[x][y].setType(FieldCell.DOUBLE_DAMAGED);
            return null;
        }
        Ship ship = fleet.findAliveShip(x, y);
        //if no ship on (x,y)
        if (ship == null) {
            if (field[x][y].getType() == FieldCell.CLEAR || field[x][y].getType() == FieldCell.NEAR_WITH_SHIP) {
                field[x][y].setType(FieldCell.MISS);
            }
            return null;
        } else {
            field[x][y].setType(FieldCell.DAMAGED_SHIP);
            fleet.updateAlive(ship);
        }
        if (ship.isAlive()) {
            return null;
        } else {
            for (FieldCoord nearCoord : ship.getNearCoord()) {
                nearCoord.setType(FieldCell.NEAR_WITH_DESTROYED_SHIP);
            }
            return ship;
        }
    }

    public boolean isCellDamaged(int x, int y) {
        return field[x][y].getType() == FieldCell.DAMAGED_SHIP;
    }

    public boolean isFreeShot(int x, int y) {
        return field[x][y].getType() == FieldCell.NEAR_WITH_DESTROYED_SHIP ||
                field[x][y].getType() == FieldCell.DOUBLE_DAMAGED ||
                field[x][y].getType() == FieldCell.DOUBLE_MISS;
    }

    public boolean isNotIntersectionWithShips(int x, int y, int shipType, boolean isHorizontal) {
        if (isHorizontal) {
            for (int i = x; i < x + shipType + 1; i++) {
                if (field[i][y].getType() == FieldCell.SHIP || field[i][y].getType() == FieldCell.NEAR_WITH_SHIP) {
                    return false;

                }
            }
        } else {
            for (int i = y; i < y + shipType + 1; i++) {
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
                    result.append(" / ");
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
                if (field[j][i].getType() == FieldCell.DOUBLE_DAMAGED) {
                    result.append(" ✖ ");
                }
                if (field[j][i].getType() == FieldCell.NEAR_WITH_DESTROYED_SHIP) {
                    result.append(" • ");
                }
            }
            result.append("\n");
        }
        result.append(fleet.toString());
        return result.toString();
    }

    public List<FieldCoord> getNearCoords(Ship ship) {
        List<FieldCoord> result = new ArrayList<>();
        FieldCoord startCoord = ship.getShipStartCoord();
        int x = startCoord.getX();
        int y = startCoord.getY();
        int type = ship.getType();
        boolean isHorizontal = ship.isHorizontal();
        if (isHorizontal) {
            addFieldCoordIfPossible(x - 1, y, result);
            addFieldCoordIfPossible(x + type + 1, y, result);
            for (int i = x - 1; i <= x + type + 1; i++) {
                addFieldCoordIfPossible(i, y + 1, result);
                addFieldCoordIfPossible(i, y - 1, result);
            }
        } else {
            addFieldCoordIfPossible(x, y - 1, result);
            addFieldCoordIfPossible(x, y + type + 1, result);
            for (int i = y - 1; i <= y + type + 1; i++) {
                addFieldCoordIfPossible(x + 1, i, result);
                addFieldCoordIfPossible(x - 1, i, result);
            }
        }
        return result;
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

    private void addFieldCoordIfPossible(int x, int y, List<FieldCoord> result) {
        if (x >= 0 && x < FIELD_SIZE && y >= 0 && y < FIELD_SIZE) {
            result.add(field[x][y]);
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
