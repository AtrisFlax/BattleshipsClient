package com.liver_rus.Battleships.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderImpossibleDeployShip;
import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkClientEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;

public class NetworkCannotDeployEvent implements NetworkClientEvent {
    private final int x;
    private final int y;
    private final int shipType;
    private final boolean isHorizontal;

    public NetworkCannotDeployEvent(int x, int y, int shipType, boolean isHorizontal) {
        this.x = x;
        this.y = y;
        this.shipType = shipType;
        this.isHorizontal = isHorizontal;
    }

    @Override
    public String proceed(GUIActions action) {
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
