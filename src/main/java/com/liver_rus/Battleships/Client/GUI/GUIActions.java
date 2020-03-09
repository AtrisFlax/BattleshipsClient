package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.GameEngine.ClientGameEngine;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;

public interface GUIActions {
    void setClientEngine(ClientGameEngine clientGameEngine);

    void shipWasPopped(Ship.Type type, int value);

    void drawDeployedShipOnMyField(GUIState state);

    void drawDeployingShip(GUIState state, boolean isDeployable);

    void setInfoAndLockGUI();

    void drawHitOnMyField(int x, int y);

    void drawMissOnMyField(int x, int y);

    void drawHitMarkOnEnemyField(int x, int y);

    void drawHitOnEnemyField(int x, int y);

    void drawMissOnEnemyField(int x, int y);

    void setStartDeployingFleetInfo(String myName);

    void redrawShipWithChangeOrientation(GUIState state);

    void setInfo(String message, String readableView);

    void drawShipOnEnemyField(GUIState shipInfo);

    void drawMyShip(Ship ship);

    void reset(String resetReason);


    //общий класс с методом

    //TODO
    //стратегия
    //метод draw event в качестве парамета
    //event разного типа от одного абстр класа
    //передали после
    //draw(event) {
    //смотрим на тип эвента instansof
    //после каста внутри типы будут x, y
    //event.getX() getY()
}
