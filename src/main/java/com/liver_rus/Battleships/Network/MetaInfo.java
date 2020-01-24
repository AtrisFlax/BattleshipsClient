package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Client.GamePrimitives.GameField;

import java.nio.channels.SocketChannel;

//class binding connection info (SocketChannel) and game primitives (GameField, ready flag)
class MetaInfo {

    final static int FIRST_CONNECTED_PLAYER_ID  = 0;
    final static int SECOND_CONNECTED_PLAYER_ID = 1;

    private final static int MAX_CONNECTIONS = ServerGameEngine.maxPlayers();

    //after accepting SEND_SHIPS message fields should be swapped
    GameField[] fields;
    SocketChannel[] channels;
    boolean[] ready;
    int numAcceptedConnections;

    /**
     *
     * @param fields injected game fields. Max size 2
     */
    public MetaInfo(GameField[] fields) {
        if (fields.length != 2) throw new IllegalArgumentException("Injected fields should be fields.length == 2");
        this.fields = fields;
        channels = new SocketChannel[MAX_CONNECTIONS];
        ready = new boolean[MAX_CONNECTIONS];
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            ready[i] = false;
        }
        numAcceptedConnections = 0;
    }

    public SocketChannel[] getChannels() {
        return channels;
    }

    public GameField getField(SocketChannel key) {
        GameField result = null;
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            if (key == channels[i]) {
                result = fields[i];
                break;
            }
        }
        return result;
    }

    public SocketChannel getChannel(int i) {
        return channels[i];
    }

    //insert only unique key
    public void put(SocketChannel key) {
        channels[numAcceptedConnections++] = key;
    }

    //swap game fields before shooting
    //player0 now shoot to player1 field
    //player1 now shoot to player0 field
    public void swapFields() {
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

    public GameField[] getFields() {
        return fields;
    }

    public int getNumAcceptedConnections() {
        return numAcceptedConnections;
    }

    public void reset() {
        for (GameField field : fields) {
            field.reset();
        }
        //swap back
        swapFields();
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            ready[i] = false;
        }
    }
}