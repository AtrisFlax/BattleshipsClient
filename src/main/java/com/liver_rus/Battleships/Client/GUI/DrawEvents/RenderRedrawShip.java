package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import com.liver_rus.Battleships.Client.GUI.GUIState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class RenderRedrawShip extends Redraw implements DrawGUIEvent {
    private final GUIState shipInfo;

    private boolean isDeployable;

    public RenderRedrawShip(GUIState shipInfo, boolean isDeployable) {
        super(shipInfo.getX(), shipInfo.getY());
        this.shipInfo = shipInfo;
        this.isDeployable = isDeployable;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isOldCoord(shipInfo.getX(), shipInfo.getY())) {
            Draw.clearCanvas(gc);
        }
        Color color = Draw.setColorForDrawShip(isDeployable);
        Draw.ShipOnField(gc, color, FirstPlayerGUIConstants.getGUIConstant(),
                shipInfo.getX(), shipInfo.getY(),
                Draw.convertTypeToShipLength(shipInfo.getShipType()),
                shipInfo.isHorizontalOrientation());
    }

}

