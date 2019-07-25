package com.liver_rus.Battleships.Client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ArrangeFleetHolderTest {

    private ArrangeFleetHolder arrangeFleetHolder;

    @BeforeEach
    void createNewArrangeFleetHolder() {
        arrangeFleetHolder = new ArrangeFleetHolder();
    }

    @DisplayName("ship left after pops")
    @Test
    void getShipsLeft() {
        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER));
        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIED));
        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIED));
        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER));
        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIED));
        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER));
        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER));
        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER));
        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER));
        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER));
        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER));
        //shipsLeft = 2-SUBMARINEs +  1-BATTLESHIP = 3
        assertEquals(3, arrangeFleetHolder.getShipsLeft());

        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP));
        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE));
        //shipsLeft = 1-SUBMARINE = 1
        assertEquals(1, arrangeFleetHolder.getShipsLeft());

        arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE));
        //shipsLeft = NO LEFT = 0
        assertEquals(0, arrangeFleetHolder.getShipsLeft());
    }

    @DisplayName("ship left after new ArrangeFleetHolder()")
    @Test
    void getShipsLeftWithoutPopShip() {
        assertEquals(ArrangeFleetHolder.TOTAL_SHIPS_AMOUNT, arrangeFleetHolder.getShipsLeft());
    }

    @DisplayName("sequential pop and check amount left")
    @Test
    void sequentialPopLeftShipsByType() {
        assertAll("Should return amount left ships by Type after sequential pop",
                () -> assertEquals(0, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIED))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIED))),
                () -> assertEquals(0, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(0, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(0, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE))),
                () -> assertEquals(0, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE))),

                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIED))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE)))
        );
    }

    @DisplayName("non sequential pop and check amount left")
    @Test
    void nonSequentialPopLeftShipsByType() {
        assertAll("Should return amount left ships by Type after non sequential pop",
                () -> assertEquals(0, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIED))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIED))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIED))),
                () -> assertEquals(0, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE))),
                () -> assertEquals(0, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE))),
                () -> assertEquals(1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(0, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIED))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(0, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),

                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.AIRCRAFT_CARRIED))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.BATTLESHIP))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.CRUISER))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.DESTROYER))),
                () -> assertEquals(-1, arrangeFleetHolder.popShip(Ship.Type.shipTypeToInt(Ship.Type.SUBMARINE)))
        );
    }
}