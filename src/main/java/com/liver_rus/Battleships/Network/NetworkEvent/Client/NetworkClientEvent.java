package com.liver_rus.Battleships.Network.NetworkEvent.Client;

import com.liver_rus.Battleships.Client.GUI.GUIActions;

public interface NetworkClientEvent {
    String proceed(GUIActions gui);

    String convertToString();
}
