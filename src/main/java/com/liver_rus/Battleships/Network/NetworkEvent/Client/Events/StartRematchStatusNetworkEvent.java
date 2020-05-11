package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;

import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.OFF;
import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.ON;

public class StartRematchStatusNetworkEvent implements ClientNetworkEvent {
    //client will start rematch or not
    private final boolean state;

    public StartRematchStatusNetworkEvent(boolean state) {
        this.state = state;
    }

    @Override
    public String proceed(GUIActions action) {
        if (state) {
            action.startRematch();
        } else {
            action.notStartRematch();
        }
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.START_REMATCH + (state ? ON : OFF);
    }
}
