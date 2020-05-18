package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.GUI.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Constants.GUIConstants;
import com.liver_rus.Battleships.Client.GUI.Constants.SecondPlayerGUIConstants;
import javafx.scene.input.MouseEvent;

import java.util.function.Supplier;

/**
 * Class for converting screen coordinates to the coordinates of the game field
 */

public class SceneCoord {
    private SceneCoord() {
    }

    public static boolean isFromFirstPlayerField(MouseEvent event) {
        double x = event.getSceneX();
        double y = event.getSceneY();
        return checkBorders(x, y, FirstPlayerGUIConstants.getGUIConstant());
    }

    public static boolean isFromSecondPlayerField(MouseEvent event) {
        double x = event.getSceneX();
        double y = event.getSceneY();
        return checkBorders(x, y, SecondPlayerGUIConstants.getGUIConstant());
    }

    private static boolean checkBorders(double x, double y, GUIConstants constants) {
        return x >= constants.getLeftX() && x <= constants.getRightX() &&
                y >= constants.getTopY() && y <= constants.getBottomY();
    }

    public static int transformToFieldX(double x, GUIConstants constants) {
        return transform(x, constants::getLeftX, constants::getWidthCell);
    }

    public static int transformToFieldY(double y, GUIConstants constants) {
        return transform(y, constants::getTopY, constants::getWidthCell);
    }

    private static int transform(double coord, Supplier<Integer> edge, Supplier<Double> width) {
        int trCoord = (int)(Math.floor((coord - edge.get()) / width.get()));
        final int MAX_RANGE = 9;
        final int OUT_RANGE = MAX_RANGE + 1;
        if (trCoord >= OUT_RANGE) {
            return MAX_RANGE;
        } else {
            return trCoord;
        }
    }
}