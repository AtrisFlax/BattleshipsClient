package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.DeployNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.DoDisconnectNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.StartRematchStatusNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.OFF;
import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.ON;

public class TryRematchStateNetworkEvent implements ServerNetworkEvent {
    //if true client ready for rematch
    public final boolean state;

    public TryRematchStateNetworkEvent(boolean rematchAnswer) {
        this.state = rematchAnswer;
    }

    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        Player passivePlayer = metaInfo.getPassivePlayer();

        if (metaInfo.isGameEnded()) {
            if (!state) {
                answer.add(activePlayer, new StartRematchStatusNetworkEvent(false));
                answer.add(passivePlayer, new StartRematchStatusNetworkEvent(false));
                answer.add(activePlayer, new DoDisconnectNetworkEvent());
                answer.add(passivePlayer, new DoDisconnectNetworkEvent());
            } else {
                activePlayer.setWantRematch(true);
            }
        }

        if (metaInfo.isPlayersSetRematch()) {
            if (metaInfo.isPlayersWantReamatch()) {
                metaInfo.resetForRematch();
                answer.add(activePlayer, new StartRematchStatusNetworkEvent(true));
                answer.add(passivePlayer, new StartRematchStatusNetworkEvent(true));
                GameField activePlayerField = activePlayer.getGameField();
                GameField passivePlayerField = passivePlayer.getGameField();
                answer.add(activePlayer, new DeployNetworkEvent(activePlayerField.getShipsLeftByTypeForDeploy()));
                answer.add(passivePlayer, new DeployNetworkEvent(passivePlayerField.getShipsLeftByTypeForDeploy()));
            }
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.REMATCH_ANSWER + (state ? ON : OFF);
    }
}
