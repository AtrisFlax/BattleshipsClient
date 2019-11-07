package com.liver_rus.Battleships.Client;


class FirstPlayerGUIConstants implements GUIConstant {
    private final static int LEFT_X = 65;
    private final static int RIGHT_X = 301;
    private final static int TOP_Y = 133;
    private final static int BOTTOM_Y = 386;
    private final static double WIDTH_CELL = 23.5;

    public int getLeftX() { return LEFT_X; }
    public int getRightX() { return RIGHT_X; }
    public int getTopY() { return TOP_Y; }
    public int getBottomY() { return BOTTOM_Y; }
    public double getWidthCell() { return WIDTH_CELL; }
}

class SecondPlayerGUIConstants  implements GUIConstant {
    private final static int LEFT_X = 304;
    private final static int RIGHT_X = 653;
    private final static int TOP_Y = 370;
    private final static int BOTTOM_Y = 719;
    private final static double WIDTH_CELL = 35.0;

    public int getLeftX() { return LEFT_X; }
    public int getRightX() { return RIGHT_X; }
    public int getTopY() { return TOP_Y; }
    public int getBottomY() { return BOTTOM_Y; }
    public double getWidthCell() { return WIDTH_CELL; }

}

public class Constants {
    class Window {
        final static int WIDTH = 700;
        final static int HEIGHT = 950;
    }

    class AboutInfo {
        final static String ABOUT_GAME_TITLE = "About Battleships";
        final static String ABOUT_GAME_HEADER = "Info1";
        final static String ABOUT_GAME_TEXT = "Info2";
    }

    final static int NONE_SELECTED_FIELD_COORD = -1;

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
}
