package com.liver_rus.Battleships.Client.GameEngine;

import com.liver_rus.Battleships.Client.GUI.FXMLDocumentMainController;
import com.liver_rus.Battleships.Client.GUI.GUIState;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;

import java.io.IOException;
import java.util.LinkedList;
//TODO что убрать
public interface ClientActions {
    void tryDeployShip(GUIState state);

    void disconnect() throws IOException;

    void startNetwork(String ip, int port, String myName);

    //TODO delete


    void setController(FXMLDocumentMainController controller);

    void popShip(Ship.Type type);

    void fleetDeploying(GUIState currentGUIState);

    void mouseMovedInsideSecondPlayerField(int x, int y);

    void hitEnemyCell(int x, int y);

    String getShipsInfoForSend();

    void setGamePhase(ClientGameEngine.Phase phase);

    ClientGameEngine.Phase getGamePhase();

    int selectShip(Ship.Type type);

    void addShipOnField(Ship ship);

    boolean isNotAllShipsDeployed();

    boolean noMoreShipLeft();

    //TODO посмотреть можно ли удалить этот метод

    LinkedList<Ship> getShips();

    int[] getShipsLeftByTypeInit();

    //проверить какие-то могут быть приватными

    //могут остаться "8" методов
    //разделиться на две группы(интерфейса)
    //процесс игры (поведенческий интефес)и действия на контроль (создать дисконект и т.д. пораждающий)
}
