package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.*;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.NetworkServerEvent;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.TryingAddTooManyShipsOnFieldException;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

import static com.liver_rus.Battleships.Network.Server.GamePrimitives.Fleet.NUM_TYPE;

public class NetworkTryDeployShipEvent implements NetworkServerEvent {
    private final int x;
    private final int y;
    private final int type;
    private final boolean isHorizontal;

    public NetworkTryDeployShipEvent(int x, int y, int type, boolean isHorizontal) {
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
                answer.add(activePlayer, new NetworkDrawShipEvent(x, y, type, isHorizontal, PlayerType.YOU));
            } else {
                answer.add(activePlayer, new NetworkCannotDeployEvent(x, y, type, isHorizontal));
            }
            if (activePlayerField.isAllShipsDeployed()) {
                activePlayer.setReadyForGame(true);
                activePlayer.setReadyForDeployment(false);
                if (metaInfo.isPlayersReadyForGame()) {
                    metaInfo.setTurnHolder();
                    answer.add(metaInfo.getTurnHolderPlayer(), new NetworkCanShootEvent());
                    answer.add(metaInfo.getNotTurnHolderPlayer(),
                            new NetworkWaitingSecondPlayerEvent("Shot of second player"));
                } else {
                    answer.add(activePlayer,
                            new NetworkWaitingSecondPlayerEvent("Deployment"));
                }
            } else {
                answer.add(activePlayer, new NetworkDeployEvent(activePlayerField.getShipsLeftByTypeForDeploy()));
            }

        } else {
            answer.add(activePlayer,
                    new NetworkCommandNotAcceptedEvent("Deploying is already has ended or hasn't started"));
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.TRY_DEPLOY_SHIP + x + y + type + ((isHorizontal) ? "H" : "V");
    }
}
