package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.NetworkClientEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;

public class NetworkEndMatchEvent implements NetworkClientEvent {
    //player show who win
    private final PlayerType playerType;

    public NetworkEndMatchEvent(PlayerType playerType) {
        this.playerType = playerType;
    }

    @Override
    public String proceed(GUIActions action) {
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.END_MATCH +  playerType.getString();
    }
}
