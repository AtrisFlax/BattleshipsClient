package com.liver_rus.Battleships.Network.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.GUIActions;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;

import java.util.List;

public interface ClientNetworkEvent {
    List<ServerNetworkEvent> proceed(GUIActions gui);
    String convertToString();
}
