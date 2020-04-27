package com.liver_rus.Battleships.NetworkEvent;

import com.liver_rus.Battleships.Network.Server.MetaInfo;

public interface NetworkServerEvent {
    Answer proceed(MetaInfo metaInfo);

    //TODO convertToString methods excessive for incoming messages on server side
    String convertToString();
}
