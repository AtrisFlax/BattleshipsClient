package com.liver_rus.Battleships.NetworkEvent;

import com.liver_rus.Battleships.NetworkEvent.Client.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreatorClientNetworkEventTest {
    CreatorClientNetworkEvent eventCreator;

    @BeforeEach
    void before() {
        eventCreator = new CreatorClientNetworkEvent();
    }

    @Test
    void creationValidEvents() {
        checkDeserialize(CANNOT_DEPLOY + "321H", NetworkCannotDeployEvent.class);
        checkDeserialize(CAN_SHOOT  , NetworkCanShootEvent.class);
        checkDeserialize(COMMAND_NOT_ACCEPTED + "REASON FROM SERVER"  , NetworkCommandNotAcceptedEvent.class);
        checkDeserialize(DEPLOY + "21111"  , NetworkDeployEvent.class);
        checkDeserialize(DO_DISCONNECT  , NetworkDoDisconnectEvent.class);
        checkDeserialize(NOT_START_REMATCH  , NetworkNotStartRematchEvent.class);
        checkDeserialize(HIT + "25" + YOU   , NetworkDrawHitEvent.class);
        checkDeserialize(MISS + "46" + ENEMY  , NetworkDrawMissEvent.class);
        checkDeserialize(DRAW_SHIP + "360H" + ENEMY  , NetworkDrawShipEvent.class);
        checkDeserialize(SET_ENEMY_NAME + "ANY NAME"  , NetworkSetEnemyNameEvent.class);
        checkDeserialize(START_REMATCH  , NetworkStartRematchEvent.class);
        checkDeserialize(WAITING_FOR_SECOND_PLAYER  , NetworkWaitingSecondPlayerEvent.class);
        checkDeserialize(WAITING_FOR_SECOND_PLAYER + "ww"  , NetworkWaitingSecondPlayerEvent.class);
        checkDeserialize(END_MATCH + ENEMY  , NetworkEndMatchEvent.class);
        checkDeserialize(DRAW_SHIP + "113V" + YOU  , NetworkDrawShipEvent.class);
    }

    @Test
    void creationInvalidEvents() {
        checkDeserialize(DEPLOY + "1123H", NetworkUnknownCommandClientEvent.class);
        checkDeserialize(CANNOT_DEPLOY + "3213H", NetworkUnknownCommandClientEvent.class);
        checkDeserialize(CAN_SHOOT + " ", NetworkUnknownCommandClientEvent.class);
        checkDeserialize("FFF" + COMMAND_NOT_ACCEPTED + "REASON FROM SERVER", NetworkUnknownCommandClientEvent.class);
        checkDeserialize(DEPLOY + "211111", NetworkUnknownCommandClientEvent.class);
        checkDeserialize("DO" + DO_DISCONNECT + "1", NetworkUnknownCommandClientEvent.class);
        checkDeserialize(" " + NOT_START_REMATCH, NetworkUnknownCommandClientEvent.class);
        checkDeserialize(HIT + "YOUR" + "25", NetworkUnknownCommandClientEvent.class);
        checkDeserialize(HIT + YOU + "233", NetworkUnknownCommandClientEvent.class);
        checkDeserialize(MISS + "ME" + "46", NetworkUnknownCommandClientEvent.class);
        checkDeserialize(MISS + ENEMY + "4", NetworkUnknownCommandClientEvent.class);
        checkDeserialize(DRAW_SHIP + ENEMY + "3603H", NetworkUnknownCommandClientEvent.class);
        checkDeserialize("T" + SET_ENEMY_NAME + "ANY NAME", NetworkUnknownCommandClientEvent.class);
    }
    
    @SuppressWarnings("rawtypes")
    private void checkDeserialize(String msg, Class expectedClass) {
        NetworkClientEvent event = eventCreator.deserializeMessage(msg);
        assertEquals(expectedClass, event.getClass());
    }
}