package com.liver_rus.Battleships.Client.GUI.NetworkEvent;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GUI.GUIState;
import com.liver_rus.Battleships.Client.GamePrimitives.WrongShipInfoSizeException;
import com.liver_rus.Battleships.Client.Tools.MessageProcessor;


//Common class deserialize
public class CreatorNetworkEvent {
    private CreatorNetworkEvent() {}

    public static NetworkEvent deserializeMessage(String msg) {
        if (msg.startsWith(Constants.NetworkCommand.HIT)) {
            String msgCoord = msg.replaceAll("\\D+", "");
            int x = MessageProcessor.getX(msgCoord);
            int y = MessageProcessor.getY(msgCoord);
            return new NetworkEventHit(x, y);
        }

        if (msg.startsWith(Constants.NetworkCommand.MISS)) {
            String msgCoord = msg.replaceAll("\\D+", "");
            int x = MessageProcessor.getX(msgCoord);
            int y = MessageProcessor.getY(msgCoord);
            return new NetworkEventMiss(x, y);
        }

        if (msg.startsWith(Constants.NetworkCommand.DESTROYED)) {
            String strShipInfo = msg.replace(Constants.NetworkCommand.DESTROYED, "");
            GUIState shipInfo = null;
            try {
                shipInfo = GUIState.create(strShipInfo);
            } catch (WrongShipInfoSizeException e) {
                e.printStackTrace();
            }
            return new NetworkEventDestroyed(shipInfo);
        }

        switch(msg) {
            case Constants.NetworkCommand.YOU_TURN:
                return new NetworkEventYouTurn();
            case Constants.NetworkCommand.ENEMY_TURN:
                return new NetworkEventEnemyTurn();
            case Constants.NetworkCommand.YOU_WIN:
                return new NetworkEventYouWin();
            case Constants.NetworkCommand.YOU_LOSE:
                return new NetworkEventYouLose();
            case Constants.NetworkCommand.DISCONNECT:
                return new NetworkEventDisconnect();

        }

        return new NetworkEventUnknown(msg);
    }
}
