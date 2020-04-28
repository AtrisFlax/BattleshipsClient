package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NetworkDoDisconnectEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.NetworkServerEvent;
import com.liver_rus.Battleships.Network.Server.MetaInfo;

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
