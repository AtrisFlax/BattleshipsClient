package com.liver_rus.Battleships.Client.GameEngine;

import com.liver_rus.Battleships.Client.GUI.FXMLDocumentMainController;
import com.liver_rus.Battleships.Client.GUI.ShipInfo;
import com.liver_rus.Battleships.Network.Client.MailBox;
import com.liver_rus.Battleships.Network.Client.NetworkClient;
import com.liver_rus.Battleships.NetworkEvent.CreatorClientNetworkEvent;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventClient;
import com.liver_rus.Battleships.NetworkEvent.NetworkEventServer;
import com.liver_rus.Battleships.NetworkEvent.incoming.*;
import com.liver_rus.Battleships.NetworkEvent.outcoming.NetworkEventDoDisconnect;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

public class ClientGameEngine implements ClientActions {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private MailBox netClient;
    private FXMLDocumentMainController controller;

    @Override
    public void close() {
        netClient.disconnect();
        netClient = null;
        controller.reset();
    }

    @Override
    public void startNetwork(String ip, int port, String myName) {
        netClient = NetworkClient.create(ip, port);
        netClient.subscribeForInbox((message) -> {
            log.info("NetworkClient inbox message =" + message);
            proceedMessage(message);
        });
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
    public void isPossibleDeployShip(ShipInfo state) {
        sendEvent(new NetworkEventIsPossibleDeployShip(state.getX(), state.getY(), state.getType(), state.isHorizontal()));
    }

    @Override
    public void resetFleet() {
        sendEvent(new NetworkEventResetFleetWhileDeploy());
    }

    @Override
    public void shot(int x, int y) {
        sendEvent(new NetworkEventShot(x, y));
    }

    private void sendEvent(NetworkEventServer event) {
        netClient.sendMessage(event.convertToString());
    }


//    @Override
//    public void popShip(int shipType) {
//        /*
//        if (getGamePhase() == ClientGameEngine.Phase.DEPLOYING_FLEET) {
//            int leftShipAmountByType = selectShip(type);
//            controller.shipWasPopped(type, leftShipAmountByType);
//        }
//         */
//    }

//    @Override
//    public void fleetDeploying(ShipInfo state) {
//        /*
//        if (getGamePhase() == Phase.DEPLOYING_FLEET) {
//            netClient.sendMessage("");
//            // TODO SEND AND WAIT ANSWER
//            // TODO PHASE TO SERVER
//            if (gameField.isNotIntersectionShipWithBorder(state)) {
//                boolean isNotIntersection = gameField.isNotIntersetionWithShips(state);
//                controller.redraw(new RenderRedrawShip(state, isNotIntersection));
//            }
//        }
//         */
//    }

//    @Override
//    public void mouseMovedInsideSecondPlayerField(int x, int y) {
//        /*
//        if (getGamePhase() == Phase.MAKE_SHOT) {
//            controller.redraw(new RenderRedrawHitEnemyEvent(x, y));
//        }
//
//         */
//    }

//    @Override
//    public void hitEnemyCell(int x, int y) {
//        /*
//        if (getGamePhase() == Phase.MAKE_SHOT) {
//            netClient.sendMessage(NetworkCommandConstant.SHOT + x + y);
//            setGamePhase(ClientGameEngine.Phase.WAITING_ANSWER);
//            controller.draw(new RenderMissEnemyEvent(x, y));
//        }
//         */
//    }

//    //TODO здесь серюлизация стратегия!!!
//    @Override
//    public String getShipsInfoForSend() {
//        return NetworkCommandConstant.SEND_SHIPS + gameField.getFleet().toString();
//    }

//    @Override
//    public int selectShip(int shipType) {
//        /*
//        if (gameField.getFleet().getShipsLeft() > 0) {
//            int popShipResult = gameField.getFleet().popShip(type);
//            final int NO_MORE_SHIP_FOR_EXTRACTION = -1;
//            if (popShipResult != NO_MORE_SHIP_FOR_EXTRACTION) {
//                return popShipResult;
//            } else {
//                return 0;
//            }
//        } else {
//            return 0;
//        }
//         */ return 0; //TODO delete fake return
//    }

    //TODO ???????????? interface cast ok   ????????????
    private void proceedMessage(String msg) {
        CreatorClientNetworkEvent eventCreator = new CreatorClientNetworkEvent();
        NetworkEventClient event = eventCreator.deserializeMessage(msg);
        String answer = event.proceed(controller);
        if (answer != null) {
            netClient.sendMessage(answer);
        }
        if (event instanceof NetworkEventDoDisconnect || event instanceof NetworkEventNoRematch) {
            netClient.disconnect();
        }
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


//    private String convertInboxToReadableView(NetworkEventServer event) {
//        /*
//        if (event instanceof NetworkEventEnemyHit) {
//            if (getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
//                XYGettable coord = (XYGettable) event;
//                return MessageProcessor.XYtoGameFormat(coord.getX(), coord.getY()) + " Enemy Ship has Hit";
//            }
//            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
//                XYGettable coord = (XYGettable) event;
//                return MessageProcessor.XYtoGameFormat(coord.getX(), coord.getY()) + " You Ship has Hit";
//            }
//        }
//        if (event instanceof NetworkEventMiss) {
//            if (getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
//                XYGettable coord = (XYGettable) event;
//                return MessageProcessor.XYtoGameFormat(coord.getX(), coord.getY()) + " You Missed";
//            }
//            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
//                XYGettable coord = (XYGettable) event;
//                return MessageProcessor.XYtoGameFormat(coord.getX(), coord.getY()) + " Enemy Missed";
//            }
//        }
//        if (event instanceof NetworkEventDestroyed) {
//            if (getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
//                return "You Destroy Enemy Ship";
//            }
//
//            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
//                return "Enemy Destroy Your Ship";
//            }
//        }
//        if (event instanceof NetworkEventDisconnect) {
//            return "Disconnect";
//        }
//         */
//        return "";
//    }


//    private String generalInfoFromMsg(NetworkEventServer event) {
//        /*
//        if (event instanceof NetworkEventYouTurn) {
//            return "You turn. Make shoot";
//        }
//        if (event instanceof NetworkEventEnemyTurn) {
//            return "Enemy turn. Waiting...";
//        }
//        if (event instanceof NetworkEventYouWin) {
//            return "You Win";
//        }
//        if (event instanceof NetworkEventYouLose) {
//            return "You Lose";
//        }
//        if (event instanceof NetworkEventDisconnect) {
//            return "Disconnect";
//        }
//        if (event instanceof NetworkEventUnknown) {
//            return "";
//        }
//         */
//        return "";
//    }
}