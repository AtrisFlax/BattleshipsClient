package com.liver_rus.Battleships.Network;

import java.io.IOException;

public interface Restartable {
    void restart(String ip, int port) throws IOException;
    void stopConnection();
}
