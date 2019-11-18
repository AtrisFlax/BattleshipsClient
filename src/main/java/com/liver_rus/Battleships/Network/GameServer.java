package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Client.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import static java.nio.ByteBuffer.allocate;

public class GameServer extends Server {
    //TODO make one buffer for all write methods
    private static final int WRITE_BUFFER_SIZE = 8192;
    private static final int READ_BUFFER_SIZE = 8192;
    private ByteBuffer writeBuffer = allocate(WRITE_BUFFER_SIZE);
    private ByteBuffer readBuffer = allocate(WRITE_BUFFER_SIZE);

    //TODO different severity logging
    private static final Logger log = Logger.getLogger(String.valueOf(Server.class));

    private ServerGameEngine gameEngine;
    private SelectionKey turnHolder;

    private final static int MAX_CONNECTIONS = ServerGameEngine.max_players();
    private int numAcceptedConnections = 0;

    public GameServer(int port) throws IOException {
        super(port);
    }

    @Override
    public void configureServer() throws IOException {
        super.configureServer();
        gameEngine = new ServerGameEngine();
    }

    @Override
    void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        if (numAcceptedConnections < MAX_CONNECTIONS) {
            numAcceptedConnections++;
            String port = socketChannel.socket().getInetAddress().toString() + ":" + socketChannel.socket().getPort();
            socketChannel.configureBlocking(false);
            socketChannel.register(super.selector, SelectionKey.OP_READ, port);
            key.attach(null);
            log.info("accepted connection from: " + port);
        } else {
            String msg = "too many connections. Max connections =" + MAX_CONNECTIONS;
            log.info(msg);
            sendMessage(key, msg);
            socketChannel.close();
        }
    }

    @Override
    public void proceedMessage(SelectionKey key, String message) throws IOException {
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
                GameField gameField = new GameField(Ship.createShips(shipsInfo));
                key.attach(gameField);
            }
        } catch (IOException ex) {
            sendMessage(key, "Incorrect fleet format");
            key.attach(null);
            log.info("Incorrect fleet format" + ex);
        }
        //SHOTXX
        if (gameEngine.isBroadcastEnabled() && turnHolder == key) {
            if (MessageProcessor.isShotLine(message)) {
                FieldCoord shootCoord = MessageProcessor.getShootCoordFromMessage(message);
                //get and mark on enemy field
                GameField field = (GameField) key.attachment();
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
                swapFields();
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
                GameField field = (GameField) key.attachment();
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

    //channel[0] -> field[1]
    //channel[1] -> field[0]
    private void swapFields() {
        ArrayList<SelectionKey> selectionKeys = new ArrayList<>(MAX_CONNECTIONS);
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                selectionKeys.add(key);
            }
        }
        Object field0 = selectionKeys.get(0).attachment();
        selectionKeys.get(0).attach(selectionKeys.get(1).attachment());
        selectionKeys.get(1).attach(field0);
    }

    private SelectionKey randomConnection() {
        ArrayList<SelectionKey> selectionKeysList = new ArrayList<>(MAX_CONNECTIONS);
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                selectionKeysList.add(key);
            }
        }
        return selectionKeysList.get(new Random(System.currentTimeMillis()).nextInt(selectionKeysList.size()));
    }

    private void resetServerGameState() {
        Set<SelectionKey> keys = selector.selectedKeys();
        for (SelectionKey selectionKey : keys) {
            selectionKey.attach(null);
        }
        gameEngine.setFirstTurn(true);
        gameEngine.setReadyForBroadcast(false);
    }

    //numConnections with GameField attachment. If both not null then ready
    private boolean isReadyBothChannel() {
        int numConnections = 0;
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                if (key.attachment() instanceof GameField) {
                    numConnections++;
                }
            }
        }
        return numConnections == MAX_CONNECTIONS;
    }

    @Override
    public SelectionKey sendOtherClient(SelectionKey receiverKey, String msg) throws IOException {
        return super.sendOtherClient(receiverKey, addSplitSymbol(msg));
    }

    @Override
    public void sendAllClient(String msg) throws IOException {
        super.sendAllClient(addSplitSymbol(msg));
    }

    @Override
    public void sendMessage(SelectionKey key, String msg) throws IOException {
        super.sendMessage(key, addSplitSymbol(msg));
    }

    @Override
    public String toString() {
        return "Server in port:" + ServerConstants.getDefaultPort();
    }

    private String addSplitSymbol(String msg) {
        return msg + Constants.NetworkMessage.SPLIT_SYMBOL.getTypeValue();
    }
}
