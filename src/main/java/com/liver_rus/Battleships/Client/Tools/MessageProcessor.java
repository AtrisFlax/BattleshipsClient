package com.liver_rus.Battleships.Client.Tools;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GamePrimitives.FieldCoord;

import java.util.regex.Pattern;

public class MessageProcessor {
    public static FieldCoord getShootCoordFromMessage(String message) {
        message = message.replaceAll("\\D+", "");
        int x = Character.getNumericValue(message.charAt(0));
        int y = Character.getNumericValue(message.charAt(1));
        return new FieldCoord(x, y);
    }

    public static String[] splitToShipInfo(String message) {
        message = message.replace(Constants.NetworkMessage.SEND_SHIPS, "");
        return message.split(Pattern.quote(Constants.NetworkMessage.SPLIT_SYMBOL));
    }
}
