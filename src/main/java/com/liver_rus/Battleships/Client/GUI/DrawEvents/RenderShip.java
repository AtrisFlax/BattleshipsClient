package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import com.liver_rus.Battleships.NetworkEvent.PlayerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RenderShip implements DrawGUIEvent {
    private final int x;
    private final int y;
    private final int shipType;
    private final boolean isHorizontal;
    PlayerType playerType;

    public RenderShip(int x, int y, int shipType, boolean isHorizontal, PlayerType playerType) {
        this.x = x;
        this.y = y;
        this.shipType = shipType;
        this.isHorizontal = isHorizontal;
        this.playerType = playerType;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (playerType == PlayerType.ME) {
            Draw.ShipOnField(gc, Color.BLACK, FirstPlayerGUIConstants.getGUIConstant(),
                    x, y,
                    Draw.convertTypeToShipLength(shipType),
                    isHorizontal);
        } else {
            Draw.ShipOnField(gc, Color.BLACK, SecondPlayerGUIConstants.getGUIConstant(),
                    x, y,
                    Draw.convertTypeToShipLength(shipType),
                    isHorizontal);
        }

    }
}


/*

public class RenderRedrawShip extends Redraw implements DrawGUIEvent {
    private final ShipInfo shipInfo;

    private boolean isDeployable;

    public RenderRedrawShip(ShipInfo shipInfo, boolean isDeployable) {
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
 */