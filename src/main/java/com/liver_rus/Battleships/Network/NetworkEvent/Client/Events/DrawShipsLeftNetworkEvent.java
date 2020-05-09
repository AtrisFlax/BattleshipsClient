package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderMarkDestroy;
import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;

public class DrawShipsLeftNetworkEvent implements ClientNetworkEvent {
    private final int num;

    public DrawShipsLeftNetworkEvent(int num) {
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
