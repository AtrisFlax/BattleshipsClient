package com.liver_rus.Battleships.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkClientEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.PlayerType;

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
