package com.liver_rus.Battleships.NetworkEvent.Server;

import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkCommandNotAcceptedEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkServerEvent;

public class NetworkUnknownCommandServerEvent implements NetworkServerEvent {
    private final String unknownMsg;

    public NetworkUnknownCommandServerEvent(String unknownMsg) {
        this.unknownMsg = unknownMsg;
    }

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer string = new Answer();
        string.add(metaInfo.getActivePlayer(), new NetworkCommandNotAcceptedEvent(
                "Unknown command from client"  + unknownMsg));
        return string;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.UNKNOWN_COMMAND + ":" +  unknownMsg;
    }
}
