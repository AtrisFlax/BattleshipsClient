package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;

public class WaitingSecondPlayerNetworkEvent implements ClientNetworkEvent {

    private final String reason;

    public WaitingSecondPlayerNetworkEvent(String reason) {
        this.reason = reason;
    }

    @Override
    public String proceed(GUIActions action) {
        action.waitSecondPlayer(reason);
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.WAIT;
    }
}
