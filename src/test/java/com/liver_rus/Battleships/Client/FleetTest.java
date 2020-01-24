package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.GamePrimitives.FieldCoord;
import com.liver_rus.Battleships.Client.GamePrimitives.Fleet;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import com.liver_rus.Battleships.Client.GamePrimitives.TryingAddTooManyShipsOnFieldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FleetTest {
    Fleet fleet;

    @BeforeEach
    void createShipsOnField() {
        fleet = new Fleet();
    }

    @Test
    void addAndRemoveShipsToFleet() throws TryingAddTooManyShipsOnFieldException {
        final int AMOUNT_SHIPS_ON_EMPTY_FLEET = 0;
        assertEquals(AMOUNT_SHIPS_ON_EMPTY_FLEET, fleet.getShipsOnField().size());
        fleet.clear();
        assertEquals(AMOUNT_SHIPS_ON_EMPTY_FLEET, fleet.getShipsOnField().size());
        fleet.add(Ship.create(new FieldCoord(3, 4), Ship.Type.DESTROYER, true));
        fleet.add(Ship.create(new FieldCoord(2, 1), Ship.Type.SUBMARINE, true));
        fleet.add(Ship.create(new FieldCoord(9, 9), Ship.Type.SUBMARINE, true));
        fleet.clear();
        //check clear reset
        assertEquals(AMOUNT_SHIPS_ON_EMPTY_FLEET, fleet.getShipsOnField().size());
    }

    @Test
    void remove() throws TryingAddTooManyShipsOnFieldException {
        ArrayList<Ship> testShipsList = new ArrayList<>();
        Ship shipForRemove = Ship.create(new FieldCoord(3, 7), Ship.Type.CRUISER, false);
        testShipsList.add(Ship.create(new FieldCoord(2, 3), Ship.Type.AIRCRAFT_CARRIER, true));
        testShipsList.add(Ship.create(new FieldCoord(5, 5), Ship.Type.SUBMARINE, true));
        testShipsList.add(shipForRemove);
        testShipsList.add(Ship.create(new FieldCoord(4, 0), Ship.Type.CRUISER, true));
        testShipsList.add(Ship.create(new FieldCoord(8, 2), Ship.Type.SUBMARINE, false));
        for (Ship ship : testShipsList) {
            fleet.add(ship);
        }
        testShipsList.remove(shipForRemove);
        fleet.remove(shipForRemove);
        assertIterableEquals(testShipsList, fleet.getShipsOnField());
    }

    @Test
    void add() throws TryingAddTooManyShipsOnFieldException {
        ArrayList<Ship> testShipsList = new ArrayList<>();
        testShipsList.add(Ship.create(new FieldCoord(2, 3), Ship.Type.AIRCRAFT_CARRIER, true));
        testShipsList.add(Ship.create(new FieldCoord(5, 5), Ship.Type.SUBMARINE, true));
        testShipsList.add(Ship.create(new FieldCoord(4, 0), Ship.Type.CRUISER, true));
        testShipsList.add(Ship.create(new FieldCoord(8, 2), Ship.Type.SUBMARINE, false));
        for (Ship ship : testShipsList) {
            fleet.add(ship);
        }
        assertIterableEquals(testShipsList, fleet.getShipsOnField());
    }

    @Test
    void findShip() throws TryingAddTooManyShipsOnFieldException {
        FieldCoord shipCoord = new FieldCoord(2, 4);
        FieldCoord findCoord = new FieldCoord(2, 6);
        FieldCoord wrongCoord = new FieldCoord(5, 5);
        Ship findingShip = Ship.create(shipCoord, Ship.Type.AIRCRAFT_CARRIER, false);
        fleet.add(findingShip);
        fleet.add(Ship.create(new FieldCoord(7, 6), Ship.Type.SUBMARINE, true));
        fleet.add(Ship.create(new FieldCoord(9, 7), Ship.Type.SUBMARINE, true));
        assertEquals(findingShip, fleet.findShip(findCoord));
        assertNull(fleet.findShip(wrongCoord));
    }


    @Test
    void testTryingAddTooManyShipsException() throws TryingAddTooManyShipsOnFieldException {
        fleet.add(Ship.create(new FieldCoord(2, 3), Ship.Type.AIRCRAFT_CARRIER, true));
        fleet.add(Ship.create(new FieldCoord(2, 3), Ship.Type.AIRCRAFT_CARRIER, true));
        fleet.add(Ship.create(new FieldCoord(2, 3), Ship.Type.AIRCRAFT_CARRIER, true));
        fleet.add(Ship.create(new FieldCoord(2, 3), Ship.Type.AIRCRAFT_CARRIER, true));
        fleet.add(Ship.create(new FieldCoord(2, 3), Ship.Type.AIRCRAFT_CARRIER, true));
        fleet.add(Ship.create(new FieldCoord(2, 3), Ship.Type.AIRCRAFT_CARRIER, true));
        fleet.add(Ship.create(new FieldCoord(2, 3), Ship.Type.AIRCRAFT_CARRIER, true));
        assertThrows(TryingAddTooManyShipsOnFieldException.class,
                () -> fleet.add(Ship.create(new FieldCoord(2, 3), Ship.Type.AIRCRAFT_CARRIER, true)));
    }


    @Test
    void testFormationFleetToTxt() throws TryingAddTooManyShipsOnFieldException {
        fleet.add(Ship.create(new FieldCoord(3, 2), Ship.Type.SUBMARINE, true));
        fleet.add(Ship.create(new FieldCoord(1, 8), Ship.Type.SUBMARINE, true));
        fleet.add(Ship.create(new FieldCoord(1, 1), Ship.Type.DESTROYER, false));
        fleet.add(Ship.create(new FieldCoord(3, 4), Ship.Type.DESTROYER, true));
        fleet.add(Ship.create(new FieldCoord(2, 6), Ship.Type.CRUISER, true));
        fleet.add(Ship.create(new FieldCoord(7, 4), Ship.Type.BATTLESHIP, false));
        fleet.add(Ship.create(new FieldCoord(9, 1), Ship.Type.AIRCRAFT_CARRIER, false));
        assertEquals(7, fleet.getShipsLeft());
        assertEquals("320H|180H|111V|341H|262H|743V|914V|", fleet.toString());
    }

    @Test
    void testFormationFleetToTxtOtherSequence() throws TryingAddTooManyShipsOnFieldException {
        fleet.add(Ship.create(new FieldCoord(4, 3), Ship.Type.AIRCRAFT_CARRIER, false));
        fleet.add(Ship.create(new FieldCoord(1, 1), Ship.Type.BATTLESHIP, false));
        fleet.add(Ship.create(new FieldCoord(6, 1), Ship.Type.DESTROYER, false));
        fleet.add(Ship.create(new FieldCoord(6, 4), Ship.Type.DESTROYER, false));
        fleet.add(Ship.create(new FieldCoord(8, 1), Ship.Type.SUBMARINE, false));
        fleet.add(Ship.create(new FieldCoord(3, 1), Ship.Type.SUBMARINE, false));
        fleet.add(Ship.create(new FieldCoord(1, 9), Ship.Type.CRUISER, true));
        assertEquals(7, fleet.getShipsLeft());
        assertEquals("434V|113V|611V|641V|810V|310V|192H|", fleet.toString());
    }


}