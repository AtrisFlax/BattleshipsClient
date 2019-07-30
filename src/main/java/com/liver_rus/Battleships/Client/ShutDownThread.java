package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.SocketFX.Constants;
import com.liver_rus.Battleships.SocketFX.GenericSocket;

import java.util.logging.Logger;

public class ShutDownThread extends Thread {

    private GenericSocket socket;
    private Logger logger;

    ShutDownThread(GenericSocket socket, Logger log) {
        this.socket = socket;
        logger = log;
    }

    @Override
    public void run() {
        if (socket != null) {
            if (socket.debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                logger.info("ShutdownHook: Shutting down Server Socket");
            }
            socket.shutdown();
        }
    }
}
