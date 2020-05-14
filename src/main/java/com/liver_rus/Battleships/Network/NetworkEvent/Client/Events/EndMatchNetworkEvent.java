package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;

import java.util.List;

public class EndMatchNetworkEvent implements ClientNetworkEvent {
    //player show who win
    private final PlayerType playerType;

    public EndMatchNetworkEvent(PlayerType playerType) {
        this.playerType = playerType;
    }

    @Override
    public List<ServerNetworkEvent> proceed(GUIActions action) {
        action.endMatch(playerType);
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.END_MATCH +  playerType.getString();
    }
}
