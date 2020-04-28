package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NetworkDeployEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NetworkSetEnemyNameEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NetworkWaitingSecondPlayerEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.NetworkServerEvent;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

public class NetworkMyNameEvent implements NetworkServerEvent {
    private final String name;

    public NetworkMyNameEvent(String name) {
        this.name = name;
    }

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        activePlayer.setName(name);
        activePlayer.setReadyForDeployment(true);
        if (!metaInfo.isPlayersReadyForDeployment()) {
            answer.add(activePlayer, new NetworkWaitingSecondPlayerEvent("Waiting start deployment"));
        } else {
            Player passivePlayer = metaInfo.getPassivePlayer();
            answer.add(activePlayer, new NetworkSetEnemyNameEvent(passivePlayer.getName()));
            answer.add(passivePlayer, new NetworkSetEnemyNameEvent(activePlayer.getName()));
            GameField activePlayerField = activePlayer.getGameField();
            GameField passivePlayerField = passivePlayer.getGameField();
            answer.add(activePlayer, new NetworkDeployEvent(activePlayerField.getShipsLeftByTypeForDeploy()));
            answer.add(passivePlayer, new NetworkDeployEvent(passivePlayerField.getShipsLeftByTypeForDeploy()));
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.MY_NAME + name;
    }
}
