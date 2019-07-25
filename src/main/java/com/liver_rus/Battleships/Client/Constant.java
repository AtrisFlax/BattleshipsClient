package com.liver_rus.Battleships.Client;

class Constant {
    private Constant() {
    }

    class Pixel {
        final static int LEFT_EDGE_PIXEL_FIRST_PLAYER_X = 65;
        final static int TOP_EDGE_PIXEL_FIRST_PLAYER_Y = 157;
        final static int RIGHT_EDGE_PIXEL_FIRST_PLAYER_X = 301;
        final static int BOTTOM_EDGE_PIXEL_FIRST_PLAYER_Y = 386;
        final static double WIDTH_FIRST_PLAYER_CELL = 23.5;

        final static int LEFT_EDGE_PIXEL_SECOND_PLAYER_X = 304;
        final static int TOP_EDGE_PIXEL_SECOND_PLAYER_Y = 370;
        final static int RIGHT_EDGE_PIXEL_SECOND_PLAYER_X = 653;
        final static int BOTTOM_EDGE_PIXEL_SECOND_PLAYER_Y = 719;
        final static double WIDTH_SECOND_PLAYER_CELL = 35.0;

        final static int WIDTH_WINDOW = 700;
        final static int HEIGHT_WINDOW = 950;
    }

    class AboutInfo {
        final static String ABOUT_GAME_TITLE = "About Battleships";
        final static String ABOUT_GAME_HEADER = "Info1";
        final static String ABOUT_GAME_TEXT = "Info2";
    }

    final static int NO_MORE_SHIPS = 0;

    public enum NetworkMessage {
        ENEMY_NAME("EnemyName "),
        DESTROYED("Destroyed"),
        SHOT("Shot"),
        HIT("Hit"),
        MISS("Miss"),
        YOU_WIN("You Win!"),
        YOU_TURN("Your Turn"),
        YOU_LOSE("You lose"),
        CLIENT_READY("ClientReady"),
        SERVER_READY("ServerReady"),
        EMPTY_STRING("");


        private String typeValue;

        private NetworkMessage(String type) {
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
