package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Client.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.logging.Logger;

import static java.nio.ByteBuffer.allocate;

public class GameServer extends Server {
    private static final int READ_BUFFER_SIZE = 8192;
    private static final int WRITE_BUFFER_SIZE = 8192;
    //TODO different severity logging
    private static final Logger log = Logger.getLogger(String.valueOf(Server.class));
    //TODO make one buffer for all write methods
    private ByteBuffer writeBuffer = allocate(WRITE_BUFFER_SIZE);
    private int port;

    private ServerGameEngine gameEngine;
    private SelectionKey turnHolder;

    private final static int MAX_CONNECTIONS = 2;
    private int numConnections = 0;
    //TODO try attachment
    // selectionKey.attach(theObject)
    // Object attachedObj = selectionKey.attachment();
    private Map<SelectionKey, GameField> connections;

    public GameServer() throws IOException {
        super();
    }

    public GameServer(int port) throws IOException {
        super(port);
    }

    @Override
     public void configureServer() throws IOException {
        super.configureServer();
        gameEngine = new ServerGameEngine();
        connections = new HashMap<>(MAX_CONNECTIONS);
    }

    @Override
    void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        if (numConnections < MAX_CONNECTIONS) {
            numConnections++;
            String port = socketChannel.socket().getInetAddress().toString() + ":" + socketChannel.socket().getPort();
            socketChannel.configureBlocking(false);
            socketChannel.register(super.selector, SelectionKey.OP_READ, port);
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
                swapFields(connections);
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

    //connection[1] -> field[2]
    //connection[2] -> field[1]
    private void swapFields(Map<SelectionKey, GameField> connections) {
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

    private String addSplitSymbol(String msg) {
        return msg + Constants.NetworkMessage.SPLIT_SYMBOL.getTypeValue();
    }

    @Override
    public String toString() {
        return "Server in port:" + ServerConstants.getDefaultPort();
    }
}
