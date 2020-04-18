package com.liver_rus.Battleships.NetworkEvent.incoming;

import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.Ship;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventServer;
import com.liver_rus.Battleships.NetworkEvent.outcoming.NetworkEventCannotDeploy;
import com.liver_rus.Battleships.NetworkEvent.outcoming.NetworkEventCommandNotAccepted;

public class NetworkEventIsPossibleDeployShip implements NetworkEventServer {

    private final int x;
    private final int y;
    private final int type;
    private final boolean isHorizontal;

    public NetworkEventIsPossibleDeployShip(int x, int y, int type, boolean isHorizontal) {
        assert (x >= 0 && x <= 9);
        assert (y >= 0 && y <= 9);
        assert (type >= 0 && type <= 4);
        this.x = x;
        this.y = y;
        this.type = type;
        this.isHorizontal = isHorizontal;
    }

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        if (metaInfo.isPlayersReadyForDeployment()) {
            GameField activePlayerField = activePlayer.getGameField();
            if (Ship.isPossibleLocateShip(x, y, type, isHorizontal, activePlayerField)) {
                answer.add(activePlayer, new NetworkEventCanDeploy(x, y, type, isHorizontal));
            } else {
                answer.add(activePlayer, new NetworkEventCannotDeploy(x, y, type, isHorizontal));
            }
        } else {
            answer.add(activePlayer, new NetworkEventCommandNotAccepted("There is no deploying right now" +
                    (metaInfo.isGameStarted()? "Now game was started" : "Waiting name")));
        }
        return answer;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.IS_POSSIBLE_DEPLOY_SHIP + x + y + type + ((isHorizontal) ? "H" : "V");
    }
}
