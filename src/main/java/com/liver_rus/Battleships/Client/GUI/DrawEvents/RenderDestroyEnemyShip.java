package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import com.liver_rus.Battleships.Client.GUI.GUIState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RenderDestroyEnemyShip implements DrawGUIEvent {

    private final int x;
    private final int y;
    private final GUIState shipInfo;


    //TODO after ser\deser be another signature
    public RenderDestroyEnemyShip(int x, int y, GUIState shipInfo) {
        this.x = x;
        this.y = y;
        this.shipInfo = shipInfo;
    }

    @Override
    public void render(GraphicsContext gc) {
        Draw.ShipOnField(gc, Color.BLACK, SecondPlayerGUIConstants.getGUIConstant(),
                shipInfo.getX(), shipInfo.getY(),
                Draw.convertTypeToShipLength(shipInfo.getShipType()),
                shipInfo.isHorizontalOrientation());
        Draw.HitCellOnField(gc, SecondPlayerGUIConstants.getGUIConstant(), x, y);
    }
}
