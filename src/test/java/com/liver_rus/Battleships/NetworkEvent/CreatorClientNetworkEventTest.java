package com.liver_rus.Battleships.NetworkEvent;

import com.liver_rus.Battleships.NetworkEvent.outcoming.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreatorClientNetworkEventTest {
    NetworkEventClient event;
    static CreatorClientNetworkEvent eventCreator;

    @BeforeAll
    static void beforeAll() {
        eventCreator = new CreatorClientNetworkEvent();
    }

    @Test
    void creationValidEvents() {
        event = eventCreator.deserializeMessage(CANNOT_DEPLOY + "321H");
        assertTrue(event instanceof NetworkEventCannotDeploy);

        event = eventCreator.deserializeMessage(CAN_SHOOT);
        assertTrue(event instanceof NetworkEventCanShoot);

        event = eventCreator.deserializeMessage(COMMAND_NOT_ACCEPTED + "REASON FROM SERVER");
        assertTrue(event instanceof NetworkEventCommandNotAccepted);

        event = eventCreator.deserializeMessage(DEPLOY + "21111");
        assertTrue(event instanceof NetworkEventDeploy);

        event = eventCreator.deserializeMessage(DO_DISCONNECT);
        assertTrue(event instanceof NetworkEventDoDisconnect);

        event = eventCreator.deserializeMessage(NOT_START_REMATCH);
        assertTrue(event instanceof NetworkEventNotStartRematch);

        event = eventCreator.deserializeMessage(HIT + "25" + YOU );
        assertTrue(event instanceof NetworkEventDrawHit);

        event = eventCreator.deserializeMessage(MISS + "46" + ENEMY);
        assertTrue(event instanceof NetworkEventDrawMiss);

        event = eventCreator.deserializeMessage(DRAW_SHIP + "360H" + ENEMY);
        assertTrue(event instanceof NetworkEventDrawShip);

        event = eventCreator.deserializeMessage(SET_ENEMY_NAME + "ANY NAME");
        assertTrue(event instanceof NetworkEventSetEnemyName);

        event = eventCreator.deserializeMessage(START_REMATCH);
        assertTrue(event instanceof NetworkEventStartRematch);

        event = eventCreator.deserializeMessage(WAITING_FOR_SECOND_PLAYER);
        assertTrue(event instanceof NetworkEventWaitingSecondPlayer);

        event = eventCreator.deserializeMessage(WAITING_FOR_SECOND_PLAYER + "ww");
        assertTrue(event instanceof NetworkEventWaitingSecondPlayer);

        event = eventCreator.deserializeMessage(END_MATCH + ENEMY);
        assertTrue(event instanceof NetworkEventEndMatch);

        event = eventCreator.deserializeMessage(DRAW_SHIP + "113V" + YOU);
        assertTrue(event instanceof NetworkEventDrawShip);
    }

    @Test
    void creationInvalidEvents() {
        event = eventCreator.deserializeMessage(DEPLOY + "1123H");
        assertTrue(event instanceof NetworkEventUnknownCommandClient);

        event = eventCreator.deserializeMessage(CANNOT_DEPLOY + "3213H");
        assertTrue(event instanceof NetworkEventUnknownCommandClient);

        event = eventCreator.deserializeMessage(CAN_SHOOT + " ");
        assertTrue(event instanceof NetworkEventUnknownCommandClient);

        event = eventCreator.deserializeMessage("FFF" + COMMAND_NOT_ACCEPTED + "REASON FROM SERVER");
        assertTrue(event instanceof NetworkEventUnknownCommandClient);

        event = eventCreator.deserializeMessage(DEPLOY + "211111");
        assertTrue(event instanceof NetworkEventUnknownCommandClient);

        event = eventCreator.deserializeMessage("DO" + DO_DISCONNECT + "1");
        assertTrue(event instanceof NetworkEventUnknownCommandClient);

        event = eventCreator.deserializeMessage(" " + NOT_START_REMATCH);
        assertTrue(event instanceof NetworkEventUnknownCommandClient);

        event = eventCreator.deserializeMessage(HIT + "YOUR" + "25");
        assertTrue(event instanceof NetworkEventUnknownCommandClient);

        event = eventCreator.deserializeMessage(HIT + YOU + "233");
        assertTrue(event instanceof NetworkEventUnknownCommandClient);

        event = eventCreator.deserializeMessage(MISS + "ME" + "46");
        assertTrue(event instanceof NetworkEventUnknownCommandClient);

        event = eventCreator.deserializeMessage(MISS + ENEMY + "4");
        assertTrue(event instanceof NetworkEventUnknownCommandClient);

        event = eventCreator.deserializeMessage(DRAW_SHIP + ENEMY + "3603H");
        assertTrue(event instanceof NetworkEventUnknownCommandClient);

        event = eventCreator.deserializeMessage("T" + SET_ENEMY_NAME + "ANY NAME");
        assertTrue(event instanceof NetworkEventUnknownCommandClient);
    }
}