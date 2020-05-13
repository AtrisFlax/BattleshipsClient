package com.liver_rus.Battleships.Network;

import com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant;

import java.util.regex.Pattern;

public class MessageSplitter {
    public static String AddSplitSymbol(String msg) {
        return msg + NetworkCommandConstant.SPLIT_SYMBOL;
    }

    public static String[] Split(String message) {
        return message.split(Pattern.quote(NetworkCommandConstant.SPLIT_SYMBOL));
    }
}
