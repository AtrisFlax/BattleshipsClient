package com.liver_rus.Battleships.Client.GameEngine;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GUI.DrawEvents.*;
import com.liver_rus.Battleships.Client.GUI.FXMLDocumentMainController;
import com.liver_rus.Battleships.Client.GUI.GUIState;
import com.liver_rus.Battleships.Client.GamePrimitives.GameField;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import com.liver_rus.Battleships.Client.GamePrimitives.TryingAddTooManyShipsOnFieldException;
import com.liver_rus.Battleships.Client.GamePrimitives.WrongShipInfoSizeException;
import com.liver_rus.Battleships.Client.Tools.MessageProcessor;
import com.liver_rus.Battleships.Network.Client.MailBox;
import com.liver_rus.Battleships.Network.Client.NetworkClient;
import com.liver_rus.Battleships.Network.Server.GameServerThread;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;


//TODO на уровне applicaton
public class ClientGameEngine implements ClientActions {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private Phase gamePhase;


    //TODO перенести в серверную часть состояние
    GameField gameField;

    private MailBox netClient;
    private GameServerThread netServer;

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
    public void disconnect() throws IOException {
        if (gameField != null) {
            netClient.disconnect();
            netClient = null;

            //TODO в disconnect
//            if (netClient.isRunning()) {
//                netClient.stopThread();
//            }
        }
        if (netServer != null) {
            if (netServer.isRunning()) {
                netServer.stopThread();
            }
        }
        gameField = new GameField();
        controller.reset("Disconnect from server");
    }

    @Override
    public void startNetwork(String ip, int port, boolean startServer, String myName) {
        if (ip != null && port != 0) {
            //create server
            if (startServer) {
                //TODO some magic
                //TODO убрать проверку на null (возможет ли тут null)
                if (netServer == null) {
                    try {
                        //TODO создать фабричный метод
                        netServer = new GameServerThread(ip, port);
                        netServer.start();
                    } catch (IOException e) {
                        controller.reset("Couldn't create server");
                        e.printStackTrace();
                    }

                } else {
                    netServer.startThread();
                }
            }
            //create client
            try {
                //TODO убрать проверку на null (возможет ли тут null)
                if (netClient == null) {
                    //TODO точно знает какой экземпляр будет создаваться
                    //TODO создать фабричный метод
                    //ClientFabric.create(ip,port)
                    netClient = new NetworkClient(ip, port);
                    netClient.subscribeForInbox((message) -> {
                        log.info("NetworkClient inbox message " + message);
                        try {
                            proceedMessage(message);
                        } catch (WrongShipInfoSizeException e) {
                            System.out.println("Exception during proceedMessage(message);");
                            e.printStackTrace();
                        }
                    });

                } else {
                    netClient.startThread();
                }

            } catch (IOException e) {
                log.log(Level.SEVERE, "Fail to make connection", e);
            }
            setGamePhase(ClientGameEngine.Phase.DEPLOYING_FLEET);
            controller.setStartDeployingFleetInfo(myName);
            //auto deployment ships for debug
            //TODO <<+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ DEBUG
            //debugShipsDeployment();
        }
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
            controller.redraw(new RenderRedrawHitEnemyEvent(x,y));
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

    private void proceedMessage(String msg) throws WrongShipInfoSizeException {
        final int UNDEFINED_COORD = -1;
        int x = UNDEFINED_COORD;
        int y = UNDEFINED_COORD;
        if (msg.startsWith(Constants.NetworkCommand.HIT) ||
                msg.startsWith(Constants.NetworkCommand.MISS) ||
                msg.startsWith(Constants.NetworkCommand.DESTROYED)
        ) {
            String msgCoord = msg.replaceAll("\\D+", "");
            x = MessageProcessor.getX(msgCoord);
            y = MessageProcessor.getY(msgCoord);
        }


        //TODO отдельный класс ser\deserl
        //пре sederl comand + argX + argY (будет в разных местах! пока не думать)
        //стратегия???
        if (msg.startsWith(Constants.NetworkCommand.HIT)) {
            if (getGamePhase() == Phase.WAITING_ANSWER) {
                controller.draw(new RenderHitEnemyEvent(x, y));
                //конвертор строчки в gui event
                //if (event != null) {
                //controller.draw(event)
                //}
                //controller.drawHitOnEnemyField(/*diseralization event*/);
            }
            if (getGamePhase() == Phase.TAKE_SHOT) {
                controller.draw(new RenderHitMeEvent(x, y));
            }
        }
        if (msg.startsWith(Constants.NetworkCommand.MISS)) {
            if (getGamePhase() == Phase.TAKE_SHOT) {
                controller.draw(new RenderMissEnemyEvent(x, y));
            }
        }
        if (msg.startsWith(Constants.NetworkCommand.DESTROYED)) {
            if (getGamePhase() == Phase.WAITING_ANSWER) {
                GUIState shipInfo = GUIState.create(msg.replace(Constants.NetworkCommand.DESTROYED, ""));
                controller.draw(new RenderDestroyEnemyShip(x, y, shipInfo));


            }
        }

        switch (msg) {
            case Constants.NetworkCommand.YOU_TURN:
                setGamePhase(Phase.MAKE_SHOT);
                break;
            case Constants.NetworkCommand.ENEMY_TURN:
                setGamePhase(Phase.TAKE_SHOT);
                break;
            case Constants.NetworkCommand.YOU_WIN:
            case Constants.NetworkCommand.YOU_LOSE:
                setGamePhase(Phase.END_GAME);
                break;
        }

        controller.setInfo(generalInfoFromMsg(msg), convertInboxToReadableView(msg, x, y));
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

    private String convertInboxToReadableView(String message, int x, int y) {
        if (message.startsWith(Constants.NetworkCommand.HIT)) {
            if (getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                return MessageProcessor.XYtoGameFormat(x, y) + " Enemy Ship has Hit";
            }
            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                return MessageProcessor.XYtoGameFormat(x, y) + " You Ship has Hit";
            }
        }
        if (message.startsWith(Constants.NetworkCommand.MISS)) {
            if (getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                return MessageProcessor.XYtoGameFormat(x, y) + " You Missed";
            }
            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                return MessageProcessor.XYtoGameFormat(x, y) + " Enemy Missed";
            }
        }
        if (message.startsWith(Constants.NetworkCommand.DESTROYED)) {
            if (getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
                return "You Destroy Enemy Ship";
            }

            if (getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                return "Enemy Destroy Your Ship";
            }
        }
        if (message.startsWith(Constants.NetworkCommand.DISCONNECT)) {
            return "Disconnect";
        }
        return message;
    }

    private String generalInfoFromMsg(String message) {
        switch (message) {
            case Constants.NetworkCommand.YOU_TURN:   return "You turn. Make shoot";
            case Constants.NetworkCommand.ENEMY_TURN: return "Enemy turn. Waiting...";
            case Constants.NetworkCommand.YOU_WIN:    return "You Win";
            case Constants.NetworkCommand.YOU_LOSE:   return "You Lose";
            case Constants.NetworkCommand.DISCONNECT: return "Disconnect";
            default:                                  return "";
            //TODO Player name exchange
        }
    }
}

//TODO numTurn tracking, incrementing and resetting
