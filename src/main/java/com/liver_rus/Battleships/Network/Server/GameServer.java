package com.liver_rus.Battleships.Network.Server;

import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.StartStopThread;
import com.liver_rus.Battleships.NetworkEvent.*;
import com.liver_rus.Battleships.NetworkEvent.Server.NetworkDisconnectEvent;
import javafx.util.Pair;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.OP_ACCEPT;

public class GameServer extends Thread implements StartStopThread {
    private static final Logger log = Logger.getLogger(String.valueOf(GameServer.class));

    private static final int WRITE_BUFFER_SIZE = 8192;
    private final ByteBuffer readBuffer = allocate(WRITE_BUFFER_SIZE);

    private ServerSocketChannel serverChannel;
    private final List<SocketChannel> openedClientChannels;
    private Selector selector;

    private final static int MAX_CONNECTIONS = MetaInfo.getMaxConnections();

    private MetaInfo metaInfo;
    private final InetAddress inetAddress;
    private final int port;
    private final CreatorServerNetworkEvent eventCreator;
    private volatile boolean isRunning = true;

    public static GameServer create(String ip, int port) throws IOException {
        return new GameServer(ip, port, null);
    }

    public static GameServer create(String ip, int port, GameField[] injectGameFields) throws IOException {
        return new GameServer(ip, port, injectGameFields);
    }

    /**
     * GameServer constructor with injection GameField[] for testing
     *
     * @param port             server port
     * @param injectGameFields injected game primitives
     */

    public GameServer(String ipAddress, int port, GameField[] injectGameFields) throws IOException {
        this.inetAddress = InetAddress.getByName(ipAddress);
        this.port = port;
        this.eventCreator = new CreatorServerNetworkEvent();
        this.metaInfo = MetaInfo.create(injectGameFields);
        openedClientChannels = new ArrayList<>(MAX_CONNECTIONS);
        configureServer();
    }

    public void sendMessage(SocketChannel socketChannel, String msg) {
        msg = addSplitSymbol(msg);
        ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
        try {
            socketChannel.write(messageBuffer);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Server failed to send message", e);
            e.printStackTrace();
        }
        messageBuffer.rewind();
    }

    //return injected GameFields
    public GameField[] getFields() {
        return metaInfo.getGameFields();
    }

    public String toString() {
        return "Server in port: " + port;
    }

    public void setFirstTurn(TurnOrder turnOrder) {
        metaInfo.setInitTurnOrder(turnOrder);
    }

    public void close() {
        try {
            isRunning = false;
            for (SocketChannel channel : openedClientChannels) {
                if (channel != null) {
                    channel.close();
                }
            }
            if (serverChannel != null) {
                serverChannel.close();
                selector.close();
            }
        } catch (IOException e) {
            //TODO add info
            e.printStackTrace();
        }
        metaInfo = null;
    }

    //TODO
    public void reset() {
        metaInfo.resetForRematch();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void startThread() {
        isRunning = true;
    }

    public void stopThread() {
        close();
        isRunning = false;
    }

    //TODO delete crutch addSplitSymbol on client and server side
    private void sendMessage(SocketChannel socketChannel, NetworkClientEvent event) {
        sendMessage(socketChannel, event.convertToString());
    }

    private void configureServer() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(inetAddress, port));
        selector = SelectorProvider.provider().openSelector();
        serverChannel.register(selector, OP_ACCEPT);
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        if (metaInfo.getNumAcceptedConnections() < MAX_CONNECTIONS) {
            String port = socketChannel.socket().getInetAddress().toString() + ":" + socketChannel.socket().getPort();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ, port);
            log.info("Accepted connection from: " + port);
            openedClientChannels.add(socketChannel);
            metaInfo.addPlayer(socketChannel);
        } else {
            String msg = "Connection rejected. Too many connections. Max connections is " + MAX_CONNECTIONS;
            log.info(msg);
            sendMessage(socketChannel, msg);
            socketChannel.close();
        }
    }

    /**
     * Reading SelectionKey results and react on events
     */
    @Override
    public void run() {
        try {
            Iterator<SelectionKey> keys;
            SelectionKey key;
            while (serverChannel.isOpen()) {
                selector.select();
                if (selector.isOpen() && isRunning) {
                    keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        key = keys.next();
                        keys.remove();
                        if (key.isValid()) {
                            if (key.isAcceptable())
                                acceptConnection(key);
                            if (key.isReadable())
                                readMessage(key);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.info("Server Error:" + e);
            closeServerChannel();
        }
    }

    private void closeServerChannel() {
        try {
            serverChannel.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        StringBuilder messageBuilder = new StringBuilder();
        readBuffer.clear();
        int read;
        while ((read = socketChannel.read(readBuffer)) > 0) {
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.limit()];
            readBuffer.get(bytes);
            messageBuilder.append(new String(bytes));
            readBuffer.clear();
        }
        String message;
        if (read < 0) {
            message = key.attachment() + " left the server.";
            socketChannel.close();
        } else {
            message = messageBuilder.toString();
        }
        metaInfo.setActivePlayer(socketChannel);
        NetworkServerEvent networkEvent = eventCreator.deserializeMessage(message);

        System.out.println("Server: msg=" + message);
        System.out.println(networkEvent.getClass().getSimpleName());


        Answer answer = networkEvent.proceed(metaInfo);
        sendEvent(answer);
        if (networkEvent instanceof NetworkDisconnectEvent) {
            log.info("Connection closed upon one's client request");
            for (SocketChannel openChannel : openedClientChannels) {
                sendMessage(openChannel, message);
            }
            close();
        }
    }

    private void sendEvent(Answer answer) {
        for (Pair<Player, NetworkClientEvent> pair : answer) {
            SocketChannel channel = pair.getKey().getChannel();
            NetworkClientEvent event = pair.getValue();
            sendMessage(channel, event);
        }
    }

    private String addSplitSymbol(String msg) {
        return msg + NetworkCommandConstant.SPLIT_SYMBOL;
    }
}
