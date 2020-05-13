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
import com.liver_rus.Battleships.Network.Server.GameServer;
import com.liver_rus.Battleships.utils.MyLogger;

import java.io.IOException;
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
    public void startClient(String ip, int port, String myName, boolean isSaveShooting) throws IOException {
        if (netClient == null) {
            netClient = NetworkClient.create(ip, port);
            netClient.subscribeForInbox(this::proceedMessage);
        } else {
            netClient.restart(ip, port);
            netClient.subscribeForInbox(this::proceedMessage);
        }
        initServerEvents(myName, isSaveShooting);
    }

    @Override
    public void startServer(String ip, int port) throws IOException {
        if (gameServer == null) {
            gameServer = GameServer.create(ip, port);
            gameServer.start();
        } else {
            gameServer.restart(ip, port);
        }
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

    private void initServerEvents(String myName, boolean isSaveShooting) {
        sendEvent(new SetSaveShootingNetworkEvent(isSaveShooting));
        sendEvent(new MyNameNetworkEvent(myName));
    }

    private void sendEvent(ServerNetworkEvent event) {
        netClient.sendMessage(event.convertToString());
    }

    private void proceedMessage(String message) {
        ClientNetworkEvent event = eventCreator.deserializeMessage(message);

        //TODO delete or wrap for debug
        System.out.println("Client event= " + event.getClass().getSimpleName());

        String answer = event.proceed(controller);
        if (answer != null) {
            netClient.sendMessage(answer);
        }
        if (event instanceof DoDisconnectNetworkEvent) {
            stopNetwork();
        }
    }

    private void stopNetwork() {
        netClient.stopConnection();
        if (gameServer != null) {
            gameServer.stopConnection();
        }
    }
}