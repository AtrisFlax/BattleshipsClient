package com.liver_rus.Battleships.NetworkEvent.Server;

import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkEventDeploy;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkEventSetEnemyName;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkEventWaitingSecondPlayer;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventServer;

public class NetworkEventMyName implements NetworkEventServer {
    private final String name;

    public NetworkEventMyName(String name) {
        this.name = name;
    }

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        activePlayer.setName(name);
        activePlayer.setReadyForDeployment(true);
        if (!metaInfo.isPlayersReadyForDeployment()) {
            answer.add(activePlayer, new NetworkEventWaitingSecondPlayer("Waiting start deployment"));
        } else {
            Player passivePlayer = metaInfo.getPassivePlayer();
            answer.add(activePlayer, new NetworkEventSetEnemyName(passivePlayer.getName()));
            answer.add(passivePlayer, new NetworkEventSetEnemyName(activePlayer.getName()));
            GameField activePlayerField = activePlayer.getGameField();
            GameField passivePlayerField = passivePlayer.getGameField();
            answer.add(activePlayer, new NetworkEventDeploy(activePlayerField.getShipsLeftByTypeForDeploy()));
            answer.add(passivePlayer, new NetworkEventDeploy(passivePlayerField.getShipsLeftByTypeForDeploy()));
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.MY_NAME + name;
    }
}
