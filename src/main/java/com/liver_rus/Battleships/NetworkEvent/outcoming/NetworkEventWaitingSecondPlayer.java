package com.liver_rus.Battleships.NetworkEvent.outcoming;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventClient;

public class NetworkEventWaitingSecondPlayer implements NetworkEventClient {

    final String reason;

    public NetworkEventWaitingSecondPlayer(String reason) {
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
