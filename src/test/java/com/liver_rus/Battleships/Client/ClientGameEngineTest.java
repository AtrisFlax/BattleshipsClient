package com.liver_rus.Battleships.Client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClientGameEngineTest {
    ClientGameEngine gameEngine;

    @BeforeEach
    void setup() {
        gameEngine = new ClientGameEngine();
    }

    @Test
    void test() {
        ArrayList<Ship> ships = new ArrayList<>();
        ships.add(Ship.createShip(new FieldCoord(1, 8), Ship.Type.SUBMARINE, true));
        ships.add(Ship.createShip(new FieldCoord(3, 2), Ship.Type.SUBMARINE, true));
        ships.add(Ship.createShip(new FieldCoord(1, 1), Ship.Type.DESTROYER, false));
        ships.add(Ship.createShip(new FieldCoord(3, 4), Ship.Type.DESTROYER, true));
        ships.add(Ship.createShip(new FieldCoord(2, 6), Ship.Type.CRUISER, true));
        ships.add(Ship.createShip(new FieldCoord(7, 4), Ship.Type.BATTLESHIP, false));
        ships.add(Ship.createShip(new FieldCoord(9, 1), Ship.Type.AIRCRAFT_CARRIER, false));
        assertEquals(7, gameEngine.getGameField().getFleet().getShipsLeft());
        for (Ship ship : ships) {
        gameEngine.addShipOnField(ship);
    }
    assertEquals("180H|320H|111V|341H|262H|743V|914V|", gameEngine.getShipsInfoForSend());
}

    @Test
    void anotherAddSequence() {
        ArrayList<Ship> ships = new ArrayList<>();
        ships.add(Ship.createShip(new FieldCoord(4, 3), Ship.Type.AIRCRAFT_CARRIER, false));
        ships.add(Ship.createShip(new FieldCoord(1, 1), Ship.Type.BATTLESHIP, false));
        ships.add(Ship.createShip(new FieldCoord(6, 1), Ship.Type.DESTROYER, false));
        ships.add(Ship.createShip(new FieldCoord(6, 4), Ship.Type.DESTROYER, false));
        ships.add(Ship.createShip(new FieldCoord(8, 1), Ship.Type.SUBMARINE, false));
        ships.add(Ship.createShip(new FieldCoord(3, 1), Ship.Type.SUBMARINE, false));
        ships.add(Ship.createShip(new FieldCoord(1, 9), Ship.Type.CRUISER, true));
        assertEquals(7, gameEngine.getGameField().getFleet().getShipsLeft());
        for (Ship ship : ships) {
            gameEngine.addShipOnField(ship);
        }
        assertEquals("434V|113V|611V|641V|810V|310V|192H|", gameEngine.getShipsInfoForSend());
    }

    @Test
    void outOfBounds() {
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> gameEngine.addShipOnField(Ship.createShip(new FieldCoord(9, 9), Ship.Type.AIRCRAFT_CARRIER, true)));
    }
}