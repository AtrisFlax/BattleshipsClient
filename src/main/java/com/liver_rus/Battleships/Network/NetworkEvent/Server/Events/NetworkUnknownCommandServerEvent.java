package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NetworkCommandNotAcceptedEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.NetworkServerEvent;
import com.liver_rus.Battleships.Network.Server.MetaInfo;

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
