package com.liver_rus.Battleships.NetworkEvent.outcoming;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventClient;

public class NetworkEventUnknownCommandClient implements NetworkEventClient {
    private final String unknownMsg;

    public NetworkEventUnknownCommandClient(String unknownMsg) {
        this.unknownMsg = unknownMsg;
    }

    @Override
    public String proceed(GUIActions action) {
        /*
        Answers answers = new Answers();
        answers.add(metaInfo.getActivePlayer(), new NetworkEventCommandNotAccepted(
                "Unknown command from client"  + unknownMsg));
        return answers;

         */
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.UNKNOWN_COMMAND + ":" +  unknownMsg;
    }
}
