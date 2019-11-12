package com.liver_rus.Battleships.Client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.*;

class FleetTest {

    Fleet fleet;

    @BeforeEach
    void createShipsOnField(){
        fleet = new Fleet();
    }

    @Test
    void clear() {
        fleet.clear();
        assertEquals(0, fleet.getShipsOnField().size());
        fleet.add(Ship.createShip(new FieldCoord(3,4), Ship.Type.DESTROYER, Ship.Orientation.HORIZONTAL));
        fleet.add(Ship.createShip(new FieldCoord(2,1), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        fleet.add(Ship.createShip(new FieldCoord(10,10), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        fleet.clear();
        assertEquals(0, fleet.getShipsOnField().size());
    }

    @Test
    void remove() {
        LinkedHashSet<Ship> testShipsList = new LinkedHashSet<>();
        Ship shipForRemove = Ship.createShip(new FieldCoord(3,8), Ship.Type.CRUISER, Ship.Orientation.VERTICAL);
        testShipsList.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.HORIZONTAL));
        testShipsList.add(Ship.createShip(new FieldCoord(5,5), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        testShipsList.add(shipForRemove);
        testShipsList.add(Ship.createShip(new FieldCoord(4,0), Ship.Type.CRUISER, Ship.Orientation.HORIZONTAL));
        testShipsList.add(Ship.createShip(new FieldCoord(8,2), Ship.Type.SUBMARINE, Ship.Orientation.VERTICAL));
        for (Ship ship : testShipsList) {
            fleet.add(ship);
        }
        testShipsList.remove(shipForRemove);
        fleet.remove(shipForRemove);
        //System.out.println(testShipsList);
        //System.out.println(shipsOnField.getShipsOnField());
        assertIterableEquals(testShipsList, fleet.getShipsOnField());
    }

    @Test
    void add() {
        LinkedHashSet<Ship> testShipsList = new LinkedHashSet<>();
        testShipsList.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.HORIZONTAL));
        testShipsList.add(Ship.createShip(new FieldCoord(5,5), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        testShipsList.add(Ship.createShip(new FieldCoord(4,0), Ship.Type.CRUISER, Ship.Orientation.HORIZONTAL));
        testShipsList.add(Ship.createShip(new FieldCoord(8,2), Ship.Type.SUBMARINE, Ship.Orientation.VERTICAL));
        for (Ship ship : testShipsList) {
            fleet.add(ship);
        }
        assertIterableEquals(testShipsList, fleet.getShipsOnField());
    }

    @Test
    void findShip() {
        FieldCoord shipCoord = new FieldCoord(2,4);
        FieldCoord findCoord = new FieldCoord(2,6);
        FieldCoord wrongCoord = new FieldCoord(5,5);
        Ship findingShip = Ship.createShip(shipCoord, Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.VERTICAL);
        fleet.add(findingShip);
        fleet.add(Ship.createShip(new FieldCoord(7,6), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        fleet.add(Ship.createShip(new FieldCoord(9,7), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        assertEquals(findingShip, fleet.findShip(new MessageAdapterFieldCoord(findCoord)));
        assertEquals(null, fleet.findShip(wrongCoord));
    }


    @Test
    void toManyShipsException() {
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.HORIZONTAL));
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.HORIZONTAL));
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.HORIZONTAL));
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.HORIZONTAL));
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.HORIZONTAL));
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.HORIZONTAL));
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.HORIZONTAL));
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.HORIZONTAL)));
    }
}