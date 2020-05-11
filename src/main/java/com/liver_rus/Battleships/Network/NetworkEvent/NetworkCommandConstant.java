package com.liver_rus.Battleships.Network.NetworkEvent;

public class NetworkCommandConstant {
    //client commands to server
    public final static String IS_POSSIBLE_DEPLOY_SHIP = "IS_POSSIBLE_DEPLOY_SHIP";
    public final static String MY_NAME = "MY_NAME";
    public final static String TRY_DEPLOY_SHIP = "TRY_DEPLOY_SHIP";
    public final static String SHOT = "SHOT";
    public final static String DISCONNECT = "DISCONNECT";
    public final static String UNKNOWN_COMMAND = "UNKNOWN_COMMAND";
    public final static String REMATCH_ANSWER = "REMATCH_ANSWER";
    public final static String RESET_FLEET_WHILE_DEPLOY = "RESET_FLEET_WHILE_DEPLOY";
    public final static String SET_SAVE_SHOOTING = "SET_SAVE_SHOOTING";

    //server commands to client
    public final static String ASK_REMATCH = "ASK_REMATCH";
    public final static String START_REMATCH = "START_REMATCH";
    public final static String WAIT = "WAIT";
    public final static String COMMAND_NOT_ACCEPTED = "COMMAND_NOT_ACCEPTED";
    public final static String SET_ENEMY_NAME = "SET_ENEMY_NAME";
    public final static String DEPLOY = "CAN_DEPLOY";
    public final static String CANNOT_DEPLOY = "CANNOT_DEPLOY";
    public final static String CAN_SHOOT = "CAN_SHOOT";
    public final static String DRAW_SHIP = "DRAW_SHIP";
    public final static String HIT = "HIT";
    public final static String DRAW_SHIP_LEFT = "DRAW_SHIP_LEFT";
    public final static String MISS = "MISS";
    public final static String NEAR = "NEAR";
    public final static String END_MATCH = "END_MATCH";
    public final static String DO_DISCONNECT = "DO_DISCONNECT";

    //service
    public final static String EMPTY_STRING = "";
    public final static String SPLIT_SYMBOL = ".";

    //player type on client side
    public final static String YOU = "YOU";
    public final static String ENEMY = "ENEMY";

    //player type on client side
    public final static String ON = "ON_STATE";
    public final static String OFF = "OFF_STATE";

    //todo make static check all String different
}