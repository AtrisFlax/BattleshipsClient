package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Network.Client.NetworkClient;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.Ship;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.TryingAddTooManyShipsOnFieldException;
import com.liver_rus.Battleships.Network.Server.GameServer;
import com.liver_rus.Battleships.Network.Server.TurnOrder;
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

import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.*;
import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {
    private static final Logger log = Logger.getLogger(String.valueOf(NetworkTest.class));

    final static int TIMEOUT_FOR_SINGLE_MESSAGE = 500;
    static final int MAX_CONNECTIONS = 2;

    private static GameField[] testGameFields;
    private static GameField[] injectedGameFields;
    private static NetworkClient client0;
    private static NetworkClient client1;
    private static GameServer server;

    List<String> inboxClient0 = new ArrayList<>();
    List<String> inboxClient1 = new ArrayList<>();

    @BeforeEach
    void setUpClientAndServer() throws IOException {
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
        log.info("Close all connection");
        client0.close();
        client1.close();
        server.close();
        inboxClient0.clear();
        inboxClient1.clear();
    }

    @Test
    void testGameFieldChangingOnServerFromClientMessages() throws InterruptedException,
            TryingAddTooManyShipsOnFieldException {
        server.setFirstTurn(TurnOrder.FIRST_CONNECTED);
        //add ships to test fields
        addShipsPreset1(testGameFields[0]);
        addShipsPreset2(testGameFields[1]);
        send(client0, MY_NAME + "Player1");
        send(client1, MY_NAME + "Player2");

        //add ships to server
        for (Ship ship : testGameFields[0].getShips()) {
            send(client0, TRY_DEPLOY_SHIP + ship.toString());
        }
        for (Ship ship : testGameFields[1].getShips()) {
            send(client1, TRY_DEPLOY_SHIP + ship.toString());
        }


        assertEquals(testGameFields[0], injectedGameFields[0]);
        assertEquals(testGameFields[1], injectedGameFields[1]);

        int x, y;
        //client0 make hit. client0 shot but mark field on client1
        x = 7;
        y = 8;
        send(client0, "SHOT" + x + y);
        testGameFields[1].shoot(x, y);
        assertEquals(testGameFields[0], injectedGameFields[0]);
        assertEquals(testGameFields[1], injectedGameFields[1]);

        //client0 make miss
        x = 8;
        y = 8;
        send(client0, "SHOT" + x + y);
        testGameFields[1].shoot(x, y);
        assertEquals(testGameFields[0], injectedGameFields[0]);
        assertEquals(testGameFields[1], injectedGameFields[1]);

        //client1 destroy ship
        x = 1;
        y = 8;
        send(client1, "SHOT" + x + y);
        testGameFields[0].shoot(x, y);
        //check after destroy
        assertEquals(testGameFields[0], injectedGameFields[0]);
        assertEquals(testGameFields[1], injectedGameFields[1]);
    }

    @Test
    void testSaveShooting() throws TryingAddTooManyShipsOnFieldException {
        server.setFirstTurn(TurnOrder.FIRST_CONNECTED);
        //add ships to test fields
        addShipsPreset1(testGameFields[0]);
        addShipsPreset2(testGameFields[1]);
        //set mode
        send(client0, SET_SAVE_SHOOTING + ON);
        send(client1, SET_SAVE_SHOOTING + OFF);
        //set name
        send(client0, MY_NAME + "Player1");
        send(client1, MY_NAME + "Player2");

        //add ships to server
        for (Ship ship : testGameFields[0].getShips()) {
            send(client0, TRY_DEPLOY_SHIP + ship.toString());
        }
        for (Ship ship : testGameFields[1].getShips()) {
            send(client1, TRY_DEPLOY_SHIP + ship.toString());
        }

        System.out.println(testGameFields[0]);
        System.out.println(testGameFields[1]);

        assertEquals(testGameFields[0], injectedGameFields[0]);
        assertEquals(testGameFields[1], injectedGameFields[1]);

        int x, y;
        //client0 make hit. client0 shot but mark field on client1
        x = 7;
        y = 8;
        send(client0, "SHOT" + x + y);
        testGameFields[1].shoot(x, y);
        assertEquals(testGameFields[0], injectedGameFields[0]);
        assertEquals(testGameFields[1], injectedGameFields[1]);

        //client0 make miss
        x = 8;
        y = 8;
        send(client0, "SHOT" + x + y);
        testGameFields[1].shoot(x, y);
        assertEquals(testGameFields[0], injectedGameFields[0]);
        assertEquals(testGameFields[1], injectedGameFields[1]);

        //client1 destroy ship
        x = 1;
        y = 8;
        send(client1, "SHOT" + x + y);
        testGameFields[0].shoot(x, y);
        //check after destroy
        assertEquals(testGameFields[0], injectedGameFields[0]);
        assertEquals(testGameFields[1], injectedGameFields[1]);
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
        String testResourceFolder = "TestCases/";
        InputStreamReader inputStreamReader = new InputStreamReader(
                Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(testResourceFolder + fileName)));
        return new BufferedReader(inputStreamReader).lines();
    }

    private void send(NetworkClient networkClient, String msg) {
        CountDownLatch sendMessageLatch = new CountDownLatch(1);
        new Thread(() -> {
            assertTimeout(Duration.ofMillis(TIMEOUT_FOR_SINGLE_MESSAGE), () -> networkClient.sendMessage(msg));
            try {
                final int NET_LAG = 10;
                Thread.sleep(NET_LAG);
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

    //TODO save shooting test. One client in save shooting mode another not
    //TODO add reset test
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
}
