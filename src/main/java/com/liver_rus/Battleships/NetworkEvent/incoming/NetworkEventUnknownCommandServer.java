package com.liver_rus.Battleships.NetworkEvent.incoming;

import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventServer;
import com.liver_rus.Battleships.NetworkEvent.outcoming.NetworkEventCommandNotAccepted;

public class NetworkEventUnknownCommandServer implements NetworkEventServer {
    private final String unknownMsg;

    public NetworkEventUnknownCommandServer(String unknownMsg) {
        this.unknownMsg = unknownMsg;
    }

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer string = new Answer();
        string.add(metaInfo.getActivePlayer(), new NetworkEventCommandNotAccepted(
                "Unknown command from client"  + unknownMsg));
        return string;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.UNKNOWN_COMMAND + ":" +  unknownMsg;
    }
}
