package com.liver_rus.Battleships.Network;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.channels.SelectionKey.*;

public class Client {

    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private SocketChannel channel = null;
    private Selector selector = null;
    private ReceiveThread clientReceiver = null;
    private BlockingQueue<String> messageSynchronize = new ArrayBlockingQueue<>(2);
    int port = 0;
    InetAddress address = null;
    private ObservableList<String> inbox;

    public Client(ObservableList<String> inbox, String address, int port) {
        this.inbox = inbox;
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.port = port;
    }

    public void makeConnection() throws IOException {
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        selector = Selector.open();
        channel.register(selector, OP_CONNECT);
        channel.connect(new InetSocketAddress(InetConstants.getLocalHost(), port));

        startClientReceiver();
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

    private void startClientReceiver() {
        clientReceiver = new ReceiveThread(channel, inbox);
        clientReceiver.start();
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
                while (channel.isOpen() ) {
                    selector.select();

                    if (selector.isOpen()) {


                        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                        SelectionKey key;
                        while (keys.hasNext()) {
                            key = keys.next();
                            keys.remove();
                            if (!key.isValid())
                                continue;
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
            log.info("message.trim " + message.trim());
            inbox.add(message.trim());
        }
    }

    public void close() {
        try {
            try {
                channel.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Failed while closing Client channel.", e);
            }
            selector.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed while closing Client selector.", e);
        }
        clientReceiver.interrupt();
    }

    public ObservableList<String> getInbox() {
        return inbox;
    }
}
