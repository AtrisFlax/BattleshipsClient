package com.liver_rus.Battleships.NetworkEvent.outcoming;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventClient;

//client do nothing
public class NetworkEventStartRematch implements NetworkEventClient {

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
