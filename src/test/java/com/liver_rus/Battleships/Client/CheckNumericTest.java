package com.liver_rus.Battleships.Client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckNumericTest {

    @DisplayName("string converted to a int")
    @Test
    void isNumeric() {
        assertAll("string is numeric",
                () -> assertTrue(CheckNumeric.isNumeric("1234")),
                () -> assertTrue(CheckNumeric.isNumeric("-5678")),
                () -> assertTrue(CheckNumeric.isNumeric("0")),
                () -> assertFalse(CheckNumeric.isNumeric("1234.57")),
                () -> assertFalse(CheckNumeric.isNumeric("-1234.58")),
                () -> assertFalse(CheckNumeric.isNumeric("-1234.58ABCDEF")),
                () -> assertFalse(CheckNumeric.isNumeric("sdflsjdf"))
        );
    }
}