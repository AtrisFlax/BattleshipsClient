package com.liver_rus.Battleships.Client;

public class Constants {
    private Constants() {
    }

    class Pixel {
        class FirstPlayer {
            final static int LEFT_X = 65;
            final static int TOP_Y = 157;
            final static int RIGHT_X = 301;
            final static int BOTTOM_Y = 386;
            final static double WIDTH_CELL = 23.5;
        }

        class SecondPlayer {
            final static int LEFT_X = 304;
            final static int TOP_Y = 370;
            final static int RIGHT_X = 653;
            final static int BOTTOM_Y = 719;
            final static double WIDTH_CELL = 35.0;
        }
    }

    class Window {
        final static int WIDTH = 700;
        final static int HEIGHT = 950;
    }

    class AboutInfo {
        final static String ABOUT_GAME_TITLE = "About Battleships";
        final static String ABOUT_GAME_HEADER = "Info1";
        final static String ABOUT_GAME_TEXT = "Info2";
    }

    final static int NO_MORE_SHIPS = 0;

    final static int NONE_SELECTED_FIELD_COORD = -1;

    public enum NetworkMessage {
        ENEMY_NAME("ENEMY_NAME"),
        DESTROYED("DESTROYED"),
        SHOT("SHOT"),
        HIT("HIT"),
        MISS("MISS"),
        YOU_WIN("YOU_WIN"),
        YOU_TURN("YOU_TURN"),
        YOU_LOSE("YOU_LOSE"),
        READY_TO_GAME("READY_TO_GAME"),
        DISCONNECT("DISCONNECT"),
        EMPTY_STRING("");

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
}
