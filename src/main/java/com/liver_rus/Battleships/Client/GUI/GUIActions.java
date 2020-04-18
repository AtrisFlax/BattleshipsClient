package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.DrawGUIEvent;
import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderHit;
import com.liver_rus.Battleships.Client.GameEngine.ClientGameEngine;

public interface GUIActions {
    void setEnemyName(String name);

    void setClientEngine(ClientGameEngine clientGameEngine);

    void draw(DrawGUIEvent event);

    void redraw(DrawGUIEvent event);

    void reset();

    void startRematch();

    void waitSecondPlayer(String reason);

    void deploy(int[] shipLeftByTypeInit);

    void disconnect();

    void notStartRematch();

    void canShot();

    void hit(RenderHit renderHit);
}
