package com.liver_rus.Battleships.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkClientEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;

//client do nothing
public class NetworkCommandNotAcceptedEvent implements NetworkClientEvent {
    private final String reason;

    public NetworkCommandNotAcceptedEvent(String reason) {
        this.reason = reason;
    }

    @Override
    public String proceed(GUIActions action) {
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.COMMAND_NOT_ACCEPTED + reason;
    }

}
