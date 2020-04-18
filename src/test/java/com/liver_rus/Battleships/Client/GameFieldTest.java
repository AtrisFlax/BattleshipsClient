package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.Ship;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.TryingAddTooManyShipsOnFieldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameFieldTest {
    GameField gameField;

    @BeforeEach
    void createShipsOnField() {
        gameField = new GameField();
    }

    @Test
    void addShipsAndResetField() throws TryingAddTooManyShipsOnFieldException {
        final int MAX_DEPLOYMENT = 7;
        int leftForDeploy = MAX_DEPLOYMENT;
        assertEquals(MAX_DEPLOYMENT, gameField.getShipsLeftForDeploy());

        gameField.addShip(3, 4, 2, true);
        leftForDeploy--;
        gameField.addShip(2, 1, 0, true);
        leftForDeploy--;
        gameField.addShip(9, 9, 0, true);
        leftForDeploy--;
        assertEquals(leftForDeploy, gameField.getShipsLeftForDeploy());
        gameField.reset();
        assertEquals(MAX_DEPLOYMENT, gameField.getShipsLeftForDeploy());
    }

    @Test
    void addShipsAndShoot() throws TryingAddTooManyShipsOnFieldException {
        int numExpectedShipOnField = 0;
        int x0 = 9;
        int y0 = 9;
        int shipType0 = 0;
        boolean isHorizontal0 = true;
        gameField.addShip(x0, y0, shipType0, isHorizontal0);
        numExpectedShipOnField++;

        assertEquals(numExpectedShipOnField, gameField.getShipLeftAlive());
        int x1 = 5;
        int y1 = 2;
        int shipType1 = 1;
        boolean isHorizontal1 = false;
        gameField.addShip(x1, y1, shipType1, isHorizontal1);
        numExpectedShipOnField++;
        assertEquals(numExpectedShipOnField, gameField.getShipLeftAlive());

        //destroy ship0
        Ship expectedDestroyedShip0 = gameField.shoot(x0, y0);
        numExpectedShipOnField--;
        assertEquals(expectedDestroyedShip0.getShipStartCoord().getX(), x0);
        assertEquals(expectedDestroyedShip0.getShipStartCoord().getY(), y0);
        assertEquals(expectedDestroyedShip0.getType(), shipType0);
        assertEquals(isHorizontal0, expectedDestroyedShip0.isHorizontal());
        assertFalse(expectedDestroyedShip0.isAlive());
        assertEquals(numExpectedShipOnField, gameField.getShipLeftAlive());
        assertFalse(gameField.isAllShipsDestroyed());

        Ship expectedDestroyedShip1 = gameField.shoot(5, 2);
        assertNull(expectedDestroyedShip1);
        assertEquals(numExpectedShipOnField, gameField.getShipLeftAlive());
        assertFalse(gameField.isAllShipsDestroyed());

        expectedDestroyedShip1 = gameField.shoot(5, 3);
        assertNotNull(expectedDestroyedShip1);
        assertEquals(expectedDestroyedShip1.getShipStartCoord().getX(), x1);
        assertEquals(expectedDestroyedShip1.getShipStartCoord().getY(), y1);
        assertEquals(expectedDestroyedShip1.getType(), shipType1);
        assertEquals(expectedDestroyedShip1.isHorizontal(), isHorizontal1);
        numExpectedShipOnField--;
        assertEquals(numExpectedShipOnField, gameField.getShipLeftAlive());
        assertTrue(gameField.isAllShipsDestroyed());
    }


    @Test
    void addTooManyShips() throws TryingAddTooManyShipsOnFieldException {
        assertFalse(gameField.isAllShipsDeployed());
        gameField.addShip(1, 2, 4, true);
        assertFalse(gameField.isAllShipsDeployed());
        gameField.addShip(7, 2, 3, false);
        gameField.addShip(3, 5, 2, false);
        gameField.addShip(6, 0, 1, true);
        assertFalse(gameField.isAllShipsDeployed());
        gameField.addShip(5, 7, 1, false);
        gameField.addShip(1, 4, 0, false);
        gameField.addShip(8, 8, 0, false);
        assertTrue(gameField.isAllShipsDeployed());
        assertFalse(gameField.isAllShipsDestroyed());
        final int NUM_EXPECTED_SHIP_ON_FIELD = 7;
        final int NUM_EXPECTED_LEFT_FOR_DEPLOY = 0;
        assertEquals(NUM_EXPECTED_SHIP_ON_FIELD, gameField.getShipLeftAlive());
        assertEquals(NUM_EXPECTED_LEFT_FOR_DEPLOY, gameField.getShipsLeftForDeploy());
        assertThrows(TryingAddTooManyShipsOnFieldException.class,
                () -> gameField.addShip(9, 9, 0, false));
        assertEquals(NUM_EXPECTED_SHIP_ON_FIELD, gameField.getShipLeftAlive());
        assertEquals(NUM_EXPECTED_LEFT_FOR_DEPLOY, gameField.getShipsLeftForDeploy());
    }

    @Test
    void inBorderShipCreation() throws TryingAddTooManyShipsOnFieldException {
        int startX0 = 2;
        int startY0 = 3;
        boolean shipCreated0 = gameField.addShip(startX0, startY0, 1, false);
        assertTrue(shipCreated0);

        int startX1 = 5;
        int startY1 = 7;
        boolean shipCreated1 = gameField.addShip(startX1, startY1, 1, false);
        assertTrue(shipCreated1);
    }

    @Test
    void outBorderShipCreation() throws TryingAddTooManyShipsOnFieldException {
        boolean shipCreated0 = gameField.addShip(9, 9, 4, false);
        boolean shipCreated1 = gameField.addShip(0, 9, 4, false);
        boolean shipCreated2 = gameField.addShip(9, 0, 1, true);
        assertFalse(shipCreated0);
        assertFalse(shipCreated1);
        assertFalse(shipCreated2);
    }

    @Test
    void createIntersectedShips() throws TryingAddTooManyShipsOnFieldException {
        boolean shipCreated0 = gameField.addShip(0, 0, 4, false);
        boolean shipCreated1 = gameField.addShip(0, 0, 4, true);
        assertTrue(shipCreated0);
        assertFalse(shipCreated1);

        boolean shipCreated2 = gameField.addShip(5, 3, 2, true);
        boolean shipCreated3 = gameField.addShip(4, 4, 2, false);
        assertTrue(shipCreated2);
        assertFalse(shipCreated3);
    }

    @Test
    void createNearShips() throws TryingAddTooManyShipsOnFieldException {
        boolean shipCreated0 = gameField.addShip(0, 0, 4, true);
        boolean shipCreated1 = gameField.addShip(0, 1, 4, true);
        assertTrue(shipCreated0);
        assertFalse(shipCreated1);

        boolean shipCreated2 = gameField.addShip(6, 6, 3, false);
        boolean shipCreated3 = gameField.addShip(7, 6, 3, false);
        assertTrue(shipCreated2);
        assertFalse(shipCreated3);
    }
}

