package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.GamePrimitives.FleetCounter;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FleetCounterTest {
    private FleetCounter fleetCounter;

    @BeforeEach
    void createNewArrangeFleetHolder() {
        fleetCounter = new FleetCounter();
    }

    @Test
    void shipLefAfterPop() {
        fleetCounter.popShip(Ship.Type.DESTROYER);
        fleetCounter.popShip(Ship.Type.AIRCRAFT_CARRIER);
        fleetCounter.popShip(Ship.Type.AIRCRAFT_CARRIER);
        fleetCounter.popShip(Ship.Type.DESTROYER);
        fleetCounter.popShip(Ship.Type.AIRCRAFT_CARRIER);
        fleetCounter.popShip(Ship.Type.CRUISER);
        fleetCounter.popShip(Ship.Type.DESTROYER);
        fleetCounter.popShip(Ship.Type.DESTROYER);
        fleetCounter.popShip(Ship.Type.CRUISER);
        fleetCounter.popShip(Ship.Type.CRUISER);
        fleetCounter.popShip(Ship.Type.DESTROYER);
        //shipsLeft = 2-SUBMARINEs +  1-BATTLESHIP = 3
        assertEquals(3, fleetCounter.getShipsLeft());
        fleetCounter.popShip(Ship.Type.BATTLESHIP);
        fleetCounter.popShip(Ship.Type.SUBMARINE);
        //shipsLeft = 1-SUBMARINE = 1
        assertEquals(1, fleetCounter.getShipsLeft());
        fleetCounter.popShip(Ship.Type.SUBMARINE);
        //shipsLeft = NO LEFT = 0
        assertEquals(0, fleetCounter.getShipsLeft());
    }

    //ship left after new ArrangeFleetHolder()
    @Test
    void getShipsLeftWithoutPopShip() {
        final int EXPECTED_SHIP_LEFT = 7;
        assertEquals(EXPECTED_SHIP_LEFT, fleetCounter.getShipsLeft());
    }

    //sequential pop and check amount left
    @Test
    void sequentialPop() {
        int NO_MORE_SHIP_FOR_EXTRACTION = -1;
        assertAll("Should return amount left ships by Type after sequential pop",
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.AIRCRAFT_CARRIER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.AIRCRAFT_CARRIER)),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.CRUISER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.CRUISER)),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.BATTLESHIP)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.BATTLESHIP)),
                () -> assertEquals(1, fleetCounter.popShip(Ship.Type.DESTROYER)),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.DESTROYER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.DESTROYER)),
                () -> assertEquals(1, fleetCounter.popShip(Ship.Type.SUBMARINE)),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.SUBMARINE)),

                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.AIRCRAFT_CARRIER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.BATTLESHIP)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.CRUISER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.DESTROYER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.SUBMARINE))
        );
        assertEquals(0, fleetCounter.getShipsLeft());
    }

    @Test
    void nonSequentialPopLeftShipsByType() {
        int NO_MORE_SHIP_FOR_EXTRACTION = -1;
        assertAll("Should return amount left ships by Type after non sequential pop",
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.AIRCRAFT_CARRIER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.AIRCRAFT_CARRIER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.AIRCRAFT_CARRIER)),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.CRUISER)),
                () -> assertEquals(1, fleetCounter.popShip(Ship.Type.SUBMARINE)),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.SUBMARINE)),
                () -> assertEquals(1, fleetCounter.popShip(Ship.Type.DESTROYER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.CRUISER)),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.BATTLESHIP)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.AIRCRAFT_CARRIER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.BATTLESHIP)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.BATTLESHIP)),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.DESTROYER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.CRUISER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.DESTROYER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.DESTROYER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.BATTLESHIP)),

                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.AIRCRAFT_CARRIER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.BATTLESHIP)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.CRUISER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.DESTROYER)),
                () -> assertEquals(NO_MORE_SHIP_FOR_EXTRACTION, fleetCounter.popShip(Ship.Type.SUBMARINE))
        );
        assertEquals(0, fleetCounter.getShipsLeft());
    }
}