package com.liver_rus.Battleships.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkClientEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;

public class NetworkWaitingSecondPlayerEvent implements NetworkClientEvent {

    final String reason;

    public NetworkWaitingSecondPlayerEvent(String reason) {
        this.reason = reason;
    }

    @Override
    public String proceed(GUIActions action) {
        action.waitSecondPlayer(reason);
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.WAITING_FOR_SECOND_PLAYER;
    }

}
