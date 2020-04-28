package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NetworkCommandNotAcceptedEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NetworkDeployEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.NetworkServerEvent;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

public class NetworkResetFleetWhileDeployEvent implements NetworkServerEvent {

    public Answer proceed(MetaInfo metaInfo)  {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        if (activePlayer.isReadyForDeployment()) {
            GameField activePlayerField = activePlayer.getGameField();
            activePlayerField.reset();
            answer.add(activePlayer, new NetworkDeployEvent(activePlayerField.getShipsLeftByTypeForDeploy()));
        } else {
            answer.add(activePlayer,
                    new NetworkCommandNotAcceptedEvent("Deploying is already has ended or hasn't started"));
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.RESET_FLEET_WHILE_DEPLOY;
    }
}
