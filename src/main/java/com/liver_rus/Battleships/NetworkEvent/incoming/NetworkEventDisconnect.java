package com.liver_rus.Battleships.NetworkEvent.incoming;

import com.liver_rus.Battleships.Network.Server.MetaInfo;
import com.liver_rus.Battleships.NetworkEvent.Answer;
import com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventServer;
import com.liver_rus.Battleships.NetworkEvent.outcoming.NetworkEventDoDisconnect;

public class NetworkEventDisconnect implements NetworkEventServer {

    @Override
    public Answer proceed(MetaInfo metaInfo) {
        Answer string = new Answer();
        string.add(metaInfo.getActivePlayer(), new NetworkEventDoDisconnect());
        string.add(metaInfo.getPassivePlayer(), new NetworkEventDoDisconnect());
        return string;
    }

    @Override
    public String convertToString() {
        return NetworkCommandConstant.DISCONNECT;
    }

}
