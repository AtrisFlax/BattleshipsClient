package com.liver_rus.Battleships.NetworkEvent.Server;

import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkDeployEvent;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkStartRematchEvent;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkWaitingSecondPlayerEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkServerEvent;

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
