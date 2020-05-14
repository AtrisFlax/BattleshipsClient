package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.DrawGUIEvent;
import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderHit;
import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderMiss;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;

public interface GUIActions {
    void setEnemyName(String name);

    void draw(DrawGUIEvent event);

    void reset();

    void askRematch();

    void waitSecondPlayer(String reason);

    void deploy(int[] shipLeftByTypeInit);

    void disconnect();

    void notStartRematch();

    void startRematch();

    void canShot();

    void hit(RenderHit renderHit);

    void miss(RenderMiss renderHit);

    void endMatch(PlayerType playerType);

    boolean isSaveShooting();

    String getMyName();
}



