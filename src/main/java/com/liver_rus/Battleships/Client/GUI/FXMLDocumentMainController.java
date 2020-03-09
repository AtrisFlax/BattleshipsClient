package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.Constants.GUIConstants;
import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.DrawEvents.DrawGUIEvent;
import com.liver_rus.Battleships.Client.GameEngine.ClientGameEngine;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import com.liver_rus.Battleships.Network.Server.GameServerThread;
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

    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private GraphicsContext overlayCanvas;
    private GraphicsContext mainCanvas;

    private ClientGameEngine clientGameEngine;
    private boolean isShipSelected;

    private GUIState currentGUIState;
    private GameServerThread gameServer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainCanvas = canvasGeneral.getGraphicsContext2D();
        overlayCanvas = canvasOverlay.getGraphicsContext2D();
        isShipSelected = false;
        currentGUIState = new GUIState();
        resetFleetButton.setDisable(true);
        disableShipButtons();
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
            FXMLDocumentConnectGame dialog = fxmlLoader.getController();
            dialog.setIPAndPortFields();
            dialog.setMyName();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setOnHiding(close -> {
                if (dialog.isStartClient()) {
                    String ip = dialog.getHost();
                    int port = dialog.getPort();
                    if (dialog.isStartServer()) {
                        gameServer.startThread(ip, port); //with stop start interface (server and client)
                    }
                    clientGameEngine.startNetwork(ip, port, dialog.getMyName());
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
    protected void handlerDisconnectMenuItem() {
        clientGameEngine.disconnect();
        gameServer.disconnect();
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

    @Override
    public void unlockDeploying() {
        isShipSelected = false;
        enableShipButtons();
    }

    @Override
    public void setLockGUI() {
        resetFleetButton.setDisable(true);
        disableShipButtons();
        labelGameStatus.setText("Fleet is deployed. Waiting for second player...");
    }

    public void draw(DrawGUIEvent guiEvent) {
        guiEvent.render(mainCanvas);
    }

    public void redraw(DrawGUIEvent guiEvent) {
        guiEvent.render(overlayCanvas);
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
    public void setInfo(String labelMessage, String listViewMessage) {
        if () {

        }
        Platform.runLater(() -> labelGameStatus.setText(labelMessage));
        if (!listViewMessage.equals("")) {
            Platform.runLater(() -> {
                statusListView.getItems().add(listViewMessage);
                statusListView.scrollTo(statusListView.getItems().size() - 1);
            });
        }
    }

    @Override
    public void reset(String resetReason) {
        Draw.clearCanvas(mainCanvas);
        Draw.clearCanvas(overlayCanvas);
        isShipSelected = false;
        currentGUIState = new GUIState();
        disableShipButtons();
        initShipButtonText();
        resetFleetButton.setDisable(true);
        menuItemConnect.setDisable(false);
        statusListView.getItems().clear();
        statusListView.getItems().add(resetReason);
        statusListView.scrollTo(statusListView.getItems().size() - 1);
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
        clientGameEngine.fleetDeploying(currentGUIState);
    }

    public void setGameServer(GameServerThread gameServer) {
        this.gameServer = gameServer;
    }
}