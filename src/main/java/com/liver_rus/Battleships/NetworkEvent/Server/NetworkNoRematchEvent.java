package com.liver_rus.Battleships.NetworkEvent.Server;

import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkNotStartRematchEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkServerEvent;

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
