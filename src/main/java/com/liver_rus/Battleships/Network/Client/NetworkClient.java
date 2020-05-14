package com.liver_rus.Battleships.Network.Client;

import com.liver_rus.Battleships.Network.MessageSplitter;
import com.liver_rus.Battleships.Network.Restartable;
import com.liver_rus.Battleships.utils.MyLogger;
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
    private static final Logger LOGGER = MyLogger.GetLogger(NetworkClient.class);
    private static final int QUEUE_SIZE = 50;

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
        LOGGER.info("NetworkClient started ip : " + ip + " port : " + port);
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

    public void sendMessage(String message) {
        clientReceiver.sendMessage(message);
    }

    @Override
    public void subscribeForInbox(Consumer<String> consumer) {
        inbox.addListener((ListChangeListener<String>) listener -> {
            final int FIRST_ELEMENT_INDEX = 0;
            String received_msg = inbox.get(FIRST_ELEMENT_INDEX);
            consumer.accept(received_msg);
            inbox.remove(FIRST_ELEMENT_INDEX);
        });
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
            LOGGER.log(Level.SEVERE, "Can't configure Client", e);
        }
    }

    @Override
    public void stopConnection() {
        clientReceiver.stopThread();
        clientReceiver.close();
    }

    private class ReceiveThread extends Thread {
        private final SocketChannel channel;
        private final ObservableList<String> inbox;

        final int BUFFER_SIZE = 16000;
        ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);

        ReceiveThread(SocketChannel client, ObservableList<String> inbox) {
            super("Receive thread");
            channel = client;
            this.inbox = inbox;
        }

        public void run() {
            try {
                Iterator<SelectionKey> iter;
                SelectionKey key;
                while (isRunning && channel.isOpen()) {
                    selector.select();
                    if (selector.isOpen()) {
                        iter = selector.selectedKeys().iterator();
                        while (iter.hasNext()) {
                            key = iter.next();
                            iter.remove();
                            if (!key.isValid()) {
                                continue;
                            }
                            if (key.isValid() && key.isConnectable()) {
                                channel.finishConnect();
                                key.interestOps(OP_READ);
                            }
                            if (key.isValid() && key.isReadable()) {
                                readMessage(key);
                            }
                            if (key.isValid() && key.isWritable()) {
                                sendMessage(key);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.info("Client Error:" + e);
                close();
            }
        }

        public void sendMessage(String message) {
            message = MessageSplitter.AddSplitSymbol(message);
            try {
                messageSynchronize.put(message);
                SelectionKey key = channel.keyFor(selector);
                key.interestOps(OP_WRITE);
                selector.wakeup();
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, "Send message failed", ex);
            }
        }

        private void startThread() {
            isRunning = true;
        }

        private void stopThread() {
            isRunning = false;
        }

        private void close() {
            try {
                channel.close();
                selector.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "NetworkClient: Failed while closing", e);
            }
        }

        private void sendMessage(SelectionKey key) throws IOException {
            while (!messageSynchronize.isEmpty()) {
                String message = messageSynchronize.poll();
                channel.write(ByteBuffer.wrap(message.getBytes()));
                LOGGER.info("Client send=  " + message);
                key.interestOps(OP_READ);
            }
        }

//        private void readMessage(SelectionKey key) throws IOException {
//            SocketChannel socketChannel = (SocketChannel) key.channel();
//            StringBuilder messageBuilder = new StringBuilder();
//            readBuffer.clear();
//            int read;
//            while ((read = socketChannel.read(readBuffer)) > 0) {
//                readBuffer.flip();
//                byte[] bytes = new byte[readBuffer.limit()];
//                readBuffer.get(bytes);
//                messageBuilder.append(new String(bytes));
//                readBuffer.clear();
//            }
//            String message;
//            if (read < 0) {
//                channel.close();
//                key.cancel();
//                return;
//            } else {
//                message = messageBuilder.toString();
//            }
//            inbox.addAll(Arrays.asList(MessageSplitter.Split(message)));
//        }


        private void readMessage(SelectionKey key) throws IOException {
            ByteBuffer buf = ByteBuffer.allocate(4096);
            channel.read(buf);
            String message = new String(buf.array()).trim();
            LOGGER.info("Client read=  " + message);
            inbox.addAll(Arrays.asList(MessageSplitter.Split(message)));
        }

    }
}





