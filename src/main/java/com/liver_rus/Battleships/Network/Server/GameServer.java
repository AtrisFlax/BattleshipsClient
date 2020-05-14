package com.liver_rus.Battleships.Network.Server;

import com.liver_rus.Battleships.Network.MessageSplitter;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.ConnectedNetworkEvent;
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

    private ServerSocketChannel serverChannel;
    private final List<SocketChannel> openedClientChannels;
    private Selector selector;

    private final static int MAX_CONNECTIONS = MetaInfo.getMaxConnections();

    private final MetaInfo metaInfo;
    private final InetAddress inetAddress;
    private final int port;
    private final CreatorServerNetworkEvent eventCreator;

    private static final int WRITE_BUFFER_SIZE = 16000;
    private final ByteBuffer readBuffer = allocate(WRITE_BUFFER_SIZE);

    private volatile boolean isRunning;

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
        LOGGER.info("GameServer started ip : " + ip + " port : " + port);
    }

    public void stopConnection() {
        stopThread();
        close();
    }

    public void sendMessage(SocketChannel socketChannel, String message) {
        message = MessageSplitter.AddSplitSymbol(message);
        LOGGER.info("Server send=  " + message + " to= " + socketChannel);
        ByteBuffer messageBuffer = ByteBuffer.wrap(message.getBytes());
        try {
            socketChannel.write(messageBuffer);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Server failed to send message", e);
            e.printStackTrace();
        }
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

    /**
     * Reading SelectionKey results and react on events
     */
    @Override
    public void run() {
        try {
            Iterator<SelectionKey> iter;
            SelectionKey key;
            while (isRunning && serverChannel.isOpen()) {
                //TODO delete println
                selector.select();
                if (selector.isOpen()) {
                    iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        key = iter.next();
                        iter.remove();
                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isValid() && key.isAcceptable()) {
                            acceptConnection(key);
                        }
                        if (key.isValid() && key.isReadable()) {
                            readMessage(key);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.info("Server Error:" + e);
            close();
        }
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

    private void sendEvent(SocketChannel socketChannel, ClientNetworkEvent event) {
        sendMessage(socketChannel, event.convertToString());
    }

    private void configureServer() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(inetAddress, port));
        selector = SelectorProvider.provider().openSelector();
        serverChannel.register(selector, OP_ACCEPT);
        selector.wakeup();
        startThread();
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        if (metaInfo.getNumAcceptedConnections() < MAX_CONNECTIONS) {
            String port = socketChannel.socket().getInetAddress().toString() + ":" + socketChannel.socket().getPort();
            socketChannel.configureBlocking(false);
            LOGGER.info("GameServer Accepted connection from: " + port);
            openedClientChannels.add(socketChannel);
            metaInfo.addPlayer(socketChannel);
            socketChannel.register(selector, SelectionKey.OP_READ, port);
            sendEvent(socketChannel, new ConnectedNetworkEvent());
        } else {
            String msg = "Connection rejected. Too many connections. Max connections is " + MAX_CONNECTIONS;
            LOGGER.info(msg);
            sendMessage(socketChannel, msg);
            socketChannel.close();
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
            LOGGER.fine("socketChannel left " + socketChannel);
            key.channel().close();
            key.cancel();
            return;
        } else {
            message = messageBuilder.toString();
        }
        LOGGER.info("Server read=  " + message);
        for (String singleSplitMessage : MessageSplitter.Split(message)) {
            proceed(socketChannel, singleSplitMessage);
        }
    }

    private void proceed(SocketChannel socketChannel, String message) {
        metaInfo.setActivePlayer(socketChannel);
        ServerNetworkEvent event = eventCreator.deserializeMessage(message);
        LOGGER.info("Server event= " + event.getClass().getSimpleName());
        Answer answer = event.proceed(metaInfo);
        sendAnswer(answer);
    }

    private void sendAnswer(Answer answer) {
        if (answer != null) {
            if (!answer.isEmpty()) {
                for (Pair<Player, ClientNetworkEvent> pair : answer) {
                    SocketChannel channel = pair.getKey().getChannel();
                    ClientNetworkEvent event = pair.getValue();
                    sendEvent(channel, event);
                }
            }
        }
    }
}
