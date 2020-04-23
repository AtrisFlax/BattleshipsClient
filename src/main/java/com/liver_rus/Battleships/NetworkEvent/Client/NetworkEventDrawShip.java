package com.liver_rus.Battleships.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderShip;
import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.Ship;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventClient;
import com.liver_rus.Battleships.NetworkEvent.PlayerType;

import static com.liver_rus.Battleships.Network.Server.GamePrimitives.Fleet.NUM_TYPE;

public class NetworkEventDrawShip implements NetworkEventClient {
    private final int x;
    private final int y;
    private final int shipType;
    private final boolean isHorizontal;
    PlayerType playerType;

    public NetworkEventDrawShip(Ship ship, PlayerType playerType) {
        this.x = ship.getX();
        this.y = ship.getY();
        this.shipType = ship.getType();
        this.isHorizontal = ship.isHorizontal();
        this.playerType = playerType;
    }

    public NetworkEventDrawShip(int x, int y, int shipType, boolean isHorizontal, PlayerType playerType) {
        assert (x >= 0 && x <= 9);
        assert (y >= 0 && y <= 9);
        assert (shipType >= 0 && shipType < NUM_TYPE);
        this.x = x;
        this.y = y;
        this.shipType = shipType;
        this.isHorizontal = isHorizontal;
        this.playerType = playerType;
    }

    @Override
    public String proceed(GUIActions action) {
        action.draw(new RenderShip(x, y, shipType, isHorizontal, playerType));
        return null;
    }

    @Override
    public String convertToString() {

        return NetworkCommandConstant.DRAW_SHIP +
                x +
                y +
                shipType +
                ((isHorizontal) ? "H" : "V" ) +
                playerType.getString();
    }
}
