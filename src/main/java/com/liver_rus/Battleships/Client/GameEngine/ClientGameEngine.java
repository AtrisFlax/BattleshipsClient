package com.liver_rus.Battleships.Client.GameEngine;

import com.liver_rus.Battleships.Client.GUI.FXMLDocumentMainController;
import com.liver_rus.Battleships.Client.GUI.ShipInfo;
import com.liver_rus.Battleships.Network.Client.MailBox;
import com.liver_rus.Battleships.Network.Client.NetworkClient;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.CreatorClientNetworkEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.Events.NetworkDoDisconnectEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Client.NetworkClientEvent;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.Events.*;
import com.liver_rus.Battleships.Network.NetworkEvent.Server.NetworkServerEvent;
import com.liver_rus.Battleships.Network.Server.GameServer;

import java.io.IOException;

public class ClientGameEngine implements ClientActions {
    private MailBox netClient;
    private FXMLDocumentMainController controller;
    private final CreatorClientNetworkEvent eventCreator;
    private GameServer gameServer;

    @Override
    public void close() {
        netClient.disconnect();
        netClient = null;
        controller.reset();
        gameServer.close();
    }

    public ClientGameEngine() {
         eventCreator = new CreatorClientNetworkEvent();
    }

    @Override
    public void startNetwork(String ip, int port, String myName, boolean isSaveShooting) {
        netClient = NetworkClient.create(ip, port);
        netClient.subscribeForInbox(this::proceedMessage);
        initServerEvents(myName, isSaveShooting);
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
        sendEvent(new NetworkTryDeployShipEvent(x, y, type, isHorizontal));
    }

    @Override
    public void resetFleet() {
        sendEvent(new NetworkResetFleetWhileDeployEvent());
    }

    @Override
    public void shot(int x, int y) {
        sendEvent(new NetworkShotEvent(x, y));
    }

    @Override
    public void startServer(String ip, int port) throws IOException {
        gameServer = GameServer.create(ip, port);
        gameServer.start();
    }

    private void initServerEvents(String myName, boolean isSaveShooting) {
        sendEvent(new NetworkSetSaveShooting(isSaveShooting));
        sendEvent(new NetworkMyNameEvent(myName));
    }

    private void sendEvent(NetworkServerEvent event) {
        netClient.sendMessage(event.convertToString());
    }

    private void proceedMessage(String message) {
        NetworkClientEvent event = eventCreator.deserializeMessage(message);

        //TODO delete or wrap for debug
        System.out.println("Client read= " + message);
        System.out.println("Client event= " + event.getClass().getSimpleName());

        String answer = event.proceed(controller);
        if (answer != null) {
            netClient.sendMessage(answer);
        }
        if (event instanceof NetworkDoDisconnectEvent || event instanceof NetworkNoRematchEvent) {
            netClient.disconnect();
        }
    }
}