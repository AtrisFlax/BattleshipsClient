package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Client.Constants.Constants;
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

public class Client implements IClient {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final int QUEUE_SIZE = 16;

    private SocketChannel channel;
    private Selector selector;
    private ReceiveThread clientReceiver;
    private BlockingQueue<String> messageSynchronize = new ArrayBlockingQueue<>(QUEUE_SIZE);

    private ObservableList<String> inbox;

    private InetAddress inetAddress;
    private int port;

    /***
     *
     * @param ipAddress IPv4 address
     * @param port
     * @throws IOException
     */
    public Client(String ipAddress, int port) throws IOException {
        this.inetAddress = InetAddress.getByName(ipAddress);
        this.port = port;
        this.inbox = FXCollections.observableArrayList();

        channel = SocketChannel.open();
        channel.configureBlocking(false);
        selector = Selector.open();
        channel.register(selector, OP_CONNECT);
        channel.connect(new InetSocketAddress(this.inetAddress, port));

        clientReceiver = new ReceiveThread(channel, inbox);
        clientReceiver.start();
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void subscribeForInbox(Consumer<String> consumer) {
        inbox.addListener((ListChangeListener<String>) listener -> {
            String received_msg = inbox.get(inbox.size() - 1);
            log.info("Client Message receive: " + received_msg);
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

    public void finalize() {
        try {
            channel.close();
            selector.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Client: Failed while closing", e);
        }
        clientReceiver.interrupt();
    }

    public ObservableList<String> getInbox() {
        return inbox;
    }


    public void clearInbox() {
        inbox.clear();
    }

    private class ReceiveThread extends Thread {
        private SocketChannel channel = null;
        ObservableList<String> inbox = null;

        ReceiveThread(SocketChannel client, ObservableList<String> inbox) {
            super("Receive thread");
            channel = client;
            this.inbox = inbox;
        }

        public void run() {
            try {
                while (channel.isOpen()) {
                    selector.select();
                    if (selector.isOpen()) {
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

            for (String inboxStr : message.trim().split(Pattern.quote(Constants.NetworkMessage.SPLIT_SYMBOL))) {
                if (inboxStr.length() != 0) {
                    inbox.add(inboxStr);
                }
            }
        }
    }
}





