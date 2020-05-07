package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.*;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.NetworkServerEvent;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.Ship;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.TryingAddTooManyShipsOnFieldException;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;
import com.liver_rus.Battleships.Network.Server.TurnOrder;

import static com.liver_rus.Battleships.Client.GUI.Constants.Constants.Debug.DEBUG_AUTO_DEPLOY;

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
        if (metaInfo.isPlayersReadyForDeployment()) {
            //TODO delete debug
            if (DEBUG_AUTO_DEPLOY) {
                Player passivePlayer = metaInfo.getPassivePlayer();
                answer.add(activePlayer, new NetworkSetEnemyNameEvent(passivePlayer.getName()));
                answer.add(passivePlayer, new NetworkSetEnemyNameEvent(activePlayer.getName()));
                GameField activePlayerField = activePlayer.getGameField();
                GameField passivePlayerField = passivePlayer.getGameField();
                try {
                    addShipsPreset1(activePlayerField);
                    addShipsPreset2(passivePlayerField);
                } catch (TryingAddTooManyShipsOnFieldException e) {
                    e.printStackTrace();
                }
                for (Ship ship: activePlayerField.getShips()) {
                    answer.add(activePlayer, new NetworkDrawShipEvent(ship.getX(), ship.getY(), ship.getType(), ship.isHorizontal(), PlayerType.YOU));
                }
                for (Ship ship: passivePlayerField.getShips()) {
                    answer.add(passivePlayer, new NetworkDrawShipEvent(ship.getX(), ship.getY(), ship.getType(), ship.isHorizontal(), PlayerType.YOU));
                }
                activePlayer.setReadyForGame(true);
                passivePlayer.setReadyForGame(true);
                activePlayer.setReadyForDeployment(false);
                passivePlayer.setReadyForDeployment(false);
                metaInfo.setInitTurnOrder(TurnOrder.FIRST_CONNECTED);
                metaInfo.setTurnHolder();
                answer.add(metaInfo.getTurnHolderPlayer(), new NetworkCanShootEvent());
            } else {
                Player passivePlayer = metaInfo.getPassivePlayer();
                answer.add(activePlayer, new NetworkSetEnemyNameEvent(passivePlayer.getName()));
                answer.add(passivePlayer, new NetworkSetEnemyNameEvent(activePlayer.getName()));
                GameField activePlayerField = activePlayer.getGameField();
                GameField passivePlayerField = passivePlayer.getGameField();
                answer.add(activePlayer, new NetworkDeployEvent(activePlayerField.getShipsLeftByTypeForDeploy()));
                answer.add(passivePlayer, new NetworkDeployEvent(passivePlayerField.getShipsLeftByTypeForDeploy()));
            }
        } else {
            answer.add(activePlayer, new NetworkWaitingSecondPlayerEvent("Deployment"));
        }
        return answer;
    }

    public String convertToString() {
        return NetworkCommandConstant.MY_NAME + name;
    }

    private void addShipsPreset1(GameField field) throws TryingAddTooManyShipsOnFieldException {
        field.addShip(1, 8, 0, true);
        field.addShip(3, 2, 0, true);
        field.addShip(1, 1, 1, false);
        field.addShip(3, 4, 1, true);
        field.addShip(2, 6, 2, true);
        field.addShip(7, 4, 3, false);
        field.addShip(9, 1, 4, false);
    }

    private void addShipsPreset2(GameField field) throws TryingAddTooManyShipsOnFieldException {
        field.addShip(6, 2, 3, false);
        field.addShip(2, 3, 2, false);
        field.addShip(1, 0, 4, true);
        field.addShip(4, 7, 1, true);
        field.addShip(8, 4, 1, false);
        field.addShip(1, 8, 0, false);
        field.addShip(7, 8, 0, false);
    }
}
