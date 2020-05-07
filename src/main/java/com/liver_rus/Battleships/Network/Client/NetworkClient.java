package com.liver_rus.Battleships.Network.Client;

import com.liver_rus.Battleships.Network.MessageSplitter;
import com.liver_rus.Battleships.Network.StartStopThread;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
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

public class NetworkClient implements MailBox, StartStopThread {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final int QUEUE_SIZE = 16;

    private SocketChannel channel;
    private Selector selector;
    private final BlockingQueue<String> messageSynchronize = new ArrayBlockingQueue<>(QUEUE_SIZE);

    private final ObservableList<String> inbox;

    private InetAddress ipAddress;
    private final int port;

    private volatile boolean isRunning = true;

    public NetworkClient(String ipAddress, int port) {
        this.port = port;
        this.inbox = FXCollections.observableArrayList();
        try {
            this.ipAddress = InetAddress.getByName(ipAddress);
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            selector = Selector.open();
            channel.register(selector, OP_CONNECT);
            channel.connect(new InetSocketAddress(this.ipAddress, this.port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ReceiveThread clientReceiver = new ReceiveThread(channel, inbox);
        clientReceiver.start();
        log.info("NetworkClient created");
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
    public void disconnect() {
        stopThread();
    }

    public void close() {
        isRunning = false;
        try {
            channel.close();
            selector.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "NetworkClient: Failed while closing", e);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void startThread() {
        isRunning = true;
    }

    public void stopThread() {
        isRunning = false;
    }

    private class ReceiveThread extends Thread {
        private final SocketChannel channel;
        private final ObservableList<String> inbox;

        ReceiveThread(SocketChannel client, ObservableList<String> inbox) {
            super("Receive thread");
            channel = client;
            this.inbox = inbox;
        }

        public void run() {
            try {
                while (channel.isOpen()) {
                    selector.select();
                    if (selector.isOpen() && isRunning) {
                        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                        SelectionKey key;
                        while (keys.hasNext()) {
                            key = keys.next();
                            keys.remove();
                            if (key.isValid()) {
                                if (key.isConnectable()) {
                                    channel.finishConnect();
                                    key.interestOps(OP_WRITE);
                                }
                                if (key.isReadable()) {
                                    receiveMessage();
                                }
                                if (key.isWritable()) {
                                    String line = messageSynchronize.poll();
                                    if (line != null) {
                                        channel.write(ByteBuffer.wrap(line.getBytes()));
                                        key.interestOps(OP_READ);
                                    }
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

        private void receiveMessage() throws IOException {
            ByteBuffer buf = ByteBuffer.allocate(4096);
            int nBytes;
            nBytes = channel.read(buf);

            if (nBytes == 4096 || nBytes == 0)
                return;
            String message = new String(buf.array()).trim();
            System.out.println(message);
            inbox.addAll(Arrays.asList(MessageSplitter.GetSplit(message)));
        }
    }
}





