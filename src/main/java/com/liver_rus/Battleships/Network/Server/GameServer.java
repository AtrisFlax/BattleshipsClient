package com.liver_rus.Battleships.Network.Server;

import com.liver_rus.Battleships.Network.MessageSplitter;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Answer;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.CreatorServerNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;
import com.liver_rus.Battleships.Network.Restartable;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.utils.MyLogger;
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

public class GameServer extends Thread implements Restartable {
    private static final Logger LOGGER = MyLogger.GetLogger(GameServer.class);

    private static final int WRITE_BUFFER_SIZE = 16384;

    private ServerSocketChannel serverChannel;
    private final List<SocketChannel> openedClientChannels;
    private Selector selector;

    private final static int MAX_CONNECTIONS = MetaInfo.getMaxConnections();

    private MetaInfo metaInfo;
    private InetAddress inetAddress;
    private int port;
    private final CreatorServerNetworkEvent eventCreator;
    private volatile boolean isRunning = true;

    private ByteBuffer buffer = allocate(WRITE_BUFFER_SIZE);;

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

    public GameServer(String ip, int port, GameField[] injectGameFields) throws IOException {
        this.inetAddress = InetAddress.getByName(ip);
        this.port = port;
        this.eventCreator = new CreatorServerNetworkEvent();
        this.metaInfo = MetaInfo.create(injectGameFields);
        this.openedClientChannels = new ArrayList<>(MAX_CONNECTIONS);
        configureServer();
        LOGGER.info("GameServer started on ip" + ip + "port" + port);
    }

    @Override
    public void restart(String ip, int port) throws IOException {
        this.inetAddress = InetAddress.getByName(ip);
        this.port = port;
        this.metaInfo = MetaInfo.create(null);
        configureServer();
        startThread();
        LOGGER.info("GameServer restarted on ip" + ip + "port" + port);
    }

    public void stopConnection() {
        stopThread();
        close();
    }

    public void sendMessage(SocketChannel socketChannel, String message) {
        message = MessageSplitter.AddSplitSymbol(message);
        System.out.println("Server send= " + message + " to= " + socketChannel);
        ByteBuffer messageBuffer = ByteBuffer.wrap(message.getBytes());
        try {
            socketChannel.write(messageBuffer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Server failed to send message", e);
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

    private void close() {
        try {
            for (SocketChannel channel : openedClientChannels) {
                if (channel != null) {
                    channel.close();
                }
            }
            openedClientChannels.clear();
            if (serverChannel != null) {
                serverChannel.close();
                selector.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception while closing :  " + GameServer.class.getSimpleName(), e);
        }
    }

    private void startThread() {
        isRunning = true;
    }

    private void stopThread() {
        isRunning = false;
    }

    private void sendMessage(SocketChannel socketChannel, ClientNetworkEvent event) {
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
            LOGGER.info("Accepted connection from: " + port);
            openedClientChannels.add(socketChannel);
            metaInfo.addPlayer(socketChannel);
        } else {
            String msg = "Connection rejected. Too many connections. Max connections is " + MAX_CONNECTIONS;
            LOGGER.info(msg);
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
            while (isRunning && serverChannel.isOpen()) {
                System.out.println("before select");
                selector.select();
                System.out.println("after select");
                if (selector.isOpen()) {
                    keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        key = keys.next();
                        keys.remove();
                        if (key.isValid() && key.isAcceptable()) {
                            System.out.println("accept");
                            acceptConnection(key);
                        }
                        if (key.isValid() && key.isReadable()) {
                            System.out.println("read");
                            readMessage(key);
                        }
                    }
                }
                System.out.println("after perform");
            }
        } catch (IOException e) {
            LOGGER.info("Server Error:" + e);
            close();
        }
    }

    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        StringBuilder messageBuilder = new StringBuilder();
        readBuffer.clear();
        int read;
        //not fix size. build message until readBuffer not empty
        while ((read = socketChannel.read(readBuffer)) > 0) {
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.limit()];
            readBuffer.get(bytes);
            messageBuilder.append(new String(bytes));
            readBuffer.clear();
        }
        String message = null;
        if (read < 0) {
            LOGGER.info(key.attachment() + " left the server.");
            socketChannel.close();
        } else {
            message = messageBuilder.toString();
        }

        if (message != null) {
            for (String singleSplitMessage : MessageSplitter.Split(message)) {
                proceed(socketChannel, singleSplitMessage);
            }
        }
    }

    private void proceed(SocketChannel socketChannel, String message) {
        metaInfo.setActivePlayer(socketChannel);
        ServerNetworkEvent event = eventCreator.deserializeMessage(message);

        //TODO delete or wrap for debug
        System.out.println("Server read= " + message);
        System.out.println("Server event= " + event.getClass().getSimpleName());

        Answer answer = event.proceed(metaInfo);
        sendAnswer(answer);
    }

    private void sendAnswer(Answer answer) {
        if (answer != null) {
            for (Pair<Player, ClientNetworkEvent> pair : answer) {
                SocketChannel channel = pair.getKey().getChannel();
                ClientNetworkEvent event = pair.getValue();
                sendMessage(channel, event);
            }
        }
    }
}
