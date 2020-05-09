package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;

//client do nothing
public class CommandNotAcceptedNetworkEvent implements ClientNetworkEvent {
    private final String reason;

    public CommandNotAcceptedNetworkEvent(String reason) {
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
