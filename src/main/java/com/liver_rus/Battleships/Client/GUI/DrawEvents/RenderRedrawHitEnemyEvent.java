package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import javafx.scene.canvas.GraphicsContext;

public class RenderRedrawHitEnemyEvent extends Redraw implements DrawGUIEvent {

    private final int x;
    private final int y;

    public RenderRedrawHitEnemyEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isOldCoord(x, y)) {
            Draw.clearCanvas(gc);
        }
        Draw.HitCellOnField(gc, SecondPlayerGUIConstants.getGUIConstant(), x, y);
    }
}

