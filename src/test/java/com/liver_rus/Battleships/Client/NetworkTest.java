package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Network.Client;
import com.liver_rus.Battleships.Network.Server;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class NetworkTest {
    private static final Logger log = Logger.getLogger(String.valueOf(NetworkTest.class));

    Thread serverThread;
    Client client1;
    Client client2;

    @BeforeEach
    void setUp() throws IOException {
        int port = 10071;
        String host = "127.0.0.1";

        serverThread = new Thread(new Server(port));
        serverThread.start();

        ObservableList<String> inbox1 = FXCollections.observableArrayList();
        ObservableList<String> inbox2 = FXCollections.observableArrayList();

        client1 = new Client(inbox1, host, port);
        client2 = new Client(inbox2, host, port);
    }

    void connectClientToServer(Client client) throws InterruptedException  {
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
        Thread.sleep(100);
    }

    void send(Client client,String msg) throws InterruptedException {
        CountDownLatch sendMessageLatch = new CountDownLatch(1);
        new Thread(() -> {
//            System.out.println("before send");
            client.sendMessage(msg);
//            System.out.println("after send");
            sendMessageLatch.countDown();
        }).start(); //send client1 -> client2;
        sendMessageLatch.await();
        Thread.sleep(500);
    }

    @Test
    void test() throws InterruptedException, IOException {
        connectClientToServer(client1);
        connectClientToServer(client2);

        String test_phrase1 = "from_client1_to_client2";
        String test_phrase2 = "from_client2_to_client1";
        send(client1, Constants.NetworkMessage.READY_TO_GAME.toString());
        send(client2, Constants.NetworkMessage.READY_TO_GAME.toString());
        send(client1, test_phrase1);
        send(client2, test_phrase2);
        Thread.sleep(1000);


        System.out.println("client1.getInbox");
        for(String msg : client1.getInbox()) {
            System.out.println(msg);
        }
        System.out.println("client2.getInbox");
        for(String msg : client2.getInbox()) {
            System.out.println(msg);
        }

        assertNotEquals(0 , client1.getInbox().size(), "Client 1 has empty inbox");
//        String recived_phrase1 = client1.getInbox().get(client1.getInbox().size() - 1);
//        assertEquals(test_phrase2, recived_phrase1);
//
        assertNotEquals(0 , client2.getInbox().size(), "Client 2 has empty inbox");
//        String recived_phrase2 = client2.getInbox().get(client2.getInbox().size() - 1);
//        assertEquals(test_phrase1, recived_phrase2);
    }

    @Test
    void disconnect()  throws InterruptedException, IOException {
        connectClientToServer(client1);
        connectClientToServer(client2);

        String test_phrase = Constants.NetworkMessage.DISCONNECT.toString();
        send(client1, test_phrase);
        send(client2, test_phrase);

        send(client1, "Send");
        send(client1, "Send");
        send(client1, "Send");
        send(client2, "Send");
        send(client2, "Send");
        send(client1, "Send");
        send(client1, "Send");
        send(client2, "Send");

        Thread.sleep(500);

        System.out.println("client1.getInbox");
        for(String msg : client1.getInbox()) {
            System.out.println(msg);
        }
        System.out.println("client2.getInbox");
        for(String msg : client2.getInbox()) {
            System.out.println(msg);
        }

        String recived_phrase1 = client1.getInbox().get(client1.getInbox().size() - 1);
        assertEquals(test_phrase, recived_phrase1);
        String recived_phrase2 = client1.getInbox().get(client1.getInbox().size() - 1);
        assertEquals(test_phrase, recived_phrase2);

    }

    @AfterEach
    void tearDown() {
        log.info("tearDown");
        client1.close();
        client2.close();
        serverThread.interrupt();
    }
}