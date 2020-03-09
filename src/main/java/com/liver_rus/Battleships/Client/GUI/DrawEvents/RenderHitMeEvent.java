package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import javafx.scene.canvas.GraphicsContext;

public class RenderHitMeEvent implements DrawGUIEvent {
    private final int x;
    private final int y;

    public RenderHitMeEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(GraphicsContext gc) {
        Draw.HitCellOnField(gc, FirstPlayerGUIConstants.getGUIConstant(),  x, y);
    }
}
