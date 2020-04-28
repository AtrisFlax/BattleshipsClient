package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NetworkNotStartRematchEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.NetworkServerEvent;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

public class NetworkNoRematchEvent implements NetworkServerEvent {

    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        if (metaInfo.isGameEnded()) {
            Player passivePlayer = metaInfo.getPassivePlayer();
            answer.add(activePlayer, new NetworkNotStartRematchEvent());
            answer.add(passivePlayer, new NetworkNotStartRematchEvent());
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.NO_REMATCH;
    }
}
