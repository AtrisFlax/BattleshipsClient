package com.liver_rus.Battleships.Network.Client;

import com.liver_rus.Battleships.Client.Constants.Constants;
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
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.nio.channels.SelectionKey.*;

public class NetworkClient implements MailBox, StartStopThread {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final int QUEUE_SIZE = 16;

    private SocketChannel channel;
    private Selector selector;
    private ReceiveThread clientReceiver;
    private BlockingQueue<String> messageSynchronize = new ArrayBlockingQueue<>(QUEUE_SIZE);

    private ObservableList<String> inbox;

    private InetAddress inetAddress;
    private int port;

    private volatile boolean isRunning = true;

    public NetworkClient(String ipAddress, int port) {
        this.port = port;
        this.inbox = FXCollections.observableArrayList();
        try {
            this.inetAddress = InetAddress.getByName(ipAddress);
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            selector = Selector.open();
            channel.register(selector, OP_CONNECT);
            channel.connect(new InetSocketAddress(this.inetAddress, port));
        } catch (IOException e ) {
            e.printStackTrace();
        }
        clientReceiver = new ReceiveThread(channel, inbox);
        clientReceiver.start();
    }

    public static NetworkClient create(String ip, int port) {
        return new NetworkClient(ip, port);
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void subscribeForInbox(Consumer<String> consumer) {
        inbox.addListener((ListChangeListener<String>) listener -> {
            String received_msg = inbox.get(inbox.size() - 1);
            log.info("NetworkClient Message receive: " + received_msg);
            consumer.accept(received_msg);
            inbox.clear();
        });
    }

    public void sendMessage(String message) {
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

    public void finalize() {
        close();
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
        private SocketChannel channel;
        ObservableList<String> inbox;

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
            } catch (IOException ignored) {
            }
        }


        private void receiveMessage() throws IOException {
            ByteBuffer buf = ByteBuffer.allocate(2048);
            int nBytes = 0;
            nBytes = channel.read(buf);

            if (nBytes == 2048 || nBytes == 0)
                return;
            String message = new String(buf.array());

            //TODO почему два сообщение за раз
            //TODO убрать SPLIT_SYBOL практику
            for (String inboxStr : message.trim().split(Pattern.quote(Constants.NetworkCommand.SPLIT_SYMBOL))) {
                if (inboxStr.length() != 0) {
                    inbox.add(inboxStr);
                }
            }
        }
    }
}





