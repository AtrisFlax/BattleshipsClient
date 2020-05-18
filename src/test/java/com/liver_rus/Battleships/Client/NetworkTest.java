package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Network.Client.NetworkClient;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Events.TryDeployShipNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.Ship;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.TryingAddTooManyShipsOnFieldException;
import com.liver_rus.Battleships.Network.Server.GameServer;
import com.liver_rus.Battleships.Network.Server.TurnOrder;
import com.liver_rus.Battleships.utils.MyLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


//TODO add reset test
class NetworkTest {
    private static final Logger LOGGER = MyLogger.GetLogger(NetworkTest.class);

    final static int TIMEOUT_FOR_SINGLE_MESSAGE = 500;
    final long NET_LAG_TIMEOUT = 20;
    final static int MAX_CONNECTIONS = 2;

    private static GameField[] testGameFields;
    private static GameField[] injectedGameFields;
    private static NetworkClient client0;
    private static NetworkClient client1;
    private static GameServer server;

    List<String> inboxClient0 = new ArrayList<>();
    List<String> inboxClient1 = new ArrayList<>();

    @BeforeEach
    void setUpClientAndServer() throws IOException {
//        LOGGER.info("an info msg");
//        LOGGER.warning("a warning msg");
//        LOGGER.severe("a severe msg");

        testGameFields = new GameField[2];
        injectedGameFields = new GameField[2];
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            testGameFields[i] = new GameField();
            injectedGameFields[i] = new GameField();
        }

