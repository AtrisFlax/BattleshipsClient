package com.liver_rus.Battleships.Client.GUI.NetworkEvent;

public class NetworkEventUnknown implements NetworkEvent {
    final private String unknownMsg;

    NetworkEventUnknown(String msg) {
        this.unknownMsg = msg;
    }

    public String getUnknownMsg() {
        return unknownMsg;
    }

}
