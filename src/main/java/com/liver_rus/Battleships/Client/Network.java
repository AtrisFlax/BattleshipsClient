package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.SocketFX.Constants;
import com.liver_rus.Battleships.SocketFX.FxSocketClient;
import com.liver_rus.Battleships.SocketFX.FxSocketServer;
import com.liver_rus.Battleships.SocketFX.GenericSocket;
import javafx.collections.ObservableList;

public class Network {

    private boolean connected;

    private boolean isClient;

    private GenericSocket socket;

    GenericSocket getSocket() {
        return socket;
    }

//    private synchronized void notifyDisconnected() {
//        connected = false;
//        notifyAll();
//    }

    private void setIsClient(boolean isClient) {
        this.isClient = isClient;
    }

    boolean getIsClient() {
        return isClient;
    }

    public synchronized void setIsConnected(boolean connected) {
        this.connected = connected;
    }

    public void connectServer(int port, ObservableList<String> rcvdMsgsData) {
        FxSocketListener fxSocketListener = new FxSocketListener();
        fxSocketListener.setDataReceiver(rcvdMsgsData);

        socket = new FxSocketServer(fxSocketListener,
                port,
                Constants.instance().DEBUG_NONE);
        socket.connect();
        setIsClient(false);
    }

    public void connectClient(String host, int port) {
        FxSocketListener fxSocketListener = new FxSocketListener();

        socket = new FxSocketClient(fxSocketListener,
                host,
                port,
                Constants.instance().DEBUG_NONE);
        socket.connect();
        setIsClient(true);
    }

    void shutdown(){
        socket.shutdown();
    }
}
