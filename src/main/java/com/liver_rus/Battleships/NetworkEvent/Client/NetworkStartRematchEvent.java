package com.liver_rus.Battleships.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkClientEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;

public class NetworkStartRematchEvent implements NetworkClientEvent {

    @Override
    public String proceed(GUIActions action) {
        action.startRematch();
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.START_REMATCH;
    }

}
