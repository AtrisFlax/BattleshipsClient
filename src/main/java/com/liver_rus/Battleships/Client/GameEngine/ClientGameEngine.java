package com.liver_rus.Battleships.Client.GameEngine;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GUI.DrawEvents.*;
import com.liver_rus.Battleships.Client.GUI.FXMLDocumentMainController;
import com.liver_rus.Battleships.Client.GUI.GUIState;
import com.liver_rus.Battleships.Client.GUI.NetworkEvent.*;
import com.liver_rus.Battleships.Client.GamePrimitives.GameField;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import com.liver_rus.Battleships.Client.GamePrimitives.TryingAddTooManyShipsOnFieldException;
import com.liver_rus.Battleships.Client.Tools.MessageProcessor;
import com.liver_rus.Battleships.Network.Client.MailBox;
import com.liver_rus.Battleships.Network.Client.NetworkClient;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.logging.Logger;


//TODO на уровне applicaton
public class ClientGameEngine implements ClientActions {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private Phase gamePhase;


    //TODO перенести в серверную часть состояние
    GameField gameField;

    private MailBox netClient;
    private FXMLDocumentMainController controller;

    public enum Phase {
        INIT, DEPLOYING_FLEET, FLEET_IS_DEPLOYED, WAITING_ANSWER, TAKE_SHOT, MAKE_SHOT, END_GAME;
    }

    public ClientGameEngine() {
        super();
        gameField = new GameField();
        setGamePhase(Phase.INIT);
    }

    @Override
    public void tryDeployShip(GUIState state) {
        if (isNotAllShipsDeployed() && isPossibleLocateShip(state)) {
            addShipOnField(Ship.create(state));
            controller.draw(new RenderDrawShip(state));
            controller.unlockDeploying();
        }
        //Fleet deployed. Send player is ready
        checkAndSetFleetIsDeployed();
    }

    @Override
    public void disconnect() {
        netClient.disconnect();
        netClient = null;
        controller.reset("Disconnect from server");
    }

    @Override
    public void startNetwork(String ip, int port, String myName) {
        netClient = NetworkClient.create(ip, port);
        netClient.subscribeForInbox((message) -> {
            log.info("NetworkClient inbox message =" + message);
            proceedMessage(message);
        });
        setGamePhase(ClientGameEngine.Phase.DEPLOYING_FLEET);
        controller.setStartDeployingFleetInfo(myName);
        //auto deployment ships for debug
        //TODO <<+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ DEBUG
        //debugShipsDeployment();
    }

    @Override
    public void setController(FXMLDocumentMainController controller) {
        this.controller = controller;
    }

    @Override
    public void popShip(Ship.Type type) {
        if (getGamePhase() == ClientGameEngine.Phase.DEPLOYING_FLEET) {
            int leftShipAmountByType = selectShip(type);
            controller.shipWasPopped(type, leftShipAmountByType);
        }
    }

    @Override
    public void fleetDeploying(GUIState state) {
        if (getGamePhase() == Phase.DEPLOYING_FLEET) {
            if (gameField.isNotIntersectionShipWithBorder(state)) {
                boolean isNotIntersection = gameField.isNotIntersetionWithShips(state);
                controller.redraw(new RenderRedrawShip(state, isNotIntersection));
            }
        }
    }

    @Override
    public void mouseMovedInsideSecondPlayerField(int x, int y) {
        if (getGamePhase() == Phase.MAKE_SHOT) {
            controller.redraw(new RenderRedrawHitEnemyEvent(x, y));
        }
    }

    @Override
    public void hitEnemyCell(int x, int y) {
        if (getGamePhase() == Phase.MAKE_SHOT) {
            netClient.sendMessage(Constants.NetworkCommand.SHOT + x + y);
            setGamePhase(ClientGameEngine.Phase.WAITING_ANSWER);
            controller.draw(new RenderMissEnemyEvent(x, y));
        }
    }

    //TODO здесь серюлизация стратегия!!!
    @Override
    public String getShipsInfoForSend() {
        return Constants.NetworkCommand.SEND_SHIPS + gameField.getFleet().toString();
    }

    @Override
    public void setGamePhase(Phase phase) {
        System.out.println("setGamePhase=" + phase);
        this.gamePhase = phase;
    }

    @Override
    public final Phase getGamePhase() {
        return gamePhase;
    }