        int port = 48999;
        String host = "127.0.0.1";
        server = GameServer.create(host, port, injectedGameFields);
        server.start();
        client0 = new NetworkClient(host, port);
        client1 = new NetworkClient(host, port);
        client0.subscribeForInbox((message) -> inboxClient0.add(message));
        client1.subscribeForInbox((message) -> inboxClient1.add(message));
    }

    @AfterEach
    void closeConnections() {
        LOGGER.info("Close all connection");
        client0.stopConnection();
        client1.stopConnection();
        server.stopConnection();
        inboxClient0.clear();
        inboxClient1.clear();
    }

    @Test
    void testGameFieldChangingOnServerFromClientMessages() throws InterruptedException,
            TryingAddTooManyShipsOnFieldException {
        server.setFirstTurn(TurnOrder.FIRST_CONNECTED);

        addShipsPreset1(testGameFields[0]);
        addShipsPreset2(testGameFields[1]);

        //add ships to test fields
        sendInitSequence(client0, "Player0", false);
        sendInitSequence(client1, "Player1", false);
        //add ships to server
        sendShipsForPlayers();

        shoot(client0,7, 8);
        shoot(client0,8, 8);
        shoot(client1,1, 8);
    }

    @Test
    void testSaveShooting() throws InterruptedException,
            TryingAddTooManyShipsOnFieldException {
        server.setFirstTurn(TurnOrder.FIRST_CONNECTED);
        //add ships to test fields
        addShipsPreset1(testGameFields[0]);
        addShipsPreset2(testGameFields[1]);
        sendInitSequence(client0, "Player0", true);
        sendInitSequence(client1, "Player1", true);

        sendShipsForPlayers();

        shoot(client0, 7, 8);
        shoot(client0, 7, 8);
        shoot(client0, 7, 8);
        //save shoot to near
        shoot(client0, 6, 8);
        shoot(client0, 6, 7);
        shoot(client0, 7, 7);
        shoot(client0, 8, 7);
        shoot(client0, 8, 8);
        shoot(client0, 8, 9);
        shoot(client0, 7, 8);
        //miss
        shoot(client0, 4, 8);
        //hits
        shoot(client1, 1, 8);
        shoot(client1, 3, 2);
        shoot(client1, 1, 8);
        shoot(client1, 3, 2);
        shoot(client1, 9, 1);
        //save
        shoot(client1, 2, 2);
        //miss
        shoot(client1, 8, 1);
    }

    private void sendInitSequence(NetworkClient client, String name, boolean saveShooting) {
        sendEvent(client, new SetSaveShootingNetworkEvent(saveShooting));
        sendEvent(client, new ConfigGameEvent(name));
    }

    //firstClient==true -> client0
    //firstClient==false -> client1
    private void sendShipsForPlayers() throws InterruptedException {
        sendShips(client0, testGameFields[0]);
        sendShips(client1, testGameFields[1]);
        assertFields();
    }

    private void sendShips(NetworkClient networkClient, GameField gameField) throws InterruptedException {
        for (Ship ship : gameField.getShips()) {
            sendEvent(networkClient,
                    new TryDeployShipNetworkEvent(ship.getX(), ship.getY(), ship.getType(), ship.isHorizontal()));
            Thread.sleep(getNetLagTimeOut());
        }
    }

    private void shoot(NetworkClient client, int x, int y) throws InterruptedException {
        GameField gameField;
        if (client == client0) {
            gameField = testGameFields[1];
        } else {
            gameField = testGameFields[0];
        }
        send(client, "SHOT" + x + y);
        Thread.sleep(getNetLagTimeOut());
        gameField.shoot(x, y);
        assertFields();
    }

    private void assertFields() {
        assertEquals(testGameFields[0], injectedGameFields[0]);
        assertEquals(testGameFields[1], injectedGameFields[1]);
    }

    private long getNetLagTimeOut() {
        return NET_LAG_TIMEOUT;
    }


    /* type <-> int
      AIRCRAFT_CARRIER(4),
        BATTLESHIP(3),
        CRUISER(2),
        DESTROYER(1),
        SUBMARINE(0),
        UNKNOWN(-1);
    */
    private void addShipsPreset1(GameField field) throws TryingAddTooManyShipsOnFieldException {
        field.addShip(1, 8, 0, true);
        field.addShip(3, 2, 0, true);
        field.addShip(1, 1, 1, false);
        field.addShip(3, 4, 1, true);
        field.addShip(2, 6, 2, true);
        field.addShip(7, 4, 3, false);
        field.addShip(9, 1, 4, false);
    }

    private void addShipsPreset2(GameField field) throws TryingAddTooManyShipsOnFieldException {
        field.addShip(6, 2, 3, false);
        field.addShip(2, 3, 2, false);
        field.addShip(1, 0, 4, true);
        field.addShip(4, 7, 1, true);
        field.addShip(8, 4, 1, false);
        field.addShip(1, 8, 0, false);
        field.addShip(7, 8, 0, false);
    }


    //TODO change content of awaitedInboxClient[0-1].txt. Now test fail
    @Test
    void fullGameCycleInboxTest() throws InterruptedException {
        String TEST_CASE = "Case1";
        server.setFirstTurn(TurnOrder.FIRST_CONNECTED);
        //Load send info
        Stream<String> sendInfoStream =
                getStringStreamFromFile(TEST_CASE + "/sendToServer.txt");
        Stream<String> client0ExpectedInboxStream =
                getStringStreamFromFile(TEST_CASE + "/awaitedInboxClient0.txt");
        Stream<String> client1ExpectedInboxStream =
                getStringStreamFromFile(TEST_CASE + "/awaitedInboxClient1.txt");
        //Extract client tag
        String[] sendInfo = sendInfoStream.toArray(String[]::new);
        String firstClientTag = sendInfo[0].split("\\s")[0];
        String secondClientTag = sendInfo[0].split("\\s")[1];
        //send messages
        for (String message : sendInfo) {
            if (message.equals("client0 client1")) continue;
            splitAndSend(message, firstClientTag, secondClientTag);
            Thread.sleep(250);
        }

        //check inboxes
        assertTrue(Arrays.deepEquals(client0ExpectedInboxStream.toArray(), inboxClient0.toArray()));
        assertTrue(Arrays.deepEquals(client1ExpectedInboxStream.toArray(), inboxClient1.toArray()));
    }

    private void splitAndSend(String str, String first_client_tag, String second_client_tag) {
        String[] splitStr = str.split("\\s");
        if (splitStr[0].equals(first_client_tag)) {
            send(client0, splitStr[1]);
        }
        if (splitStr[0].equals(second_client_tag)) {
            send(client1, splitStr[1]);
        }
    }

    private Stream<String> getStringStreamFromFile(String fileName) {
        String testCasesFolderName = "TestCases/";
        String path = testCasesFolderName + fileName;
        InputStreamReader inputStreamReader = new InputStreamReader(
                Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(path))
        );
        return new BufferedReader(inputStreamReader).lines();
    }


    private void sendEvent(NetworkClient networkClient, ServerNetworkEvent event) {
        send(networkClient, event.convertToString());
    }

    private void send(NetworkClient networkClient, String msg) {
        CountDownLatch sendMessageLatch = new CountDownLatch(1);
        new Thread(() -> {
            assertTimeout(Duration.ofMillis(TIMEOUT_FOR_SINGLE_MESSAGE), () -> networkClient.sendMessage(msg));
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendMessageLatch.countDown();
        }).start();
        try {
            sendMessageLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
