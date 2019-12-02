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

    public static class NetworkMessage {
        public final static String DESTROYED = "DESTROYED";
        public final static String SHOT = "SHOT";
        public final static String HIT = "HIT";
        public final static String MISS = "MISS";
        public final static String YOU_WIN = "YOU_WIN";
        public final static String YOU_TURN = "YOU_TURN";
        public final static String ENEMY_TURN = "ENEMY_TURN";
        public final static String YOU_LOSE = "YOU_LOSE";
        public final static String DISCONNECT = "DISCONNECT";
        public final static String SEND_SHIPS = "SEND_SHIPS";
        public final static String EMPTY_STRING = "";
        public final static String SPLIT_SYMBOL = "|";
        //TODO player names exchange
        //public final static String ENEMY_NAME = "ENEMY_NAME";
    }

    public final static int ShipInfoLength = 4;

    public final static int NONE_SELECTED_FIELD_COORD = -1;
}
