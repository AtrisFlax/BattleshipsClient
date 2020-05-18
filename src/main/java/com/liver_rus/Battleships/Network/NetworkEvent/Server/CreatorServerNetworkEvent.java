package com.liver_rus.Battleships.Network.NetworkEvent.Server;

import com.liver_rus.Battleships.Network.NetworkEvent.Server.Events.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.liver_rus.Battleships.Network.NetworkEvent.NetworkCommandConstant.*;

//Common deserialize class
public class CreatorServerNetworkEvent {
    private final Pattern configPlayerPattern;
    private final Pattern configGamePattern;
    private final Pattern tryDeployShipPattern;
    private final Pattern shotPattern;
    private final Pattern disconnectPattern;
    private final Pattern tryRematchPattern;
    private final Pattern resetFleetWhileDeployPattern;

    public CreatorServerNetworkEvent() {
        String state = "(" + ON + "|" + OFF + ")";
        configPlayerPattern = Pattern.compile("^" + CONFIG_PLAYER + state + NAME + "(.+)");
        configGamePattern = Pattern.compile("^" + CONFIG_GAME + state + state);
        tryDeployShipPattern = Pattern.compile("^" + TRY_DEPLOY_SHIP + "(\\d)(\\d)(\\d)([VH])" + "$");
        shotPattern = Pattern.compile("^" + SHOT + "(\\d)(\\d)" + "$");
        disconnectPattern = Pattern.compile("^" + DISCONNECT + "$");
        tryRematchPattern = Pattern.compile("^" + REMATCH_ANSWER + state + "$");
        resetFleetWhileDeployPattern = Pattern.compile("^" + RESET_FLEET_WHILE_DEPLOY + "$");
    }

    public ServerNetworkEvent deserializeMessage(String msg) {
        Matcher matcher;

        matcher = configPlayerPattern.matcher(msg);
        if (matcher.find()) {
            return new ConfigPlayerEvent(matcher.group(1).equals(ON), matcher.group(2));
        }

        matcher = configGamePattern.matcher(msg);
        if (matcher.find()) {
            return new ConfigGameEvent(matcher.group(1).equals(ON), matcher.group(1).equals(ON));
        }

        matcher = tryDeployShipPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int type = Integer.parseInt(matcher.group(3));
            boolean isHorizontal = matcher.group(4).equals("H");
            return new TryDeployShipNetworkEvent(x, y, type, isHorizontal);
        }

        matcher = shotPattern.matcher(msg);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            return new ShotNetworkEvent(x, y);
        }

        matcher = disconnectPattern.matcher(msg);
        if (matcher.find()) {
            return new DisconnectNetworkEvent();
        }

        matcher = tryRematchPattern.matcher(msg);
        if (matcher.find()) {
            return new TryRematchStateNetworkEvent(matcher.group(1).equals(ON));
        }

        matcher = resetFleetWhileDeployPattern.matcher(msg);
        if (matcher.find()) {
            return new ResetFleetWhileDeployNetworkEvent();
        }

        return new UnknownCommandServerNetworkEvent(msg);
    }
}
