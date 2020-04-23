package com.liver_rus.Battleships.Client.GameEngine;

import com.liver_rus.Battleships.Client.GUI.FXMLDocumentMainController;
import com.liver_rus.Battleships.Client.GUI.ShipInfo;
import com.liver_rus.Battleships.Network.Client.MailBox;
import com.liver_rus.Battleships.Network.Client.NetworkClient;
import com.liver_rus.Battleships.Network.Server.GameServer;
import com.liver_rus.Battleships.NetworkEvent.Client.NetworkEventDoDisconnect;
import com.liver_rus.Battleships.NetworkEvent.CreatorClientNetworkEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventClient;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventServer;
import com.liver_rus.Battleships.NetworkEvent.Server.*;

import java.io.IOException;

public class ClientGameEngine implements ClientActions {
    private MailBox netClient;
    private FXMLDocumentMainController controller;
    CreatorClientNetworkEvent eventCreator;
    GameServer gameServer;

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
        sendEvent(new NetworkEventMyName(myName));
    }

    @Override
    public void setController(FXMLDocumentMainController controller) {
        this.controller = controller;
    }

    @Override
    public void tryDeployShip(ShipInfo state) {
        sendEvent(new NetworkEventTryDeployShip(state.getX(), state.getY(), state.getType(), state.isHorizontal()));
    }

    @Override
    public void resetFleet() {
        sendEvent(new NetworkEventResetFleetWhileDeploy());
    }

    @Override
    public void shot(int x, int y) {
        sendEvent(new NetworkEventShot(x, y));
    }

    @Override
    public void startServer(String ip, int port) throws IOException {
        gameServer = GameServer.create(ip, port);
        gameServer.start();
    }

    private void sendEvent(NetworkEventServer event) {
        netClient.sendMessage(event.convertToString());
    }

    private void proceedMessage(String msg) {
        NetworkEventClient event = eventCreator.deserializeMessage(msg);

        System.out.println("Client: msg=" + msg);
        System.out.println(event.getClass().getSimpleName());

        String answer = event.proceed(controller);
        if (answer != null) {
            netClient.sendMessage(answer);
        }
        if (event instanceof NetworkEventDoDisconnect || event instanceof NetworkEventNoRematch) {
            netClient.disconnect();
        }
    }
}