package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderMarkDestroy;
import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.NetworkClientEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;

public class NetworkDrawShipsLeftEvent implements NetworkClientEvent {
    private final int num;

    public NetworkDrawShipsLeftEvent(int num) {
        this.num = num;
    }

    @Override
    public String proceed(GUIActions action) {
        action.draw(new RenderMarkDestroy(num));
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.DRAW_SHIP_LEFT + num;
    }
}
