package com.liver_rus.Battleships.Client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FleetTest {
    Fleet fleet;

    @BeforeEach
    void createShipsOnField(){
        fleet = new Fleet();
    }


    //TODO более понятные имена
    @Test
    void clear() {
        fleet.clear();
        assertEquals(0, fleet.getShipsOnField().size());
        fleet.add(Ship.createShip(new FieldCoord(3,4), Ship.Type.DESTROYER, true));
        fleet.add(Ship.createShip(new FieldCoord(2,1), Ship.Type.SUBMARINE, true));
        fleet.add(Ship.createShip(new FieldCoord(10,10), Ship.Type.SUBMARINE, true));
        fleet.clear();
        assertEquals(0, fleet.getShipsOnField().size());
    }

    @Test
    void remove() {
        //TODO
        //Linked List одинаковый порядок выдачи дают
        //LinkedHashSet плохо
        //нужно только добавить удалить и итерировать
        //непривязыватся к реализации использовать интерфейсы (LinkedHashSet<Ship>)
        ArrayList<Ship> testShipsList = new ArrayList<>();
        Ship shipForRemove = Ship.createShip(new FieldCoord(3,8), Ship.Type.CRUISER, false);
        testShipsList.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, true));
        testShipsList.add(Ship.createShip(new FieldCoord(5,5), Ship.Type.SUBMARINE, true));
        testShipsList.add(shipForRemove);
        testShipsList.add(Ship.createShip(new FieldCoord(4,0), Ship.Type.CRUISER, true));
        testShipsList.add(Ship.createShip(new FieldCoord(8,2), Ship.Type.SUBMARINE, false));
        for (Ship ship : testShipsList) {
            fleet.add(ship);
        }
        testShipsList.remove(shipForRemove);
        fleet.remove(shipForRemove);
        assertIterableEquals(testShipsList, fleet.getShipsOnField());
    }

    @Test
    void add() {
        ArrayList<Ship> testShipsList = new ArrayList<>();
        testShipsList.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, true));
        testShipsList.add(Ship.createShip(new FieldCoord(5,5), Ship.Type.SUBMARINE, true));
        testShipsList.add(Ship.createShip(new FieldCoord(4,0), Ship.Type.CRUISER, true));
        testShipsList.add(Ship.createShip(new FieldCoord(8,2), Ship.Type.SUBMARINE, false));
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
        Ship findingShip = Ship.createShip(shipCoord, Ship.Type.AIRCRAFT_CARRIER, false);
        fleet.add(findingShip);
        fleet.add(Ship.createShip(new FieldCoord(7,6), Ship.Type.SUBMARINE, true));
        fleet.add(Ship.createShip(new FieldCoord(9,7), Ship.Type.SUBMARINE, true));
        assertEquals(findingShip, fleet.findShip(new MessageAdapterFieldCoord(findCoord)));
        assertEquals(null, fleet.findShip(wrongCoord));
    }


    //TODO custom exepctions
    @Test
    void toManyShipsException() {
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, true));
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, true));
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, true));
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, true));
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, true));
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, true));
        fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, true));
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> fleet.add(Ship.createShip(new FieldCoord(2,3), Ship.Type.AIRCRAFT_CARRIER, true)));
    }
}