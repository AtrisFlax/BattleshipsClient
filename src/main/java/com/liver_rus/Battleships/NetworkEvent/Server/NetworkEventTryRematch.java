package com.liver_rus.Battleships.NetworkEvent.Server;

import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkEventDeploy;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkEventStartRematch;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkEventWaitingSecondPlayer;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventServer;

public class NetworkEventTryRematch implements NetworkEventServer {

    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        if (metaInfo.isGameEnded()) {
            activePlayer.setWantRematch(true);
            answer.add(activePlayer, new NetworkEventWaitingSecondPlayer("Waiting second player for rematch"));
        }
        if (metaInfo.isPlayersWantReamatch()) {
            metaInfo.resetForRematch();
            Player passivePlayer = metaInfo.getPassivePlayer();
            answer.add(activePlayer, new NetworkEventStartRematch());
            answer.add(passivePlayer, new NetworkEventStartRematch());
            activePlayer.setWantRematch(false);
            passivePlayer.setWantRematch(false);
            activePlayer.setReadyForDeployment(true);
            passivePlayer.setReadyForDeployment(true);
            GameField activePlayerField = activePlayer.getGameField();
            GameField passivePlayerField = passivePlayer.getGameField();
            answer.add(activePlayer, new NetworkEventDeploy(activePlayerField.getShipsLeftByTypeForDeploy()));
            answer.add(passivePlayer, new NetworkEventDeploy(passivePlayerField.getShipsLeftByTypeForDeploy()));
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.TRY_REMATCH;
    }
}
