package com.liver_rus.Battleships.NetworkEvent.Server;

import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkDoDisconnectEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkServerEvent;

public class NetworkDisconnectEvent implements NetworkServerEvent {

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer string = new Answer();
        string.add(metaInfo.getActivePlayer(), new NetworkDoDisconnectEvent());
        string.add(metaInfo.getPassivePlayer(), new NetworkDoDisconnectEvent());
        return string;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.DISCONNECT;
    }

}
