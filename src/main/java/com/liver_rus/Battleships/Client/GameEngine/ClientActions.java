package com.liver_rus.Battleships.Client.GameEngine;

import com.liver_rus.Battleships.Client.GUI.FXMLDocumentMainController;
import com.liver_rus.Battleships.Client.GUI.ShipInfo;

import java.io.IOException;
public interface ClientActions {
    void close() throws IOException;

    void startNetwork(String ip, int port, String myName);

    void setController(FXMLDocumentMainController controller);

    void tryDeployShip(ShipInfo currentGUIState);

    void resetFleet();

    void shot(int x, int y);

    void startServer(String ip, int port) throws IOException;
}
