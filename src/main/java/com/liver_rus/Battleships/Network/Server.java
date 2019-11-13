package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Client.*;

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
    private static final int WRITE_BUFFER_SIZE = 8192;
    private static final Logger log = Logger.getLogger(String.valueOf(Server.class));
    private ServerSocketChannel serverChannel = null;
    private Selector selector;
    private ByteBuffer readBuffer = allocate(READ_BUFFER_SIZE);
    //TODO make one buffer for all write methods
    private ByteBuffer writeBuffer = allocate(WRITE_BUFFER_SIZE);
    private int port;

    private ServerGameEngine gameEngine;
    private SelectionKey turnHolder;

    private final static int MAX_CONNECTIONS = 2;
    private int numConnections = 0;
    //TODO try attachment
//    selectionKey.attach(theObject);
//
//    Object attachedObj = selectionKey.attachment();
    private Map<SelectionKey, GameField> connections;


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
    private void configureServer() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ServerConstants.getLocalHost(), port);
        serverChannel.socket().bind(inetSocketAddress);
        selector = SelectorProvider.provider().openSelector();
        serverChannel.register(selector, OP_ACCEPT);

        gameEngine = new ServerGameEngine();
        connections = new HashMap<>(MAX_CONNECTIONS);
    }

    /**
     * Reading SelectionKey results and react on events
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                //log.info("Server starting on port " + ServerConstants.getDefaultPort());
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
    private void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        if (numConnections < MAX_CONNECTIONS) {
            numConnections++;
            String port = socketChannel.socket().getInetAddress().toString() + ":" + socketChannel.socket().getPort();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ, port);
            log.info("accepted connection from: " + port);
            Set<SelectionKey> all_connections = selector.keys();
            //remember connection
            for (SelectionKey connect : all_connections) {
                if (connect.channel() instanceof SocketChannel) {
                    connections.putIfAbsent(connect, new GameField());
                }
            }
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

        //log.info("Server recv> " + message +  " " + key.toString());
        //Game dependent part.
        proceedMessage(key, message);
    }

    private void proceedMessage(SelectionKey key, String message) throws IOException {
        gameEngineProceed(key, message);
        sendAnswer(key, message);
        //print field for debug
        //connections.get(key).printOnConsole();
    }

    private void gameEngineProceed(SelectionKey key, String message) throws IOException {
        if (message.equals(Constants.NetworkMessage.DISCONNECT.toString())) {
            log.info("Connection closed upon one's client request");
            sendAllClient(message);
            resetServerGameState();
            return;
        }
        try {
            //if get SEND_SHIP290H|430H|221V|451H|372H|853V|1024V|
            if (message.startsWith(Constants.NetworkMessage.SEND_SHIPS.toString())) {
                String[] shipsInfo = MessageProcessor.splitToShipInfo(message);
                if (shipsInfo.length != FleetCounter.NUM_MAX_SHIPS) {
                    //TODO create custom class exception A
                    throw new IOException("Not enough ships");
                } else {
                    for (String shipInfo : shipsInfo) {
                        if (shipInfo.length() != Constants.ShipInfoLength) {
                            //TODO create custom class exception A
                            throw new IOException("Not enough symbols in ship");
                        } else {
                            ServerGameEngine.addShipOnField(connections.get(key), Ship.createShip(shipInfo));
                        }
                    }
                    //for debug print field with ships
                    //connections.get(key).printOnConsole();
                }
            }
        } catch (IOException ex) {
            log.info("Incorrect fleet format" + ex);
            connections.put(key, new GameField());
            sendMessage(key, "Incorrect fleet format");
        }
        //SHOTXX
        if (gameEngine.isBroadcastEnabled() && turnHolder == key) {
            if (MessageProcessor.isShotLine(message)) {
                FieldCoord shootCoord = MessageProcessor.getShootCoordFromMessage(message);
                //get and mark on enemy field
                //TODO REPLACE GET FIELD WITH GET CHANNEL ATTACHABLE
                GameField field = connections.get(key);
                FieldCoord adaptedShootCoord = new MessageAdapterFieldCoord(shootCoord);
                field.setCellAsDamaged(adaptedShootCoord);
                if (field.isCellDamaged(adaptedShootCoord)) {
                    Ship ship = field.getFleet().findShip(adaptedShootCoord);
                    ship.tagShipCell(adaptedShootCoord);
                    if (field.isShipsDestroyed()) {
                        gameEngine.setGamePhase(ServerGameEngine.Phase.END_GAME);
                    }
                }
                return;
            }
        }
        if (gameEngine.isFirstTurn()) {
            if (isReadyBothChannel()) {
                log.info("both clients ready to game");
                gameEngine.setFirstTurn(false);
                swapField(connections);
                turnHolder = randomConnection();
                sendMessage(turnHolder, Constants.NetworkMessage.YOU_TURN.toString());
                sendOtherClient(turnHolder, Constants.NetworkMessage.ENEMY_TURN.toString());
                gameEngine.setReadyForBroadcast(true);
            }
            return;
        }
    }

    private void sendAnswer(SelectionKey key, String message) throws IOException {
        if (gameEngine.isBroadcastEnabled() && turnHolder == key) {
            //SHOTXX
            if (MessageProcessor.isShotLine(message)) {
                FieldCoord shootCoord = MessageProcessor.getShootCoordFromMessage(message);
                GameField field = connections.get(key);
                FieldCoord adaptedShootCoord = new MessageAdapterFieldCoord(shootCoord);
                if (field.isCellDamaged(adaptedShootCoord)) {
                    //field.printOnConsole();
                    Ship ship = field.getFleet().findShip(adaptedShootCoord);
                    sendAllClient(Constants.NetworkMessage.HIT.toString() + shootCoord);
                    if (!ship.isAlive()) {
                        sendAllClient(Constants.NetworkMessage.DESTROYED.toString() + ship.toString());
                        //update ships list
                        field.updateShipList();
                    }
                    if (field.isShipsDestroyed()) {
                        sendMessage(key, Constants.NetworkMessage.YOU_WIN.toString());
                        sendOtherClient(key, Constants.NetworkMessage.YOU_LOSE.toString());
                    } else {
                        sendMessage(key, Constants.NetworkMessage.YOU_TURN.toString());
                        sendOtherClient(key, Constants.NetworkMessage.ENEMY_TURN.toString());
                    }
                } else {
                    sendAllClient(Constants.NetworkMessage.MISS.toString() + shootCoord);
                    sendMessage(key, Constants.NetworkMessage.ENEMY_TURN.toString());
                    turnHolder = sendOtherClient(key, Constants.NetworkMessage.YOU_TURN.toString());
                }

            }
        }
    }

    //connection[1] has and shot to field[2]
    //connection[2] has and shot to field[1]
    private void swapField(Map<SelectionKey, GameField> connections) {
        ArrayList<SelectionKey> keys = new ArrayList<>(2);
        ArrayList<GameField> value = new ArrayList<>(2);
        for (Map.Entry<SelectionKey, GameField> entry : connections.entrySet()) {
            keys.add(entry.getKey());
            value.add(entry.getValue());
        }
        connections.clear();
        Collections.reverse(value);
        for (int i = 0; i < keys.size(); i++) {
            connections.put(keys.get(i), value.get(i));
        }

    }

    //probably need exchange GameField(value) single time then work with enemy value by key
    SelectionKey getEnemySelectionKey(SelectionKey receiverKey) {
        SelectionKey enemyKey = null;
        for (SelectionKey key : selector.keys()) {
            if (key != receiverKey) {
                enemyKey = key;
                break;
            }
        }
        if (enemyKey == null) {
            throw new NullPointerException();
        }
        return enemyKey;
    }

    private SelectionKey randomConnection() {
        List<SelectionKey> list = new ArrayList<>(connections.keySet());
        return list.get(new Random(System.currentTimeMillis()).nextInt(list.size()));
    }

    private void resetServerGameState() {
        for (Map.Entry<SelectionKey, GameField> entry : connections.entrySet()) {
            try {
                entry.getKey().channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connections.clear();
            gameEngine.setFirstTurn(true);
            gameEngine.setReadyForBroadcast(false);
        }
    }

    private boolean isReadyBothChannel() {
        if (connections.size() == 2) {
            for (GameField field : connections.values()) {
                if (field.isEmpty()) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Broadcast clients about events in other clients. Don't broadcast back to sender
     *
     * @param msg
     * @param receiverKey
     * @return SelectionKey of other client
     * @throws IOException
     */
    private SelectionKey sendOtherClient(SelectionKey receiverKey, String msg) throws IOException {
        msg += Constants.NetworkMessage.SPLIT_SYMBOL.getTypeValue();
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
    private void sendAllClient(String msg) throws IOException {
        msg += Constants.NetworkMessage.SPLIT_SYMBOL.getTypeValue();
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
        msg += Constants.NetworkMessage.SPLIT_SYMBOL.getTypeValue();
        ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
        if (key.isValid() && key.channel() instanceof SocketChannel) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            socketChannel.write(messageBuffer);
            messageBuffer.rewind();
        }
    }

    @Override
    public String toString() {
        return "Server in port:" + String.valueOf(ServerConstants.getDefaultPort());
    }

}
