package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GamePrimitives.FieldCoord;
import com.liver_rus.Battleships.Client.GamePrimitives.GameField;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import com.liver_rus.Battleships.Client.GamePrimitives.WrongShipInfoSizeException;
import com.liver_rus.Battleships.Client.Tools.MessageProcessor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Logger;

import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.OP_ACCEPT;

public class GameServer implements Runnable, IGameServer {

    public enum TurnOrder {
        FIRST_CONNECTED,
        SECOND_CONNECTED,
        RANDOM_TURN
    }

    private static final int WRITE_BUFFER_SIZE = 8192;
    private ByteBuffer writeBuffer = allocate(WRITE_BUFFER_SIZE);
    private ByteBuffer readBuffer = allocate(WRITE_BUFFER_SIZE);

    private ServerSocketChannel serverChannel = null;
    private Selector selector;

    private static final Logger log = Logger.getLogger(String.valueOf(GameServer.class));

    private ServerGameEngine gameEngine;
    private SocketChannel turnHolder;

    private final static int MAX_CONNECTIONS = ServerGameEngine.maxPlayers();

    private MetaInfo metaInfo;
    private InetAddress inetAddress;
    private int port;

    private TurnOrder initTurnOrder;

    public GameServer(String ipAddress, int port) throws IOException {
        this.inetAddress = InetAddress.getByName(ipAddress);
        this.port = port;
        GameField[] fields = new GameField[MAX_CONNECTIONS];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = new GameField();
        }
        metaInfo = new MetaInfo(fields);
        initTurnOrder = TurnOrder.RANDOM_TURN;
        configureServer();
    }

    /**
     * GameServer constructor with injection GameField[] for testing
     *
     * @param port             server port
     * @param injectGameFields injected game primitives
     * @throws IOException
     */

    public GameServer(String ipAddress, int port, GameField[] injectGameFields) throws IOException {
        this.port = port;
        this.inetAddress = InetAddress.getByName(ipAddress);
        if (injectGameFields.length != MAX_CONNECTIONS) {
            //TODO create custom exception
            throw new IllegalArgumentException("GameServer constructor accepted gameFields with invalid length. Valid" +
                    "length=" + MAX_CONNECTIONS);
        } else {
            metaInfo = new MetaInfo(injectGameFields);
        }
        this.initTurnOrder = TurnOrder.RANDOM_TURN;
        configureServer();
    }

    //return SocketChannel of Another client.
    // Only to SocketChannel in game sendAnotherClient return a different channel from receiverChannel
    public SocketChannel sendAnotherClient(SocketChannel receiverChannel, String msg) throws IOException {
        msg = addSplitSymbol(msg);
        ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
        SocketChannel otherClientChannel = metaInfo.getOtherClientChannel(receiverChannel);
        otherClientChannel.write(messageBuffer);
        messageBuffer.rewind();
        return otherClientChannel;
    }

    /**
     * //     * Broadcast clients about events for all clients
     * //     *
     * //     * @param msg
     * //     * @throws IOException
     * //
     */
    public void sendAllClients(String msg) throws IOException {
        msg = addSplitSymbol(msg);
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
     * a@throws IOException
     */
    public void sendMessage(SocketChannel socketChannel, String msg) throws IOException {
        msg = addSplitSymbol(msg);
        ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
        socketChannel.write(messageBuffer);
        messageBuffer.rewind();
    }

    //return injected GameFields
    public GameField[] getFields() {
        return metaInfo.getFields();
    }

    public String toString() {
        return "Server in port:" + ServerConstants.getDefaultPort();
    }

    public void setTurnOrder(TurnOrder turnOrder) {
        this.initTurnOrder = turnOrder;
    }

    //TODO reset server
    //reset game state. not connection
    public void reset() {
        metaInfo.reset();
        gameEngine = new ServerGameEngine();
        setTurnOrder(TurnOrder.RANDOM_TURN);
    }

    private void configureServer() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(inetAddress, port));
        selector = SelectorProvider.provider().openSelector();
        serverChannel.register(selector, OP_ACCEPT);
        gameEngine = new ServerGameEngine();
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        if (metaInfo.getNumAcceptedConnections() < MAX_CONNECTIONS) {
            String port = socketChannel.socket().getInetAddress().toString() + ":" + socketChannel.socket().getPort();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ, port);
            log.info("accepted connection from: " + port);
            metaInfo.put(socketChannel);
        } else {
            String msg = "too many connections. Max connections =" + MAX_CONNECTIONS;
            log.info(msg);
            sendMessage(socketChannel, msg);
            socketChannel.close();
        }
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
                log.info("Server Error:" + e);
                closeServerChannel();
            }
        }
    }

    private void closeServerChannel() {
        try {
            serverChannel.close();
        } catch (IOException ex) {
            ex.printStackTrace();
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
            message = key.attachment() + " left the server.";
            socketChannel.close();
        } else {
            message = messageBuilder.toString();
        }
        proceedMessage(socketChannel, message);
    }

    private void proceedMessage(SocketChannel channel , String message) throws IOException {
        //System.out.println("SERVER GET MESSAGE=" + message + " FROM CHANNEL=" + channel);
        checkDisconnect(message);
        gameEngineProceed(channel, message);;
        sendAnswer(channel, message);
    }

    private void checkDisconnect(String message) throws IOException {
        if (message.equals(Constants.NetworkMessage.DISCONNECT)) {
            log.info("Connection closed upon one's client request");
            sendAllClients(message);
            reset();
        }
    }

    private void gameEngineProceed(SocketChannel channel, String message) throws IOException {
        try {
            if (message.startsWith(Constants.NetworkMessage.SEND_SHIPS)) {
                addShipsOnField(metaInfo.getField(channel), message);
                metaInfo.setReady(channel);
            }
        } catch (IOException | WrongShipInfoSizeException ex) {
            sendMessage(channel, "Incorrect fleet format");
            log.info("Incorrect fleet format" + ex);
        }

        if (gameEngine.isBroadcastEnabled() && turnHolder == channel) {
            if (message.startsWith(Constants.NetworkMessage.SHOT)) {
                FieldCoord shootCoord = MessageProcessor.getShootCoordFromMessage(message);
                //get and mark on enemy field
                GameField field = metaInfo.getField(channel);
                Ship ship = field.getFleet().findShip(shootCoord);
                field.shoot(shootCoord);
                if (ship != null && !ship.isAlive()) {
                    gameEngine.setDestroyedShip(ship);
                }
                return;
            }
        }
        if (gameEngine.isFirstTurn() && isReadyBothChannel()) {
            sendStartGameForClients();
        }
    }

    private void sendStartGameForClients() throws IOException {
        log.info("Server: Both clients ready to game");
        gameEngine.setFirstTurn(false);
        metaInfo.swapFields();
        setFirstTurnHolder();
        sendMessage(turnHolder, Constants.NetworkMessage.YOU_TURN);
        sendAnotherClient(turnHolder, Constants.NetworkMessage.ENEMY_TURN);
        gameEngine.setReadyForBroadcast(true);
    }

    private void setFirstTurnHolder() {
        switch (initTurnOrder) {
            case FIRST_CONNECTED:
                turnHolder = metaInfo.getFirstConnectedPlayerChannel();
                break;
            case SECOND_CONNECTED:
                turnHolder = metaInfo.getSecondConnectedPlayerChannel();
                break;
            case RANDOM_TURN:
                turnHolder = randomConnection();
                break;
        }
    }

    private void addShipsOnField(GameField field, String message) throws IOException, WrongShipInfoSizeException {
        String[] shipsInfo = MessageProcessor.splitToShipInfo(message);
        Ship[] ships = Ship.createShips(shipsInfo);
        for (Ship ship : ships) {
            field.addShip(ship);
        }
    }

    private void sendAnswer(SocketChannel channel, String message) throws IOException {
        if (gameEngine.isBroadcastEnabled() && turnHolder == channel) {
            if (message.startsWith(Constants.NetworkMessage.SHOT)) {
                FieldCoord shootCoord = MessageProcessor.getShootCoordFromMessage(message);
                GameField field = metaInfo.getField(channel);
                if (field.isCellDamaged(shootCoord)) {
                    sendHit(channel, shootCoord, field);
                } else {
                    sendMiss(channel, shootCoord);
                }
            }
        }
    }

    private void sendHit(SocketChannel channel, FieldCoord shootCoord, GameField field) throws IOException {
        sendAllClients(Constants.NetworkMessage.HIT + shootCoord);
        checkAndSendShipDestruction();
        checkAndSendFleetDestruction(channel, field);
    }

    private void checkAndSendFleetDestruction(SocketChannel channel, GameField field) throws IOException {
        if (field.allShipsDestroyed()) {
            sendMessage(channel, Constants.NetworkMessage.YOU_WIN);
            sendAnotherClient(channel, Constants.NetworkMessage.YOU_LOSE);
        } else {
            sendMessage(channel, Constants.NetworkMessage.YOU_TURN);
            sendAnotherClient(channel, Constants.NetworkMessage.ENEMY_TURN);
        }
    }

    private void checkAndSendShipDestruction() throws IOException {
        Ship destroyedShip = gameEngine.getDestroyedShip();
        if(destroyedShip != null) {
            sendAllClients(Constants.NetworkMessage.DESTROYED + destroyedShip);
            gameEngine.setDestroyedShip(null);
        }
    }

    private void sendMiss(SocketChannel channel, FieldCoord shootCoord) throws IOException {
        sendAllClients(Constants.NetworkMessage.MISS + shootCoord);
        sendMessage(channel, Constants.NetworkMessage.ENEMY_TURN);
        turnHolder = sendAnotherClient(channel, Constants.NetworkMessage.YOU_TURN);
    }

    private SocketChannel randomConnection() {
        int randID = new Random(System.currentTimeMillis()).nextInt(MAX_CONNECTIONS);
        SocketChannel[] channels = metaInfo.getChannels();
        return channels[randID];
    }

    private boolean isReadyBothChannel() {
        boolean result = true;
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            if (!metaInfo.isReady(i)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private String addSplitSymbol(String msg) {
        return msg + Constants.NetworkMessage.SPLIT_SYMBOL;
    }
}
