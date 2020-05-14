package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.GUI.Constants.Constants;
import com.liver_rus.Battleships.Client.GUI.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.Constants.GUIConstants;
import com.liver_rus.Battleships.Client.GUI.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.DrawEvents.*;
import com.liver_rus.Battleships.Client.GameEngine.ClientGameEngine;
import com.liver_rus.Battleships.Network.NetworkEvent.PlayerType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.liver_rus.Battleships.Network.Server.GamePrimitives.Fleet.NUM_TYPE;
import static com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField.FIELD_SIZE;


public class FXMLDocumentMainController implements Initializable, GUIActions, ClientEngineHolder {
    private static final Logger log = Logger.getLogger(FXMLDocumentMainController.class.getName());
    @FXML
    public MenuItem menuItemSaveShooting;

    @FXML
    private Button shipType4Button;

    @FXML
    private Button shipType3Button;

    @FXML
    private Button shipType2Button;

    @FXML
    private Button shipType1Button;

    @FXML
    private List<Button> shipTypeList;

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
    private Label labelGameStatus;

    @FXML
    private Label playerMeLabel;

    @FXML
    private Label playerEnemyLabel;

    //reset and no button
    @FXML
    private Button rightButton;

    @FXML
    private Button leftButton;

    @FXML
    private ListView<String> statusListView;
    private GraphicsContext overlayCanvas;
    private GraphicsContext mainCanvas;

    private ClientGameEngine clientGameEngine;

    private ShipInfo shipInfo;

    private boolean isShipSelected;
    private boolean isDeploying;
    private boolean isShooting;

    private boolean isSaveShooting;
    private String myName;

