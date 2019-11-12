package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Network.Client;
import com.liver_rus.Battleships.Network.Server;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    void send(Client client, String msg) throws InterruptedException {
        CountDownLatch sendMessageLatch = new CountDownLatch(1);
        new Thread(() -> {
            client.sendMessage(msg);
            sendMessageLatch.countDown();
        }).start();
        sendMessageLatch.await();
        Thread.sleep(100);
    }


    @Test
    void gameCycle() throws InterruptedException, IOException {
        connectClientToServer(client1);
        connectClientToServer(client2);

        send(client1, "SEND_SHIPS124H|723V|352V|601H|571V|140V|880V|");
        send(client2, "SEND_SHIPS623V|232V|104H|471H|841V|180V|780V|");


        if (client1.getInbox().get(client2.getInbox().size() - 1).equals("YOU_TURN")) {
            System.out.println("*********WRONG TURN RERUN********");
            return;
        }

        send(client1, "SHOT54");
        send(client2, "SHOT53");
        send(client1, "SHOT10");
        send(client1, "SHOT20");
        send(client1, "SHOT23");
        send(client1, "SHOT30");
        send(client1, "SHOT40");
        send(client1, "SHOT00");
        send(client2, "SHOT33");
        send(client1, "SHOT50");
        send(client1, "SHOT53");
        send(client2, "SHOT45");
        send(client1, "SHOT62");
        send(client1, "SHOT63");
        send(client1, "SHOT64");
        send(client1, "SHOT65");
        send(client1, "SHOT47");
        send(client1, "SHOT25");
        send(client1, "SHOT24");
        send(client1, "SHOT78");
        send(client1, "SHOT83");
        send(client2, "SHOT12");
        send(client2, "SHOT22");
        send(client2, "SHOT32");
        send(client2, "SHOT42");
        send(client2, "SHOT02");
        send(client1, "SHOT85");
        send(client1, "SHOT84");
        send(client1, "SHOT86");
        send(client2, "SHOT52");
        send(client2, "SHOT60");
        send(client2, "SHOT70");
        send(client2, "SHOT80");
        send(client1, "SHOT37");
        send(client2, "SHOT57");
        send(client2, "SHOT64");
        send(client1, "SHOT57");
        send(client1, "SHOT17");
        send(client2, "SHOT79");
        send(client1, "SHOT18");

//        int i = 0;
//        System.out.println("client1.getInbox");
//        for (String msg : client1.getInbox()) {
//            System.out.println(i++ + ": " + msg);
//        }
//
//        i = 0;
//        System.out.println("client2.getInbox");
//        for (String msg : client2.getInbox()) {
//            System.out.println(i++ + ": " + msg);
//        }

        assertEquals(Constants.NetworkMessage.YOU_TURN.toString(), client1.getInbox().get(client2.getInbox().size() - 1));
    }


    //TODO connect -> disconnect -> reconnect
    @Disabled
    @Test
    void disconnectAndReconnect() throws InterruptedException, IOException {
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
        for (String msg : client1.getInbox()) {
            System.out.println(msg);
        }
        System.out.println("client2.getInbox");
        for (String msg : client2.getInbox()) {
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