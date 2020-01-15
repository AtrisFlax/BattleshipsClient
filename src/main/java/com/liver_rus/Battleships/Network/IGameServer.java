package com.liver_rus.Battleships.Network;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface IGameServer {
    SocketChannel sendAnotherClient(SocketChannel receiverChannel, String msg) throws IOException;
    void sendAllClients(String msg) throws IOException;
    void sendMessage(SocketChannel socketChannel, String msg) throws IOException;
}

