package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.GUI.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;
import javafx.scene.canvas.GraphicsContext;

public class RenderShip implements DrawGUIEvent {
    private final int x;
    private final int y;
    private final int shipType;
    private final boolean isHorizontal;
    private final PlayerType playerType;

    public RenderShip(int x, int y, int shipType, boolean isHorizontal, PlayerType playerType) {
        this.x = x;
        this.y = y;
        this.shipType = shipType;
        this.isHorizontal = isHorizontal;
        this.playerType = playerType;
    }

    @Override
    public void render(GraphicsContext gc) {
        System.out.println("WHAT");
        System.out.println(playerType == PlayerType.YOU);
        if (playerType == PlayerType.YOU) {
            Draw.Ship(gc, FirstPlayerGUIConstants.getGUIConstant(),
                    x, y,
                    shipType,
                    isHorizontal);
        } else {
            Draw.Ship(gc, SecondPlayerGUIConstants.getGUIConstant(),
                    x, y,
                    shipType,
                    isHorizontal);
        }
    }
}