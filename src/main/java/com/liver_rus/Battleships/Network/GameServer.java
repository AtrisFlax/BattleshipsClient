package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GamePrimitive.FieldCoord;
import com.liver_rus.Battleships.Client.GamePrimitive.GameField;
import com.liver_rus.Battleships.Client.GamePrimitive.Ship;
import com.liver_rus.Battleships.Client.Tools.MessageAdapterFieldCoord;
import com.liver_rus.Battleships.Client.Tools.MessageProcessor;

import java.io.IOException;
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

//TODO GameServer один метод hangleMessage procceedMessga(от входященго меняю значение GameFiled'а)


/* TODO interface
public SelectionKey sendOtherClient(SelectionKey receiverKey, String msg) throws IOException {
        return super.sendOtherClient(receiverKey, addSplitSymbol(msg));
        }

@Override
public void sendAllClients(String msg) throws IOException {
        super.sendAllClients(addSplitSymbol(msg));
        }

@Override
public void sendMessage(SelectionKey key, String msg) throws IOException {
        super.sendMessage(key, addSplitSymbol(msg));
        }
*/

/*
интерфес метаинфо возрващающий мапу с данными
сервер метфо инфо, который по ключу возвращает объект селекон ки
 */

/*
+test game server
приходит сообщение
проверяем game field как поменялся
написать метод служебный для полученя gamefield

 */


/*
передают gameSever gamefield
и смотрим как gameserver себя ведет. из теста имеет доступ gameField из теста



 */


/*
скрипт который пакует в jar и работает ли на разных машинах


 */

/*
научится деплоить скриптами
bash скрипт для этого
(любая среда для запуска linux команд

в папочку ложим сгенированый jar


ide делать артефакт? (но делаем на билд сервер (скрипт))
 */

public class GameServer implements Runnable {

    public enum TurnOrder {
        FIRST_CONNECTED,
        SECOND_CONNECTED,
        RANDOM_TURN
    }

    //TODO make one buffer for all write methods
    private static final int WRITE_BUFFER_SIZE = 8192;
    private static final int READ_BUFFER_SIZE = 8192;
    private ByteBuffer writeBuffer = allocate(WRITE_BUFFER_SIZE);
    private ByteBuffer readBuffer = allocate(WRITE_BUFFER_SIZE);

    private ServerSocketChannel serverChannel = null;
    private Selector selector;

    //TODO different severity logging
    private static final Logger log = Logger.getLogger(String.valueOf(GameServer.class));

    private ServerGameEngine gameEngine;
    private SocketChannel turnHolder;

    private final static int MAX_CONNECTIONS = ServerGameEngine.maxPlayers();
    private int numAcceptedConnections = 0;

    private MetaInfo metaInfo;
    private int port;

    private TurnOrder turnOrder;

    public GameServer(int port) throws IOException {
        this.port = port;
        metaInfo = new MetaInfo(new GameField[MAX_CONNECTIONS]);
        turnOrder = TurnOrder.RANDOM_TURN;
        configureServer();
    }

    /**
     * GameServer constructor with injection GameField[] for testing
     *
     * @param port             server port
     * @param injectGameFields injected game primitives
     * @param turnOrder        determinate whose turn is the first
     * @throws IOException
     */

    public GameServer(int port, GameField[] injectGameFields, TurnOrder turnOrder) throws IOException {
        this.port = port;
        if (injectGameFields.length != MAX_CONNECTIONS) {
            //TODO create custom exception
            throw new IllegalArgumentException("GameServer constructor accepted gameFields with invalid length. Valid" +
                    "length=" + MAX_CONNECTIONS);
        } else {
            metaInfo = new MetaInfo(injectGameFields);
        }
        this.turnOrder = turnOrder;
        configureServer();
    }

