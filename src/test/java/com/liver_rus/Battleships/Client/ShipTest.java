package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    @Test
    void getIsShipAlive() {
        Ship ship = Ship.create(2, 2, Ship.Type.AIRCRAFT_CARRIER, false);
        assertTrue(ship.isAlive());
        ship.tagShipCell(2, 4);
        assertTrue(ship.isAlive());
        ship.tagShipCell(2, 5);
        assertTrue(ship.isAlive());
        ship.tagShipCell(2, 2);
        assertTrue(ship.isAlive());
        ship.tagShipCell(2, 6);
        assertTrue(ship.isAlive());
        ship.tagShipCell(2, 3);
        assertFalse(ship.isAlive());
    }

    @Test
    void getShipStartCoord() {
        int startX = 2;
        int startY = 2;
        Ship ship = Ship.create(startX, startY, Ship.Type.DESTROYER, false);
        assertEquals(startX, ship.getShipStartCoord().getX());
        assertEquals(startY, ship.getShipStartCoord().getY());

        int startX1 = 5;
        int startY1 = 7;
        Ship ship1 = Ship.create(startX1, startY1, Ship.Type.DESTROYER, false);
        assertEquals(startX1, ship1.getShipStartCoord().getX());
        assertEquals(startY1, ship1.getShipStartCoord().getY());
    }

    @Test
    void outOfBounceShipCreation() {
        assertThrows(IllegalArgumentException.class,
                () -> Ship.create(9, 9, Ship.Type.AIRCRAFT_CARRIER, true));
        assertThrows(IllegalArgumentException.class,
                () -> Ship.create(0, 9, Ship.Type.AIRCRAFT_CARRIER, false));
        assertThrows(IllegalArgumentException.class,
                () -> Ship.create(9, 0, Ship.Type.DESTROYER, true));
    }
}