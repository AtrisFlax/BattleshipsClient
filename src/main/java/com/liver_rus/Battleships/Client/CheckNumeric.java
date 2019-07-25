package com.liver_rus.Battleships.Client;

class CheckNumeric {
    static boolean isNumeric(String strNum) {
        boolean isNum = true;
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException e) {
            isNum = false;
        }
        return isNum;
    }
}
