package com.liver_rus.Battleships.Network.NetworkEvent.Client;

import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.*;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.*;
import static com.liver_rus.Battleships.Network.Server.GamePrimitives.Fleet.NUM_TYPE;

//Common deserialize class
public class CreatorClientNetworkEvent {
    private final Pattern eventCannotDeployPattern;
    private final Pattern eventCanShootPattern;
    private final Pattern eventCommandNotAcceptedPattern;
    private final Pattern eventDeployPattern;
    private final Pattern eventDoDisconnectPattern;
    private final Pattern eventNotStartRematchPattern;
    private final Pattern eventDrawHitPattern;
    private final Pattern eventDrawMissPattern;
    private final Pattern eventDrawNearPattern;
    private final Pattern eventDrawShipPattern;
    private final Pattern eventSetEnemyNamePattern;
    private final Pattern eventStartRematchPattern;
    private final Pattern eventWaitingForSecondPlayerPattern;
    private final Pattern eventEndMatchPattern;
    private final Pattern eventDrawShipsLeftPattern;

    public CreatorClientNetworkEvent() {
        String xyto = "(\\d)(\\d)(\\d)([VH])"; //x y type orientation
        String xy = "(\\d)(\\d)";
        String player = "(" + YOU + "|" + ENEMY + ")";
        String shipsLeftByType = "(\\d)(\\d)(\\d)(\\d)(\\d)"; //x y type orientation

        eventCannotDeployPattern = Pattern.compile("^" + CANNOT_DEPLOY + xyto + "$");
        eventCanShootPattern = Pattern.compile("^" + CAN_SHOOT + "$");
        eventCommandNotAcceptedPattern = Pattern.compile("^" + COMMAND_NOT_ACCEPTED + "(.*)");
        eventDeployPattern = Pattern.compile("^" + DEPLOY + shipsLeftByType + "$");
        eventDoDisconnectPattern = Pattern.compile("^" + DO_DISCONNECT + "$");
        eventNotStartRematchPattern = Pattern.compile("^" + NOT_START_REMATCH + "$");
        eventDrawHitPattern = Pattern.compile("^" + HIT + xy + player + "$");
        eventDrawMissPattern = Pattern.compile("^" + MISS + xy + player + "$");
        eventDrawNearPattern = Pattern.compile("^" + NEAR + xy + player + "$");
        eventDrawShipPattern = Pattern.compile("^" + DRAW_SHIP + xyto + player + "$");
        eventSetEnemyNamePattern = Pattern.compile("^" + SET_ENEMY_NAME + "(.+)");
        eventStartRematchPattern = Pattern.compile("^" + START_REMATCH + "$");
        eventWaitingForSecondPlayerPattern = Pattern.compile("^" + WAIT + "(.*)");
        eventEndMatchPattern = Pattern.compile("^" + END_MATCH + player + "$");
        eventDrawShipsLeftPattern = Pattern.compile("^" + DRAW_SHIP_LEFT + "(\\d)" + "$");
    }

    public NetworkClientEvent deserializeMessage(String msg) {
        Matcher matcher = eventCannotDeployPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int type = Integer.parseInt(matcher.group(3));
            boolean isHorizontal = matcher.group(4).equals("H");
            return new NetworkCannotDeployEvent(x, y, type, isHorizontal);
        }
        matcher = eventCanShootPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkCanShootEvent();
        }
        matcher = eventCommandNotAcceptedPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkCommandNotAcceptedEvent(matcher.group(1));
        }
        matcher = eventDeployPattern.matcher(msg);
        if (matcher.find()) {
            List<Integer> list = new ArrayList<>();
            for (int i = 1; i < NUM_TYPE + 1; i++) {
                list.add(Integer.parseInt(matcher.group(i)));
            }
            return new NetworkDeployEvent(list.stream().mapToInt(Integer::intValue).toArray());
        }
        matcher = eventDoDisconnectPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkDoDisconnectEvent();
        }
        matcher = eventNotStartRematchPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkNotStartRematchEvent();
        }
        matcher = eventDrawHitPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            PlayerType playerType = matcher.group(3).equals(YOU) ? PlayerType.YOU : PlayerType.ENEMY;
            return new NetworkDrawHitEvent(x, y, playerType);
        }
        matcher = eventDrawMissPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            PlayerType playerType = matcher.group(3).equals(YOU) ? PlayerType.YOU : PlayerType.ENEMY;
            return new NetworkDrawMissEvent(x, y, playerType);
        }
        matcher = eventDrawNearPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            return new NetworkDrawNearEvent(x, y);
        }
        matcher = eventDrawShipPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int type = Integer.parseInt(matcher.group(3));
            boolean isHorizontal = matcher.group(4).equals("H");
            PlayerType playerType = matcher.group(5).equals(YOU) ? PlayerType.YOU : PlayerType.ENEMY;
            return new NetworkDrawShipEvent(x, y, type, isHorizontal, playerType);
        }
        matcher = eventSetEnemyNamePattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkSetEnemyNameEvent(matcher.group(1));
        }
        matcher = eventStartRematchPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkStartRematchEvent();
        }
        matcher = eventWaitingForSecondPlayerPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkWaitingSecondPlayerEvent(matcher.group(1));
        }
        matcher = eventEndMatchPattern.matcher(msg);
        if (matcher.find()) {
            PlayerType playerType = matcher.group(1).equals(YOU) ? PlayerType.YOU : PlayerType.ENEMY;
            return new NetworkEndMatchEvent(playerType);
        }
        matcher = eventDrawShipsLeftPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkDrawShipsLeftEvent(Integer.parseInt(matcher.group(1)));
        }
        return new NetworkUnknownCommandClientEvent(msg);
    }
}
