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
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Thread.sleep(200);
    }


    @Test
    void gameCycle() throws InterruptedException {
        connectClientToServer(client1);
        connectClientToServer(client2);

        send(client1, "SEND_SHIPS124H|723V|352V|601H|571V|140V|880V|");
        send(client2, "SEND_SHIPS623V|232V|104H|471H|841V|180V|780V|");

        boolean client2FirstTurn = true;
        if (client2.getInbox().get(client2.getInbox().size() - 1).equals("YOU_TURN")) {
            client2FirstTurn = false;
        }

        if (client2FirstTurn) {
            send(client2, "SHOT01");
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

         String[] client1ExpectedInbox = {
                "ENEMY_TURN",
                "MISS53",
                "YOU_TURN",
                "HIT10",
                "YOU_TURN",
                "HIT20",
                "YOU_TURN",
                "HIT23",
                "YOU_TURN",
                "HIT30",
                "YOU_TURN",
                "HIT40",
                "YOU_TURN",
                "MISS00",
                "ENEMY_TURN",
                "MISS33",
                "YOU_TURN",
                "HIT50",
                "DESTROYED104H",
                "YOU_TURN",
                "MISS53",
                "ENEMY_TURN",
                "MISS45",
                "YOU_TURN",
                "HIT62",
                "YOU_TURN",
                "HIT63",
                "YOU_TURN",
                "HIT64",
                "YOU_TURN",
                "HIT65",
                "DESTROYED623V",
                "YOU_TURN",
                "HIT47",
                "YOU_TURN",
                "HIT25",
                "YOU_TURN",
                "HIT24",
                "DESTROYED232V",
                "YOU_TURN",
                "HIT78",
                "DESTROYED780V",
                "YOU_TURN",
                "MISS83",
                "ENEMY_TURN",
                "HIT12",
                "ENEMY_TURN",
                "HIT22",
                "ENEMY_TURN",
                "HIT32",
                "ENEMY_TURN",
                "HIT42",
                "ENEMY_TURN",
                "MISS02",
                "YOU_TURN",
                "HIT85",
                "YOU_TURN",
                "HIT84",
                "DESTROYED841V",
                "YOU_TURN",
                "MISS86",
                "ENEMY_TURN",
                "HIT52",
                "DESTROYED124H",
                "ENEMY_TURN",
                "HIT60",
                "ENEMY_TURN",
                "HIT70",
                "DESTROYED601H",
                "ENEMY_TURN",
                "MISS80",
                "YOU_TURN",
                "MISS37",
                "ENEMY_TURN",
                "HIT57",
                "ENEMY_TURN",
                "MISS64",
                "YOU_TURN",
                "HIT57",
                "DESTROYED471H",
                "YOU_TURN",
                "MISS17",
                "ENEMY_TURN",
                "MISS79",
                "YOU_TURN",
                "HIT18",
                "DESTROYED180V",
                "YOU_WIN"};

        String[] client2ExpectedInbox = {
                "YOU_TURN",
                "MISS53",
                "ENEMY_TURN",
                "HIT10",
                "ENEMY_TURN",
                "HIT20",
                "ENEMY_TURN",
                "HIT23",
                "ENEMY_TURN",
                "HIT30",
                "ENEMY_TURN",
                "HIT40",
                "ENEMY_TURN",
                "MISS00",
                "YOU_TURN",
                "MISS33",
                "ENEMY_TURN",
                "HIT50",
                "DESTROYED104H",
                "ENEMY_TURN",
                "MISS53",
                "YOU_TURN",
                "MISS45",
                "ENEMY_TURN",
                "HIT62",
                "ENEMY_TURN",
                "HIT63",
                "ENEMY_TURN",
                "HIT64",
                "ENEMY_TURN",
                "HIT65",
                "DESTROYED623V",
                "ENEMY_TURN",
                "HIT47",
                "ENEMY_TURN",
                "HIT25",
                "ENEMY_TURN",
                "HIT24",
                "DESTROYED232V",
                "ENEMY_TURN",
                "HIT78",
                "DESTROYED780V",
                "ENEMY_TURN",
                "MISS83",
                "YOU_TURN",
                "HIT12",
                "YOU_TURN",
                "HIT22",
                "YOU_TURN",
                "HIT32",
                "YOU_TURN",
                "HIT42",
                "YOU_TURN",
                "MISS02",
                "ENEMY_TURN",
                "HIT85",
                "ENEMY_TURN",
                "HIT84",
                "DESTROYED841V",
                "ENEMY_TURN",
                "MISS86",
                "YOU_TURN",
                "HIT52",
                "DESTROYED124H",
                "YOU_TURN",
                "HIT60",
                "YOU_TURN",
                "HIT70",
                "DESTROYED601H",
                "YOU_TURN",
                "MISS80",
                "ENEMY_TURN",
                "MISS37",
                "YOU_TURN",
                "HIT57",
                "YOU_TURN",
                "MISS64",
                "ENEMY_TURN",
                "HIT57",
                "DESTROYED471H",
                "ENEMY_TURN",
                "MISS17",
                "YOU_TURN",
                "MISS79",
                "ENEMY_TURN",
                "HIT18",
                "DESTROYED180V",
                "YOU_LOSE"};

        if (client2FirstTurn) {
            Stream<String> client1HeadStream = Stream.of("YOU_TURN", "MISS54");
            Stream<String> client1ExpectedStream = Stream.concat(client1HeadStream, Arrays.stream(client1ExpectedInbox));
            assertTrue(Arrays.deepEquals(client1ExpectedStream.toArray(), client1.getInbox().toArray()));

            Stream<String> client2HeadStream = Stream.of("ENEMY_TURN", "MISS54");
            Stream<String> client2ExpectedStream = Stream.concat(client2HeadStream, Arrays.stream(client2ExpectedInbox));
            assertTrue(Arrays.deepEquals(client2ExpectedStream.toArray(), client2.getInbox().toArray()));
        } else {
            assertTrue(Arrays.deepEquals(client1ExpectedInbox, client1.getInbox().toArray()));
            assertTrue(Arrays.deepEquals(client2ExpectedInbox, client2.getInbox().toArray()));
        }
    }


    //TODO connect -> disconnect -> reconnect test cycle
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