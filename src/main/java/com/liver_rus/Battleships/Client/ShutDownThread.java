package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.SocketFX.Constants;
import com.liver_rus.Battleships.SocketFX.FxSocketClient;
import com.liver_rus.Battleships.SocketFX.FxSocketServer;

import java.util.logging.Logger;

public class ShutDownThread extends Thread {

    private FxSocketClient socketClient;
    private FxSocketServer socketServer;
    private Logger logger;

    ShutDownThread(FxSocketClient sockClient, FxSocketServer sockServer, Logger log) {
        socketClient = sockClient;
        socketServer = sockServer;
        logger = log;
    }

    @Override
    public void run() {
        if (socketClient != null) {
            if (socketClient.debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                logger.info("ShutdownHook: Shutting down Server Socket");
            }
            socketClient.shutdown();
        }
        if (socketServer != null) {
            if (socketServer.debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                logger.info("ShutdownHook: Shutting down Server Socket");
            }
            socketServer.shutdown();
        }
    }
}
