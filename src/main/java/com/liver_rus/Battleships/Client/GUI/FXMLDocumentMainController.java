package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.Constants.GUIConstants;
import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GameEngine.ClientGameEngine;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


//TODO objects and handlers separate to file
//logic in another file
public class FXMLDocumentMainController implements Initializable, GUIActions {
    private static final int UNDEFINED_COORD = -1;

    @FXML
    private Button shipType4Button;

    @FXML
    private Button shipType3Button;

    @FXML
    private Button shipType2Button;

    @FXML
    private Button shipType1Button;

    @FXML
    private Button shipType0Button;

    @FXML
    private MenuItem menuItemConnect;

    @FXML
    private MenuItem menuItemDisconnect;

    @FXML
    private Canvas canvasOverlay;

    @FXML
    private Canvas canvasGeneral;

    @FXML
    private Label numRoundLabel;

    @FXML
    private Label labelGameStatus;

    @FXML
    private Label playerMyLabel;

    @FXML
    private Label playerEnemyLabel;

    @FXML
    private Button resetFleetButton;

    @FXML
    private ListView<String> statusListView;

    private int lastMyX = UNDEFINED_COORD;
    private int lastMyY = UNDEFINED_COORD;
    private int lastEnemyX = UNDEFINED_COORD;
    private int lastEnemyY = UNDEFINED_COORD;

    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private GraphicsContext overlayCanvas;
    private GraphicsContext mainCanvas;

    private ClientGameEngine clientGameEngine;
    private boolean isShipSelected;

