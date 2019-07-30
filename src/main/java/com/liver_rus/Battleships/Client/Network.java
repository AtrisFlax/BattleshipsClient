package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.SocketFX.Constants;
import com.liver_rus.Battleships.SocketFX.FxSocketClient;
import com.liver_rus.Battleships.SocketFX.FxSocketServer;
import javafx.collections.ObservableList;

public class Network {

    private boolean connected;

    private FxSocketClient socketClient;
    private FxSocketServer socketServer;

    public FxSocketClient getSocketClient() {
        return socketClient;
    }

    public FxSocketServer getSocketServer() {
        return socketServer;
    }

    private synchronized void notifyDisconnected() {
        connected = false;
        notifyAll();
    }

    public synchronized void setIsConnected(boolean connected) {
        this.connected = connected;
    }

    public void connectServer(int port, ObservableList<String> rcvdMsgsData) {
        FxSocketListener fxSocketListener = new FxSocketListener();
        fxSocketListener.setDataReceiver(rcvdMsgsData);

        socketServer = new FxSocketServer(fxSocketListener,
                port,
                Constants.instance().DEBUG_NONE);
        socketServer.connect();
        socketClient = null;
    }

    public void connectClient(String host, int port) {
        FxSocketListener fxSocketListener = new FxSocketListener();

        socketClient = new FxSocketClient(fxSocketListener,
                host,
                port,
                Constants.instance().DEBUG_NONE);
        socketClient.connect();
        socketServer = null;
    }

    void shutdown(){
        socketClient.shutdown();
        socketServer.shutdown();
    }
}
