package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.NetworkClientEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;

public class NetworkDoDisconnectEvent implements NetworkClientEvent {

    @Override
    public String proceed(GUIActions action) {
        action.disconnect();
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.DO_DISCONNECT;
    }
}