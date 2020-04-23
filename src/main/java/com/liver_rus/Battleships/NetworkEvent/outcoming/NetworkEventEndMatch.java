package com.liver_rus.Battleships.NetworkEvent.outcoming;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventClient;
import com.liver_rus.Battleships.NetworkEvent.PlayerType;

public class NetworkEventEndMatch implements NetworkEventClient {
    //player show who win
    private final PlayerType playerType;

    public NetworkEventEndMatch(PlayerType playerType) {
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
