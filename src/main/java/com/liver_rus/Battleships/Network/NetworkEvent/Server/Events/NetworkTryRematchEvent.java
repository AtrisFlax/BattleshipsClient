package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NetworkDeployEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NetworkStartRematchEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NetworkWaitingSecondPlayerEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.NetworkServerEvent;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

public class NetworkTryRematchEvent implements NetworkServerEvent {

    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        if (metaInfo.isGameEnded()) {
            activePlayer.setWantRematch(true);
            answer.add(activePlayer, new NetworkWaitingSecondPlayerEvent("Waiting second player for rematch"));
        }
        if (metaInfo.isPlayersWantReamatch()) {
            metaInfo.resetForRematch();
            Player passivePlayer = metaInfo.getPassivePlayer();
            answer.add(activePlayer, new NetworkStartRematchEvent());
            answer.add(passivePlayer, new NetworkStartRematchEvent());
            activePlayer.setWantRematch(false);
            passivePlayer.setWantRematch(false);
            activePlayer.setReadyForDeployment(true);
            passivePlayer.setReadyForDeployment(true);
            GameField activePlayerField = activePlayer.getGameField();
            GameField passivePlayerField = passivePlayer.getGameField();
            answer.add(activePlayer, new NetworkDeployEvent(activePlayerField.getShipsLeftByTypeForDeploy()));
            answer.add(passivePlayer, new NetworkDeployEvent(passivePlayerField.getShipsLeftByTypeForDeploy()));
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.TRY_REMATCH;
    }
}
