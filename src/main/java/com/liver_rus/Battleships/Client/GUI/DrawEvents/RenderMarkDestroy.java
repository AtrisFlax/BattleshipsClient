package com.liver_rus.Battleships.Client.GUI.DrawEvents;

import com.liver_rus.Battleships.Client.GUI.Draw;
import javafx.scene.canvas.GraphicsContext;

import static com.liver_rus.Battleships.Network.Server.GamePrimitives.Fleet.NUM_MAX_SHIPS;
import static com.liver_rus.Battleships.Network.Server.GamePrimitives.Fleet.NUM_TYPE;

public class RenderMarkDestroy implements DrawGUIEvent {
    private final int left;

    public RenderMarkDestroy(int left) {
        assert left >= 0 && left < NUM_TYPE;
        this.left = left;
    }
    @Override
    public void render(GraphicsContext gc) {
        Draw.MarkDestroy(gc, NUM_MAX_SHIPS - left - 1 );
    }
}