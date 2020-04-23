package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import javafx.scene.canvas.GraphicsContext;

public class RenderImpossibleDeployShip implements DrawGUIEvent {
    private final int x;
    private final int y;
    private final int shipType;
    private final boolean isHorizontal;

    public RenderImpossibleDeployShip(int x, int y, int shipType, boolean isHorizontal) {
        this.x = x;
        this.y = y;
        this.shipType = shipType;
        this.isHorizontal = isHorizontal;
    }

    @Override
    public void render(GraphicsContext gc) {
        Draw.impossibleDraw(gc, FirstPlayerGUIConstants.getGUIConstant(), x, y, shipType, isHorizontal);
    }
}
