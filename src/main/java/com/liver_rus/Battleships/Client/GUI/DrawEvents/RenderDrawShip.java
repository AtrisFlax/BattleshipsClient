package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import com.liver_rus.Battleships.Client.GUI.GUIState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RenderDrawShip implements DrawGUIEvent {
    private final GUIState shipInfo;

    //TODO after ser\deser be another signature
    public RenderDrawShip(GUIState shipInfo) {
        this.shipInfo = shipInfo;
    }

    @Override
    public void render(GraphicsContext gc) {
        Draw.ShipOnField(gc, Color.BLACK, FirstPlayerGUIConstants.getGUIConstant(),
                shipInfo.getX(), shipInfo.getY(),
                Draw.convertTypeToShipLength(shipInfo.getShipType()),
                shipInfo.isHorizontalOrientation());
    }
}
