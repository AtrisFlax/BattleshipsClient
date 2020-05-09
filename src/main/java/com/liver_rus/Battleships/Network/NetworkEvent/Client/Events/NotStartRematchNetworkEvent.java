package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;

//client do nothing
public class NotStartRematchNetworkEvent implements ClientNetworkEvent {

    @Override
    public String proceed(GUIActions action) {
        action.notStartRematch();
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.NOT_START_REMATCH;
    }

}
