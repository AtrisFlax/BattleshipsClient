package com.liver_rus.Battleships.NetworkEvent;

import com.liver_rus.Battleships.NetworkEvent.incoming.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.liver_rus.Battleships.NetworkEvent.NetworkCommandConstant.*;

//Common deserialize class
public class CreatorServerNetworkEvent {
    static Pattern isPossibleDeployShipPattern;
    static Pattern myNamePattern;
    static Pattern tryDeployShipPattern;
    static Pattern shotPattern;
    static Pattern disconnectPattern;
    static Pattern noTryRematchPattern;
    static Pattern tryRematchPattern;
    static Pattern resetFleetWhileDeployPattern;


    public CreatorServerNetworkEvent() {
        isPossibleDeployShipPattern = Pattern.compile("^" + IS_POSSIBLE_DEPLOY_SHIP + "(\\d)(\\d)(\\d)([VH])" + "$");
        myNamePattern = Pattern.compile("^" + MY_NAME + "(.+)");
        tryDeployShipPattern = Pattern.compile("^" + TRY_DEPLOY_SHIP + "(\\d)(\\d)(\\d)([VH])" + "$");
        shotPattern = Pattern.compile("^" + SHOT + "(\\d)(\\d)" + "$");
        disconnectPattern = Pattern.compile("^" + DISCONNECT + "$");
        noTryRematchPattern = Pattern.compile("^" + NO_REMATCH + "$");
        tryRematchPattern = Pattern.compile("^" + TRY_REMATCH + "$");
        resetFleetWhileDeployPattern = Pattern.compile("^" + RESET_FLEET_WHILE_DEPLOY + "$");
    }

    public NetworkEventServer deserializeMessage(String msg) {
        Matcher matcher = myNamePattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkEventMyName(matcher.group(1));
        }

        matcher = tryDeployShipPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int type = Integer.parseInt(matcher.group(3));
            boolean isHorizontal = matcher.group(4).equals("H");
            return new NetworkEventTryDeployShip(x, y, type, isHorizontal);
        }

        matcher = shotPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            return new NetworkEventShot(x, y);
        }

        matcher = disconnectPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkEventDisconnect();
        }

        matcher = noTryRematchPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkEventNoRematch();
        }

        matcher = tryRematchPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkEventTryRematch();
        }

        matcher = resetFleetWhileDeployPattern.matcher(msg);
        if (matcher.find()) {
            return new NetworkEventResetFleetWhileDeploy();
        }


        return new NetworkEventUnknownCommandServer(msg);
    }
}
