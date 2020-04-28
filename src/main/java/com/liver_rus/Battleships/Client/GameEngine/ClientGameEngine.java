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
    public void startNetwork(String ip, int port, String myName) {
        netClient = NetworkClient.create(ip, port);
        netClient.subscribeForInbox(this::proceedMessage);
        sendEvent(new NetworkMyNameEvent(myName));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setController(FXMLDocumentMainController controller) {
        this.controller = controller;
    }

    @Override
    public void tryDeployShip(ShipInfo state) {
        sendEvent(new NetworkTryDeployShipEvent(state.getX(), state.getY(), state.getType(), state.isHorizontal()));
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

    private void sendEvent(NetworkServerEvent event) {
        netClient.sendMessage(event.convertToString());
    }

    private void proceedMessage(String msg) {
        NetworkClientEvent event = eventCreator.deserializeMessage(msg);

        System.out.println("Client: msg= " + msg);
        System.out.println("Client: event= " + event.getClass().getSimpleName());

        String answer = event.proceed(controller);
        if (answer != null) {
            netClient.sendMessage(answer);
        }
        if (event instanceof NetworkDoDisconnectEvent || event instanceof NetworkNoRematchEvent) {
            netClient.disconnect();
        }
    }
}