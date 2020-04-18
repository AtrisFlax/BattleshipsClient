package com.liver_rus.Battleships.NetworkEvent.incoming;

import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventServer;
import com.liver_rus.Battleships.NetworkEvent.outcoming.NetworkEventDeploy;
import com.liver_rus.Battleships.NetworkEvent.outcoming.NetworkEventSetEnemyName;
import com.liver_rus.Battleships.NetworkEvent.outcoming.NetworkEventWaitingSecondPlayer;

public class NetworkEventMyName implements NetworkEventServer {
    private final String name;

    public NetworkEventMyName(String name) {
        this.name = name;
    }

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer string = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        activePlayer.setName(name);
        activePlayer.setReadyForDeployment(true);
        if (!metaInfo.isPlayersReadyForDeployment()) {
            string.add(activePlayer, new NetworkEventWaitingSecondPlayer("Waiting start deployment"));
        } else {
            Player passivePlayer = metaInfo.getPassivePlayer();
            string.add(activePlayer, new NetworkEventSetEnemyName(passivePlayer.getName()));
            string.add(passivePlayer, new NetworkEventSetEnemyName(activePlayer.getName()));
            GameField activePlayerField = activePlayer.getGameField();
            GameField passivePlayerField = passivePlayer.getGameField();
            string.add(activePlayer, new NetworkEventDeploy(activePlayerField.getShipsLeftByTypeForDeploy()));
            string.add(passivePlayer, new NetworkEventDeploy(passivePlayerField.getShipsLeftByTypeForDeploy()));
        }
        return string;
    }

    public String convertToString() {
        return NetworkCommandConstant.MY_NAME + name;
    }
}
