package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.GUI.DrawEvents.DrawGUIEvent;
import com.liver_rus.Battleships.Client.GameEngine.ClientGameEngine;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;

public interface GUIActions {
    void setClientEngine(ClientGameEngine clientGameEngine);

    void shipWasPopped(Ship.Type type, int value);

    void draw(DrawGUIEvent event);

    void setInfo(String message, String readableView);

    void unlockDeploying();

    void setLockGUI();

    void reset(String resetReason);



    //???????????????

    void setStartDeployingFleetInfo(String myName);

}
