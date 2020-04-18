package com.liver_rus.Battleships.NetworkEvent.outcoming;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderImpossibleDeployShip;
import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventClient;
import com.liver_rus.Battleships.NetworkEvent.PlayerType;

public class NetworkEventCannotDeploy implements NetworkEventClient {
    private final int x;
    private final int y;
    private final int shipType;
    private final boolean isHorizontal;

    public NetworkEventCannotDeploy(int x, int y, int shipType, boolean isHorizontal) {
        this.x = x;
        this.y = y;
        this.shipType = shipType;
        this.isHorizontal = isHorizontal;
    }

    @Override
    public String proceed(GUIActions action) {
        action.draw(new RenderImpossibleDeployShip(x, y, shipType, isHorizontal, PlayerType.ME));
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
