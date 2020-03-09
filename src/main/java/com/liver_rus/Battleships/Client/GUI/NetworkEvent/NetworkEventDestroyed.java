package com.liver_rus.Battleships.Client.GUI.NetworkEvent;

import com.liver_rus.Battleships.Client.GUI.GUIState;

public class NetworkEventDestroyed implements NetworkEvent {
    private final GUIState shipInfo;

    public NetworkEventDestroyed(GUIState shipInfo) {
        this.shipInfo = shipInfo;
    }

    public GUIState getShipInfo() {
        return shipInfo;
    }
}
