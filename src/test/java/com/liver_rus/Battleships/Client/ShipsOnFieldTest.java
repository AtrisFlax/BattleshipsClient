package com.liver_rus.Battleships.Client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class ShipsOnFieldTest {

    ShipsOnField shipsOnField;

    @BeforeEach
    void createShipsOnField(){
        shipsOnField = new ShipsOnField();
    }

    @Test
    void clear() {
        shipsOnField.clear();
        assertEquals(0, shipsOnField.getShipsOnField().size());

        shipsOnField.add(new Ship(new FieldCoord(3,4), Ship.Type.DESTROYER, Ship.Orientation.HORIZONTAL));
        shipsOnField.add(new Ship(new FieldCoord(2,1), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        shipsOnField.add(new Ship(new FieldCoord(10,10), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        shipsOnField.clear();
        assertEquals(0, shipsOnField.getShipsOnField().size());
    }

    @Test
    void remove() {
        LinkedHashSet<Ship> testShipsList = new LinkedHashSet<>();

        Ship shipForRemove = new Ship(new FieldCoord(3,8), Ship.Type.CRUISER, Ship.Orientation.VERTICAL);

        testShipsList.add(new Ship(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIED, Ship.Orientation.HORIZONTAL));
        testShipsList.add(new Ship(new FieldCoord(5,5), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        testShipsList.add(shipForRemove);
        testShipsList.add(new Ship(new FieldCoord(4,0), Ship.Type.CRUISER, Ship.Orientation.HORIZONTAL));
        testShipsList.add(new Ship(new FieldCoord(8,2), Ship.Type.SUBMARINE, Ship.Orientation.VERTICAL));

        for (Ship ship : testShipsList) {
            shipsOnField.add(ship);
        }

        testShipsList.remove(shipForRemove);
        shipsOnField.remove(shipForRemove);

        System.out.println(testShipsList);
        System.out.println(shipsOnField.getShipsOnField());

        assertIterableEquals(testShipsList, shipsOnField.getShipsOnField());


    }

    @Test
    void add() {
        LinkedHashSet<Ship> testShipsList = new LinkedHashSet<>();

        testShipsList.add(new Ship(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIED, Ship.Orientation.HORIZONTAL));
        testShipsList.add(new Ship(new FieldCoord(5,5), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        testShipsList.add(new Ship(new FieldCoord(4,0), Ship.Type.CRUISER, Ship.Orientation.HORIZONTAL));
        testShipsList.add(new Ship(new FieldCoord(8,2), Ship.Type.SUBMARINE, Ship.Orientation.VERTICAL));

        for (Ship ship : testShipsList) {
            shipsOnField.add(ship);
        }

        assertIterableEquals(testShipsList, shipsOnField.getShipsOnField());
    }

    @Test
    void findShip() {
        FieldCoord shipCoord = new FieldCoord(2,4);
        FieldCoord findCoord = new FieldCoord(2,6);
        FieldCoord wrongCoord = new FieldCoord(5,5);
        shipsOnField.add(new Ship(new FieldCoord(3,3), Ship.Type.DESTROYER, Ship.Orientation.HORIZONTAL));
        Ship findingShip = new Ship(shipCoord, Ship.Type.AIRCRAFT_CARRIED, Ship.Orientation.VERTICAL);
        shipsOnField.add(findingShip);
        shipsOnField.add(new Ship(new FieldCoord(9,7), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        assertEquals(findingShip, shipsOnField.findShip(findCoord));
        assertEquals(null, shipsOnField.findShip(wrongCoord));
    }
}