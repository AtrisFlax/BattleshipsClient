package com.liver_rus.Battleships.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderHit;
import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkClientEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.PlayerType;

public class NetworkDrawHitEvent implements NetworkClientEvent {
    private final int x;
    private final int y;
    private final PlayerType playerType;

    public NetworkDrawHitEvent(int x, int y, PlayerType playerType) {
        assert (x >= 0 && x <= 9);
        assert (y >= 0 && y <= 9);
        this.x = x;
        this.y = y;
        this.playerType = playerType;
    }

    @Override
    public String proceed(GUIActions action) {
        action.hit(new RenderHit(x, y, playerType));
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.HIT + x + y +  playerType.getString();
    }
}
