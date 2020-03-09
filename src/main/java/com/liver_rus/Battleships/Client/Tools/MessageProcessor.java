package com.liver_rus.Battleships.Client.Tools;

import com.liver_rus.Battleships.Client.Constants.Constants;

import java.util.regex.Pattern;

public class MessageProcessor {
    private MessageProcessor() {}

    //TODO должно быть два метода
    public static int getX(String message) {
        return Character.getNumericValue(message.charAt(0));
    }

    public static int getY(String message) {
        return Character.getNumericValue(message.charAt(1));
    }

    public static String[] splitToShipInfo(String message) {
        message = message.replace(Constants.NetworkCommand.SEND_SHIPS, "");
        return message.split(Pattern.quote(Constants.NetworkCommand.SPLIT_SYMBOL));
    }

    //Char + Num format A1 B5 D2
    public static String XYtoGameFormat(int x, int y) {
        int tmpX = x + 1;
        int tmpY = y + 1;
        String strY = tmpY > 0 && tmpY < 27 ? String.valueOf((char) (tmpY + 'A' - 1)) : null;
        return strY + tmpX;
    }
}
