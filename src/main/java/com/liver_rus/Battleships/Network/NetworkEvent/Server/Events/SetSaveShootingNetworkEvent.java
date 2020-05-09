package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.OFF;
import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.ON;

public class SetSaveShootingNetworkEvent implements ServerNetworkEvent {

    private final boolean state;

    public SetSaveShootingNetworkEvent(boolean state) {
        this.state = state;
    }

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        if (!metaInfo.isPlayersReadyForGame()) {
            activePlayer.setSaveShooting(state);
        }
        return answer;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.SET_SAVE_SHOOTING + (state ? ON : OFF);
    }
}
