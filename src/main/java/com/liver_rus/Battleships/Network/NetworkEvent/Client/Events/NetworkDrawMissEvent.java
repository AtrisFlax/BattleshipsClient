package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderMiss;
import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.NetworkClientEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;

//client do nothing
public class NetworkDrawMissEvent implements NetworkClientEvent {
    private final int x;
    private final int y;
    private final PlayerType playerType;

    public NetworkDrawMissEvent(int x, int y, PlayerType playerType) {
        assert (x >= 0 && x <= 9);
        assert (y >= 0 && y <= 9);
        this.x = x;
        this.y = y;
        this.playerType = playerType;
    }

    @Override
    public String proceed(GUIActions action) {
        action.draw(new RenderMiss(x, y, playerType));
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.MISS + x + y +  playerType.getString();
    }
}
