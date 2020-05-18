package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.CommandNotAcceptedNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;
import com.liver_rus.Battleships.Network.Server.GamePreferences;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.OFF;
import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.ON;

public class ConfigGameEvent implements ServerNetworkEvent {
    private final boolean salvoMode;
    private final boolean adjacentShips;

    public ConfigGameEvent(boolean salvoMode, boolean adjacentShips) {
        this.salvoMode = salvoMode;
        this.adjacentShips = adjacentShips;
    }

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        //TODO setPreferences before deploying
        //TODO add some auth. Both player anyone can change game preferences
        if (!metaInfo.isPlayersInGame()) {
            metaInfo.setPreferences(new GamePreferences(salvoMode, adjacentShips));
        } else {
            answer.add(activePlayer,
                    new CommandNotAcceptedNetworkEvent("You can't change game pref while game"));
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.CONFIG_PLAYER + (salvoMode ? ON : OFF) +  (adjacentShips ? ON : OFF);
    }
}
