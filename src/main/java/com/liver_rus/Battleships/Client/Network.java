package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.SocketFX.*;
import javafx.collections.ObservableList;

class Network implements INetwork {

    private boolean connected = false;

    private boolean isClient = false;

    private GenericSocket socket = null;

    @Override
    public GenericSocket getSocket() {
        return socket;
    }

    private synchronized void notifyDisconnected() {
        connected = false;
        notifyAll();
    }

    @Override
    public void setIsClient(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    public boolean getIsClient() {
        return isClient;
    }

    @Override
    public synchronized void setIsConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public void connectAsServer(int port, ObservableList<String> rcvdMsgsData) {
        setIsClient(false);
        socket = new FxSocketServer(new FxSocketListener(rcvdMsgsData),
                port,
                Constants.instance().DEBUG_NONE);
        socket.connect();
    }

    @Override
    public void connectAsClient(String host, int port) {
        setIsClient(true);
        socket = new FxSocketClient(new FxSocketListener(),
                host,
                port,
                Constants.instance().DEBUG_NONE);
        socket.connect();
    }

    @Override
    public void shutdown() {
        socket.shutdown();
    }

    @Override
    public void sendMessage(String msg) {
        socket.sendMessage(msg);
    }

    class FxSocketListener implements SocketListener {

        ObservableList<String> rcvdMsgsData;

        FxSocketListener() {
        }

        FxSocketListener(ObservableList<String> rcvdMsgsData) {
            this.rcvdMsgsData = rcvdMsgsData;
        }

        @Override
        public void onMessage(String line) {
            if (line != null && !line.equals(Constant.NetworkMessage.EMPTY_STRING)) {
                rcvdMsgsData.add(line);
            }
        }

        @Override
        public void onClosedStatus(boolean isClosed) {
            if (isClosed) {
                notifyDisconnected();
            } else {
                setIsConnected(true);
            }
        }
    }

}
