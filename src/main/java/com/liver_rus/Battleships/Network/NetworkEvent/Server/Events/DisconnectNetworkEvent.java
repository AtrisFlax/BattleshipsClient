package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.DoDisconnectNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

public class DisconnectNetworkEvent implements ServerNetworkEvent {

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        for (Player player : metaInfo.getPlayers()){
            answer.add(player, new DoDisconnectNetworkEvent());
        }
        return answer;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.DISCONNECT;
    }
}
