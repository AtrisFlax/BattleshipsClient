package com.liver_rus.Battleships.Network;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

final class InetConstants {
    private static InetAddress INET_ADDRESS;
    private static final int PORT = 8283;

    private InetConstants() {
    }

    private static final Logger log = Logger.getLogger(String.valueOf(InetConstants.class));

    static InetAddress getLocalHost() {
        try {
            INET_ADDRESS = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.log(Level.WARNING, "Error with getting local host address!");
        }
        return INET_ADDRESS;
    }

    static int getDefaultPort() {
        return PORT;
    }
}
