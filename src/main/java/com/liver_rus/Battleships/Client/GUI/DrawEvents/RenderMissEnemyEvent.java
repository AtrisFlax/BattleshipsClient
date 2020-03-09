package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import com.liver_rus.Battleships.Client.GUI.NetworkEvent.XYGettable;
import javafx.scene.canvas.GraphicsContext;

public class RenderMissEnemyEvent implements DrawGUIEvent {
    private final int x;
    private final int y;

    public RenderMissEnemyEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public RenderMissEnemyEvent(XYGettable event) {
        this.x = event.getX();
        this.y = event.getY();
    }

    @Override
    public void render(GraphicsContext gc) {
        Draw.MissCellOnField(gc, SecondPlayerGUIConstants.getGUIConstant(), x, y);
    }
}

