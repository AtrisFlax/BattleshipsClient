package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Client.GamePrimitive.GameField;

import java.nio.channels.SocketChannel;

//class binding connection info (SocketChannel) and game primitives (GameField, ready flag)
class MetaInfo {

    final static int FIRST_CONNECTED_PLAYER_ID = 0;
    final static int SECOND_CONNECTED_PLAYER_ID = 1;

    private final static int MAX_CONNECTIONS = ServerGameEngine.maxPlayers();

    SocketChannel[] channels;
    GameField[] fields;
    boolean[] ready;

    MetaInfo(GameField[] fields) {
        channels = new SocketChannel[MAX_CONNECTIONS];
        this.fields = fields;
        ready = new boolean[MAX_CONNECTIONS];
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            ready[i] = false;
        }
    }

    SocketChannel[] getChannels() {
        return channels;
    }

    GameField getField(SocketChannel key) {
        GameField result = null;
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            if (key == channels[i]) {
                result = fields[i];
                break;
            }
        }
        return result;
    }

    SocketChannel getKey(int i) {
        return channels[i];
    }

    //insert only unique key
    void put(SocketChannel key, int index) {
        channels[index] = key;
    }

    void swapFields() {
        GameField temp = fields[0];
        fields[0] = fields[1];
        fields[1] = temp;
    }

    boolean isReady(int i) {
        return ready[i];
    }

    void setReady(SocketChannel key) {
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            if (key == channels[i]) {
                ready[i] = true;
                break;
            }
        }
    }

    SocketChannel getFirstConnectedPlayerChannel() {
        return channels[FIRST_CONNECTED_PLAYER_ID];
    }

    SocketChannel getSecondConnectedPlayerChannel() {
        return channels[SECOND_CONNECTED_PLAYER_ID];
    }

    public SocketChannel getOtherClientChannel(SocketChannel receiverChannel) {
        SocketChannel otherChannel = null;
        if (channels[0] == receiverChannel) otherChannel = channels[1];
        if (channels[1] == receiverChannel) otherChannel = channels[0];
        return otherChannel;
    }
}