package com.liver_rus.Battleships.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventClient;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.liver_rus.Battleships.Network.Server.GamePrimitives.Fleet.NUM_TYPE;

//client do nothing
public class NetworkEventDeploy implements NetworkEventClient {

    int[] shipLeftByTypeInit;

    //left ships
    public NetworkEventDeploy(int[] shipLeftByTypeInit) {
        assert shipLeftByTypeInit.length == NUM_TYPE;
        this.shipLeftByTypeInit = shipLeftByTypeInit;
    }

    @Override
    public String proceed(GUIActions action) {
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


