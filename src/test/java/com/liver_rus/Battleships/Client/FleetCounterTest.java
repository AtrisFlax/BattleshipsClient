package com.liver_rus.Battleships.Client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FleetCounterTest {
    private FleetCounter fleetCounter;

    @BeforeEach
    void createNewArrangeFleetHolder() {
        fleetCounter = new FleetCounter();
    }

    @DisplayName("ship left after pops")
    @Test
    void getShipsLeft() {
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER));
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIER));
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIER));
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER));
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIER));
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER));
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER));
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER));
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER));
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER));
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER));
        //shipsLeft = 2-SUBMARINEs +  1-BATTLESHIP = 3
        assertEquals(3, fleetCounter.getShipsLeft());
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP));
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE));
        //shipsLeft = 1-SUBMARINE = 1
        assertEquals(1, fleetCounter.getShipsLeft());
        fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE));
        //shipsLeft = NO LEFT = 0
        assertEquals(0, fleetCounter.getShipsLeft());
    }

    @DisplayName("ship left after new ArrangeFleetHolder()")
    @Test
    void getShipsLeftWithoutPopShip() {
        assertEquals(FleetCounter.NUM_MAX_SHIPS, fleetCounter.getShipsLeft());
    }

    @DisplayName("sequential pop and check amount left")
    @Test
    void sequentialPopLeftShipsByType() {
        assertAll("Should return amount left ships by Type after sequential pop",
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIER))),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE))),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE))),

                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE)))
        );

        assertEquals(0, fleetCounter.getShipsLeft());
    }

    @DisplayName("non sequential pop and check amount left")
    @Test
    void nonSequentialPopLeftShipsByType() {
        assertAll("Should return amount left ships by Type after non sequential pop",
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIER))),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE))),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE))),
                () -> assertEquals(1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(0, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),

                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, fleetCounter.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE)))
        );

        assertEquals(0, fleetCounter.getShipsLeft());
    }
}