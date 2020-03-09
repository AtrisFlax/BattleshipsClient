package com.liver_rus.Battleships.Network.Server;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GamePrimitives.GameField;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import com.liver_rus.Battleships.Client.GamePrimitives.WrongShipInfoSizeException;
import com.liver_rus.Battleships.Client.Tools.MessageProcessor;
import com.liver_rus.Battleships.Network.StartStopThread;

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

public class GameServerThread extends Thread implements StartStopThread {

    public enum TurnOrder {
        FIRST_CONNECTED,
        SECOND_CONNECTED,
        RANDOM_TURN
    }

    private static final int WRITE_BUFFER_SIZE = 8192;
    private ByteBuffer writeBuffer = allocate(WRITE_BUFFER_SIZE);
    private ByteBuffer readBuffer = allocate(WRITE_BUFFER_SIZE);

    private ServerSocketChannel serverChannel;
    private Selector selector;

    private static final Logger log = Logger.getLogger(String.valueOf(GameServerThread.class));

    private ServerGameEngine gameEngine;
    private SocketChannel turnHolder;

    private final static int MAX_CONNECTIONS = ServerGameEngine.maxPlayers();

    private MetaInfo metaInfo;
    private InetAddress inetAddress;
    private int port;
    private GameField[] injectGameFields;

    private TurnOrder initTurnOrder;

    private volatile boolean isRunning = true;

