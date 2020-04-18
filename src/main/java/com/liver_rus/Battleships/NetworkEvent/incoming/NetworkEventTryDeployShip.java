package com.liver_rus.Battleships.NetworkEvent.incoming;

import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.TryingAddTooManyShipsOnFieldException;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventServer;
import com.liver_rus.Battleships.NetworkEvent.PlayerType;
import com.liver_rus.Battleships.NetworkEvent.outcoming.*;

import static com.liver_rus.Battleships.Network.Server.GamePrimitives.Fleet.NUM_TYPE;

public class NetworkEventTryDeployShip implements NetworkEventServer {

    private final int x;
    private final int y;
    private final int type;
    private final boolean isHorizontal;

    public NetworkEventTryDeployShip(int x, int y, int type, boolean isHorizontal) {
        assert (x >= 0 && x <= 9);
        assert (y >= 0 && y <= 9);
        assert (type >= 0 && type  < NUM_TYPE);
        this.x = x;
        this.y = y;
        this.type = type;
        this.isHorizontal = isHorizontal;
    }

    public Answer proceed(MetaInfo metaInfo)  {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        if (activePlayer.isReadyForDeployment()) {
            GameField activePlayerField = activePlayer.getGameField();
            boolean shipCreated = false;
            try {
                shipCreated = activePlayerField.addShip(x, y, type, isHorizontal);
            } catch (TryingAddTooManyShipsOnFieldException e) {
                e.printStackTrace();
            }
            if (shipCreated) {
                answer.add(activePlayer, new NetworkEventDrawShip(x, y, type, isHorizontal, PlayerType.ME));
            } else {
                answer.add(activePlayer, new NetworkEventCannotDeploy(x, y, type, isHorizontal));
            }
            if (activePlayerField.isAllShipsDeployed()) {
                activePlayer.setReadyForGame(true);
                activePlayer.setReadyForDeployment(false);
                if (metaInfo.isPlayersReadyForGame()) {
                    metaInfo.setTurnHolder();
                    answer.add(metaInfo.getTurnHolderPlayer(), new NetworkEventCanShoot());
                    answer.add(metaInfo.getNotTurnHolderPlayer(),
                            new NetworkEventWaitingSecondPlayer("Waiting shot of second player"));
                } else {
                    answer.add(activePlayer,
                            new NetworkEventWaitingSecondPlayer("Waiting deployment of second player"));
                }
            } else {
                answer.add(activePlayer, new NetworkEventDeploy(activePlayerField.getShipsLeftByTypeForDeploy()));
            }

        } else {
            answer.add(activePlayer,
                    new NetworkEventCommandNotAccepted("Deploying is already has ended or hasn't started"));
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.TRY_DEPLOY_SHIP + x + y + type + ((isHorizontal) ? "H" : "V");
    }
}
