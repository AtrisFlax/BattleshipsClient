package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NotStartRematchNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

public class NoRematchNetworkEvent implements ServerNetworkEvent {

    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        if (metaInfo.isGameEnded()) {
            Player passivePlayer = metaInfo.getPassivePlayer();
            answer.add(activePlayer, new NotStartRematchNetworkEvent());
            answer.add(passivePlayer, new NotStartRematchNetworkEvent());
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.NO_REMATCH;
    }
}
