package com.liver_rus.Battleships.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventClient;

//client do nothing
public class NetworkEventCommandNotAccepted implements NetworkEventClient {

    String reason;

    public NetworkEventCommandNotAccepted(String reason) {
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
