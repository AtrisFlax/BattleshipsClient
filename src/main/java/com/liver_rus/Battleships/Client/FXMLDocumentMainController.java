package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Network.Client;
import com.liver_rus.Battleships.Network.Server;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import java.util.ArrayList;
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

    private ArrayList<Button> shipTypeButtons;

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

    private ObservableList<String> networkInbox;

    private FieldCoord lastMyFieldCoord;
    private FieldCoord lastEnemyFieldCoord;
    private GraphicsContext overlayCanvas;
    private GraphicsContext mainCanvas;
    private boolean isFirstChangeLastFieldCoord = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initNetwork();
        initGUI();
        initGameEngine();
    }

    private void initNetwork() {
        networkInbox = FXCollections.observableArrayList();
        networkInbox.addListener((ListChangeListener<String>) listener -> {
            String received_msg = networkInbox.get(networkInbox.size() - 1);
            log.info("Client Message receive: " + received_msg);
            try {
                proceedMessage(received_msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            networkInbox.clear();
        });
    }

    private void initGameEngine() {
        gameEngine = new ClientGameEngine();
        //TODO lastMyFieldCoord lastEnemyFieldCoord move on GameEngine class
        lastMyFieldCoord = new FieldCoord((byte) Constants.NONE_SELECTED_FIELD_COORD, (byte) Constants.NONE_SELECTED_FIELD_COORD);
        lastEnemyFieldCoord = new FieldCoord((byte) Constants.NONE_SELECTED_FIELD_COORD, (byte) Constants.NONE_SELECTED_FIELD_COORD);
    }

    private void initGUI() {
        resetFleetButton.setDisable(true);
        mainCanvas = canvasGeneral.getGraphicsContext2D();
        overlayCanvas = canvasOverlay.getGraphicsContext2D();
        setupShipButtonBehavior(shipType4Button, Ship.Type.AIRCRAFT_CARRIER);
        setupShipButtonBehavior(shipType3Button, Ship.Type.BATTLESHIP);
        setupShipButtonBehavior(shipType2Button, Ship.Type.CRUISER);
        setupShipButtonBehavior(shipType1Button, Ship.Type.DESTROYER);
        setupShipButtonBehavior(shipType0Button, Ship.Type.SUBMARINE);
        shipTypeButtons = new ArrayList<>();
        shipTypeButtons.add(shipType0Button);
        shipTypeButtons.add(shipType1Button);
        shipTypeButtons.add(shipType2Button);
        shipTypeButtons.add(shipType3Button);
        shipTypeButtons.add(shipType4Button);
    }

    private void setupShipButtonBehavior(Button button, Ship.Type type) {
        button.setOnMouseClicked(event -> {
                    if (gameEngine.getGamePhase() == ClientGameEngine.Phase.DEPLOYING_FLEET) {
                        button.setDisable(false);
                        if (!gameEngine.getShipSelected()) {
                            int popedShip = gameEngine.selectShip(type);
                            button.setText(popedShip + "  x");
                        }
                    }
                }
        );
    }

    //Отрисовка при передвижени курсора
    @FXML
    void handleOverlayCanvasMouseMoved(MouseEvent event) {
        if (gameEngine.getGamePhase() == ClientGameEngine.Phase.DEPLOYING_FLEET) {
            gameEngine.setCurrentState(
                    new FieldCoord(
                            event.getSceneX(), event.getSceneY(), new FirstPlayerGUIConstants()),
                    gameEngine.getShipType(),
                    gameEngine.getShipOrientation()
            );
            if (gameEngine.isShipSelected() &&
                    SceneCoord.isFromFirstPlayerField(event.getSceneX(), event.getSceneY()) &&
                    gameEngine.getGameField().isNotIntersectionShipWithBorder(gameEngine.getCurrentGUIState())) {
                if (gameEngine.getGameField().isPossibleLocateShip(gameEngine.getCurrentGUIState())) {
                    overlayCanvas.setStroke(Color.BLACK);
                    Draw.ShipOnMyField(overlayCanvas, gameEngine.getCurrentGUIState());
                } else {
                    overlayCanvas.setStroke(Color.RED);
                    Draw.ShipOnMyField(overlayCanvas, gameEngine.getCurrentGUIState());
                }
                if (lastMyFieldCoord.equals(gameEngine.getCurrentGUIState().getFieldCoord()) && !isFirstChangeLastFieldCoord) {
                    clearCanvas(overlayCanvas);
                    lastMyFieldCoord = gameEngine.getCurrentGUIState().getFieldCoord();
                } else {
                    isFirstChangeLastFieldCoord = false;
                }
            } else {
                clearCanvas(overlayCanvas);
            }
        }

        // Отрисовка линнии по указанной мышью координате на поле противника
        if (gameEngine.getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
            if (SceneCoord.isFromSecondPlayerField(event.getSceneX(), event.getSceneY())) {
                overlayCanvas.setStroke(Color.BLACK);
                FieldCoord fieldCoord = new FieldCoord(event.getSceneX(), event.getSceneY(), new SecondPlayerGUIConstants());
                Draw.MissCellOnEnemyField(overlayCanvas, fieldCoord);
                if (lastEnemyFieldCoord.equals(fieldCoord) && !isFirstChangeLastFieldCoord) {
                    clearCanvas(overlayCanvas);
                    lastEnemyFieldCoord = fieldCoord;
                } else {
                    isFirstChangeLastFieldCoord = false;
                }
            }
        }
    }

    private void clearCanvas(GraphicsContext context) {
        context.clearRect(0, 0, Constants.Window.WIDTH, Constants.Window.HEIGHT);
    }

    @FXML
    void handleResetFleetButton() {
        gameEngine.reset();
        resetFleetButton.setDisable(true);
        clearCanvas(overlayCanvas);
        clearCanvas(mainCanvas);
        shipType4Button.setText("1  x");
        shipType3Button.setText("1  x");
        shipType2Button.setText("1  x");
        shipType1Button.setText("2  x");
        shipType0Button.setText("2  x");
    }

    @FXML
    void handleToMouseClick(MouseEvent event) {
        //меняем расположения корабля с горизонтального на вертикальное (или наоборот) на среднюю клавишу мыши
        if (event.getButton().equals(MouseButton.MIDDLE)) {
            if (gameEngine.getShipType() != Ship.Type.SUBMARINE) {
                gameEngine.getCurrentGUIState().changeShipOrientation();
//                System.out.println("Current state MouseButton.MIDDLE" + gameEngine.getCurrentState());
                //перерисовываем после смены
                overlayCanvas.clearRect(0, 0, Constants.Window.WIDTH, Constants.Window.HEIGHT);
                if (gameEngine.getGameField().isNotIntersectionShipWithBorder(gameEngine.getCurrentGUIState()) &&
                        gameEngine.getGameField().isPossibleLocateShip(gameEngine.getCurrentGUIState())) {
                    Draw.ShipOnMyField(overlayCanvas, gameEngine.getCurrentGUIState());
                }
            }
        }

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            //Размещение корабля, после выбора
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.DEPLOYING_FLEET) {
                if (SceneCoord.isFromFirstPlayerField(event.getSceneX(), event.getSceneY()) &&
                        gameEngine.isShipSelected() &&
                        gameEngine.getGameField().getFleet().getShipsLeft() >= 0) {
                    resetFleetButton.setDisable(false);
                    if (gameEngine.getGameField().isPossibleLocateShip(gameEngine.getCurrentGUIState()) &&
                            gameEngine.getGameField().getFleet().getShipsLeft() >= 0) {
                        gameEngine.addShipOnField(Ship.createShip(gameEngine.getCurrentGUIState()));
                        Draw.ShipOnMyField(mainCanvas, gameEngine.getCurrentGUIState());
                        gameEngine.setShipSelected(false);
                    }
                }
                //Флот расставлен. Объявление о готовности игрока.. ################handleToMouseClick###################
                if (!gameEngine.isShipSelected() && gameEngine.getGameField().getFleet().getShipsLeft() == 0) {
                    gameEngine.setGamePhase(ClientGameEngine.Phase.FLEET_IS_DEPLOYED);
                    network.sendMessage(Constants.NetworkMessage.SEND_SHIPS.toString() + gameEngine.getShipsInfoForSend());
                    resetFleetButton.setDisable(true);
                    labelGameStatus.setText("Fleet is deployed. Waiting for second player...");
                }
            }
            //выстрел в поле противника
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
                if (SceneCoord.isFromSecondPlayerField(event.getSceneX(), event.getSceneY())) {
                    FieldCoord shootFieldCoord = new FieldCoord(event.getSceneX(), event.getSceneY(), new SecondPlayerGUIConstants());
                    Draw.MissCellOnEnemyField(mainCanvas, shootFieldCoord);
                    network.sendMessage(Constants.NetworkMessage.SHOT.toString() + shootFieldCoord);
                    gameEngine.setGamePhase(ClientGameEngine.Phase.WAITING_ANSWER);
                    gameEngine.setShootCoord(shootFieldCoord);
                }
            }
        }
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
                String host = controller.getHost();
                String port = controller.getPort();
                if (host != null && port != null) {
                    network = new Client(networkInbox, host, Integer.parseInt(port));
                    try {
                        network.makeConnection();
                    } catch (IOException e) {
                        statusListView.getItems().add("Fail to make connection");
                        log.log(Level.SEVERE, "Fail to make connection", e);
                    }
                    if (controller.isServerSelected()) {
                        try {
                            serverThread = new Thread(new Server(Integer.parseInt(port)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        serverThread.start();
                    }
                    gameEngine.setGamePhase(ClientGameEngine.Phase.DEPLOYING_FLEET);
                    labelGameStatus.setText("Deploying fleet. Select and place ship");
                    //auto deployment ships for debug
                    //testShipsDeployment();
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
        network.close();
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    private void proceedMessage(String message) throws IOException {
        log.info("Client inbox message " + message);
        gameEngine.proceedMessage(message); //must be before gui
        guiProceed(message);
    }

    private void guiProceed(String message) throws IOException {
        Platform.runLater(() -> {
            String readableString = convertInboxToReadableView(message);
            if (readableString != null) {
                statusListView.getItems().add(convertInboxToReadableView(message));
                statusListView.scrollTo(statusListView.getItems().size() - 1);
            }
        });

        if (MessageProcessor.isHit(message)) {
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                Draw.HitCellOnEnemyField(mainCanvas, gameEngine.getShootCoord());
            }
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                Draw.HitCellOnMyField(mainCanvas, gameEngine.getShootCoord());
            }
            return;
        }

        if (MessageProcessor.isMiss(message)) {
            //Miss [/] auto placed by gui handler
            //  if(gameEngine.getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
            //      Draw.MissCellOnEnemyField(mainCanvas, gameEngine.getShootCoord());
            //  }
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                Draw.MissCellOnMyField(mainCanvas, gameEngine.getShootCoord());
            }
            return;
        }

        if (MessageProcessor.isDestroyed(message)) {
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                Draw.ShipOnEnemyField(mainCanvas, new DrawAdapterShip(
                        Ship.createShip(message.replace(Constants.NetworkMessage.DESTROYED.toString(), ""))));
            }
            //on my field all ships(frame) already has drawn
            //  if (gameEngine.getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
            //      Draw.ShipOnMyField(mainCanvas, Ship.createShip(message));
            //  }
            return;
        }

        if (MessageProcessor.isYouTurn(message)) {
            Platform.runLater(() -> labelGameStatus.setText("Make shoot. Take coordinate"));
            return;
        }

        if (MessageProcessor.isEnemyTurn(message)) {
            Platform.runLater(() -> labelGameStatus.setText("Enemy turn. Waiting..."));
            return;
        }

        if (MessageProcessor.isYouWin(message)) {
            Platform.runLater(() -> labelGameStatus.setText("You Win!!!"));
            return;
        }

        if (MessageProcessor.isYouLose(message)) {
            Platform.runLater(() -> labelGameStatus.setText("You Lose!!!"));
        }

        //TODO Player name exchange
        //if (MessageProcessor.isEnemyName(message)) {
        //    Platform.runLater(() -> labelGameStatus.setText("You Lose!!!"));
        //    return;
        //}

    }

    private String convertInboxToReadableView(String message) {
        //HITXX
        if (MessageProcessor.isHit(message)) {
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                return gameEngine.getShootCoord().toGameFormat() + " Enemy Ship has Hit";
            }
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                return gameEngine.getShootCoord().toGameFormat() + " You Ship has Hit";
            }
        }
        //MISSXX
        if (MessageProcessor.isMiss(message)) {
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.WAITING_ANSWER) {
                return gameEngine.getShootCoord().toGameFormat() + " You Missed";
            }
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                return gameEngine.getShootCoord().toGameFormat() + " Enemy Missed";
            }
        }
        //DESTROYEDXX
        if (MessageProcessor.isDestroyed(message)) {
            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.MAKE_SHOT) {
                return "You Destroy Enemy Ship";
            }

            if (gameEngine.getGamePhase() == ClientGameEngine.Phase.TAKE_SHOT) {
                return "Enemy Destroy Your Ship";
            }
        }
