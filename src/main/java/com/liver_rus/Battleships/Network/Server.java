package com.liver_rus.Battleships.Network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.logging.Logger;

import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.OP_ACCEPT;

//echo server
public class Server implements Runnable {
    private static final int READ_BUFFER_SIZE = 8192;
    private static final int WRITE_BUFFER_SIZE = 8192;
    private static final Logger log = Logger.getLogger(String.valueOf(Server.class));
    private ServerSocketChannel serverChannel = null;
    Selector selector;
    private ByteBuffer readBuffer = allocate(READ_BUFFER_SIZE);
    //TODO make one buffer for all write methods
    private ByteBuffer writeBuffer = allocate(WRITE_BUFFER_SIZE);
    private int port;

    public Server() throws IOException {
        port = ServerConstants.getDefaultPort();
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
    void configureServer() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ServerConstants.getLocalHost(), port);
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
                Iterator<SelectionKey> keys;
                SelectionKey key;
                while (serverChannel.isOpen()) {
                    selector.select();
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
    void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        String address = socketChannel.socket().getInetAddress().toString() + ":" + socketChannel.socket().getPort();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, address);
        log.info("accepted connection from: " + address);
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

        proceedMessage(key, message);
    }

    void proceedMessage(SelectionKey key, String message) throws IOException {
        broadcastUsers(message);
    }

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

    /**
     * Broadcast clients about events in other clients. Don't broadcast back to receiverKey
     *
     * @param msg
     * @param receiverKey
     * @return SelectionKey of other client
     * @throws IOException
     */
    SelectionKey sendOtherClient(SelectionKey receiverKey, String msg) throws IOException {
        ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
        SelectionKey otherClientKey = null;
        for (SelectionKey key : selector.keys()) {
            if (key != receiverKey) {
                if (key.isValid() && key.channel() instanceof SocketChannel) {
                    otherClientKey = key;
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    socketChannel.write(messageBuffer);
                    messageBuffer.rewind();
                }
            }
        }
        if (otherClientKey == null) {
            throw new NullPointerException();
        }
        return otherClientKey;
    }

    /**
     * Broadcast clients about events for all clients
     *
     * @param msg
     * @throws IOException
     */
    void sendAllClient(String msg) throws IOException {
        ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                socketChannel.write(messageBuffer);
                messageBuffer.rewind();
            }
        }
    }

    /**
     * Broadcast msg to key
     *
     * @param msg
     * @throws IOException
     */
    void sendMessage(SelectionKey key, String msg) throws IOException {
        ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
        if (key.isValid() && key.channel() instanceof SocketChannel) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            socketChannel.write(messageBuffer);
            messageBuffer.rewind();
        }
    }

    @Override
    public String toString() {
        return "Server is connected to port:" + String.valueOf(ServerConstants.getDefaultPort());
    }

}
