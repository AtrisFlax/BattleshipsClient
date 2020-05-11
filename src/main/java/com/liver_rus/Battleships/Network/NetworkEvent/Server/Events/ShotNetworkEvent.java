package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.*;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.FieldCoord;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.Ship;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

public class ShotNetworkEvent implements ServerNetworkEvent {
    private final int x;
    private final int y;

    public ShotNetworkEvent(int x, int y) {
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
                Ship destroyedShip = passivePlayerField.saveShoot(x, y);
                //free shoot == turnHolder same. send CanShoot
                if (activePlayer.isSaveShooting() && passivePlayerField.isFreeShot(x, y)) {
                    answer.add(metaInfo.getTurnHolderPlayer(), new CanShootNetworkEvent());
                    return answer;
                }
                if (passivePlayerField.isCellDamaged(x, y)) {
                    //draw hit
                    answer.add(activePlayer, new DrawHitNetworkEvent(x, y, PlayerType.ENEMY));
                    answer.add(passivePlayer, new DrawHitNetworkEvent(x, y, PlayerType.YOU));
                    if (destroyedShip != null) {
                        //draw left num ships indicator
                        int numAliveShips = passivePlayerField.getShipLeftAlive();
                        answer.add(activePlayer, new DrawShipsLeftNetworkEvent(numAliveShips));
                        //draw ship
                        answer.add(activePlayer, new DrawShipNetworkEvent(destroyedShip, PlayerType.ENEMY));
                        answer.add(passivePlayer, new DrawShipNetworkEvent(destroyedShip, PlayerType.YOU));
                        metaInfo.setTurnHolderPlayer(activePlayer);
                        //send near cells of destroyedShip
                        if (activePlayer.isSaveShooting()) {
                            for (FieldCoord destroyedShipNearCoord : destroyedShip.getNearCoord()) {
                                answer.add(activePlayer, new DrawNearNetworkEvent(
                                        destroyedShipNearCoord.getX(), destroyedShipNearCoord.getY()
                                ));
                            }
                        }
                        //end game. send alive ships for loser and ask for rematch
                        if (passivePlayerField.isAllShipsDestroyed()) {
                            GameField field = activePlayer.getGameField();
                            for (Ship leavesShip : field.getShips()) {
                                answer.add(passivePlayer, new DrawShipNetworkEvent(leavesShip, PlayerType.ENEMY));
                            }
                            answer.add(activePlayer, new EndMatchNetworkEvent(PlayerType.ENEMY));
                            answer.add(passivePlayer, new EndMatchNetworkEvent(PlayerType.YOU));
                            answer.add(activePlayer, new AskForRematchNetworkEvent());
                            answer.add(passivePlayer, new AskForRematchNetworkEvent());
                            metaInfo.setGameEnded();
                            return answer;
                        }

                    }
                    metaInfo.setTurnHolderPlayer(activePlayer);
                } else {
                    //draw miss
                    answer.add(activePlayer, new DrawMissNetworkEvent(x, y, PlayerType.ENEMY));
                    answer.add(passivePlayer, new DrawMissNetworkEvent(x, y, PlayerType.YOU));
                    metaInfo.setTurnHolderPlayer(passivePlayer);
                }
                //send can shoot for turnHolderPlayer
                answer.add(metaInfo.getTurnHolderPlayer(), new CanShootNetworkEvent());
            } else {
                answer.add(activePlayer, new CommandNotAcceptedNetworkEvent("Not You Turn"));
            }
        } else {
            answer.add(activePlayer, new CommandNotAcceptedNetworkEvent("Game has not been started"));
        }
        return answer;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.SHOT + x + y;
    }
}
