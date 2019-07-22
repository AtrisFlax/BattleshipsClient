package com.liver_rus.Battleships.Client;

class CheckNumeric {
    static boolean isNumeric(String strNum) {
        boolean isNum = true;
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException e) {
            isNum = false;
        }
        return isNum;
    }
}
