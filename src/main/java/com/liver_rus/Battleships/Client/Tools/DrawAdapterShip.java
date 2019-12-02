package com.liver_rus.Battleships.Client.Tools;

import com.liver_rus.Battleships.Client.GamePrimitive.Ship;

//TODO только метод который конвертирует координаты(инплейс) только там где рисуем
public class DrawAdapterShip extends Ship {
    public DrawAdapterShip(Ship ship) {
        super(ship.getShipStartCoord().getX() - 2,
                ship.getShipStartCoord().getY() - 2,
                Ship.Type.shipTypeToInt(ship.getType()),
                ship.isHorizontal());
    }
}
