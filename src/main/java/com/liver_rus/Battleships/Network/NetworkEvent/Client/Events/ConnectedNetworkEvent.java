package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Events.MyNameNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Events.SetSaveShootingNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;

import java.util.ArrayList;
import java.util.List;

public class ConnectedNetworkEvent implements ClientNetworkEvent {

    @Override
    public List<ServerNetworkEvent> proceed(GUIActions action) {
        List<ServerNetworkEvent> answer = new ArrayList<>();
        answer.add(new SetSaveShootingNetworkEvent(action.isSaveShooting()));
        answer.add(new MyNameNetworkEvent(action.getMyName()));
        return answer;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.CONNECTED;
    }

}
