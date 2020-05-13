package com.liver_rus.Battleships.Network.Client;

import com.liver_rus.Battleships.Network.Restartable;

import java.util.function.Consumer;

public interface MailBox extends Restartable {
    void subscribeForInbox(Consumer<String> consumer);
    void sendMessage(String message);
}