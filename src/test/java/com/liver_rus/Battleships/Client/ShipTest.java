package com.liver_rus.Battleships.Client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipTest {

    Ship ship;

    @BeforeEach
    void createShip() {
        ship = new Ship(new FieldCoord(2, 2), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.VERTICAL);
    }

    @Test
    void getIsShipAlive() {
        assertTrue(ship.isAlive());
        ship.tagShipCell(new FieldCoord(3,5));
        ship.tagShipCell(new FieldCoord(3,6));
        ship.tagShipCell(new FieldCoord(3,3));
        assertTrue(ship.isAlive());
        ship.tagShipCell(new FieldCoord(3,7));
        assertTrue(ship.isAlive());
        ship.tagShipCell(new FieldCoord(3,4));
        assertFalse(ship.isAlive());
    }
}