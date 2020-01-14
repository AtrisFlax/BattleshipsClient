package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.GamePrimitive.FieldCoord;
import com.liver_rus.Battleships.Client.GamePrimitive.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShipTest {
    Ship ship;

    @BeforeEach
    void createShip() {
        ship = Ship.createShip(new FieldCoord(2, 2), Ship.Type.AIRCRAFT_CARRIER, false);
    }

    @Test
    void getIsShipAlive() {
        assertTrue(ship.isAlive());
        ship.tagShipCell(new FieldCoord(2,4));
        assertTrue(ship.isAlive());
        ship.tagShipCell(new FieldCoord(2,5));
        assertTrue(ship.isAlive());
        ship.tagShipCell(new FieldCoord(2,2));
        assertTrue(ship.isAlive());
        ship.tagShipCell(new FieldCoord(2,6));
        assertTrue(ship.isAlive());
        ship.tagShipCell(new FieldCoord(2,3));
        assertFalse(ship.isAlive());
    }
}