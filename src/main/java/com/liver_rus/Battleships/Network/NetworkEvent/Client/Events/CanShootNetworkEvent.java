package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;

public class CanShootNetworkEvent implements ClientNetworkEvent {

    @Override
    public String proceed(GUIActions action) {
        action.canShot();
        return null;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.CAN_SHOOT;
    }

}
