package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.SocketFX.GenericSocket;
import javafx.collections.ObservableList;

public interface INetwork {
    GenericSocket getSocket();

    void setIsClient(boolean isClient);

    boolean getIsClient();

    void setIsConnected(boolean connected);

    void connectAsServer(int port, ObservableList<String> rcvdMsgsData);

    void connectAsClient(String host, int port);

    void shutdown();

    void sendMessage(String msg);
}
