package com.liver_rus.Battleships.Network.Client;

import com.liver_rus.Battleships.Network.MessageSplitter;
import com.liver_rus.Battleships.Network.Restartable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.channels.SelectionKey.*;

public class NetworkClient implements MailBox, Restartable {
    //TODO replace logger
    private static final Logger log = Logger.getLogger(NetworkClient.class.getName());
    private static final int QUEUE_SIZE = 16;

    private SocketChannel channel;
    private Selector selector;
    private final BlockingQueue<String> messageSynchronize = new ArrayBlockingQueue<>(QUEUE_SIZE);

    private final ObservableList<String> inbox;

    private InetAddress ipAddress;
    private int port;

    private volatile boolean isRunning = true;

    ReceiveThread clientReceiver;

    public NetworkClient(String ip, int port) {
        this.inbox = FXCollections.observableArrayList();
        configure(ip, port);
        this.clientReceiver = new ReceiveThread(channel, inbox);
        this.clientReceiver.start();
        log.info("NetworkClient created");
    }

    private void configure(String ip, int port) {
        this.port = port;
        try {
            this.ipAddress = InetAddress.getByName(ip);
            this.channel = SocketChannel.open();
            this.channel.configureBlocking(false);
            this.selector = Selector.open();
            this.channel.register(selector, OP_CONNECT);
            this.channel.connect(new InetSocketAddress(this.ipAddress, this.port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void restart(String ip, int port) {
        this.inbox.clear();
        configure(ip, port);
        log.info("NetworkClient restarted");
        clientReceiver.startThread();
    }

    public static NetworkClient create(String ip, int port) {
        return new NetworkClient(ip, port);
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void subscribeForInbox(Consumer<String> consumer) {
        inbox.addListener((ListChangeListener<String>) listener -> {
            final int FIRST_ELEMENT_INDEX = 0;
            String received_msg = inbox.get(FIRST_ELEMENT_INDEX);
            System.out.println("Client read= " + received_msg + " to= " + channel);
            consumer.accept(received_msg);
            inbox.remove(FIRST_ELEMENT_INDEX);
        });
    }

    public void sendMessage(String message) {
        message = MessageSplitter.AddSplitSymbol(message);
        //TODO for debug delete
        System.out.println("Client send= " + message);
        try {
            messageSynchronize.put(message);
            SelectionKey key = channel.keyFor(selector);
            key.interestOps(OP_WRITE);
            selector.wakeup();
        } catch (InterruptedException ignored) {
            log.log(Level.SEVERE, "Send message failed");
        }
    }

    @Override
    public void stopConnection() {
        clientReceiver.close();
    }

    private class ReceiveThread extends Thread {
        private final SocketChannel channel;
        private final ObservableList<String> inbox;

        final int BUFFER_SIZE = 16384;
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        ReceiveThread(SocketChannel client, ObservableList<String> inbox) {
            super("Receive thread");
            channel = client;
            this.inbox = inbox;
        }

        private void startThread() {
            isRunning = true;
        }

        private void stopThread() {
            isRunning = false;
        }

        private void close() {
            stopThread();
            try {
                channel.close();
                selector.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, "NetworkClient: Failed while closing", e);
            }
        }

        public void run() {
            try {
                while (isRunning && channel.isOpen()) {
                    selector.select();
                    if (selector.isOpen()) {
                        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                        SelectionKey key;
                        while (keys.hasNext()) {
                            key = keys.next();
                            keys.remove();
                            if (key.isValid() && key.isConnectable()) {
                                channel.finishConnect();
                                key.interestOps(OP_WRITE);
                            }
                            if (key.isValid() && key.isReadable()) {
                                receiveMessage();
                            }
                            if (key.isValid() && key.isWritable()) {
                                String line = messageSynchronize.poll();
                                if (line != null) {
                                    sendMessage(line);
                                    key.interestOps(OP_READ);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.info("Client Error:" + e);
                close();
            }
        }

        private void sendMessage(String line) throws IOException {
            channel.write(ByteBuffer.wrap(line.getBytes()));
        }


        private void receiveMessage() throws IOException {
            buffer.clear();
            int nBytes;
            nBytes = channel.read(buffer);
            //TODO if buffer full - (nBytes == BUFFER_SIZE) ???
            if (nBytes == BUFFER_SIZE || nBytes == 0)
                return;
            String message = new String(buffer.array()).trim();
            System.out.println(message);
            inbox.addAll(Arrays.asList(MessageSplitter.Split(message)));
        }

    }
}





