package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.DeployNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.StartRematchNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.WaitingSecondPlayerNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

public class TryRematchNetworkEvent implements ServerNetworkEvent {

    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        if (metaInfo.isGameEnded()) {
            activePlayer.setWantRematch(true);
            answer.add(activePlayer, new WaitingSecondPlayerNetworkEvent("Waiting second player for rematch"));
        }
        if (metaInfo.isPlayersWantReamatch()) {
            metaInfo.resetForRematch();
            Player passivePlayer = metaInfo.getPassivePlayer();
            answer.add(activePlayer, new StartRematchNetworkEvent());
            answer.add(passivePlayer, new StartRematchNetworkEvent());
            activePlayer.setWantRematch(false);
            passivePlayer.setWantRematch(false);
            activePlayer.setReadyForDeployment(true);
            passivePlayer.setReadyForDeployment(true);
            GameField activePlayerField = activePlayer.getGameField();
            GameField passivePlayerField = passivePlayer.getGameField();
            answer.add(activePlayer, new DeployNetworkEvent(activePlayerField.getShipsLeftByTypeForDeploy()));
            answer.add(passivePlayer, new DeployNetworkEvent(passivePlayerField.getShipsLeftByTypeForDeploy()));
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.TRY_REMATCH;
    }
}
