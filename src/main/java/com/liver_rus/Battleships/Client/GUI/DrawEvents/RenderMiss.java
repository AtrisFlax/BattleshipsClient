package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import com.liver_rus.Battleships.NetworkEvent.PlayerType;
import javafx.scene.canvas.GraphicsContext;

public class RenderMiss implements DrawGUIEvent {
    private final int x;
    private final int y;
    private final PlayerType playerType;

    public RenderMiss(int x, int y, PlayerType playerType) {
        this.x = x;
        this.y = y;
        this.playerType = playerType;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (playerType == PlayerType.ME) {
            Draw.MissCellOnField(gc, FirstPlayerGUIConstants.getGUIConstant(),  x, y);
        } else {
            Draw.MissCellOnField(gc, SecondPlayerGUIConstants.getGUIConstant(), x, y);
        }
    }
}