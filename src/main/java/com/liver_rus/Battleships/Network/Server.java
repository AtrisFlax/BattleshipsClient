//TODO Выделить игроспецифичную часть в стратигию
package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Client.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.logging.Logger;

import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.OP_ACCEPT;

public class Server implements Runnable {
    private static final int READ_BUFFER_SIZE = 8192;
    private static final Logger log = Logger.getLogger(String.valueOf(Server.class));
    private ServerSocketChannel serverChannel = null;
    private Selector selector;
    private ByteBuffer readBuffer = allocate(READ_BUFFER_SIZE);
    private int port;

    //Game dependent part
    private final static int MAX_CONNECTIONS = 2;
    private int numConnections = 0;
    Map<SelectionKey, Boolean> connections = new HashMap<>(2);
    private boolean isFirstTurn = true;
    boolean isReadyForBroadcast = false;

    public Server() throws IOException {
        port = InetConstants.getDefaultPort();
        configureServer();
    }

    public Server(int port) throws IOException {
        this.port = port;
        configureServer();
    }

    /**
     * Configuring server
     *
     * @throws IOException
     */
    private void configureServer() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(InetConstants.getLocalHost(), port);
        serverChannel.socket().bind(inetSocketAddress);
        selector = SelectorProvider.provider().openSelector();
        serverChannel.register(selector, OP_ACCEPT);
    }

    /**
     * Reading SelectionKey results and react on events
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                log.info("Server starting on port " + InetConstants.getDefaultPort());
                Iterator<SelectionKey> keys;
                SelectionKey key;
                while (serverChannel.isOpen()) {
                    selector.select();
                    keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        key = keys.next();
                        keys.remove();

                        if (!key.isValid())
                            continue;

                        if (key.isAcceptable())
                            acceptConnection(key);

                        if (key.isReadable())
                            readMessage(key);

                        if (key.isWritable())
                            readMessage(key);

                    }
                }
            } catch (Exception e) {
                try {
                    serverChannel.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    /**
     * Accept new connection (client)
     *
     * @param key
     * @throws IOException
     */
    private void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        if (numConnections < MAX_CONNECTIONS ) {
            numConnections++;
            String address = (new StringBuilder(socketChannel.socket().getInetAddress().toString())).append(":").append(socketChannel.socket().getPort()).toString();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ, address);
            log.info("accepted connection from: " + address);
        } else {
            String msg = "too many connections. Max connections =" + MAX_CONNECTIONS;
            log.info(msg);
            sendMessage(key, msg);
            socketChannel.close();
        }
    }

    /**
     * Read message from client
     *
     * @param key
     * @throws IOException
     */
    private void readMessage(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        StringBuilder messageBuilder = new StringBuilder();

        readBuffer.clear();
        int read = 0;
        while ((read = socketChannel.read(readBuffer)) > 0) {
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.limit()];
            readBuffer.get(bytes);
            messageBuilder.append(new String(bytes));
            readBuffer.clear();
        }
        String message;
        if (read < 0) {
            message = key.attachment() + " left the chat.";
            socketChannel.close();
        } else {
            message = messageBuilder.toString();
        }

        //Game dependent part
        proceedMessage(key, message);

        log.info(message);
    }

    private void proceedMessage(SelectionKey key, String message) throws IOException {
        if (isReadyForBroadcast) {
            broadcastUsers(key, message);
        };

        if (message.equals(Constants.NetworkMessage.DISCONNECT.toString())) {
            log.info("Connection closed upon one's client request");
            sendMessage(key, message);
            resetServerGameState();
        } else {
            setReadyFlag(key, message);
            if (isReadyBothChannel()) {
                if (isFirstTurn) {
                    log.info("both clients ready to game");
                    sendMessage(randomConnection(), Constants.NetworkMessage.YOU_TURN.toString());
                    isFirstTurn = false;
                }
                isReadyForBroadcast = true;
            }
        }
    }

    private SelectionKey randomConnection() {
        List<SelectionKey> list = new ArrayList<>(connections.keySet());
        return list.get(new Random(System.currentTimeMillis()).nextInt(list.size()));
    }

    private void resetServerGameState() throws IOException {
        for (Map.Entry<SelectionKey, Boolean> entry: connections.entrySet()){
            try {
                entry.getKey().channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            connections.clear();
            isFirstTurn = true;
            isReadyForBroadcast = false;
        }
    }

    private void setReadyFlag(SelectionKey key, String message) {
        if (Constants.NetworkMessage.READY_TO_GAME.toString().equals(message)) {
            connections.put(key, true);
        }
    }

    private boolean isReadyBothChannel() {
        return connections.values().stream().noneMatch(Boolean.FALSE::equals) && connections.size() == MAX_CONNECTIONS;
    }

    /**
     * Broadcast clients about events in other clients. Don't broadcast back to sender
     *
     * @param msg
     * @param receiverKey
     * @throws IOException
     */
    private void broadcastUsers(SelectionKey receiverKey, String msg) throws IOException {
        ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
        for (SelectionKey key : selector.keys()) {
            if (key != receiverKey) {
                if (key.isValid() && key.channel() instanceof SocketChannel) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    socketChannel.write(messageBuffer);
                    messageBuffer.rewind();
                }
            }
        }
    }

    /**
     * Broadcast clients about events for all clients
     *
     * @param msg
     * @throws IOException
     */
    private void broadcastUsers(String msg) throws IOException {
        ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                socketChannel.write(messageBuffer);
                messageBuffer.rewind();
            }
        }
    }

    private void sendMessage(SelectionKey key, String msg) throws IOException {
        ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
        if (key.isValid() && key.channel() instanceof SocketChannel) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            socketChannel.write(messageBuffer);
            messageBuffer.rewind();
        }
    }


    @Override
    public String toString() {
        return "Server in port:" + String.valueOf(InetConstants.getDefaultPort());
    }

//    public static void main(String[] args) throws IOException {
//        new Thread(new Server()).start();
//    }


}
