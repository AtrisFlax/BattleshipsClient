package com.liver_rus.Battleships.Client.Constants;


//TODO подумать что с пакетами сделать(разбить)

//TODO GUI <=> newtork interface

public class Constants {
    public static class Window {
        public final static int WIDTH = 700;
        public final static int HEIGHT = 950;
    }

    public static class AboutInfo {
        public final static String ABOUT_GAME_TITLE = "About Battleships";
        public final static String ABOUT_GAME_HEADER = "Info1";
        public final static String ABOUT_GAME_TEXT = "Info2";
    }

    public enum NetworkMessage {
        ENEMY_NAME("ENEMY_NAME"),
        DESTROYED("DESTROYED"),
        SHOT("SHOT"),
        HIT("HIT"),
        MISS("MISS"),
        YOU_WIN("YOU_WIN"),
        YOU_TURN("YOU_TURN"),
        ENEMY_TURN("ENEMY_TURN"),
        YOU_LOSE("YOU_LOSE"),
        DISCONNECT("DISCONNECT"),
        SEND_SHIPS("SEND_SHIPS"),
        EMPTY_STRING(""),
        SPLIT_SYMBOL("|");
        
        private String typeValue;

        NetworkMessage(String type) {
            typeValue = type;
        }

        static public NetworkMessage getType(String pType) {
            for (NetworkMessage type: NetworkMessage.values()) {
                if (type.getTypeValue().equals(pType)) {
                    return type;
                }
            }
            throw new RuntimeException("unknown type");
        }

        public String getTypeValue() {
            return typeValue;
        }
    }

    public final static int ShipInfoLength = 4;

    public final static int NONE_SELECTED_FIELD_COORD = -1;
}
