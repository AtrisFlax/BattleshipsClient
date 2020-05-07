package com.liver_rus.Battleships.Network.NetworkEvent.Server.Events;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.*;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.NetworkServerEvent;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.FieldCoord;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.Ship;
import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.Network.Server.Player;

public class NetworkShotEvent implements NetworkServerEvent {
    private final int x;
    private final int y;

    public NetworkShotEvent(int x, int y) {
        assert (x >= 0 && x <= 9);
        assert (y >= 0 && y <= 9);
        this.x = x;
        this.y = y;
    }

    //TODO simpilify
    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer answer = new Answer();
        Player activePlayer = metaInfo.getActivePlayer();
        Player passivePlayer = metaInfo.getPassivePlayer();
        GameField passivePlayerField = passivePlayer.getGameField();
        if (metaInfo.isPlayersReadyForGame()) {
            if (activePlayer == metaInfo.getTurnHolderPlayer()) {
                if (activePlayer.isSaveShooting()) {
                    Ship destroyedShip = passivePlayerField.saveShoot(x, y);
                    if (passivePlayerField.isFreeShot(x, y)) {
                        metaInfo.setTurnHolderPlayer(activePlayer);
                    } else {
                        if (passivePlayerField.isCellDamaged(x, y)) {
                            answer.add(activePlayer, new NetworkDrawHitEvent(x, y, PlayerType.ENEMY));
                            answer.add(passivePlayer, new NetworkDrawHitEvent(x, y, PlayerType.YOU));
                            if (destroyedShip != null) {
                                answer.add(activePlayer, new NetworkDrawShipEvent(destroyedShip, PlayerType.ENEMY));
                                answer.add(passivePlayer, new NetworkDrawShipEvent(destroyedShip, PlayerType.YOU));
                                metaInfo.setTurnHolderPlayer(activePlayer);
                                if (passivePlayerField.isAllShipsDestroyed()){
                                    GameField field = activePlayer.getGameField();
                                    for (Ship leavesShip: field.getShips()) {
                                        answer.add(passivePlayer, new NetworkDrawShipEvent(leavesShip, PlayerType.ENEMY));
                                    }
                                    answer.add(activePlayer, new NetworkEndMatchEvent(PlayerType.YOU));
                                    answer.add(passivePlayer, new NetworkEndMatchEvent(PlayerType.ENEMY));
                                    metaInfo.setGameEnded();
                                    return answer;
                                }
                                if (activePlayer.isSaveShooting()) {
                                    for (FieldCoord destroyedShipNearCoord: destroyedShip.getNearCoord()) {
                                        answer.add(activePlayer, new NetworkDrawMissEvent(
                                                destroyedShipNearCoord.getX(),
                                                destroyedShipNearCoord.getY(),
                                                PlayerType.ENEMY)
                                        );
                                    }
                                }
                            }
                            metaInfo.setTurnHolderPlayer(activePlayer);
                        } else {
                            answer.add(activePlayer, new NetworkDrawMissEvent(x, y, PlayerType.ENEMY));
                            answer.add(passivePlayer, new NetworkDrawMissEvent(x, y, PlayerType.YOU));
                            metaInfo.setTurnHolderPlayer(passivePlayer);
                        }
                    }
                } else {
                    Ship destroyedShip = passivePlayerField.shoot(x, y);
                    if (passivePlayerField.isCellDamaged(x, y)) {
                        answer.add(activePlayer, new NetworkDrawHitEvent(x, y, PlayerType.ENEMY));
                        answer.add(passivePlayer, new NetworkDrawHitEvent(x, y, PlayerType.YOU));
                        if (destroyedShip != null) {
                            answer.add(activePlayer, new NetworkDrawShipEvent(destroyedShip, PlayerType.ENEMY));
                            answer.add(passivePlayer, new NetworkDrawShipEvent(destroyedShip, PlayerType.YOU));
                            metaInfo.setTurnHolderPlayer(activePlayer);
                            if (passivePlayerField.isAllShipsDestroyed()){
                                GameField field = activePlayer.getGameField();
                                for (Ship leavesShip: field.getShips()) {
                                    answer.add(passivePlayer, new NetworkDrawShipEvent(leavesShip, PlayerType.ENEMY));
                                }
                                answer.add(activePlayer, new NetworkEndMatchEvent(PlayerType.YOU));
                                answer.add(passivePlayer, new NetworkEndMatchEvent(PlayerType.ENEMY));
                                metaInfo.setGameEnded();
                                return answer;
                            }
                        }
                        metaInfo.setTurnHolderPlayer(activePlayer);
                    } else {
                        answer.add(activePlayer, new NetworkDrawMissEvent(x, y, PlayerType.ENEMY));
                        answer.add(passivePlayer, new NetworkDrawMissEvent(x, y, PlayerType.YOU));
                        metaInfo.setTurnHolderPlayer(passivePlayer);
                    }
                }
                ///
                answer.add(metaInfo.getTurnHolderPlayer(), new NetworkCanShootEvent());
            } else {
                answer.add(activePlayer, new NetworkCommandNotAcceptedEvent("Not You Turn"));
            }
        } else {
            answer.add(activePlayer, new NetworkCommandNotAcceptedEvent("Game has not been started"));
        }
        return answer;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.SHOT + x + y;
    }
}
