package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;

public class UnknownCommandClientNetworkEvent implements ClientNetworkEvent {
    private final String unknownMsg;

    public UnknownCommandClientNetworkEvent(String unknownMsg) {
        this.unknownMsg = unknownMsg;
    }

    @Override
    public String proceed(GUIActions action) {
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.UNKNOWN_COMMAND + ":" +  unknownMsg;
    }
}
