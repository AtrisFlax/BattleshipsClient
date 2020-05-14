package com.liver_rus.Battleships.Network.NetworkEvent.Client.Events;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.liver_rus.Battleships.Network.Server.GamePrimitives.Fleet.NUM_TYPE;

//client do nothing
public class DeployNetworkEvent implements ClientNetworkEvent {
    private final int[] shipLeftByTypeInit;

    //left ships
    public DeployNetworkEvent(int[] shipLeftByTypeInit) {
        assert shipLeftByTypeInit.length == NUM_TYPE;
        this.shipLeftByTypeInit = shipLeftByTypeInit;
    }

    @Override
    public List<ServerNetworkEvent> proceed(GUIActions action) {
        action.deploy(shipLeftByTypeInit);
        return null;
    }

    @Override
    public String convertToString() {
        String joined = Arrays.stream(shipLeftByTypeInit)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
        return NetworkCommandConstant.DEPLOY + joined;
    }
}


