package com.liver_rus.Battleships.Client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientGameEngineTest {

    ClientGameEngine clientGameEngine;

    @BeforeEach
    void setup() {
        clientGameEngine = new ClientGameEngine();
    }

    @Test
    void test() {
        clientGameEngine.addShipOnField(Ship.createShip(new FieldCoord(1, 8), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        clientGameEngine.addShipOnField(Ship.createShip(new FieldCoord(3, 2), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        clientGameEngine.addShipOnField(Ship.createShip(new FieldCoord(1, 1), Ship.Type.DESTROYER, Ship.Orientation.VERTICAL));
        clientGameEngine.addShipOnField(Ship.createShip(new FieldCoord(3, 4), Ship.Type.DESTROYER, Ship.Orientation.HORIZONTAL));
        clientGameEngine.addShipOnField(Ship.createShip(new FieldCoord(2, 6), Ship.Type.CRUISER, Ship.Orientation.HORIZONTAL));
        clientGameEngine.addShipOnField(Ship.createShip(new FieldCoord(7, 4), Ship.Type.BATTLESHIP, Ship.Orientation.VERTICAL));
        clientGameEngine.addShipOnField(Ship.createShip(new FieldCoord(9, 1), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.VERTICAL));

        assertEquals(7, clientGameEngine.getGameField().getFleet().getShipsLeft());

        clientGameEngine.setGamePhase(ClientGameEngine.Phase.FLEET_IS_DEPLOYED);

        clientGameEngine.setGamePhase(ClientGameEngine.Phase.MAKE_SHOT);

        //gameEngine.setShootCoord(FieldCoord(Random.nextInt(), event.getSceneY(), false););

        clientGameEngine.setGamePhase(ClientGameEngine.Phase.TAKE_SHOT);
    }
}