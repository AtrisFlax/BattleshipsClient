package com.liver_rus.Battleships.NetworkEvent;

import com.liver_rus.Battleships.Client.GUI.GUIActions;

public interface NetworkClientEvent {
    String proceed(GUIActions gui);

    //TODO convertToString methods excessive for incoming messages on server side
    String convertToString();
}
