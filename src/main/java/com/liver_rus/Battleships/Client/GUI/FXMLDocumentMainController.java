package com.liver_rus.Battleships.Client.GUI;

import com.liver_rus.Battleships.Client.Constants.Constants;
import com.liver_rus.Battleships.Client.Constants.FirstPlayerGUIConstants;
import com.liver_rus.Battleships.Client.Constants.SecondPlayerGUIConstants;
import com.liver_rus.Battleships.Client.GameEngine.ClientGameEngine;
import com.liver_rus.Battleships.Client.GamePrimitives.FieldCoord;
import com.liver_rus.Battleships.Client.GamePrimitives.Ship;
import com.liver_rus.Battleships.Client.GamePrimitives.WrongShipInfoSizeException;
import com.liver_rus.Battleships.Network.Client;
import com.liver_rus.Battleships.Network.GameServer;
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

public class FXMLDocumentMainController implements Initializable {
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

    private ClientGameEngine gameEngine;

    private Client network;

    private Thread serverThread;

    private GraphicsContext overlayCanvas;
    private GraphicsContext mainCanvas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initGUI();
        gameEngine = new ClientGameEngine();
    }

    //TODO в отдельный файл со стачискем методом handleOverlayCanvasMouseMoved

    //Отрисовка при передвижени курсора
    @FXML
    void handleOverlayCanvasMouseMoved(MouseEvent event) {
        if (gameEngine.getGamePhase() == ClientGameEngine.Phase.DEPLOYING_FLEET) {
            drawDeployingShip(event);
        }

        if (gameEngine.getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
            drawShotLineOnEnemyField(event);
        }
    }

    @FXML
    void handleResetFleetButton() {
        gameEngine.reset();
        resetFleetButton.setDisable(true);
        clearCanvas(overlayCanvas);
        clearCanvas(mainCanvas);
        int[] amountShipByType = gameEngine.getShipsLeftByType();
        shipType4Button.setText(amountShipByType[4] + " x");
        shipType3Button.setText(amountShipByType[3] + " x");
        shipType2Button.setText(amountShipByType[2] + " x");
        shipType1Button.setText(amountShipByType[1] + " x");
        shipType0Button.setText(amountShipByType[0] + " x");
    }

    @FXML
    void handleToMouseClick(MouseEvent event) {
        handleToMouseClickSecondButton(event);
        handleToMouseClickPrimaryButton(event);
    }

    @FXML
    void handleMenuItemConnectGame() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            handleResetFleetButton();
            menuItemConnect.setDisable(true);
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
            stage.setOnHidden(close -> {
                String ip = controller.getHost();
                int port = controller.getPort();
                if (ip != null && port != 0) {
                    //create server
                    if (controller.isStartServer()) {
                        try {
                            serverThread = new Thread(new GameServer(ip, port));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        serverThread.start();
                    }
                    //create client
                    try {
                        network = new Client(ip, port);
                        network.subscribeForInbox((message) -> {
                            log.info("Client inbox message " + message);
                            gameEngine.proceedMessage(message); //must be before gui
                            guiProceed(message);
                        });
                    } catch (IOException e) {
                        statusListView.getItems().add("Fail to make connection");
                        log.log(Level.SEVERE, "Fail to make connection", e);
                    }
                    gameEngine.setGamePhase(ClientGameEngine.Phase.DEPLOYING_FLEET);
                    labelGameStatus.setText("Deploying fleet. Select and place ship");
                    //auto deployment ships for debug
                    debugShipsDeployment();
                } else {
                    close.consume();
                }
                playerMyLabel.setText(controller.getMyName());
            });
            stage.show();
            numRoundLabel.setText(Integer.toString(1));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    @FXML
    void handleMenuItemExit() {
        Platform.exit();
    }

    @FXML
    void handleMenuItemAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Constants.AboutInfo.ABOUT_GAME_TITLE);
        alert.setHeaderText(Constants.AboutInfo.ABOUT_GAME_HEADER);
        alert.setContentText(Constants.AboutInfo.ABOUT_GAME_TEXT);
        alert.showAndWait();
    }

    @FXML
    void handleDisconnectMenuItem() {
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    //auto deployment for debug
    public void debugShipsDeployment() {
        gameEngine.addShipOnField(Ship.create(new FieldCoord(1, 8), Ship.Type.SUBMARINE, true));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(3, 2), Ship.Type.SUBMARINE, true));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(1, 1), Ship.Type.DESTROYER, false));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(3, 4), Ship.Type.DESTROYER, true));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(2, 6), Ship.Type.CRUISER, true));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(7, 4), Ship.Type.BATTLESHIP, false));
        gameEngine.addShipOnField(Ship.create(new FieldCoord(9, 1), Ship.Type.AIRCRAFT_CARRIER, false));
        for (Ship ship : gameEngine.getShips()) {
            Draw.ShipOnMyField(mainCanvas, ship);
        }
        gameEngine.setGamePhase(ClientGameEngine.Phase.FLEET_IS_DEPLOYED);
        gameEngine.getGameField().printOnConsole();
        network.sendMessage(gameEngine.getShipsInfoForSend());
        resetFleetButton.setDisable(true);
        labelGameStatus.setText("Fleet is deployed. Waiting for second player");
    }

    private void handleToMouseClickPrimaryButton(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            //Размещение корабля, после выбора
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.DEPLOYING_FLEET) {
                deployShip(event);
            }
            //выстрел в поле противника
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
                shootToEnemyField(event);
            }
        }
    }

    private void deployShip(MouseEvent event) {
        if (isShipSelectedAndNotAllDeployed(event)) {
            resetFleetButton.setDisable(false);
            if (isPoissibleLocateShipAndNotAllShipsDeployed()) {
                gameEngine.addShipOnField(Ship.create(gameEngine.getCurrentGUIState()));
                Draw.ShipOnMyField(mainCanvas, gameEngine.getCurrentGUIState());
                gameEngine.setShipSelected(false);
            }
        }
        //Флот расставлен. Объявление о готовности игрока
        checkAndSetFleetIsDeployed();
    }

    private boolean isPoissibleLocateShipAndNotAllShipsDeployed() {
        return gameEngine.isPossibleLocateShip() &&
                gameEngine.isNotAllShipsDeployed();
    }

    private boolean isShipSelectedAndNotAllDeployed(MouseEvent event) {
        return SceneCoord.isFromFirstPlayerField(event) &&
                gameEngine.isShipSelected() && gameEngine.isNotAllShipsDeployed();
    }

    private void shootToEnemyField(MouseEvent event) {
        if (SceneCoord.isFromSecondPlayerField(event)) {
            FieldCoord shootFieldCoord = new FieldCoord(event, SecondPlayerGUIConstants.getGUIConstant());
            Draw.MissCellOnEnemyField(mainCanvas, shootFieldCoord);
            network.sendMessage(Constants.NetworkMessage.SHOT + shootFieldCoord);
            gameEngine.setGamePhase(ClientGameEngine.Phase.WAITING_ANSWER);
            gameEngine.setShootCoord(shootFieldCoord);
        }
    }

    private void checkAndSetFleetIsDeployed() {
        if (!gameEngine.isShipSelected() && gameEngine.NoMoreShipLeft()) {
            gameEngine.setGamePhase(ClientGameEngine.Phase.FLEET_IS_DEPLOYED);
            network.sendMessage(gameEngine.getShipsInfoForSend());
            resetFleetButton.setDisable(true);
            labelGameStatus.setText("Fleet is deployed. Waiting for second player...");
        }
    }

    //меняем расположения корабля с горизонтального на вертикальное (или наоборот) на правую клавишу мыши
    private void handleToMouseClickSecondButton(MouseEvent event) {
        if (event.getButton().equals(MouseButton.SECONDARY)) {
            if (gameEngine.getShipType() != Ship.Type.SUBMARINE) {
                gameEngine.changeShipOrientation();
                overlayCanvas.clearRect(0, 0, Constants.Window.WIDTH, Constants.Window.HEIGHT);
                if (gameEngine.isNotIntersectionShipWithBorder() && gameEngine.isPossibleLocateShip()) {
                    Draw.ShipOnMyField(overlayCanvas, gameEngine.getCurrentGUIState());
                }
            }
        }
    }

    private void guiProceed(String message) {
        Platform.runLater(() -> {
            String readableString = convertInboxToReadableView(message);
            if (readableString != null) {
                statusListView.getItems().add(convertInboxToReadableView(message));
                statusListView.scrollTo(statusListView.getItems().size() - 1);
            }
        });
        if (message.startsWith(Constants.NetworkMessage.HIT)) {
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                Draw.HitCellOnEnemyField(mainCanvas, gameEngine.getShootCoord());
            }
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                Draw.HitCellOnMyField(mainCanvas, gameEngine.getShootCoord());
            }
            return;
        }
        if (message.startsWith(Constants.NetworkMessage.MISS)) {
            //on enemy field miss [/] auto placed by gui handler
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                Draw.MissCellOnMyField(mainCanvas, gameEngine.getShootCoord());
            }
            return;
        }
        if (message.startsWith(Constants.NetworkMessage.DESTROYED)) {
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                Ship ship = null;
                try {
                    ship = Ship.create(message.replace(Constants.NetworkMessage.DESTROYED, ""));

                } catch (WrongShipInfoSizeException | IOException e) {
                    e.printStackTrace();
                }
                Draw.ShipOnEnemyField(mainCanvas, ship);
            }
            //on my field all ships (frame) already has drawn
            return;
        }
        switch (message) {
            case Constants.NetworkMessage.YOU_TURN:
                Platform.runLater(() -> labelGameStatus.setText("Make shoot. Take coordinate"));
                return;
            case Constants.NetworkMessage.ENEMY_TURN:
                Platform.runLater(() -> labelGameStatus.setText("Enemy turn. Waiting..."));
                return;
            case Constants.NetworkMessage.YOU_WIN:
                Platform.runLater(() -> labelGameStatus.setText("You Win!!!"));
                return;
            case Constants.NetworkMessage.YOU_LOSE:
                Platform.runLater(() -> labelGameStatus.setText("You Lose!!!"));
                return;
            //TODO Player name exchange
            /*case Constants.NetworkMessage.isEnemyName:
                Platform.runLater(() -> labelGameStatus.setText("You Lose!!!"));
                return;*/
        }
    }

    private String convertInboxToReadableView(String message) {
        //HITXX
        if (message.startsWith(Constants.NetworkMessage.HIT)) {
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                return gameEngine.getShootCoord().toGameFormat() + " Enemy Ship has Hit";
            }
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                return gameEngine.getShootCoord().toGameFormat() + " You Ship has Hit";
            }
        }
        //MISSXX
        if (message.startsWith(Constants.NetworkMessage.MISS)) {
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                return gameEngine.getShootCoord().toGameFormat() + " You Missed";
            }
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                return gameEngine.getShootCoord().toGameFormat() + " Enemy Missed";
            }
        }
        //DESTROYEDXX
        if (message.startsWith(Constants.NetworkMessage.DESTROYED)) {
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
                return "You Destroy Enemy Ship";
            }

            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                return "Enemy Destroy Your Ship";
            }
        }
        switch (message) {
            case Constants.NetworkMessage.YOU_WIN:
                return "You Win";
            case Constants.NetworkMessage.YOU_LOSE:
                return "You Lose";
            case Constants.NetworkMessage.DISCONNECT:
                return "Disconnect";
            /*Excessive output
            case Constants.NetworkMessage.YOU_TURN:
                return "You Turn";
            case Constants.NetworkMessage.ENEMY_TURN:
                return "Enemy Turn"; */
        }
        return null;
    }

    private void drawDeployingShip(MouseEvent event) {
        gameEngine.setCurrentState(
                new FieldCoord(event, FirstPlayerGUIConstants.getGUIConstant()),
                gameEngine.getShipType(),
                gameEngine.getShipOrientation()
        );
        if (isPossibleDrawShip(event)) {
            clearOldShipDrawing();
            setColorForDrawShip();
            Draw.ShipOnMyField(overlayCanvas, gameEngine.getCurrentGUIState());
        } else {
            clearCanvas(overlayCanvas);
        }
    }

    private void clearOldShipDrawing() {
        if (isCoordinateHadNotBeenChanged() && !gameEngine.isFirstChangeFieldCoordMyField()) {
            clearCanvas(overlayCanvas);
            gameEngine.setLastMyFieldCoord();
        } else {
            gameEngine.setIsFirstChangeFieldCoordMyField(false);
        }
    }

    private boolean isCoordinateHadNotBeenChanged() {
        FieldCoord newCoord = gameEngine.getCurrentGUIState().getFieldCoord();
        FieldCoord oldCoord = gameEngine.getLastMyFieldCoord();
        return newCoord.equals(oldCoord);
    }

    private boolean isCoordinateHadNotBeenChanged(FieldCoord newCoord) {
        FieldCoord oldCoord = gameEngine.getLastMyFieldCoord();
        return newCoord.equals(oldCoord);
    }

    private boolean isPossibleDrawShip(MouseEvent event) {
        return gameEngine.isShipSelected()
                && SceneCoord.isFromFirstPlayerField(event)
                && gameEngine.isNotIntersectionShipWithBorder();
    }

    private void setColorForDrawShip() {
        if (gameEngine.isPossibleLocateShip()) {
            overlayCanvas.setStroke(Color.BLACK);
        } else {
            overlayCanvas.setStroke(Color.RED);
        }
    }

    // Отрисовка линнии по указанной мышью координате на поле противника
    private void drawShotLineOnEnemyField(MouseEvent event) {
        if (SceneCoord.isFromSecondPlayerField(event)) {
            FieldCoord currentSceneCoord = new FieldCoord(event, SecondPlayerGUIConstants.getGUIConstant());
            overlayCanvas.setStroke(Color.BLACK);
            clearOldShotLineDrawing(currentSceneCoord);
            Draw.MissCellOnEnemyField(overlayCanvas, currentSceneCoord);
        }
    }

    private void clearOldShotLineDrawing(FieldCoord currentSceneCoord) {
        if (isCoordinateHadNotBeenChanged(currentSceneCoord) && !gameEngine.isFirstChangeFieldCoordEnemyField()) {
            clearCanvas(overlayCanvas);
            gameEngine.setLastEnemyFieldCoord(currentSceneCoord);
        } else {
            gameEngine.setFirstChangeFieldCoordEnemyField(false);
        }
    }

    private void clearCanvas(GraphicsContext context) {
        context.clearRect(0, 0, Constants.Window.WIDTH, Constants.Window.HEIGHT);
    }

    private void initGUI() {
        resetFleetButton.setDisable(true);
        mainCanvas = canvasGeneral.getGraphicsContext2D();
        overlayCanvas = canvasOverlay.getGraphicsContext2D();
        setupDeployShipButtonBehavior(shipType4Button, Ship.Type.AIRCRAFT_CARRIER);
        setupDeployShipButtonBehavior(shipType3Button, Ship.Type.BATTLESHIP);
        setupDeployShipButtonBehavior(shipType2Button, Ship.Type.CRUISER);
        setupDeployShipButtonBehavior(shipType1Button, Ship.Type.DESTROYER);
        setupDeployShipButtonBehavior(shipType0Button, Ship.Type.SUBMARINE);
    }

    private void setupDeployShipButtonBehavior(Button button, Ship.Type type) {
        button.setOnMouseClicked(event -> {
                    if (gameEngine.getGamePhase() == ClientGameEngine.Phase.DEPLOYING_FLEET) {
                        button.setDisable(false);
                        if (!gameEngine.getShipSelected()) {
                            int leftShipAmountByType = gameEngine.selectShip(type);
                            button.setText(leftShipAmountByType + "  x");
                        }
                    }
                }
        );
    }
}