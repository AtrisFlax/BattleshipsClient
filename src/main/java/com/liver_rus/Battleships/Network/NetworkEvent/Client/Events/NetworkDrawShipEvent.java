package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderShip;
import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.NetworkClientEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.Ship;

import static com.liver_rus.Battleships.Network.Server.GamePrimitives.Fleet.NUM_TYPE;

public class NetworkDrawShipEvent implements NetworkClientEvent {
    private final int x;
    private final int y;
    private final int shipType;
    private final boolean isHorizontal;
    private final PlayerType playerType;

    public NetworkDrawShipEvent(Ship ship, PlayerType playerType) {
        this.x = ship.getX();
        this.y = ship.getY();
        this.shipType = ship.getType();
        this.isHorizontal = ship.isHorizontal();
        this.playerType = playerType;
    }

    public NetworkDrawShipEvent(int x, int y, int shipType, boolean isHorizontal, PlayerType playerType) {
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
