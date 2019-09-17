package com.liver_rus.Battleships.Client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

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

        server.sendMessage("Test");

        client.shutdown();
        server.shutdown();
    }
}