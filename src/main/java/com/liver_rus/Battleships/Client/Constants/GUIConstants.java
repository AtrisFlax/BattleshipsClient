package com.liver_rus.Battleships.Client.Constants;

import javafx.scene.paint.Color;

public interface GUIConstants {
    int getLeftX();
    int getRightX();
    int getTopY();
    int getBottomY();
    double getWidthCell();
    double DASH_WIDTH = 3.0;
    Color POSSIBLE_DEPLOY_COLOR = Color.BLACK;
    Color IMPOSSIBLE_DEPLOY_COLOR = Color.RED;
}
