package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Network.Client;
import com.liver_rus.Battleships.Network.GameServer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkTest {
    private static final Logger log = Logger.getLogger(String.valueOf(NetworkTest.class));

    Thread serverThread;
    Client client1;
    Client client2;

    @BeforeEach
    void setUp() throws IOException {
        int port = 10071;
        String host = "127.0.0.1";

        serverThread = new Thread(new GameServer(port));
        serverThread.start();

        ObservableList<String> inbox1 = FXCollections.observableArrayList();
        ObservableList<String> inbox2 = FXCollections.observableArrayList();

        client1 = new Client(inbox1, host, port);
        client2 = new Client(inbox2, host, port);
    }

    void connectClientToServer(Client client) throws InterruptedException {
        CountDownLatch connectionLatch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                client.makeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectionLatch.countDown();
        }).start();
        connectionLatch.await();
        Thread.sleep(500);
    }

    void send(Client client, String msg) {
        CountDownLatch sendMessageLatch = new CountDownLatch(1);
        new Thread(() -> {
            client.sendMessage(msg);
            sendMessageLatch.countDown();
        }).start();
        try {
            sendMessageLatch.await();
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    void gameCycle() throws InterruptedException {
        Stream<String> sendInfoStream = getStringStreamFromFile("TestCases/Case1/sendToServer.txt");
        Stream<String> client1ExpectedInboxStream = getStringStreamFromFile("TestCases/Case1/awaitedInboxClient1.txt");
        Stream<String> client2ExpectedInboxStream = getStringStreamFromFile("TestCases/Case1/awaitedInboxClient2.txt");
        //CONNECT TO SERVER
        connectClientToServer(client1);
        connectClientToServer(client2);
        //SEND SHIP INFO
        final int SHIPS_INFO_LIMIT = 2;
        String[] sendInfo = sendInfoStream.toArray(String[]::new);
        //SEND SHIP INFO
        for (int i = 0; i < SHIPS_INFO_LIMIT; i++)
            splitAndSend(sendInfo[i]);
        //SEND SHOTS
        boolean client1FirstTurn = client1.getInbox().get(client1.getInbox().size() - 1).equals("YOU_TURN");
        if (client1FirstTurn) {
            //skip fake shot (MISS_SHOT) for client2 first turn
            for (int i = SHIPS_INFO_LIMIT + 1; i < sendInfo.length; i++)
                splitAndSend(sendInfo[i]);
        } else {
            for (int i = SHIPS_INFO_LIMIT; i < sendInfo.length; i++)
                splitAndSend(sendInfo[i]);
        }
        //CHECK INBOX
        final int SKIP_ANOTHER_TURN_INDEX = 2;
        if (client1FirstTurn) {
            assertTrue(Arrays.deepEquals(client1ExpectedInboxStream.skip(SKIP_ANOTHER_TURN_INDEX).toArray(String[]::new), client1.getInbox().toArray()));
            assertTrue(Arrays.deepEquals(client2ExpectedInboxStream.skip(SKIP_ANOTHER_TURN_INDEX).toArray(String[]::new), client2.getInbox().toArray()));
        } else {
            assertTrue(Arrays.deepEquals(client1ExpectedInboxStream.toArray(), client1.getInbox().toArray()));
            assertTrue(Arrays.deepEquals(client2ExpectedInboxStream.toArray(), client2.getInbox().toArray()));
        }
    }

    private Stream<String> getStringStreamFromFile(String fileName) {
        InputStreamReader InputStreamReader = new InputStreamReader(ClassLoader.getSystemResourceAsStream(fileName));
        return new BufferedReader(InputStreamReader).lines();
    }

    @AfterEach
    void tearDown() {
        log.info("tearDown");
        client1.close();
        client2.close();
        serverThread.interrupt();
    }

    void splitAndSend(String str) {
        String[] splitStr = str.split("\\s+");
        if (splitStr[0].equals("client1")) {
            send(client1, splitStr[1]);
            return;
        }
        if (splitStr[0].equals("client2")) {
            send(client2, splitStr[1]);
            return;
        }
    }
}