    private int[] shipLeftByTypeInit;
    private boolean askRematch;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainCanvas = canvasGeneral.getGraphicsContext2D();
        overlayCanvas = canvasOverlay.getGraphicsContext2D();
        shipInfo = new ShipInfo();
        rightButton.setDisable(true);
        disableShipButtons();
        isShipSelected = false;
        isDeploying = false;
        isShooting = false;
        isSaveShooting = true;
        askRematch = false;
        shipLeftByTypeInit = new int[NUM_TYPE];
        leftButton.managedProperty().bind(leftButton.visibleProperty());
        leftButton.setVisible(false);
    }

    @Override
    public void setEnemyName(String name) {
        Platform.runLater(() -> {
            playerEnemyLabel.setText(name);
            Draw.clearCanvas(mainCanvas);
            statusListView.getItems().clear();
        });
    }

    @Override
    public void setClientEngine(ClientGameEngine clientGameEngine) {
        this.clientGameEngine = clientGameEngine;
    }

    @Override
    public void askRematch() {
        Platform.runLater(() -> {
            askRematch = true;
            labelGameStatus.setText("Do you want rematch?");
            leftButton.setText("Yes");
            leftButton.setVisible(true);
            rightButton.setText("No");
            rightButton.setVisible(true);
            rightButton.setDisable(false);
        });
    }

    @Override
    public void waitSecondPlayer(String reason) {
        Platform.runLater(() -> {
            labelGameStatus.setText("Wait Second Player:" + reason);
            isDeploying = false;
            disableShipButtons();
            menuItemConnect.setDisable(true);
            menuItemDisconnect.setDisable(false);
            rightButton.setDisable(true);
        });
    }

    @Override
    public void deploy(int[] shipLeftByTypeInit) {
        Platform.runLater(() -> {
            isDeploying = true;
            this.shipLeftByTypeInit = shipLeftByTypeInit;
            initShipButtonText(shipLeftByTypeInit);
            labelGameStatus.setText("Deploy fleet. Select and place ship");
            menuItemConnect.setDisable(true);
            menuItemDisconnect.setDisable(false);
            rightButton.setDisable(true);
        });
        enableShipButtons();
    }

    @Override
    public void disconnect() {
        Platform.runLater(() -> {
            isDeploying = false;
            isShooting = false;
            labelGameStatus.setText("Player disconnected");
            menuItemConnect.setDisable(false);
            menuItemDisconnect.setDisable(true);
            leftButton.setVisible(false);
            rightButton.setText("Reset");
            rightButton.setDisable(true);
        });
    }

    @Override
    public void notStartRematch() {
        Platform.runLater(() -> {
            labelGameStatus.setText("Enemy has chosen No rematch");
            menuItemConnect.setDisable(false);
            menuItemDisconnect.setDisable(true);
            rightButton.setDisable(true);
        });
    }

    @Override
    public void startRematch() {
        Platform.runLater(() -> {
            labelGameStatus.setText("Time for rematch!");
            menuItemConnect.setDisable(false);
            menuItemDisconnect.setDisable(true);
            rightButton.setDisable(true);
            Draw.clearCanvas(overlayCanvas);
            Draw.clearCanvas(mainCanvas);
            statusListView.getItems().clear();
        });
    }

    @Override
    public void canShot() {
        Platform.runLater(() -> {
            labelGameStatus.setText("Shoot!!!");
            isShooting = true;
        });
    }

    @Override
    public void hit(RenderHit hitEvent) {
        String fromWho = getFromWho(hitEvent.getPlayerType());
        setGUI(hitEvent, "Hit  ", fromWho, hitEvent.getX(), hitEvent.getY());
    }

    @Override
    public void miss(RenderMiss missEvent) {
        String fromWho = getFromWho(missEvent.getPlayerType());
        setGUI(missEvent, "Miss", fromWho, missEvent.getX(), missEvent.getY());
    }

    @Override
    public void endMatch(PlayerType playerType) {
        String player = getFromWho(playerType);
        setInfo(player + " Win!");
    }

    @FXML
    public void handlerButtonShipType4() {
        handlerButtonShip(4);
    }

    @FXML
    public void handlerButtonShipType3() {
        handlerButtonShip(3);
    }

    @FXML
    public void handlerButtonShipType2() {
        handlerButtonShip(2);
    }

    @FXML
    public void handlerButtonShipType1() {
        handlerButtonShip(1);
    }

    @FXML
    public void handlerButtonShipType0() {
        handlerButtonShip(0);
    }

    @FXML
    public void handlerLeftButton() {
        if (askRematch) {
            //TODO make leftButton invisible
            askRematch = false;
            leftButton.setVisible(false);
            clientGameEngine.rematch(true);
            labelGameStatus.setText("Wait second player");
            rightButton.setText("Reset");
            rightButton.setDisable(true);
        }
    }

    @FXML
    public void handlerRightButton() {
        if (askRematch) { //while askRematch
            askRematch = false;
            rightButton.setDisable(true);
            rightButton.setText("Reset");
            leftButton.setVisible(false);
            clientGameEngine.rematch(false);
        } else { //while deploying
            reset();
        }
    }

    @FXML
    public void handlerToCanvasMouseClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            handlerToMouseClickPrimaryButton(event);
        }
        if (event.getButton().equals(MouseButton.SECONDARY)) {
            handlerToMouseClickSecondButton(event);
        }
    }

    @FXML
    public void handlerOverlayCanvasMouseMoved(MouseEvent event) {
        //TODO delete
//        System.out.println("scene x=" + event.getSceneX() + ", y=" + event.getSceneY());
        if (isDeploying) {
            if (SceneCoord.isFromFirstPlayerField(event)) {
                if (isShipSelected) {
                    setXY(event, FirstPlayerGUIConstants.getGUIConstant());
                    if (isNotIntersectionShipWithBorder(shipInfo)) {
                        drawOverlay(new RenderRedrawShip(shipInfo));
                    }
                }
                return;
            } else {
                Draw.clearCanvas(overlayCanvas);
            }
        }
        if (isShooting) {
            if (SceneCoord.isFromSecondPlayerField(event)) {
                int x = SceneCoord.transformToFieldX(event.getSceneX(), SecondPlayerGUIConstants.getGUIConstant());
                int y = SceneCoord.transformToFieldY(event.getSceneY(), SecondPlayerGUIConstants.getGUIConstant());
                //TODO delete
//                System.out.println("trans x=" + x + ", y=" + y);
                drawOverlay(new RenderRedrawHitEnemy(x, y));
            } else {
                Draw.clearCanvas(overlayCanvas);
            }
        }
    }

    @FXML
    public void handlerMenuItemConnectGame() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
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
                    myName = dialog.getMyName();
                    setLabelMyName(myName);
                    if (dialog.isStartServer()) {
                        try {
                            clientGameEngine.startServer(ip, port);
                        } catch (IOException e) {
                            e.printStackTrace();
                            createAlert("Server doesn't created");
                        }
                    }
                    clientGameEngine.startClient(ip, port);
                    menuItemSaveShooting.setDisable(true);
                    menuItemConnect.setDisable(true);
                    menuItemDisconnect.setDisable(false);
                }
            });
            stage.show();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    private void createAlert(String title) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText("Try another port");
        alert.setContentText("");
        alert.showAndWait();
    }

    @FXML
    public void handlerMenuItemExit() {
        Platform.exit();
    }

    @FXML
    public void handlerMenuItemAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Constants.AboutInfo.ABOUT_GAME_TITLE);
        alert.setHeaderText(Constants.AboutInfo.ABOUT_GAME_HEADER);
        alert.setContentText(Constants.AboutInfo.ABOUT_GAME_TEXT);
        alert.showAndWait();
    }

    @FXML
    public void handlerDisconnectMenuItem() {
        menuItemSaveShooting.setDisable(true);
        clientGameEngine.disconnect();
    }

    @FXML
    public void handlerKeyReleased(KeyEvent keyEvent) {
        if (KeyCode.ESCAPE == keyEvent.getCode()) {
            if (isShipSelected) {
                int oldTypeValue = shipLeftByTypeInit[shipInfo.getType()];
                shipTypeList.get(shipInfo.getType()).setText(oldTypeValue + " x");
                enableShipButtons();
                Draw.clearCanvas(overlayCanvas);
                isShipSelected = false;
            }
        }
    }

    @FXML
    public void handlerMenuItemSaveShooting() {
        if (isSaveShooting) {
            menuItemSaveShooting.setText("Save shooting : ✗");
            isSaveShooting = false;
        } else {
            menuItemSaveShooting.setText("Save shooting : ✓");
            isSaveShooting = true;
        }
    }

    public void draw(DrawGUIEvent guiEvent) {
        Platform.runLater(() -> {
            if (guiEvent instanceof RenderImpossibleDeployShip) {
                drawOverlay(guiEvent);
                return;
            }
            guiEvent.render(mainCanvas);
        });
    }

    public void drawOverlay(DrawGUIEvent guiEvent) {
        guiEvent.render(overlayCanvas);
    }

    public boolean isNotIntersectionShipWithBorder(ShipInfo info) {
        if (info.getX() < 0 || info.getX() >= FIELD_SIZE) return false;
        if (info.getY() < 0 || info.getY() >= FIELD_SIZE) return false;
        if (info.isHorizontal()) {
            return info.getX() + info.getType() < FIELD_SIZE;
        } else {
            return info.getY() + info.getType() < FIELD_SIZE;
        }
    }

    public void reset() {
        Platform.runLater(() -> {
            if (isDeploying) {
                labelGameStatus.setText("Fleet reset");
                Draw.clearCanvas(mainCanvas);
                isShipSelected = false;
                shipInfo = new ShipInfo();
                rightButton.setDisable(true);
                menuItemConnect.setDisable(false);
                statusListView.getItems().clear();
                statusListView.scrollTo(statusListView.getItems().size() - 1);
                clientGameEngine.resetFleet();
                isDeploying = false;
            }
        });
    }

    public boolean isSaveShooting() {
        return isSaveShooting;
    }

    public String getMyName() {
        return myName;
    }

    private String getFromWho(PlayerType playerType) {
        return (playerType == PlayerType.YOU) ? "Enemy" : "    You";
    }

    private void setGUI(DrawGUIEvent event, String action, String fromWho, int x, int y) {
        Platform.runLater(() -> {
            draw(event);

            String infoFormat = fromWho + ": " + action + " " + XYtoGameFormat(x, y);
            setInfo(infoFormat);
            labelGameStatus.setText(fromWho + " " + action);
        });
    }

    //convert coord 00 -> A1
    private static String XYtoGameFormat(int x, int y) {
        int tmpX = x + 1;
        int tmpY = y + 1;
        String strY = tmpY > 0 && tmpY < 27 ? String.valueOf((char) (tmpY + 'A' - 1)) : null;
        return strY + tmpX;
    }

    private void initShipButtonText(int[] amountShipByType) {
        Platform.runLater(() -> {
            for (int i = 0; i < NUM_TYPE; i++) {
                shipTypeList.get(i).setText(amountShipByType[i] + " x");
                shipTypeList.get(i).setDisable(amountShipByType[i] == 0);
            }
        });
    }

    private void handlerButtonShip(int type) {
        isShipSelected = true;
        shipInfo.setShipType(type);
        disableShipButtons();
        int reducedValue = shipLeftByTypeInit[type] - 1;
        shipTypeList.get(type).setText(reducedValue + " x");
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

    private void handlerToMouseClickPrimaryButton(MouseEvent event) {
        if (isDeploying) {
            if (SceneCoord.isFromFirstPlayerField(event)) {
                if (isShipSelected) {
                    if (isNotIntersectionShipWithBorder(shipInfo)) {
                        setXY(event, FirstPlayerGUIConstants.getGUIConstant());
                        clientGameEngine.tryDeployShip(shipInfo);
                        Draw.clearCanvas(overlayCanvas);
                        isDeploying = false;
                        isShipSelected = false;
                        return;
                    }
                }
            }
        }
        if (isShooting) {
            if (SceneCoord.isFromSecondPlayerField(event)) {
                Draw.clearCanvas(overlayCanvas);
                int x = SceneCoord.transformToFieldX(event.getSceneX(), SecondPlayerGUIConstants.getGUIConstant());
                int y = SceneCoord.transformToFieldY(event.getSceneY(), SecondPlayerGUIConstants.getGUIConstant());
                clientGameEngine.shot(x, y);
                isShooting = false;
                labelGameStatus.setText("");
            }
        }
    }

    private void setXY(MouseEvent event, GUIConstants constants) {
        int x = SceneCoord.transformToFieldX(event.getSceneX(), constants);
        int y = SceneCoord.transformToFieldY(event.getSceneY(), constants);
        shipInfo.setXY(x, y);
    }

    private void handlerToMouseClickSecondButton(MouseEvent event) {
        if (isDeploying) {
            if (SceneCoord.isFromFirstPlayerField(event)) {
                if (isShipSelected) {
                    shipInfo.changeShipOrientation();
                    setXY(event, FirstPlayerGUIConstants.getGUIConstant());
                    Draw.clearCanvas(overlayCanvas);
                    if (isNotIntersectionShipWithBorder(shipInfo)) {
                        drawOverlay(new RenderRedrawShip(shipInfo));
                    }
                }
            }
        }
    }

    private void setInfo(String listViewMessage) {
        Platform.runLater(() -> {
            statusListView.getItems().add(listViewMessage);
            statusListView.scrollTo(statusListView.getItems().size() - 1);
        });
    }

    private void setLabelMyName(String name) {
        playerMeLabel.setText(name);
    }

    @FXML
    private void listViewScrollHandler(ScrollEvent scrollEvent) {
        int scrollIndex;
        if (scrollEvent.getDeltaY() > 0) {
            scrollIndex = statusListView.getSelectionModel().getSelectedIndex() - 1;
        } else {
            scrollIndex = statusListView.getSelectionModel().getSelectedIndex() + 1;
        }
        statusListView.getSelectionModel().select(scrollIndex);
    }
}