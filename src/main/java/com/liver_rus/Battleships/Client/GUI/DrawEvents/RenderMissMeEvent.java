package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import javafx.scene.canvas.GraphicsContext;

public class RenderMissMeEvent implements DrawGUIEvent {
    private final int x;
    private final int y;

    RenderMissMeEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(GraphicsContext gc) {
        Draw.MissCellOnField(gc, FirstPlayerGUIConstants.getGUIConstant(), x, y);
    }
}