    @Override
    public int selectShip(Ship.Type type) {
        if (gameField.getFleet().getShipsLeft() > 0) {
            int popShipResult = gameField.getFleet().popShip(type);
            final int NO_MORE_SHIP_FOR_EXTRACTION = -1;
            if (popShipResult != NO_MORE_SHIP_FOR_EXTRACTION) {
                return popShipResult;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public void addShipOnField(Ship ship) {
        try {
            gameField.getFleet().add(ship);
        } catch (TryingAddTooManyShipsOnFieldException e) {
            e.printStackTrace();
        }
        gameField.markFieldCellsByShip(ship);
    }


    //TODO ???????????? interface cast ok   ????????????
    private void proceedMessage(String msg) {
        NetworkEvent networkEvent = CreatorNetworkEvent.deserializeMessage(msg);
        if (networkEvent instanceof NetworkEventHit) {
            if (getGamePhase() == Phase.WAITING_ANSWER) {

                controller.draw(new RenderHitEnemyEvent((XYGettable) networkEvent));
            }
            if (getGamePhase() == Phase.TAKE_SHOT) {
                controller.draw(new RenderHitMeEvent((XYGettable) networkEvent));
            }
        }

        if (networkEvent instanceof NetworkEventMiss) {
            if (getGamePhase() == Phase.TAKE_SHOT) {
                controller.draw(new RenderMissEnemyEvent((XYGettable) networkEvent));
            }
        }

        if (networkEvent instanceof NetworkEventDestroyed) {
            if (getGamePhase() == Phase.WAITING_ANSWER) {
                controller.draw(new RenderDestroyEnemyShip((NetworkEventDestroyed) networkEvent));
            }
        }

        if (networkEvent instanceof NetworkEventYouTurn) {
            setGamePhase(Phase.MAKE_SHOT);
        }

        if (networkEvent instanceof NetworkEventEnemyTurn) {
            setGamePhase(Phase.TAKE_SHOT);
        }

        if (networkEvent instanceof NetworkEventYouWin || networkEvent instanceof NetworkEventYouLose) {
            setGamePhase(Phase.END_GAME);
        }

        switch (msg) {
            case Constants.NetworkCommand.YOU_TURN:
                break;
            case Constants.NetworkCommand.ENEMY_TURN:
                setGamePhase(Phase.TAKE_SHOT);
                break;
            case Constants.NetworkCommand.YOU_WIN:
            case Constants.NetworkCommand.YOU_LOSE:
                setGamePhase(Phase.END_GAME);
                break;
        }


        controller.setInfo(generalInfoFromMsg(networkEvent), convertInboxToReadableView(networkEvent));

    }

    @Override
    public boolean isNotAllShipsDeployed() {
        return getGameField().getFleet().getShipsLeft() >= 0;
    }

    @Override
    public boolean noMoreShipLeft() {
        return getGameField().getFleet().getShipsLeft() == 0;
    }

    @Override
    public LinkedList<Ship> getShips() {
        return gameField.getFleet().getShips();
    }

    @Override
    public int[] getShipsLeftByTypeInit() {
        return GameField.getShipLeftByTypeInit();
    }

    //debug method
//    public void debugShipsDeployment() {
//        addShipOnField(Ship.create(1, 8, Ship.Type.SUBMARINE, true));
//        addShipOnField(Ship.create(3, 2, Ship.Type.SUBMARINE, true));
//        addShipOnField(Ship.create(1, 1, Ship.Type.DESTROYER, false));
//        addShipOnField(Ship.create(3, 4, Ship.Type.DESTROYER, true));
//        addShipOnField(Ship.create(2, 6, Ship.Type.CRUISER, true));
//        addShipOnField(Ship.create(7, 4, Ship.Type.BATTLESHIP, false));
//        addShipOnField(Ship.create(9, 1, Ship.Type.AIRCRAFT_CARRIER, false));
//        for (Ship ship : getShips()) {
//            controller.drawMyShip(ship);
//        }
//        setGamePhase(ClientGameEngine.Phase.FLEET_IS_DEPLOYED);
//        getGameField().printOnConsole();
//        netClient.sendMessage(getShipsInfoForSend());
//    }

    private boolean isPossibleLocateShip(GUIState state) {
        return gameField.isNotIntersetionWithShips(state) && gameField.isNotIntersectionShipWithBorder(state);
    }

    private void checkAndSetFleetIsDeployed() {
        if (noMoreShipLeft()) {
            setGamePhase(ClientGameEngine.Phase.FLEET_IS_DEPLOYED);
            netClient.sendMessage(getShipsInfoForSend());
            controller.setLockGUI();
        }
    }


    //TODO simplify method
    private String convertInboxToReadableView(NetworkEvent event) {
        if (event instanceof NetworkEventHit) {
            if (getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                XYGettable coord = (XYGettable) event;
                return MessageProcessor.XYtoGameFormat(coord.getX(), coord.getY()) + " Enemy Ship has Hit";
            }
            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                XYGettable coord = (XYGettable) event;
                return MessageProcessor.XYtoGameFormat(coord.getX(), coord.getY()) + " You Ship has Hit";
            }
        }
        if (event instanceof NetworkEventMiss) {
            if (getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                XYGettable coord = (XYGettable) event;
                return MessageProcessor.XYtoGameFormat(coord.getX(), coord.getY()) + " You Missed";
            }
            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                XYGettable coord = (XYGettable) event;
                return MessageProcessor.XYtoGameFormat(coord.getX(), coord.getY()) + " Enemy Missed";
            }
        }
        if (event instanceof NetworkEventDestroyed) {
            if (getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
                return "You Destroy Enemy Ship";
            }

            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                return "Enemy Destroy Your Ship";
            }
        }
        if (event instanceof NetworkEventDisconnect) {
            return "Disconnect";
        }
        return "";
    }


    private String generalInfoFromMsg(NetworkEvent event) {
        if (event instanceof NetworkEventYouTurn) {
            return "You turn. Make shoot";
        }
        if (event instanceof NetworkEventEnemyTurn) {
            return "Enemy turn. Waiting...";
        }
        if (event instanceof NetworkEventYouWin) {
            return "You Win";
        }
        if (event instanceof NetworkEventYouLose) {
            return "You Lose";
        }
        if (event instanceof NetworkEventDisconnect) {
            return "Disconnect";
        }
        if (event instanceof NetworkEventUnknown) {
            return "";
        }
        return "";
    }
}

//TODO numTurn tracking, incrementing and resetting
