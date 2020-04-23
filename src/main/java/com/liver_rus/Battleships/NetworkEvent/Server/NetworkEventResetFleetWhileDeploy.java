package com.liver_rus.Battleships.NetworkEvent.Server;

import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkEventCommandNotAccepted;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkEventDeploy;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventServer;

public class NetworkEventResetFleetWhileDeploy implements NetworkEventServer {

    public Answer proceed(MetaInfo metaInfo)  {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        if (activePlayer.isReadyForDeployment()) {
            GameField activePlayerField = activePlayer.getGameField();
            activePlayerField.reset();
            answer.add(activePlayer, new NetworkEventDeploy(activePlayerField.getShipsLeftByTypeForDeploy()));
        } else {
            answer.add(activePlayer,
                    new NetworkEventCommandNotAccepted("Deploying is already has ended or hasn't started"));
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.RESET_FLEET_WHILE_DEPLOY;
    }
}
