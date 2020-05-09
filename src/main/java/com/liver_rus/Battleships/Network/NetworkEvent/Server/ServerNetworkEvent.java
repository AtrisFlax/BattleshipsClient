package com.liver_rus.Battleships.Network.NetworkEvent.Server;

import com.liver_rus.Battleships.Network.Server.MetaInfo;

public interface ServerNetworkEvent {
    Answer proceed(MetaInfo metaInfo);
    String convertToString();
}
