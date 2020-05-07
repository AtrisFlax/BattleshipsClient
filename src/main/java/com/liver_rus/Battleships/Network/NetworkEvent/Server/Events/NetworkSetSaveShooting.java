package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.NetworkServerEvent;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.OFF;
import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.ON;

public class NetworkSetSaveShooting implements NetworkServerEvent {

    private final boolean state;

    public NetworkSetSaveShooting(boolean state) {
        this.state = state;
    }

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        activePlayer.setSaveShooting(state);
        return answer;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.SET_SAVE_SHOOTING + (state ? ON : OFF);
    }
}
