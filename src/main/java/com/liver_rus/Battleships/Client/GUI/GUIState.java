package com.liver_rus.Battleships.Client.GUI;

//Class describes virtual ship for draw

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import com.liver_rus.Battleships.Client.GamePrimitives.WrongShipInfoSizeException;

import java.io.IOException;


//TODO убарть опциаонально возможно заменится эвентами
public class GUIState {
    private int x;
    private int y;
    private Ship.Type shipType;
    private boolean isHorizontalShipOrientation;

    public GUIState() {
        this.x = 0;
        this.y = 0;
        setShipType(Ship.Type.UNKNOWN);
        isHorizontalShipOrientation = true;
    }

    public GUIState(int x, int y, int shipType, boolean isHorizontal) {
        this.x = x;
        this.y = y;
        this.shipType = Ship.Type.shipIntToType(shipType);
        this.isHorizontalShipOrientation = isHorizontal;
    }

    //shipInfo format - XYZO
    //XY - x,y coordinate from 0 to 9
    //Z - type from 0 to 4
    //O - letter 'H' or 'V'
    public static GUIState create(String shipInfo) throws WrongShipInfoSizeException {
        if (shipInfo.length() == Constants.ShipInfoLength) {
            int x = Character.getNumericValue(shipInfo.charAt(0));
            int y = Character.getNumericValue(shipInfo.charAt(1));
            int type = Character.getNumericValue(shipInfo.charAt(2));
            boolean isHorizontal = false;
            try {
                isHorizontal = Ship.charToIsHorizontal(shipInfo.charAt(3));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new GUIState(x, y, type, isHorizontal);
        } else {
            throw new WrongShipInfoSizeException(shipInfo);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Ship.Type getShipType() {
        return shipType;
    }

    public void setShipType(Ship.Type shipType) {
        this.shipType = shipType;
    }

    public boolean isHorizontalOrientation() {
        return isHorizontalShipOrientation;
    }

    public void setOrientation(boolean isHorizontal) {
        isHorizontalShipOrientation = isHorizontal;
    }

    public void changeShipOrientation() {
        isHorizontalShipOrientation = !isHorizontalShipOrientation;
    }

    @Override
    public String toString() {
        return "GUIState: x=" + x + "  " +
                "y=" + y + "  " +
                "shipType=" + shipType + "  " +
                "isHorizontal=" + isHorizontalShipOrientation + "  ";
    }
}