package com.liver_rus.Battleships.Client.GamePrimitives;

import com.liver_rus.Battleships.Client.Constants.Constants;

public class WrongShipInfoSizeException extends Throwable {
    String shipInfo;

    public WrongShipInfoSizeException(String shipInfo) {
        this.shipInfo = shipInfo;
    }

    public String toString() {
            return "Can't create ship from an shipInfo = " + shipInfo + "."
                    + "shipInfo should have length = " + Constants.ShipInfoLength
                    + " but have length = " + shipInfo.length();
        }
    }

