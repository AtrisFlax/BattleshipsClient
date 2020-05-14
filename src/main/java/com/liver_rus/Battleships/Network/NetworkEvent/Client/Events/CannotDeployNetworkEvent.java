package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderImpossibleDeployShip;
import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;

import java.util.List;

public class CannotDeployNetworkEvent implements ClientNetworkEvent {
    private final int x;
    private final int y;
    private final int shipType;
    private final boolean isHorizontal;

    public CannotDeployNetworkEvent(int x, int y, int shipType, boolean isHorizontal) {
        this.x = x;
        this.y = y;
        this.shipType = shipType;
        this.isHorizontal = isHorizontal;
    }

    @Override
    public List<ServerNetworkEvent> proceed(GUIActions action) {
        action.draw(new RenderImpossibleDeployShip(x, y, shipType, isHorizontal));
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.CANNOT_DEPLOY +
                x +
                y +
                shipType +
                ((isHorizontal) ? "H" : "V");
    }
}
