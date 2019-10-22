package com.liver_rus.Battleships.Client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameEngineTest {

    GameEngine gameEngine;

    @BeforeEach
    void setup() {
        gameEngine = new GameEngine();
    }

    @Test
    void test() {
        gameEngine.addShipOnField(new Ship(new FieldCoord(1, 8), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        gameEngine.addShipOnField(new Ship(new FieldCoord(3, 2), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        gameEngine.addShipOnField(new Ship(new FieldCoord(1, 1), Ship.Type.DESTROYER, Ship.Orientation.VERTICAL));
        gameEngine.addShipOnField(new Ship(new FieldCoord(3, 4), Ship.Type.DESTROYER, Ship.Orientation.HORIZONTAL));
        gameEngine.addShipOnField(new Ship(new FieldCoord(2, 6), Ship.Type.CRUISER, Ship.Orientation.HORIZONTAL));
        gameEngine.addShipOnField(new Ship(new FieldCoord(7, 4), Ship.Type.BATTLESHIP, Ship.Orientation.VERTICAL));
        gameEngine.addShipOnField(new Ship(new FieldCoord(9, 1), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.VERTICAL));

        assertEquals(7, gameEngine.getMyField().getShips().getShipsLeft());

        gameEngine.setPhase(GameEngine.Phase.FLEET_IS_DEPLOYED);

        gameEngine.setPhase(GameEngine.Phase.MAKE_SHOT);

        //gameEngine.setShootCoord(FieldCoord(Random.nextInt(), event.getSceneY(), false););

        gameEngine.setPhase(GameEngine.Phase.TAKE_SHOT);
    }
}