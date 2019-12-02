package com.liver_rus.Battleships.Client;

//TODO только метод который конвертирует координаты(инплейс) только там где рисуем
class DrawAdapterShip extends Ship {
    DrawAdapterShip(Ship ship) {
        super(ship.getShipStartCoord().getX() - 2,
                ship.getShipStartCoord().getY() - 2,
                Ship.Type.shipTypeToInt(ship.getType()),
                ship.isHorizontal());
    }
}
