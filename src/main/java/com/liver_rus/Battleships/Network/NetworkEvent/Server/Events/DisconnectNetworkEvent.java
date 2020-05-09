package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.DoDisconnectNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;
import com.liver_rus.Battleships.Network.Server.MetaInfo;

public class DisconnectNetworkEvent implements ServerNetworkEvent {

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        answer.add(metaInfo.getActivePlayer(), new DoDisconnectNetworkEvent());
        answer.add(metaInfo.getPassivePlayer(), new DoDisconnectNetworkEvent());
        return answer;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.DISCONNECT;
    }

}
