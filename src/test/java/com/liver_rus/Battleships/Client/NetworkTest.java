package com.liver_rus.Battleships.Client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NetworkTest {

    @Test
    void test() {
        int port = 2015;
        String host = "localhost";

        Network server = new Network();
        Network client = new Network();

        client.connectAsClient(host, port);
        ObservableList<String> rcvdMsgsData = FXCollections.observableArrayList();
        server.connectAsServer(port, rcvdMsgsData);


        String test_phrase = "Test";
        server.sendMessage("test_txt");
        String recived_phrase = rcvdMsgsData.get((rcvdMsgsData.size() - 1));

        assertEquals(test_phrase, recived_phrase);

        client.shutdown();
        server.shutdown();
    }
}