package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.GUI.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;
import javafx.scene.canvas.GraphicsContext;

public final class RenderMiss implements DrawGUIEvent {
    private final int x;
    private final int y;
    private final PlayerType playerType;

    public RenderMiss(int x, int y, PlayerType playerType) {
        this.x = x;
        this.y = y;
        this.playerType = playerType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (playerType == PlayerType.YOU) {
            Draw.Miss(gc, FirstPlayerGUIConstants.getGUIConstant(),  x, y);
        } else {
            Draw.Miss(gc, SecondPlayerGUIConstants.getGUIConstant(), x, y);
        }
    }
}
