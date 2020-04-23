package com.liver_rus.Battleships.NetworkEvent.Server;

import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.Ship;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.Client.*;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventServer;
import com.liver_rus.Battleships.NetworkEvent.PlayerType;

public class NetworkEventShot implements NetworkEventServer {
    private final int x;
    private final int y;

    public NetworkEventShot(int x, int y) {
        assert (x >= 0 && x <= 9);
        assert (y >= 0 && y <= 9);
        this.x = x;
        this.y = y;
    }

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        Player passivePlayer = metaInfo.getPassivePlayer();
        GameField passivePlayerField = passivePlayer.getGameField();
        if (metaInfo.isPlayersReadyForGame()) {
            if (activePlayer == metaInfo.getTurnHolderPlayer()) {
                Ship destroyedShip = passivePlayerField.shoot(x, y);
                if (passivePlayerField.isFieldCellDamaged(x, y)) {
                    answer.add(activePlayer, new NetworkEventDrawHit(x, y, PlayerType.ENEMY));
                    answer.add(passivePlayer, new NetworkEventDrawHit(x, y, PlayerType.YOU));
                    if (destroyedShip != null) {
                        answer.add(activePlayer, new NetworkEventDrawShip(destroyedShip, PlayerType.ENEMY));
                        answer.add(passivePlayer, new NetworkEventDrawShip(destroyedShip, PlayerType.YOU));
                        metaInfo.setTurnHolderPlayer(activePlayer);
                        if (passivePlayerField.isAllShipsDestroyed()){
                            GameField field = activePlayer.getGameField();
                            for (Ship leavesShip: field.getShips()) {
                                answer.add(passivePlayer, new NetworkEventDrawShip(leavesShip, PlayerType.ENEMY));
                            }
                            answer.add(activePlayer, new NetworkEventEndMatch(PlayerType.YOU));
                            answer.add(passivePlayer, new NetworkEventEndMatch(PlayerType.ENEMY));
                            metaInfo.setGameEnded();
                            return answer;
                        }
                    }
                    metaInfo.setTurnHolderPlayer(activePlayer);
                } else {
                    answer.add(activePlayer, new NetworkEventDrawMiss(x, y, PlayerType.ENEMY));
                    answer.add(passivePlayer, new NetworkEventDrawMiss(x, y, PlayerType.YOU));
                    metaInfo.setTurnHolderPlayer(passivePlayer);
                }
                answer.add(metaInfo.getTurnHolderPlayer(), new NetworkEventCanShoot());
            } else {
                answer.add(activePlayer, new NetworkEventCommandNotAccepted("Not You Turn"));
            }
        } else {
            answer.add(activePlayer, new NetworkEventCommandNotAccepted("Game has not been started"));
        }
        return answer;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.SHOT + x + y;
    }
}
