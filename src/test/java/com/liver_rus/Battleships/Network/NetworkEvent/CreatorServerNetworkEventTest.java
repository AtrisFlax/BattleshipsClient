package com.liver_rus.Battleships.Network.NetworkEvent;

import com.liver_rus.Battleships.Network.NetworkEvent.Server.CreatorServerNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Events.*;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreatorServerNetworkEventTest {
    CreatorServerNetworkEvent eventCreator;

    @BeforeEach
    void beforeEach() {
        eventCreator = new CreatorServerNetworkEvent();
    }

    @Test
    void creationValidEvents() {
        checkDeserialize(NetworkCommandConstant.DISCONNECT, DisconnectNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.MY_NAME + "Player1", MyNameNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.MY_NAME, MyNameNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.SHOT + "56", ShotNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.TRY_DEPLOY_SHIP + "553H", TryDeployShipNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.REMATCH_ANSWER, TryRematchStateNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.RESET_FLEET_WHILE_DEPLOY, ResetFleetWhileDeployNetworkEvent.class);
    }

    @Test
    void creationInvalidEvents() {
        checkDeserialize("DISSSSONET", UnknownCommandServerNetworkEvent.class);
        checkDeserialize("DIS", UnknownCommandServerNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP + "2223H", UnknownCommandServerNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP, UnknownCommandServerNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP + "23H", UnknownCommandServerNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP + "232", UnknownCommandServerNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.SHOT + "854", UnknownCommandServerNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.SHOT, UnknownCommandServerNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.SHOT + "1", UnknownCommandServerNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.SHOT + "1", UnknownCommandServerNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.TRY_DEPLOY_SHIP + "2\23H", UnknownCommandServerNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.TRY_DEPLOY_SHIP + "23", UnknownCommandServerNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.TRY_DEPLOY_SHIP, UnknownCommandServerNetworkEvent.class);
        checkDeserialize(NetworkCommandConstant.REMATCH_ANSWER + "|", UnknownCommandServerNetworkEvent.class);
        checkDeserialize("  ", UnknownCommandServerNetworkEvent.class);
        checkDeserialize("| | | |", UnknownCommandServerNetworkEvent.class);
    }

    @SuppressWarnings("rawtypes")
    private void checkDeserialize(String msg, Class expectedClass) {
        ServerNetworkEvent event = eventCreator.deserializeMessage(msg);
        assertEquals(expectedClass, event.getClass());
    }
}