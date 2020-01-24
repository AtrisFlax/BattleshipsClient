package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.GamePrimitives.FieldCoord;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    @Test
    void getIsShipAlive() {
        Ship ship = Ship.create(new FieldCoord(2, 2), Ship.Type.AIRCRAFT_CARRIER, false);
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

    @Test
    void getShipStartCoord(){
        FieldCoord startCoord = new FieldCoord(2,2);
        Ship ship = Ship.create(startCoord, Ship.Type.DESTROYER, false);
        assertEquals(startCoord, ship.getShipStartCoord());

        FieldCoord startCoord1 = new FieldCoord(5,7);
        Ship ship1 = Ship.create(startCoord1, Ship.Type.BATTLESHIP, true);
        assertEquals(startCoord1, ship1.getShipStartCoord());
    }

    @Test
    void outOfBounceShipCreation() {
        assertThrows(IllegalArgumentException.class,
                () -> Ship.create(new FieldCoord(9, 9), Ship.Type.AIRCRAFT_CARRIER, true));
        assertThrows(IllegalArgumentException.class,
                () -> Ship.create(new FieldCoord(0, 9), Ship.Type.AIRCRAFT_CARRIER, false));
        assertThrows(IllegalArgumentException.class,
                () -> Ship.create(new FieldCoord(9, 0), Ship.Type.DESTROYER, true));
    }
}