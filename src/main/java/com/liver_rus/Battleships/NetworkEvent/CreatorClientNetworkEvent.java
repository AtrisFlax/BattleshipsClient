package com.liver_rus.Battleships.NetworkEvent;

import com.liver_rus.Battleships.NetworkEvent.outcoming.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.liver_rus.Battleships.Network.Server.GamePrimitives.Fleet.NUM_TYPE;
import static com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant.*;

//Common deserialize class
public class CreatorClientNetworkEvent {
    static Pattern eventCanDeployPattern;
    static Pattern eventCannotDeployPattern;
    static Pattern eventCanShootPattern;
    static Pattern eventCommandNotAcceptedPattern;
    static Pattern eventDeployPattern;
    static Pattern eventDoDisconnectPattern;
    static Pattern eventNotStartRematchPattern;
    static Pattern eventDrawHitPattern;
    static Pattern eventDrawMissPattern;
    static Pattern eventDrawShipPattern;
    static Pattern eventSetEnemyNamePattern;
    static Pattern eventStartRematchPattern;
    static Pattern eventWaitingForSecondPlayerPattern;
    static Pattern eventWaitingForSecondPlayerDeploymentPattern;
    static Pattern eventWaitingForSecondPlayerShotPattern;
    static Pattern eventWaitingSecondPlayerForRematchPattern;
    static Pattern eventEndMatchPattern;
    static Pattern eventYouWinPattern;

    public CreatorClientNetworkEvent() {
        String xyto = "(\\d)(\\d)(\\d)([VH])"; //x y type orientation
        String xy = "(\\d)(\\d)";
        String player = "(" + YOU + "|" + ENEMY + ")";
        String shipsLeftByType = "(\\d)(\\d)(\\d)(\\d)(\\d)"; //x y type orientation

        eventCanDeployPattern = Pattern.compile("^" + DEPLOY + xyto + "$");
        eventCannotDeployPattern = Pattern.compile("^" + CANNOT_DEPLOY + xyto + "$");
        eventCanShootPattern = Pattern.compile("^" + CAN_SHOOT + "$");
        eventCommandNotAcceptedPattern = Pattern.compile("^" + COMMAND_NOT_ACCEPTED + "(.+)");
        eventDeployPattern = Pattern.compile("^" + DEPLOY + shipsLeftByType + "$");
        eventDoDisconnectPattern = Pattern.compile("^" + DO_DISCONNECT + "$");
        eventNotStartRematchPattern = Pattern.compile("^" + NOT_START_REMATCH + "$");
        eventDrawHitPattern = Pattern.compile("^" + HIT + xy + player + "$");
        eventDrawMissPattern = Pattern.compile("^" + MISS + xy + player + "$");
        eventDrawShipPattern = Pattern.compile("^" + DRAW_SHIP + xyto + player + "$");
        eventSetEnemyNamePattern = Pattern.compile("^" + SET_ENEMY_NAME + "(.+)");
        eventStartRematchPattern = Pattern.compile("^" + START_REMATCH + "$");
        eventWaitingForSecondPlayerPattern = Pattern.compile("^" + WAITING_FOR_SECOND_PLAYER + "(.+)");
        eventEndMatchPattern = Pattern.compile("^" + END_MATCH + "$");
    }

    public NetworkEventClient deserializeMessage(String msg) {
        Matcher matcher = eventCannotDeployPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int type = Integer.parseInt(matcher.group(3));
            boolean isHorizontal = matcher.group(4).equals("H");
            return new NetworkEventCannotDeploy(x, y, type, isHorizontal);
        }
        matcher = eventCanShootPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkEventCanShoot();
        }
        matcher = eventCommandNotAcceptedPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkEventCommandNotAccepted(matcher.group(1));
        }
        matcher = eventDeployPattern.matcher(msg);
        if (matcher.find()) {
            List<Integer> list = new ArrayList<>();
            for (int i = 1; i < NUM_TYPE + 1; i++) {
                list.add(Integer.parseInt(matcher.group(i)));
            }
            return new NetworkEventDeploy(list.stream().mapToInt(Integer::intValue).toArray());
        }
        matcher = eventDoDisconnectPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkEventDoDisconnect();
        }
        matcher = eventNotStartRematchPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkEventNotStartRematch();
        }
        matcher = eventDrawHitPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            PlayerType playerType = matcher.group(3).equals(YOU) ? PlayerType.ME : PlayerType.ENEMY;
            return new NetworkEventDrawHit(x, y, playerType);
        }
        matcher = eventDrawMissPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            PlayerType playerType = matcher.group(3).equals(YOU) ? PlayerType.ME : PlayerType.ENEMY;
            return new NetworkEventDrawMiss(x, y, playerType);
        }
        matcher = eventDrawShipPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int type = Integer.parseInt(matcher.group(3));
            boolean isHorizontal = matcher.group(4).equals("H");
            PlayerType playerType = matcher.group(3).equals(YOU) ? PlayerType.ME : PlayerType.ENEMY;
            return new NetworkEventDrawShip(x, y, type, isHorizontal, playerType);
        }
        matcher = eventSetEnemyNamePattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkEventSetEnemyName(matcher.group(1));
        }
        matcher = eventStartRematchPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkEventStartRematch();
        }
        matcher = eventWaitingForSecondPlayerPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkEventWaitingSecondPlayer(matcher.group(1));
        }
        matcher = eventEndMatchPattern.matcher(msg);
        if (matcher.find()) {
            PlayerType playerType = matcher.group(3).equals(YOU) ? PlayerType.ME : PlayerType.ENEMY;
            return new NetworkEventEndMatch(playerType);
        }
        return new NetworkEventUnknownCommandClient(msg);
    }
}