    private void configureServer() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ServerConstants.getLocalHost(), port);
        serverChannel.socket().bind(inetSocketAddress);
        selector = SelectorProvider.provider().openSelector();
        serverChannel.register(selector, OP_ACCEPT);
        gameEngine = new ServerGameEngine();
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        if (numAcceptedConnections < MAX_CONNECTIONS) {
            String port = socketChannel.socket().getInetAddress().toString() + ":" + socketChannel.socket().getPort();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ, port);
            log.info("accepted connection from: " + port);
            metaInfo.put(socketChannel, numAcceptedConnections);
            numAcceptedConnections++;
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
                try {
                    serverChannel.close();
                } catch (IOException ignored) {

                }
            }
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
        if (message.equals(Constants.NetworkMessage.DISCONNECT)) {
            log.info("Connection closed upon one's client request");
            sendAllClients(message);
            resetServerGameState();
            return;
        }

        gameEngineProceed(channel, message);
        sendAnswer(channel, message);
    }

    private void gameEngineProceed(SocketChannel channel, String message) throws IOException {
        try {
            if (message.startsWith(Constants.NetworkMessage.SEND_SHIPS)) {
                String[] shipsInfo = MessageProcessor.splitToShipInfo(message);
                Ship[] ships = Ship.createShips(shipsInfo);
                GameField field = metaInfo.getField(channel);
                for (Ship ship : ships) {
                    field.addShip(ship);
                }
                metaInfo.setReady(channel);
            }
        } catch (IOException ex) {
            sendMessage(channel, "Incorrect fleet format");
            log.info("Incorrect fleet format" + ex);
        }

        if (gameEngine.isBroadcastEnabled() && turnHolder == channel) {
            if (message.startsWith(Constants.NetworkMessage.SHOT)) {
                FieldCoord shootCoord = MessageProcessor.getShootCoordFromMessage(message);
                //get and mark on enemy field
                GameField field = metaInfo.getField(channel);
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
        if (gameEngine.isFirstTurn() && isReadyBothChannel()) {
            log.info("Server: Both clients ready to game");
            gameEngine.setFirstTurn(false);
            metaInfo.swapFields();
            switch (turnOrder) {
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
            sendMessage(turnHolder, Constants.NetworkMessage.YOU_TURN);
            sendOtherClient(turnHolder, Constants.NetworkMessage.ENEMY_TURN);
            gameEngine.setReadyForBroadcast(true);
            return;
        }
    }

    private void sendAnswer(SocketChannel channel, String message) throws IOException {
        if (gameEngine.isBroadcastEnabled() && turnHolder == channel) {
            //SHOTXX
            if (message.startsWith(Constants.NetworkMessage.SHOT)) {
                FieldCoord shootCoord = MessageProcessor.getShootCoordFromMessage(message);
                GameField field = metaInfo.getField(channel);
                FieldCoord adaptedShootCoord = new MessageAdapterFieldCoord(shootCoord);
                if (field.isCellDamaged(adaptedShootCoord)) {
                    //field.printOnConsole();
                    Ship ship = field.getFleet().findShip(adaptedShootCoord);
                    sendAllClients(Constants.NetworkMessage.HIT + shootCoord);
                    if (!ship.isAlive()) {
                        sendAllClients(Constants.NetworkMessage.DESTROYED + ship.toString());
                        //update ships list
                        field.updateShipList();
                    }
                    if (field.isShipsDestroyed()) {
                        sendMessage(channel, Constants.NetworkMessage.YOU_WIN);
                        sendOtherClient(channel, Constants.NetworkMessage.YOU_LOSE);
                    } else {
                        sendMessage(channel, Constants.NetworkMessage.YOU_TURN);
                        sendOtherClient(channel, Constants.NetworkMessage.ENEMY_TURN);
                    }
                } else {
                    sendAllClients(Constants.NetworkMessage.MISS + shootCoord);
                    sendMessage(channel, Constants.NetworkMessage.ENEMY_TURN);
                    turnHolder = sendOtherClient(channel, Constants.NetworkMessage.YOU_TURN);
                }
            }
        }
    }

    private SocketChannel randomConnection() {
        int randID = new Random(System.currentTimeMillis()).nextInt(MAX_CONNECTIONS);
        SocketChannel[] channels = metaInfo.getChannels();
        return channels[randID];
    }

    //TODO normal state server tracking
    private void resetServerGameState() {
        metaInfo = new MetaInfo(new GameField[MAX_CONNECTIONS]);
        gameEngine.setFirstTurn(true);
        gameEngine.setReadyForBroadcast(false);
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

    public SocketChannel sendOtherClient(SocketChannel receiverChannel, String msg) throws IOException {
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

    public String toString() {
        return "Server in port:" + ServerConstants.getDefaultPort();
    }

    private String addSplitSymbol(String msg) {
        return msg + Constants.NetworkMessage.SPLIT_SYMBOL;
    }
}
