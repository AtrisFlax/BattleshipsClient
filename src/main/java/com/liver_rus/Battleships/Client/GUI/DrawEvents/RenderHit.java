package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import com.liver_rus.Battleships.NetworkEvent.PlayerType;
import javafx.scene.canvas.GraphicsContext;

public class RenderHit implements DrawGUIEvent {
    private final int x;
    private final int y;
    private final PlayerType playerType;

    public RenderHit(int x, int y, PlayerType playerType) {
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
            Draw.Hit(gc, FirstPlayerGUIConstants.getGUIConstant(),  x, y);
        } else {
            Draw.Hit(gc, SecondPlayerGUIConstants.getGUIConstant(),  x, y);
        }
    }
}
