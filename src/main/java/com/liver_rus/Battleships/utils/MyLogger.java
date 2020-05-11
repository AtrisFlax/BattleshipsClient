package com.liver_rus.Battleships.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class MyLogger {

    public static java.util.logging.Logger GetLogger(Class className) {
        InputStream stream = className.getClassLoader().
                getResourceAsStream("logging/logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return java.util.logging.Logger.getLogger(className.getName());
    }
}


