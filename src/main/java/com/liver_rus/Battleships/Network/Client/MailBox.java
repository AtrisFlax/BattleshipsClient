package com.liver_rus.Battleships.Network.Client;

import java.util.function.Consumer;

public interface MailBox {
    void subscribeForInbox(Consumer<String> consumer);
    void sendMessage(String message);

    void disconnect();
//    ClientGameEngine knows too much
    ///netClient.startThread(); same
}


//TODO СМ readme
//mvn clean нужен ли ????