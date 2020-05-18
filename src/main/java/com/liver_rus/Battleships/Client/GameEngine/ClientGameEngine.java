package com.liver_rus.Battleships.Client.GameEngine;

import com.liver_rus.Battleships.Client.GUI.FXMLDocumentMainController;
import com.liver_rus.Battleships.Client.GUI.ShipInfo;
import com.liver_rus.Battleships.Network.Client.MailBox;
import com.liver_rus.Battleships.Network.Client.NetworkClient;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.ClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.CreatorClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.DoDisconnectNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Events.*;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.ServerNetworkEvent;
import com.liver_rus.Battleships.Network.Server.GamePreferences;
import com.liver_rus.Battleships.Network.Server.GameServer;
import com.liver_rus.Battleships.utils.MyLogger;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class ClientGameEngine implements ClientActions {
    private MailBox netClient;
    private FXMLDocumentMainController controller;
    private final CreatorClientNetworkEvent eventCreator;
    private GameServer gameServer;

    //TODO logging
    private static final Logger LOGGER = MyLogger.GetLogger(ClientGameEngine.class);

    public ClientGameEngine() {
        eventCreator = new CreatorClientNetworkEvent();
    }

    @Override
    public void startClient(String ip, int port) {
        netClient = NetworkClient.create(ip, port);
        netClient.subscribeForInbox(this::proceedMessage);
    }

    @Override
    public void startServer(String ip, int port, GamePreferences preferences) throws IOException {
        gameServer = GameServer.create(ip, port, preferences);
        gameServer.start();
    }

    @Override
    public void disconnect() {
        sendEvent(new DisconnectNetworkEvent());
    }

    @Override
    public void setController(FXMLDocumentMainController controller) {
        this.controller = controller;
    }

    @Override
    public void tryDeployShip(ShipInfo state) {
        int x = state.getX();
        int y = state.getY();
        int type = state.getType();
        boolean isHorizontal = state.isHorizontal();
        sendEvent(new TryDeployShipNetworkEvent(x, y, type, isHorizontal));
    }

    @Override
    public void resetFleet() {
        sendEvent(new ResetFleetWhileDeployNetworkEvent());
    }

    @Override
    public void shot(int x, int y) {
        sendEvent(new ShotNetworkEvent(x, y));
    }

    @Override
    public void rematch(boolean wantRematch) {
        sendEvent(new TryRematchStateNetworkEvent(wantRematch));
    }

    private void sendEvent(ServerNetworkEvent event) {
        netClient.sendMessage(event.convertToString());
    }

    private void proceedMessage(String message) {
        ClientNetworkEvent event = eventCreator.deserializeMessage(message);
        LOGGER.info("Client event= " + event.getClass().getSimpleName());
        List<ServerNetworkEvent> answer = event.proceed(controller);
        sendAnswer(answer);
        if (event instanceof DoDisconnectNetworkEvent) {
            stopNetwork();
        }
    }

    private void sendAnswer(List<ServerNetworkEvent> answer) {
        if (answer != null) {
            for (ServerNetworkEvent answerStr : answer) {
                netClient.sendMessage(answerStr.convertToString());
            }
        }
    }

    private void stopNetwork() {
        netClient.stopConnection();
        if (gameServer != null) {
            gameServer.stopConnection();
        }
    }
}