package com.liver_rus.Battleships.Network.Server.GamePrimitives;

import com.liver_rus.Battleships.Client.GUI.Constants.Constants;

public class WrongShipInfoSizeException extends Exception {
    public WrongShipInfoSizeException(String shipInfo) {
        super("Can't create ship from an shipInfo = " + shipInfo + "."
                + "shipInfo should have length = " + Constants.ShipInfoLength
                + " but have length = " + shipInfo.length());
    }
}