    private GUIState currentGUIState;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainCanvas = canvasGeneral.getGraphicsContext2D();
        overlayCanvas = canvasOverlay.getGraphicsContext2D();
        isShipSelected = false;
        currentGUIState = new GUIState();
        resetFleetButton.setDisable(true);
        disableShipButtons();
        setUndefLastXY();
        initShipButtonText();
    }

    @FXML
    protected void handleResetFleetButton() {
        reset("You reset fleet");
    }

    @FXML
    protected void handlerButtonShipType4() {
        handlerButtonShip(Ship.Type.AIRCRAFT_CARRIER);
    }

    @FXML
    protected void handlerButtonShipType3() {
        handlerButtonShip(Ship.Type.BATTLESHIP);
    }

    @FXML
    protected void handlerButtonShipType2() {
        handlerButtonShip(Ship.Type.CRUISER);
    }

    @FXML
    protected void handlerButtonShipType1() {
        handlerButtonShip(Ship.Type.DESTROYER);
    }

    @FXML
    protected void handlerButtonShipType0() {
        handlerButtonShip(Ship.Type.SUBMARINE);
    }

    @FXML
    protected void handleToCanvasMouseClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            handleToMouseClickPrimaryButton(event);
        }
        if (event.getButton().equals(MouseButton.SECONDARY)) {
            handleToMouseClickSecondButton();
        }
    }

    @FXML
    protected void handleOverlayCanvasMouseMoved(MouseEvent event) {
        if (isShipSelected) {
            if (SceneCoord.isFromFirstPlayerField(event)) {
                setXY(event, FirstPlayerGUIConstants.getGUIConstant());
                System.out.println("event x=" + event.getX() + ", y=" + event.getY());
                System.out.println("currentGUIState  = " + currentGUIState);

                clientGameEngine.fleetDeploying(currentGUIState);
            }
        }

        if (SceneCoord.isFromSecondPlayerField(event)) {
            int x = SceneCoord.transformToFieldX(event.getSceneX(), SecondPlayerGUIConstants.getGUIConstant());
            int y = SceneCoord.transformToFieldY(event.getSceneY(), SecondPlayerGUIConstants.getGUIConstant());
            clientGameEngine.mouseMovedInsideSecondPlayerField(x, y);
        }
    }

    @FXML
    protected void handleMenuItemConnectGame() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            handleResetFleetButton();
            fxmlLoader.setLocation(getClass().getResource("/fxml/FXMLDocumentConnectGame.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Connect to game");
            FXMLDocumentConnectGame controller = fxmlLoader.getController();
            controller.setIPAndPortFields();
            controller.setMyName();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setOnHiding(close -> {
                if (controller.isStartClient()) {
                    String ip = controller.getHost();
                    int port = controller.getPort();
                    //TODO serverThreadClassHolder.startServerThread(ip, port) //with stop start interface (server and client)
                    clientGameEngine.startNetwork(ip, port, controller.isStartServer(), controller.getMyName());
                }
            });
            stage.show();
            //TODO round traction
            //numRoundLabel.setText(Integer.toString(1));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    @FXML
    protected void handleMenuItemExit() {
        Platform.exit();
    }

    @FXML
    protected void handleMenuItemAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Constants.AboutInfo.ABOUT_GAME_TITLE);
        alert.setHeaderText(Constants.AboutInfo.ABOUT_GAME_HEADER);
        alert.setContentText(Constants.AboutInfo.ABOUT_GAME_TEXT);
        alert.showAndWait();
    }

    @FXML
    protected void handlerDisconnectMenuItem() throws IOException {
        clientGameEngine.disconnect();
    }

    @Override
    public void setClientEngine(ClientGameEngine clientGameEngine) {
        this.clientGameEngine = clientGameEngine;
    }

    @Override
    public void shipWasPopped(Ship.Type type, int value) {
        setShipButtonTypeText(type, value);
        isShipSelected = true;
    }
    //TODO другой интерфейс
    @Override
    public void drawDeployedShipOnMyField(GUIState state) {
        Draw.ShipOnMyField(mainCanvas, state);
        isShipSelected = false;
        enableShipButtons();
    }

    @Override
    public void drawDeployingShip(GUIState state, boolean isDeployable) {
        clearOldShip(state.getX(), state.getY());
        setColorForDrawShip(isDeployable);
        Draw.ShipOnMyField(overlayCanvas, state);
    }

    @Override
    public void setInfoAndLockGUI() {
        resetFleetButton.setDisable(true);
        disableShipButtons();
        labelGameStatus.setText("Fleet is deployed. Waiting for second player...");
    }

    @Override
    public void drawHitOnMyField(int x, int y) {
        Draw.HitCellOnMyField(mainCanvas, x, y);
    }

    @Override
    public void drawMissOnMyField(int x, int y) {
        Draw.MissCellOnMyField(mainCanvas, x, y);
    }

    @Override
    public void drawHitMarkOnEnemyField(int x, int y) {
        clearOldHitMark(x, y);
        Draw.MissCellOnEnemyField(overlayCanvas, x, y);
    }


    //TODO strategy pattern

    interface DrawGUIEvent {
        void render(GraphicsContext gc);
    }

    class RenderHitEnemyEvent implements DrawGUIEvent {
        private final int x;
        private final int y;

        RenderHitEnemyEvent(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void render(GraphicsContext gc) {
            //custom render realization hide !!!
            Draw.HitCellOnEnemyField(mainCanvas, x, y);
        }
    }


    @Override
    public void drawHitOnEnemyField(int x, int y) {
        //TODO убрать
        Draw.HitCellOnEnemyField(mainCanvas, x, y);
    }

    @Override
    public void drawMissOnEnemyField(int x, int y) {
        Draw.MissCellOnEnemyField(mainCanvas, x, y);
    }

    @Override
    public void setStartDeployingFleetInfo(String myName) {
        labelGameStatus.setText("Deploying fleet. Select and place ship");
        enableShipButtons();
        menuItemConnect.setDisable(true);
        menuItemDisconnect.setDisable(false);
        playerMyLabel.setText(myName);
    }

    @Override
    public void redrawShipWithChangeOrientation(GUIState state) {
        clearCanvas(overlayCanvas);
        Draw.ShipOnMyField(overlayCanvas, state);
    }

    @Override
    public void setInfo(String message, String readableView) {
        if (readableView != null) {
            Platform.runLater(() -> {
                statusListView.getItems().add(readableView);
                statusListView.scrollTo(statusListView.getItems().size() - 1);
            });
        }

        Platform.runLater(() -> labelGameStatus.setText(message));

    }

    @Override
    public void drawShipOnEnemyField(GUIState shipInfo) {
        Draw.ShipOnEnemyField(mainCanvas, shipInfo);
    }

    @Override
    public void drawMyShip(Ship ship) {
        Draw.ShipOnMyField(mainCanvas, ship);
    }

    @Override
    public void reset(String resetReason) {
        clearCanvas(mainCanvas);
        clearCanvas(overlayCanvas);
        isShipSelected = false;
        currentGUIState = new GUIState();
        disableShipButtons();
        setUndefLastXY();
        initShipButtonText();
        resetFleetButton.setDisable(true);
        menuItemConnect.setDisable(false);
        statusListView.getItems().clear();
        statusListView.getItems().add(resetReason);
        statusListView.scrollTo(statusListView.getItems().size() - 1);

    }

    private void setUndefLastXY() {
        lastMyX = UNDEFINED_COORD;
        lastMyY = UNDEFINED_COORD;
        lastEnemyX = UNDEFINED_COORD;
        lastEnemyY = UNDEFINED_COORD;
    }

    private void initShipButtonText() {
        int[] amountShipByType = clientGameEngine.getShipsLeftByTypeInit();
        shipType4Button.setText(amountShipByType[4] + " x");
        shipType3Button.setText(amountShipByType[3] + " x");
        shipType2Button.setText(amountShipByType[2] + " x");
        shipType1Button.setText(amountShipByType[1] + " x");
        shipType0Button.setText(amountShipByType[0] + " x");
    }

    private void handlerButtonShip(Ship.Type type) {
        currentGUIState.setShipType(type);
        disableShipButtons();
        clientGameEngine.popShip(type);
    }

    private void disableShipButtons() {
        shipType4Button.setDisable(true);
        shipType3Button.setDisable(true);
        shipType2Button.setDisable(true);
        shipType1Button.setDisable(true);
        shipType0Button.setDisable(true);
    }

    private void enableShipButtons() {
        shipType4Button.setDisable(false);
        shipType3Button.setDisable(false);
        shipType2Button.setDisable(false);
        shipType1Button.setDisable(false);
        shipType0Button.setDisable(false);
    }

    private void setShipButtonTypeText(Ship.Type type, int value) {
        switch (type) {
            case AIRCRAFT_CARRIER:
                shipType4Button.setText(value + " x");
                break;
            case BATTLESHIP:
                shipType3Button.setText(value + " x");
                break;
            case CRUISER:
                shipType2Button.setText(value + " x");
                break;
            case DESTROYER:
                shipType1Button.setText(value + " x");
                break;
            case SUBMARINE:
                shipType0Button.setText(value + " x");
                break;
        }
    }

    private void handleToMouseClickPrimaryButton(MouseEvent event) {
        if (SceneCoord.isFromFirstPlayerField(event)) {
            if (isShipSelected) {
                setXY(event, FirstPlayerGUIConstants.getGUIConstant());
                clientGameEngine.tryDeployShip(currentGUIState);
            }
            return;
        }
        if (SceneCoord.isFromSecondPlayerField(event)) {
            int x = SceneCoord.transformToFieldX(event.getSceneX(), SecondPlayerGUIConstants.getGUIConstant());
            int y = SceneCoord.transformToFieldY(event.getSceneY(), SecondPlayerGUIConstants.getGUIConstant());
            clientGameEngine.hitEnemyCell(x, y);
        }
    }

    private void setXY(MouseEvent event, GUIConstants constants) {
        int x = SceneCoord.transformToFieldX(event.getSceneX(), constants);
        int y = SceneCoord.transformToFieldY(event.getSceneY(), constants);
        currentGUIState.setXY(x, y);
    }

    private void handleToMouseClickSecondButton() {
        currentGUIState.changeShipOrientation();
        clientGameEngine.changeShipOrientation(currentGUIState);
    }

    private void clearOldShip(int x, int y) {
        if (lastEnemyX == UNDEFINED_COORD || lastEnemyY == UNDEFINED_COORD) {
            lastEnemyX = x;
            lastEnemyY = y;
        } else {
            if (myFieldCoordinateHadBeenChanged(x, y)) {
                clearCanvas(overlayCanvas);
                lastEnemyX = x;
                lastEnemyY = y;
            }
        }
    }

    private void setColorForDrawShip(boolean isDeployable) {
        if (isDeployable) {
            overlayCanvas.setStroke(Color.BLACK);
        } else {
            overlayCanvas.setStroke(Color.RED);
        }
    }

    private boolean myFieldCoordinateHadBeenChanged(int x, int y) {
        return lastMyX != x || lastMyY != y;
    }

    private boolean enemyFieldCoordinateHadBeenChanged(int x, int y) {
        return lastEnemyX != x || lastEnemyY != y;
    }

    private void clearCanvas(GraphicsContext context) {
        context.clearRect(0, 0, Constants.Window.WIDTH, Constants.Window.HEIGHT);
    }

    private void clearOldHitMark(int x, int y) {
        if (lastEnemyX == UNDEFINED_COORD || lastEnemyY == UNDEFINED_COORD) {
            lastEnemyX = x;
            lastEnemyY = y;
        } else {
            if (enemyFieldCoordinateHadBeenChanged(x, y)) {
                clearCanvas(overlayCanvas);
                lastEnemyX = x;
                lastEnemyY = y;
            }
        }
    }
}