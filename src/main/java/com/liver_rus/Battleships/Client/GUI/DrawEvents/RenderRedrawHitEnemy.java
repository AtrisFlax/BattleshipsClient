package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.GUI.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import javafx.scene.canvas.GraphicsContext;

//while shooting draw[/] on enemy field
public class RenderRedrawHitEnemy extends Redraw implements DrawGUIEvent {

    public RenderRedrawHitEnemy(int x, int y) {
        super(x, y);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isOldCoord(getX(), getY())) {
            Draw.clearCanvas(gc);
        }
        Draw.Miss(gc, SecondPlayerGUIConstants.getGUIConstant(), getX(), getY());
    }
}

