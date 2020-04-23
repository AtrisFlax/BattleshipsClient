package com.liver_rus.Battleships.Client.GameEngine;

import com.liver_rus.Battleships.Client.GUI.FXMLDocumentMainController;
import com.liver_rus.Battleships.Client.GUI.ShipInfo;

import java.io.IOException;
//TODO что убрать
public interface ClientActions {
    void close() throws IOException;

    void startNetwork(String ip, int port, String myName);

    void setController(FXMLDocumentMainController controller);

    void tryDeployShip(ShipInfo currentGUIState);

    void resetFleet();

    void shot(int x, int y);

//    void popShip(int shipType);
//
//    void fleetDeploying(ShipInfo currentGUIState);
//
//    void mouseMovedInsideSecondPlayerField(int x, int y);
//
//    void hitEnemyCell(int x, int y);
//
//    int selectShip(int shipType);
//
//    void addShipOnField(Ship ship);

    //проверить какие-то могут быть приватными

    //могут остаться "8" методов
    //разделиться на две группы(интерфейса)
    //процесс игры (поведенческий интефес)и действия на контроль (создать дисконект и т.д. пораждающий)
}
