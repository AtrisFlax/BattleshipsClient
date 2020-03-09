package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import javafx.scene.canvas.GraphicsContext;

public class RenderRedrawHitEnemyEvent extends Redraw implements DrawGUIEvent {

    public RenderRedrawHitEnemyEvent(int x, int y) {
        super(x, y);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isOldCoord(getX(), getY())) {
            Draw.clearCanvas(gc);
        }
        Draw.HitCellOnField(gc, SecondPlayerGUIConstants.getGUIConstant(), getX(), getY());
    }
}