    public GameServerThread(String ipAddress, int port) throws IOException {
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
     * GameServerThread constructor with injection GameField[] for testing
     *
     * @param port             server port
     * @param injectGameFields injected game primitives
     * @throws IOException
     */

    public GameServerThread(String ipAddress, int port, GameField[] injectGameFields) throws IOException {
        this.port = port;
        this.inetAddress = InetAddress.getByName(ipAddress);
        if (injectGameFields.length != MAX_CONNECTIONS) {
            throw new IllegalArgumentException("GameServerThread constructor accepted gameFields with invalid length. Valid" +
                    "length=" + MAX_CONNECTIONS);
        } else {
            this.injectGameFields = injectGameFields;
            metaInfo = new MetaInfo(injectGameFields);
        }
        this.initTurnOrder = TurnOrder.RANDOM_TURN;
        configureServer();
    }

    /**
     * Broadcast msg to key
     *
     * @param msg a@throws IOException
     */
    public void sendMessage(SocketChannel socketChannel, String msg) throws IOException {
        msg = addSplitSymbol(msg);
        ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
        socketChannel.write(messageBuffer);
        messageBuffer.rewind();
    }

    public void sendMessage(SocketChannel socketChannel, NetworkEvent event) throws IOException {
        //TODO  msg = addSplitSymbol(msg); часть серлилазации пересмореть или перенисти в стратгию
        event.convert();
        msg = addSplitSymbol(msg);
        ByteBuffer messageBuffer = ByteBuffer.wrap(msg.getBytes());
        socketChannel.write(messageBuffer);
        messageBuffer.rewind();
    }

    //return injected GameFields
    public GameField[] getFields() {
        return metaInfo.getGameFields();
    }

    public String toString() {
        return "Server in port: " + port;
    }

    public void setTurnOrder(TurnOrder turnOrder) {
        this.initTurnOrder = turnOrder;
    }

    public void finalize() throws IOException {
        close();
    }

    //Close clients channels. Reset game state
    public void close() throws IOException {
        isRunning = false;
        for (SocketChannel channel : metaInfo.getChannels()) {
            if (channel != null)
                channel.close();
        }
        if (serverChannel != null) {
            serverChannel.close();
            selector.close();
        }
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
        try {
            Iterator<SelectionKey> keys;
            SelectionKey key;
            while (serverChannel.isOpen()) {
                selector.select();
                if (selector.isOpen() && isRunning) {
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
            }
        } catch (IOException e) {
            log.info("Server Error:" + e);
            closeServerChannel();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void startThread() {
        isRunning = true;
    }

    public void stopThread() {
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRunning = false;
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

    private void proceedMessage(SocketChannel channel, String message) throws IOException {
        checkDisconnect(message);
        gameEngineProceed(channel, message);
        sendAnswer(channel, message);
    }

    private void checkDisconnect(String message) throws IOException {
        if (message.equals(Constants.NetworkCommand.DISCONNECT)) {
            log.info("Connection closed upon one's client request");
            for (SocketChannel channel : metaInfo.getChannels()) {
                sendMessage(channel, message);
            }
            close();
        }
    }

    private void gameEngineProceed(SocketChannel channel, String message) throws IOException {
        try {
            if (message.startsWith(Constants.NetworkCommand.SEND_SHIPS)) {
                System.out.println(metaInfo.getField(channel));
                addShipsOnField(metaInfo.getField(channel), message);
                metaInfo.setReady(channel);
            }
        } catch (IOException | WrongShipInfoSizeException ex) {
            sendMessage(channel, "Incorrect fleet format");
            log.info("Incorrect fleet format" + ex);
        }

        if (gameEngine.isBroadcastEnabled() && turnHolder == channel) {
            if (message.startsWith(Constants.NetworkCommand.SHOT)) {
                String msgCoord = message.replaceAll("\\D+", "");
                int x = MessageProcessor.getX(msgCoord);
                int y = MessageProcessor.getY(msgCoord);
                //get and mark on enemy field
                GameField field = metaInfo.getField(channel);
                Ship ship = field.getFleet().findShip(x, y);
                field.shoot(x, y);
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
        sendMessage(turnHolder, Constants.NetworkCommand.YOU_TURN);
        sendMessage(metaInfo.getOtherClientChannel(turnHolder), Constants.NetworkCommand.ENEMY_TURN);
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
            if (message.startsWith(Constants.NetworkCommand.SHOT)) {
                String msgCoord = message.replaceAll("\\D+", "");
                int x = MessageProcessor.getX(msgCoord);
                int y = MessageProcessor.getY(msgCoord);
                GameField field = metaInfo.getField(channel);
                if (field.isCellDamaged(x, y)) {
                    sendHit(channel, field, x, y);
                } else {
                    sendMiss(channel, x, y);
                }
            }
        }
    }

    interface NetworkEvent {
        String convert();
    }
    interface Desrialize {
        String convert();
    }

    class HitNetworkEvent implements NetworkEvent {

        int x;
        int y;

        HitNetworkEvent(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String convert() {
            return Constants.NetworkCommand.HIT + x + y;
        }
    }

    private void sendHit(SocketChannel channel, GameField field, int x, int y) throws IOException {
        for (SocketChannel socketChannel : metaInfo.getChannels()) {
            //TODO serialize strategy

            HitNetworkEvent event = new HitNetworkEvent(x, y);
            sendMessage(socketChannel, event);
        }
        checkAndSendShipDestruction();
        checkAndSendFleetDestruction(channel, field);
    }

    private void checkAndSendFleetDestruction(SocketChannel channel, GameField field) throws IOException {
        if (field.allShipsDestroyed()) {
            sendMessage(channel, Constants.NetworkCommand.YOU_WIN);

            sendMessage(metaInfo.getOtherClientChannel(channel), Constants.NetworkCommand.YOU_LOSE);
        } else {
            sendMessage(channel, Constants.NetworkCommand.YOU_TURN);
            sendMessage(metaInfo.getOtherClientChannel(channel), Constants.NetworkCommand.ENEMY_TURN);
        }
    }

    private void checkAndSendShipDestruction() throws IOException {
        Ship destroyedShip = gameEngine.getDestroyedShip();
        if (destroyedShip != null) {
            for (SocketChannel channel : metaInfo.getChannels()) {
                sendMessage(channel, Constants.NetworkCommand.DESTROYED + destroyedShip);
            }
            gameEngine.setDestroyedShip(null);
        }
    }

    private void sendMiss(SocketChannel channel, int x, int y) throws IOException {
        for (SocketChannel ch : metaInfo.getChannels()) {
            sendMessage(ch, Constants.NetworkCommand.MISS + x + y);
        }
        sendMessage(channel, Constants.NetworkCommand.ENEMY_TURN);
        turnHolder = metaInfo.getOtherClientChannel(channel);
        sendMessage(turnHolder, Constants.NetworkCommand.YOU_TURN);
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
        return msg + Constants.NetworkCommand.SPLIT_SYMBOL;
    }
}
