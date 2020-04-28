package com.liver_rus.Battleships.Network.NetworkEvent;

import com.liver_rus.Battleships.Network.NetworkEvent.Server.CreatorServerNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Events.*;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.NetworkServerEvent;
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
        checkDeserialize(NetworkCommandConstant.DISCONNECT, NetworkDisconnectEvent.class);
        checkDeserialize(NetworkCommandConstant.NO_REMATCH, NetworkNoRematchEvent.class);
        checkDeserialize(NetworkCommandConstant.MY_NAME + "Player1", NetworkMyNameEvent.class);
        checkDeserialize(NetworkCommandConstant.MY_NAME, NetworkMyNameEvent.class);
        checkDeserialize(NetworkCommandConstant.SHOT + "56", NetworkShotEvent.class);
        checkDeserialize(NetworkCommandConstant.TRY_DEPLOY_SHIP + "553H", NetworkTryDeployShipEvent.class);
        checkDeserialize(NetworkCommandConstant.TRY_REMATCH, NetworkTryRematchEvent.class);
        checkDeserialize(NetworkCommandConstant.NO_REMATCH, NetworkNoRematchEvent.class);
        checkDeserialize(NetworkCommandConstant.RESET_FLEET_WHILE_DEPLOY, NetworkResetFleetWhileDeployEvent.class);
    }

    @Test
    void creationInvalidEvents() {
        checkDeserialize("DISSSSONET", NetworkUnknownCommandServerEvent.class);
        checkDeserialize("DIS", NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP + "2223H", NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP, NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP + "23H", NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP + "232", NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.SHOT + "854", NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.SHOT, NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.SHOT + "1", NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.SHOT + "1", NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.TRY_DEPLOY_SHIP + "2\23H", NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.TRY_DEPLOY_SHIP + "23", NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.TRY_DEPLOY_SHIP, NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.TRY_REMATCH + "|", NetworkUnknownCommandServerEvent.class);
        checkDeserialize(NetworkCommandConstant.NO_REMATCH + "||", NetworkUnknownCommandServerEvent.class);
        checkDeserialize("  ", NetworkUnknownCommandServerEvent.class);
        checkDeserialize("| | | |", NetworkUnknownCommandServerEvent.class);
    }

    @SuppressWarnings("rawtypes")
    private void checkDeserialize(String msg, Class expectedClass) {
        NetworkServerEvent event = eventCreator.deserializeMessage(msg);
        assertEquals(expectedClass, event.getClass());
    }
}