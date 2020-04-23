package com.liver_rus.Battleships.Client.GUI;

//Class describes virtual ship for draw

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.Ship;
import com.liver_rus.Battleships.Network.Server.GamePrimitives.WrongShipInfoSizeException;

import java.io.IOException;


//TODO убарть опциаонально возможно заменится эвентами


public class ShipInfo {
    private int x;
    private int y;
    private int  shipType;
    private boolean isHorizontal;


    final static int UNKNOWN_TYPE = -1;
    private static ShipInfo instance;

    ShipInfo() {
        this.x = 0;
        this.y = 0;
        shipType = UNKNOWN_TYPE;
        isHorizontal = true;
    }

    public static ShipInfo getGUIConstant() {
        if (instance == null) {
            instance = new ShipInfo();
        }
        return instance;
    }

    public ShipInfo(int x, int y, int shipType, boolean isHorizontal) {
        this.x = x;
        this.y = y;
        this.shipType = shipType;
        this.isHorizontal = isHorizontal;
    }

    //shipInfo format - X Y Z O
    //XY - x,y coordinate from 0 to 9
    //Z - type from 0 to 4
    //O - letter 'H' or 'V'
    public static ShipInfo create(String shipInfo) throws WrongShipInfoSizeException {
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
            return new ShipInfo(x, y, type, isHorizontal);
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

    public int getType() {
        return shipType;
    }

    public void setShipType(int  shipType) {
        this.shipType = shipType;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setOrientation(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
    }

    public void changeShipOrientation() {
        isHorizontal = !isHorizontal;
    }

    @Override
    public String toString() {
        return "GUIState: x=" + x + "  " +
                "y=" + y + "  " +
                "shipType=" + shipType + "  " +
                "isHorizontal=" + isHorizontal + "  ";
    }
}