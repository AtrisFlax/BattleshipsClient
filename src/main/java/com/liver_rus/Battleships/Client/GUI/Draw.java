package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.GUI.Constants.Constants;
import com.liver_rus.Battleships.Client.GUI.Constants.GUIConstants;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Draw {
    private final static double CELL_LINE_WIDTH = 2;
    private final static double SHIP_LINE_WIDTH = 2.2;
    private final static double DESTROY_LIST_WIDTH = 4;

    private final static Color MISS_CELL_COLOR = Color.BLACK;
    private final static Color DESTROY_LIST_COLOR = Color.BLACK;
    private final static Color HIT_CELL_COLOR = Color.BLACK;
    private static final Color POSSIBLE_DEPLOY_COLOR = Color.BLACK;
    private static final Color IMPOSSIBLE_DEPLOY_COLOR = Color.RED;

    private static final int NEXT_ROW_ID = 3;

    public static void clearCanvas(GraphicsContext context) {
        context.clearRect(0, 0, Constants.Window.WIDTH, Constants.Window.HEIGHT);
    }

    public static void Miss(GraphicsContext graphicContext, GUIConstants constants, int x, int y) {
        double width = constants.getWidthCell();
        graphicContext.setStroke(MISS_CELL_COLOR);
        graphicContext.setLineWidth(CELL_LINE_WIDTH);
        graphicContext.strokeLine(
                x * width + constants.getLeftX(),
                y * width + constants.getTopY() + width,
                x * width + constants.getLeftX() + width,
                y * width + constants.getTopY());
    }

    //see picture Battleships_Paper_Game.jpg
    //0 1 2   - first col of ships
    //3 4 5 6 - second col of ships
    public static void MarkDestroy(GraphicsContext graphicContext, int shipNumber) {
        double leftX, leftY;
        int row;
        if (shipNumber < NEXT_ROW_ID) {
            leftX = Constants.DestroyedListGUI.LEFT_X_ROW0;
            leftY = Constants.DestroyedListGUI.LEFT_Y_ROW0;
            row = shipNumber;
        } else {
            row = shipNumber - NEXT_ROW_ID;
            leftX = Constants.DestroyedListGUI.LEFT_X_ROW1;
            leftY = Constants.DestroyedListGUI.LEFT_Y_ROW1;
        }
        double widthX = Constants.DestroyedListGUI.WIDTH_CELL_X;
        double widthY = Constants.DestroyedListGUI.WIDTH_CELL_Y;
        graphicContext.setStroke(DESTROY_LIST_COLOR);
        graphicContext.setLineWidth(DESTROY_LIST_WIDTH);
        graphicContext.strokeLine(
                leftX,
                leftY + widthY * row,
                leftX + widthX,
                leftY + widthY * row + widthY);
    }

    public static void Hit(GraphicsContext gc, GUIConstants constants, int x, int y) {
        double width = constants.getWidthCell();
        gc.setStroke(HIT_CELL_COLOR);
        gc.setLineWidth(CELL_LINE_WIDTH);
        gc.strokeLine(
                x * width + constants.getLeftX(),
                y * width + constants.getTopY(),
                x * width + constants.getLeftX() + width,
                y * width + constants.getTopY() + width
        );
        gc.strokeLine(
                x * width + constants.getLeftX(),
                y * width + constants.getTopY() + width,
                x * width + constants.getLeftX() + width,
                y * width + constants.getTopY()
        );
    }

    public static void Ship(GraphicsContext gc, GUIConstants constant,
                            int x, int y, int shipType, boolean isHorizontal) {
        Draw.Ship(gc, POSSIBLE_DEPLOY_COLOR, constant, x, y, shipType, isHorizontal);
    }

    //draw red and clear
    public static void impossibleDraw(GraphicsContext gc, GUIConstants constant,
                                      int x, int y, int shipType, boolean isHorizontal) {
        Timeline timeLine = new Timeline(
                new KeyFrame(Duration.ZERO, event ->
                        Draw.Ship(gc, IMPOSSIBLE_DEPLOY_COLOR, constant, x, y, shipType, isHorizontal)),
                new KeyFrame(Duration.seconds(0.25), event ->
                        clearCanvas(gc)));
        timeLine.setCycleCount(1);
        timeLine.play();
    }

    private static void Ship(GraphicsContext gc, Color shipColor, GUIConstants constant,
                             int x, int y, int shipType, boolean isHorizontal) {
        gc.setStroke(shipColor);
        gc.setLineWidth(SHIP_LINE_WIDTH);
        gc.setLineDashes(0);
        int shipLength = convertTypeToShipLength(shipType);
        if (isHorizontal) {
            gc.strokeRect(
                    constant.getLeftX() + x * constant.getWidthCell(),
                    constant.getTopY() + y * constant.getWidthCell(),
                    constant.getWidthCell() * shipLength,
                    constant.getWidthCell()
            );
        } else {
            gc.strokeRect(
                    constant.getLeftX() + x * constant.getWidthCell(),
                    constant.getTopY() + y * constant.getWidthCell(),
                    constant.getWidthCell(),
                    constant.getWidthCell() * shipLength
            );
        }
    }

    private static int convertTypeToShipLength(int type) {
        return type + 1;
    }
}



