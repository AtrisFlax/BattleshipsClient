package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.Constants.GUIConstants;
import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GUI.DrawEvents.DrawGUIEvent;
import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderHit;
import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderRedrawHitEnemy;
import com.liver_rus.Battleships.Client.GUI.DrawEvents.RenderRedrawShip;
import com.liver_rus.Battleships.Client.GameEngine.ClientGameEngine;
import com.liver_rus.Battleships.Network.Server.GameServer;
import com.liver_rus.Battleships.NetworkEvent.PlayerType;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.liver_rus.Battleships.Network.Server.GamePrimitives.Fleet.NUM_TYPE;
import static com.liver_rus.Battleships.Network.Server.GamePrimitives.GameField.FIELD_SIZE;


//TODO objects and handles separate to file
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

    @FXML
    private Button resetFleetButton;

    @FXML
    private ListView<String> statusListView;

    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private GraphicsContext overlayCanvas;
    private GraphicsContext mainCanvas;

    private ClientGameEngine clientGameEngine;


    private ShipInfo shipInfo;
    private GameServer gameServer;


    private boolean isShipSelected;
    boolean isDeploying;
    boolean isShooting;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainCanvas = canvasGeneral.getGraphicsContext2D();
        overlayCanvas = canvasOverlay.getGraphicsContext2D();
        shipInfo = new ShipInfo();
        resetFleetButton.setDisable(true);
        disableShipButtons();
        isShipSelected = false;
        isDeploying = false;
        isShooting = false;
    }

    private void setMyName(String name) {
        playerMeLabel.setText(name);
    }

    @Override
    public void setEnemyName(String name) {
        Platform.runLater(() -> { playerEnemyLabel.setText(name); });
    }

    @FXML
    protected void handleButtonShipType4() {
        handleButtonShip(4);
    }

    @FXML
    protected void handleButtonShipType3() {
        handleButtonShip(3);
    }

    @FXML
    protected void handleButtonShipType2() {
        handleButtonShip(2);
    }

    @FXML
    protected void handleButtonShipType1() {
        handleButtonShip(1);
    }

    @FXML
    protected void handleButtonShipType0() {
        handleButtonShip(0);
    }

    @FXML
    public void handleResetFleetButton() {
        reset();
    }

    @FXML
    protected void handleToCanvasMouseClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            handleToMouseClickPrimaryButton(event);
        }
        if (event.getButton().equals(MouseButton.SECONDARY)) {
            handleToMouseClickSecondButton(event);
        }
    }

    @FXML
    protected void handleOverlayCanvasMouseMoved(MouseEvent event) {
        if (isDeploying) {
            if (SceneCoord.isFromFirstPlayerField(event)) {
                if (isShipSelected) {
                    setXY(event, FirstPlayerGUIConstants.getGUIConstant());
                    if (isNotIntersectionShipWithBorder(shipInfo)) {
                        redraw(new RenderRedrawShip(shipInfo));
                    }
                }
                return;
            }
        }
        if (isShooting) {
            if (SceneCoord.isFromSecondPlayerField(event)) {
                int x = SceneCoord.transformToFieldX(event.getSceneX(), SecondPlayerGUIConstants.getGUIConstant());
                int y = SceneCoord.transformToFieldY(event.getSceneY(), SecondPlayerGUIConstants.getGUIConstant());
                redraw(new RenderRedrawHitEnemy(x, y));
            }
        }
    }

    @FXML
    protected void handleMenuItemConnectGame() {
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
                    String myName = dialog.getMyName();
                    setMyName(myName);
                    if (dialog.isStartServer()) {
                        try {
                            gameServer = GameServer.create(ip, port);
                            gameServer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Server doesn't created");
                            alert.setHeaderText("Try another port");
                            alert.setContentText("");
                            alert.showAndWait();
                        }
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
    protected void handleDisconnectMenuItem() {
        clientGameEngine.close();
        gameServer.close();
    }

    @Override
    public void setClientEngine(ClientGameEngine clientGameEngine) {
        this.clientGameEngine = clientGameEngine;
    }

    public void draw(DrawGUIEvent guiEvent) {
        guiEvent.render(mainCanvas);
    }

    public void redraw(DrawGUIEvent guiEvent) {
        guiEvent.render(overlayCanvas);
    }

    @Override
    public void startRematch() {
        statusListView.getItems().clear();
        statusListView.getItems().add("Rematch has started");
        Draw.clearCanvas(mainCanvas);
        Draw.clearCanvas(overlayCanvas);
    }

    @Override
    public void waitSecondPlayer(String reason) {
        isDeploying = false;
        disableShipButtons();
        menuItemConnect.setDisable(true);
        menuItemDisconnect.setDisable(false);
        resetFleetButton.setDisable(true);
        Platform.runLater(() -> statusListView.getItems().add("Waiting:" + reason));
    }

    @Override
    public void deploy(int[] shipLeftByTypeInit) {
        isDeploying = true;
        initShipButtonText(shipLeftByTypeInit);
        Platform.runLater(()->{
            labelGameStatus.setText("Deploying fleet. Select and place ship");
            menuItemConnect.setDisable(true);
            menuItemDisconnect.setDisable(false);
            resetFleetButton.setDisable(true);
        });
        enableShipButtons();
    }

    @Override
    public void disconnect() {
        labelGameStatus.setText("Player disconnected");
        menuItemConnect.setDisable(false);
        menuItemDisconnect.setDisable(true);
        resetFleetButton.setDisable(true);
    }

    @Override
    public void notStartRematch() {
        labelGameStatus.setText("Player choose no rematch");
        menuItemConnect.setDisable(false);
        menuItemDisconnect.setDisable(true);
        resetFleetButton.setDisable(true);
    }

    @Override
    public void canShot() {
        isShooting = true;
    }

    @Override
    public void hit(RenderHit renderHit) {
        draw(renderHit);
        if (renderHit.getPlayerType() == PlayerType.YOU) {
            String info = "YOU: HIT" + renderHit.getX() + renderHit.getY();
            setInfo(info);
        }
        if (renderHit.getPlayerType() == PlayerType.ENEMY) {
            String info = "ENEMY: HIT" + renderHit.getX() + renderHit.getY();
            setInfo(info);
        }
    }

    private void setInfo(String listViewMessage) {
            Platform.runLater(() -> {
                statusListView.getItems().add(listViewMessage);
                statusListView.scrollTo(statusListView.getItems().size() - 1);
            });
    }

    public void reset() {
        if (isDeploying) {
            labelGameStatus.setText("Fleet reset");
            Draw.clearCanvas(mainCanvas);
            Draw.clearCanvas(overlayCanvas);
            isShipSelected = false;
            shipInfo = new ShipInfo();
            resetFleetButton.setDisable(true);
            menuItemConnect.setDisable(false);
            statusListView.getItems().clear();
            statusListView.scrollTo(statusListView.getItems().size() - 1);
            clientGameEngine.resetFleet();
            isDeploying = false;
        }
    }

    private void initShipButtonText(int[] amountShipByType) {
        Platform.runLater(()-> {
            for (int i = 0; i < NUM_TYPE; i++) {
                shipTypeList.get(i).setText(amountShipByType[i] + " x");
                shipTypeList.get(i).setDisable(amountShipByType[i] == 0);
            }

        });
    }

    private void handleButtonShip(int type) {
        isShipSelected = true;
        shipInfo.setShipType(type);
        disableShipButtons();
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

    private void handleToMouseClickPrimaryButton(MouseEvent event) {
        if (isDeploying) {
            if (SceneCoord.isFromFirstPlayerField(event)) {
                if (isShipSelected) {
                    setXY(event, FirstPlayerGUIConstants.getGUIConstant());
                    clientGameEngine.tryDeployShip(shipInfo);
                    Draw.clearCanvas(overlayCanvas);
                }
                isDeploying = false;
                isShipSelected = false;
                return;
            }
        }

        if (isShooting) {
            if (SceneCoord.isFromSecondPlayerField(event)) {
                int x = SceneCoord.transformToFieldX(event.getSceneX(), SecondPlayerGUIConstants.getGUIConstant());
                int y = SceneCoord.transformToFieldY(event.getSceneY(), SecondPlayerGUIConstants.getGUIConstant());
                clientGameEngine.shot(x, y);
                isShooting = false;
            }
        }
    }

    private void setXY(MouseEvent event, GUIConstants constants) {
        int x = SceneCoord.transformToFieldX(event.getSceneX(), constants);
        int y = SceneCoord.transformToFieldY(event.getSceneY(), constants);
        shipInfo.setXY(x, y);
    }

    private void handleToMouseClickSecondButton(MouseEvent event) {
        if (isDeploying) {
            if (SceneCoord.isFromFirstPlayerField(event)) {
                if (isShipSelected) {
                    shipInfo.changeShipOrientation();
                    setXY(event, FirstPlayerGUIConstants.getGUIConstant());
                    Draw.clearCanvas(overlayCanvas);
                    if (isNotIntersectionShipWithBorder(shipInfo)) {
                        redraw(new RenderRedrawShip(shipInfo));
                    }
                }
            }
        }
    }

    public void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
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
}