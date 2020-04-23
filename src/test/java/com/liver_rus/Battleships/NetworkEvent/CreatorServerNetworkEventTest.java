package com.liver_rus.Battleships.NetworkEvent;

import com.liver_rus.Battleships.NetworkEvent.incoming.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreatorServerNetworkEventTest {
    NetworkEventServer event;
    static CreatorServerNetworkEvent eventCreator;

    @BeforeAll
    static void beforeAll() {
        eventCreator = new CreatorServerNetworkEvent();
    }

    @Test
    void creationValidEvents() {
        event = eventCreator.deserializeMessage(NetworkCommandConstant.DISCONNECT);
        assertTrue(event instanceof NetworkEventDisconnect);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.NO_REMATCH);
        assertTrue(event instanceof NetworkEventNoRematch);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.MY_NAME + "Player1");
        assertTrue(event instanceof NetworkEventMyName);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.MY_NAME);
        assertTrue(event instanceof NetworkEventMyName);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.SHOT + "56");
        assertTrue(event instanceof NetworkEventShot);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.TRY_DEPLOY_SHIP + "553H");
        assertTrue(event instanceof NetworkEventTryDeployShip);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.TRY_REMATCH);
        assertTrue(event instanceof NetworkEventTryRematch);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.NO_REMATCH);
        assertTrue(event instanceof NetworkEventNoRematch);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.RESET_FLEET_WHILE_DEPLOY);
        assertTrue(event instanceof NetworkEventResetFleetWhileDeploy);
    }

    @Test
    void creationInvalidEvents() {
        event = eventCreator.deserializeMessage("DISSSSONET");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage("DIS");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP + "2223H");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP);
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP + "23H");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP + "232");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.SHOT + "854");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.SHOT);
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.SHOT + "1");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.SHOT + "1");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.TRY_DEPLOY_SHIP + "2\23H");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.TRY_DEPLOY_SHIP + "23");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.TRY_DEPLOY_SHIP);
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.TRY_REMATCH + "|");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage(NetworkCommandConstant.NO_REMATCH + "||");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage("  ");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);

        event = eventCreator.deserializeMessage("| | | |");
        assertTrue(event instanceof NetworkEventUnknownCommandServer);
    }
}