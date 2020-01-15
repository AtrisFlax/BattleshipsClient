package com.liver_rus.Battleships.Network;

import javafx.collections.ObservableList;

import java.util.function.Consumer;

public interface IClient {
    void subscribeForInbox(Consumer<String> consumer);
    void sendMessage(String message);
    ObservableList<String> getInbox();
}
