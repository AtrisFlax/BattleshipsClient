package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.Constants.GUIConstants;
import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Draw;
import com.liver_rus.Battleships.NetworkEvent.PlayerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static com.liver_rus.Battleships.Client.Constants.GUIConstants.DASH_WIDTH;

public class RenderImpossibleDeployShip implements DrawGUIEvent {
    private final int x;
    private final int y;
    private final int shipType;
    private final boolean isHorizontal;
    PlayerType playerType;

    public RenderImpossibleDeployShip(int x, int y, int shipType, boolean isHorizontal, PlayerType playerType) {
        this.x = x;
        this.y = y;
        this.shipType = shipType;
        this.isHorizontal = isHorizontal;
        this.playerType = playerType;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setLineDashes(DASH_WIDTH);
        Color color = GUIConstants.IMPOSSIBLE_DEPLOY_COLOR;
        if (playerType == PlayerType.ME) {
            Draw.ShipOnField(gc, color, FirstPlayerGUIConstants.getGUIConstant(),
                    x, y, Draw.convertTypeToShipLength(shipType), isHorizontal);
        } else {
            Draw.ShipOnField(gc, color, SecondPlayerGUIConstants.getGUIConstant(),
                    x, y, Draw.convertTypeToShipLength(shipType), isHorizontal);
        }
        gc.setLineDashes(0);
    }
}
