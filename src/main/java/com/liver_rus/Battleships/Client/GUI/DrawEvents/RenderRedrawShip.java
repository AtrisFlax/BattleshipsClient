package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import com.liver_rus.Battleships.Client.GUI.ShipInfo;
import javafx.scene.canvas.GraphicsContext;

public class RenderRedrawShip extends Redraw implements DrawGUIEvent {
    private final ShipInfo shipInfo;

    public RenderRedrawShip(ShipInfo shipInfo) {
        super(shipInfo.getX(), shipInfo.getY());
        this.shipInfo = shipInfo;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isOldCoord(getX(), getY())) {
            Draw.clearCanvas(gc);
        }
        Draw.Ship(gc, FirstPlayerGUIConstants.getGUIConstant(),
                getX(), getY(),
                shipInfo.getType(),
                shipInfo.isHorizontal());
    }
}