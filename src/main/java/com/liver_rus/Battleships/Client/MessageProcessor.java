package com.liver_rus.Battleships.Client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageProcessor {
    public static boolean isShotLine(String line) {
        return matchHeadPlusDD(Constants.NetworkMessage.SHOT.toString(), line);
    }

    static boolean isHit(String line) {
        return matchHeadPlusDD(Constants.NetworkMessage.HIT.toString(), line);
    }

    static boolean isMiss(String line) {
        return matchHeadPlusDD(Constants.NetworkMessage.MISS.toString(), line);
    }

    static boolean isDestroyed(String line) {
        return matchHeadPlusDDDT(Constants.NetworkMessage.DESTROYED.toString(), line);
    }

    static private boolean matchHeadPlusDD(String head, String line) {
        Pattern p = Pattern.compile("^" + head + "\\d{2}$");
        Matcher m = p.matcher(line);
        return m.matches();
    }

    static private boolean matchHeadPlusDDDT(String head, String line) {
        Pattern p = Pattern.compile("^" + head + "\\d{3}[VH]$");
        Matcher m = p.matcher(line);
        return m.matches();
    }

    public static FieldCoord getShootCoordFromMessage(String message) {
        message = message.replaceAll("\\D+", "");
        int x = Character.getNumericValue(message.charAt(0));
        int y = Character.getNumericValue(message.charAt(1));
        return new FieldCoord(x, y);
    }

    public static String[] splitToShipInfo(String message) {
        message = message.replace(Constants.NetworkMessage.SEND_SHIPS.toString(), "");
        return message.split(Pattern.quote(Constants.NetworkMessage.SPLIT_SYMBOL.getTypeValue()));
    }

    static boolean isEnemyTurn(String message) {
        return message.equals(Constants.NetworkMessage.ENEMY_TURN.toString());
    }

    static boolean isYouTurn(String message) {
        return message.equals(Constants.NetworkMessage.YOU_TURN.toString());
    }

    static boolean isYouWin(String message) {
        return message.equals(Constants.NetworkMessage.YOU_WIN.toString());
    }

    static boolean isYouLose(String message) {
        return message.equals(Constants.NetworkMessage.YOU_LOSE.toString());
    }

    static boolean isDisconnect(String message) {
        return message.equals(Constants.NetworkMessage.DISCONNECT.toString());
    }
}