//        if (MessageProcessor.isYouTurn(message)) {
//            return "You Turn";
//        }
//
//        if (MessageProcessor.isEnemyTurn(message)) {
//            return "Enemy Turn";
//        }
        if (MessageProcessor.isYouWin(message)) {
            return "You Win";
        }
        if (MessageProcessor.isYouLose(message)) {
            return "You Lose";
        }
        if (MessageProcessor.isDisconnect(message)) {
            return "Disconnect";
        }
        return null;
    }

    //auto deployment for debug
    public void testShipsDeployment() {
        ArrayList<Ship> ships = new ArrayList<>();
        ships.add(Ship.createShip(new FieldCoord(1, 8), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        ships.add(Ship.createShip(new FieldCoord(3, 2), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        ships.add(Ship.createShip(new FieldCoord(1, 1), Ship.Type.DESTROYER, Ship.Orientation.VERTICAL));
        ships.add(Ship.createShip(new FieldCoord(3, 4), Ship.Type.DESTROYER, Ship.Orientation.HORIZONTAL));
        ships.add(Ship.createShip(new FieldCoord(2, 6), Ship.Type.CRUISER, Ship.Orientation.HORIZONTAL));
        ships.add(Ship.createShip(new FieldCoord(7, 4), Ship.Type.BATTLESHIP, Ship.Orientation.VERTICAL));
        ships.add(Ship.createShip(new FieldCoord(9, 1), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.VERTICAL));
        for (Ship ship : ships) {
            gameEngine.addShipOnField(ship);
        }
        for (Ship ship : ships) {
            Draw.ShipOnMyField(mainCanvas, new DrawAdapterShip(ship));
        }
        gameEngine.setGamePhase(ClientGameEngine.Phase.FLEET_IS_DEPLOYED);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gameEngine.getGameField().printOnConsole();
        network.sendMessage(Constants.NetworkMessage.SEND_SHIPS.toString() + gameEngine.getShipsInfoForSend());
        resetFleetButton.setDisable(true);
        labelGameStatus.setText("Fleet is deployed. Waiting for second player");
    }
}


