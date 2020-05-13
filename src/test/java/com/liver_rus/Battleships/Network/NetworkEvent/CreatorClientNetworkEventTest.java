package com.liver_rus.Battleships.Network.NetworkEvent;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.CreatorClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreatorClientNetworkEventTest {
    CreatorClientNetworkEvent eventCreator;

    @BeforeEach
    void before() {
        eventCreator = new CreatorClientNetworkEvent();
    }

    @Test
    void creationValidEvents() {
        checkDeserialize(CANNOT_DEPLOY + "321H", CannotDeployNetworkEvent.class);
        checkDeserialize(CAN_SHOOT  , CanShootNetworkEvent.class);
        checkDeserialize(COMMAND_NOT_ACCEPTED + "REASON FROM SERVER"  , CommandNotAcceptedNetworkEvent.class);
        checkDeserialize(DEPLOY + "21111"  , DeployNetworkEvent.class);
        checkDeserialize(DO_DISCONNECT  , DoDisconnectNetworkEvent.class);
        checkDeserialize(START_MATCH, StartMatchStatusNetworkEvent.class);
        checkDeserialize(HIT + "25" + YOU   , DrawHitNetworkEvent.class);
        checkDeserialize(MISS + "46" + ENEMY  , DrawMissNetworkEvent.class);
        checkDeserialize(NEAR + "56" + ENEMY  , DrawNearNetworkEvent.class);
        checkDeserialize(DRAW_SHIP + "360H" + ENEMY  , DrawShipNetworkEvent.class);
        checkDeserialize(SET_ENEMY_NAME + "ANY NAME"  , SetEnemyNameNetworkEvent.class);
        checkDeserialize(ASK_REMATCH, AskForRematchNetworkEvent.class);
        checkDeserialize(WAIT, WaitingSecondPlayerNetworkEvent.class);
        checkDeserialize(WAIT + "ww"  , WaitingSecondPlayerNetworkEvent.class);
        checkDeserialize(END_MATCH + ENEMY  , EndMatchNetworkEvent.class);
        checkDeserialize(DRAW_SHIP + "113V" + YOU  , DrawShipNetworkEvent.class);
    }

    @Test
    void creationInvalidEvents() {
        checkDeserialize(DEPLOY + "1123H", UnknownCommandClientNetworkEvent.class);
        checkDeserialize(CANNOT_DEPLOY + "3213H", UnknownCommandClientNetworkEvent.class);
        checkDeserialize(CAN_SHOOT + " ", UnknownCommandClientNetworkEvent.class);
        checkDeserialize("FFF" + COMMAND_NOT_ACCEPTED + "REASON FROM SERVER", UnknownCommandClientNetworkEvent.class);
        checkDeserialize(DEPLOY + "211111", UnknownCommandClientNetworkEvent.class);
        checkDeserialize("DO" + DO_DISCONNECT + "1", UnknownCommandClientNetworkEvent.class);
        checkDeserialize(" " + START_MATCH, UnknownCommandClientNetworkEvent.class);
        checkDeserialize(HIT + "YOUR" + "25", UnknownCommandClientNetworkEvent.class);
        checkDeserialize(HIT + YOU + "233", UnknownCommandClientNetworkEvent.class);
        checkDeserialize(MISS + "ME" + "46", UnknownCommandClientNetworkEvent.class);
        checkDeserialize(MISS + ENEMY + "4", UnknownCommandClientNetworkEvent.class);
        checkDeserialize(DRAW_SHIP + ENEMY + "3603H", UnknownCommandClientNetworkEvent.class);
        checkDeserialize("T" + SET_ENEMY_NAME + "ANY NAME", UnknownCommandClientNetworkEvent.class);
    }
    
    @SuppressWarnings("rawtypes")
    private void checkDeserialize(String msg, Class expectedClass) {
        ClientNetworkEvent event = eventCreator.deserializeMessage(msg);
        assertEquals(expectedClass, event.getClass());
    }
}