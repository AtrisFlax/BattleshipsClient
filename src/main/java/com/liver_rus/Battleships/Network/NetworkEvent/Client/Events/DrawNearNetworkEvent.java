package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderMiss;
import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;

import java.util.List;

public class DrawNearNetworkEvent implements ClientNetworkEvent {
    private final int x;
    private final int y;

    public DrawNearNetworkEvent(int x, int y) {
        assert (x >= 0 && x <= 9);
        assert (y >= 0 && y <= 9);
        this.x = x;
        this.y = y;
    }

    @Override
    public List<ServerNetworkEvent> proceed(GUIActions action) {
        action.draw(new RenderMiss(x, y, PlayerType.ENEMY));
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.NEAR + x + y;
    }
}
