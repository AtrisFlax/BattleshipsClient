package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.SocketFX.GenericSocket;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FXMLDocumentMainController implements Initializable{ //Controller

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

    private int numRound;

    private ObservableList<String> rcvdMsgsData;
    private ObservableList<String> sentMsgsData;


    private final static Logger LOGGER
            = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final static int NONE_SELECTED = -1;

    private GameEngine gameEngine; //model

    public enum ConnectionDisplayState {
        DISCONNECTED, ATTEMPTING, CONNECTED
    }

    private FieldCoord lastMyFieldCoord;
    private FieldCoord lastEnemyFieldCoord;

    private GraphicsContext canvasOverlayGraphicsContext;
    private GraphicsContext canvasGeneralGraphicsContext;

    private boolean isFirstChangeLastFieldCoord = true;
    private boolean isServerReadyFirst = true;

    private Network network;

    private boolean isClient;

    private void isClient(boolean isClient) {
        this.isClient = isClient;
    }

    private boolean getIsClient() {
        return isClient;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        network = new Network();

        network.setIsConnected(false);
        resetFleetButton.setDisable(true);

        sentMsgsData = FXCollections.observableArrayList();
        statusListView.setItems(sentMsgsData);
        statusListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        rcvdMsgsData = FXCollections.observableArrayList();
        rcvdMsgsData.addListener((ListChangeListener<String>) listener -> {
            resolveSocketAndProceedMassage(rcvdMsgsData.get(rcvdMsgsData.size() - 1));
        });
        statusListView.setItems(rcvdMsgsData);
        statusListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        lastMyFieldCoord = new FieldCoord((byte) NONE_SELECTED, (byte) NONE_SELECTED);
        lastEnemyFieldCoord = new FieldCoord((byte) NONE_SELECTED, (byte) NONE_SELECTED);

        gameEngine = new GameEngine();
        gameEngine.setPhase(GameEngine.Phase.ARRANGE_FLEET);

        canvasGeneralGraphicsContext = canvasGeneral.getGraphicsContext2D();
        canvasOverlayGraphicsContext = canvasOverlay.getGraphicsContext2D();

        init_typeButton(shipType4Button, Ship.Type.AIRCRAFT_CARRIED);
        init_typeButton(shipType3Button, Ship.Type.BATTLESHIP);
        init_typeButton(shipType2Button, Ship.Type.CRUISER);
        init_typeButton(shipType1Button, Ship.Type.DESTROYER);
        init_typeButton(shipType0Button, Ship.Type.SUBMARINE);

        shipTypeButtons = new ArrayList<>();
        shipTypeButtons.add(shipType0Button);
        shipTypeButtons.add(shipType1Button);
        shipTypeButtons.add(shipType2Button);
        shipTypeButtons.add(shipType3Button);
        shipTypeButtons.add(shipType4Button);

        Runtime.getRuntime().addShutdownHook(new ShutDownThread(network.getSocketClient(), network.getSocketServer(), LOGGER));
    }

    private void init_typeButton(Button button, Ship.Type type) {
        button.setDisable(true);
        button.setOnMouseClicked(event -> {
                    gameEngine.setType(type);
                    //Выбор корабля
                    if (!gameEngine.getIsShipSelected() && gameEngine.getGamePhase() == GameEngine.Phase.ARRANGE_FLEET) {
                        labelGameStatus.setText("Arrange fleet. Select ship");
                        if (gameEngine.getFleetHolder().getShipsLeft() > 0) {
                            int popedShip = gameEngine.getFleetHolder().popShip(Ship.Type.shipTypeToInt(type));
                            if (popedShip >= Constant.NO_MORE_SHIPS) {
                                button.setText(popedShip + "  x");
                                gameEngine.setIsSelected(true);
                            }
                        }
                    }
                }
        );
    }

    private void displayState(ConnectionDisplayState state) {
        switch (state) {
            case DISCONNECTED:
                resetFleetButton.setDisable(true);
                for (Button ship : shipTypeButtons) {
                    ship.setDisable(true);
                }
                menuItemDisconnect.setDisable(true);
                labelGameStatus.setText("Disconnected from server");
                statusListView.getItems().add("Fail connect server attempt: " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                break;
            case ATTEMPTING:
                resetFleetButton.setDisable(true);
                for (Button ship : shipTypeButtons) {
                    ship.setDisable(true);
                }
                labelGameStatus.setText("Attempting connection");
                break;
            case CONNECTED:
                resetFleetButton.setDisable(false);
                for (Button ship : shipTypeButtons) {
                    ship.setDisable(false);
                }
                menuItemDisconnect.setDisable(false);
                labelGameStatus.setText("Connected. Locate fleet");
                statusListView.getItems().add("Connect: " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                break;
        }
    }

    /*
     * Отрисовка при передвижени курсора
     */

    @FXML
    void handleOverlayCanvasMouseMoved(MouseEvent event) {
        if (gameEngine.getPhase() == GameEngine.Phase.ARRANGE_FLEET) {
            PixelCoord scenePixelCoord = new PixelCoord(event.getSceneX(), event.getSceneY());
            FieldCoord fieldCoord = scenePixelCoord.transformMyFieldPixelCoordToFieldCoord();
            Ship.Orientation orientation = gameEngine.getShipOrientation();
            Ship.Type type = gameEngine.getShipType();
            if (gameEngine.getIsShipSelected() &&
                    PixelCoord.isCoordFromMyPlayerField(scenePixelCoord.getX(), scenePixelCoord.getY()) &&
                    gameEngine.getMyGameField().isNotIntersectShipWithBorder(fieldCoord, type, orientation)) {
                if (gameEngine.getMyGameField().isPossibleLocateShip(fieldCoord, type, orientation)) {
                    canvasOverlayGraphicsContext.setStroke(Color.BLACK);
                    Draw.ShipMyField(canvasOverlayGraphicsContext, fieldCoord, gameEngine.getShipType(), gameEngine.getShipOrientation());
                } else {
                    canvasOverlayGraphicsContext.setStroke(Color.RED);
                    Draw.ShipMyField(canvasOverlayGraphicsContext, fieldCoord, gameEngine.getShipType(), gameEngine.getShipOrientation());
                }
                if (lastMyFieldCoord.equals(fieldCoord) &&
                        !isFirstChangeLastFieldCoord) {
                    clearCanvas(canvasOverlayGraphicsContext);
                    lastMyFieldCoord = fieldCoord;
                } else {
                    isFirstChangeLastFieldCoord = false;
                }
            } else {
                clearCanvas(canvasOverlayGraphicsContext);
            }
        }

        // Отрисовка линнии по указанной мышью координате на поле противника
        if (gameEngine.getPhase() == GameEngine.Phase.MAKE_SHOT) {
            labelGameStatus.setText("Make shoot. PixelCoord coordinate");
            PixelCoord scenePixelConstant = new PixelCoord(event.getSceneX(), event.getSceneY());
            if (PixelCoord.isCoordFromEnemyPlayerField(scenePixelConstant.getX(), scenePixelConstant.getY())) {
                canvasOverlayGraphicsContext.setStroke(Color.BLACK);
                FieldCoord fieldCoord = scenePixelConstant.transformEnemyFieldPixelCoordToFieldCoord();
                Draw.LineEnemyPlayer(canvasOverlayGraphicsContext, fieldCoord);
                if (lastEnemyFieldCoord.equals(fieldCoord) && !isFirstChangeLastFieldCoord) {
                    clearCanvas(canvasOverlayGraphicsContext);
                    lastEnemyFieldCoord = fieldCoord;
                } else {
                    isFirstChangeLastFieldCoord = false;
                }
            }
        }
    }

    private void clearCanvas(GraphicsContext context) {
        context.clearRect(0, 0, Constant.Pixel.WIDTH_WINDOW, Constant.Pixel.HEIGHT_WINDOW);
    }

    @FXML
    void handleRestFleetButton() {
        gameEngine.reset();
        resetFleetButton.setDisable(true);
        clearCanvas(canvasOverlayGraphicsContext);
        clearCanvas(canvasGeneralGraphicsContext);
        shipType4Button.setText("1  x");
        shipType3Button.setText("1  x");
        shipType2Button.setText("1  x");
        shipType1Button.setText("2  x");
        shipType0Button.setText("2  x");
    }

    @FXML
    void handleToMouseClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.MIDDLE)) {
            if (gameEngine.getShipType() != Ship.Type.SUBMARINE) {
                Ship.Type shipType = gameEngine.getShipType();
                FieldCoord myFieldCoord = (new PixelCoord(event.getSceneX(), event.getSceneY())).transformEnemyFieldPixelCoordToFieldCoord();
                if (gameEngine.getShipOrientation() == Ship.Orientation.HORIZONTAL) {
                    gameEngine.setShipOrientation(Ship.Orientation.VERTICAL);
                } else {
                    gameEngine.setShipOrientation(Ship.Orientation.HORIZONTAL);
                }
                canvasOverlayGraphicsContext.clearRect(0, 0, Constant.Pixel.WIDTH_WINDOW, Constant.Pixel.HEIGHT_WINDOW);
                Ship.Orientation orientation = gameEngine.getShipOrientation();
                if (gameEngine.getMyGameField().isNotIntersectShipWithBorder(myFieldCoord, shipType, orientation) &&
                        gameEngine.getMyGameField().isPossibleLocateShip(myFieldCoord, shipType, orientation)) {
                    Draw.ShipMyField(canvasOverlayGraphicsContext, myFieldCoord, gameEngine.getShipType(), gameEngine.getShipOrientation());
                }
            }
        }

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            PixelCoord scenePixelCoord = new PixelCoord(event.getSceneX(), event.getSceneY());
            //Размещение корабля, после выбора
            if (gameEngine.getGamePhase() == GameEngine.Phase.ARRANGE_FLEET) {
                if (PixelCoord.isCoordFromMyPlayerField(scenePixelCoord.getX(), scenePixelCoord.getY()) &&
                        gameEngine.getIsShipSelected() &&
                        gameEngine.getFleetHolder().getShipsLeft() >= 0) {
                    resetFleetButton.setDisable(false);
                    FieldCoord myFieldCoord = scenePixelCoord.transformMyFieldPixelCoordToFieldCoord();
                    Ship.Type type = gameEngine.getShipType();
                    Ship.Orientation orient = gameEngine.getShipOrientation();
                    if (gameEngine.getMyGameField().isPossibleLocateShip(myFieldCoord, type, orient)) {
                        gameEngine.addShipOnField(new Ship(myFieldCoord, type, orient));
                        Draw.ShipMyField(canvasGeneralGraphicsContext, myFieldCoord, gameEngine.getShipType(), gameEngine.getShipOrientation());
                        gameEngine.getMyGameField().markFieldByShip(myFieldCoord, type, orient);
                        gameEngine.setIsSelected(false);
                    } else {
                        //TODO После накорректоного размещения добавить Warning Message Box
                        Logger logger = Logger.getLogger(getClass().getName());
                        logger.log(Level.SEVERE, "Failed to arrange Ship.");
                    }
                }
                //Объявление о готовности игрока. Флот расставлен. ################handleToMouseClick###################
                if (!gameEngine.getIsShipSelected() && gameEngine.getFleetHolder().getShipsLeft() == 0 && isServerReadyFirst) {
                    isServerReadyFirst = false;
                    resetFleetButton.setDisable(true);
                    labelGameStatus.setText("Wait for Second Player");
                    if (getIsClient()) {
                        labelGameStatus.setText("Make shot");
                        network.getSocketClient().sendMessage(Constant.NetworkMessage.CLIENT_READY.toString());
                        network.getSocketClient().sendMessage(Constant.NetworkMessage.ENEMY_NAME + playerMyLabel.getText());
                    } else {
                        network.getSocketServer().sendMessage(Constant.NetworkMessage.SERVER_READY.toString());
                        network.getSocketServer().sendMessage(Constant.NetworkMessage.ENEMY_NAME + playerMyLabel.getText());
                    }
                    statusListView.getItems().add("Fleet Arranged");
                    gameEngine.setPhase(GameEngine.Phase.READY);
                }
            }
            //выстрел в поле противника
            if (gameEngine.getGamePhase() == GameEngine.Phase.MAKE_SHOT) {
                if (PixelCoord.isCoordFromEnemyPlayerField(scenePixelCoord.getX(), scenePixelCoord.getY())) {
                    FieldCoord shootFieldCoord = scenePixelCoord.transformEnemyFieldPixelCoordToFieldCoord();
                    Draw.LineEnemyPlayer(canvasGeneralGraphicsContext, shootFieldCoord);
                    if (getIsClient()) {
                        network.getSocketClient().sendMessage(Constant.NetworkMessage.SHOT.toString() + shootFieldCoord);
                    } else {
                        network.getSocketServer().sendMessage(Constant.NetworkMessage.SHOT.toString() + shootFieldCoord);
                    }
                    gameEngine.setShootCoord(shootFieldCoord);
                }
            }
        }
    }

    @FXML
    void handleMenuItemCreateServer() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            menuItemConnect.setDisable(true);
            handleRestFleetButton();
            fxmlLoader.setLocation(getClass().getResource("/fxml/FXMLDocumentCreateGame.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Create game");
            gameEngine.reset();
            FXMLDocumentCreateGame controller = fxmlLoader.getController();
            controller.setPortField();
            controller.setMyName();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setOnHidden(close -> {
                String port = controller.getPort();
                if (port != null) {
                    network.connectServer(Integer.parseInt(port), rcvdMsgsData);
                    gameEngine.setPhase(GameEngine.Phase.ARRANGE_FLEET);
                    isClient(false);
                } else {
                    close.consume();
                }
                playerMyLabel.setText(controller.getMyName());
            });
            gameEngine.setPhase(GameEngine.Phase.ARRANGE_FLEET);
            stage.show();
            numRoundLabel.setText(Integer.toString(++numRound));
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    @FXML
    void handleMenuItemConnectGame() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            handleRestFleetButton();
            menuItemConnect.setDisable(true);
            fxmlLoader.setLocation(getClass().getResource("/fxml/FXMLDocumentConnectGame.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Connect to game");
            gameEngine.reset();
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
                    network.connectClient(host, Integer.parseInt(port));
                    gameEngine.setPhase(GameEngine.Phase.ARRANGE_FLEET);
                    isClient(true);
                } else {
                    close.consume();
                }
                playerMyLabel.setText(controller.getMyName());
            });
            gameEngine.setPhase(GameEngine.Phase.ARRANGE_FLEET);
            stage.show();
            numRoundLabel.setText(Integer.toString(++numRound));

        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    @FXML
    void handleMenuItemExit() {
        Platform.exit();
    }

    @FXML
    void handleMenuItemAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Constant.AboutInfo.ABOUT_GAME_TITLE);
        alert.setHeaderText(Constant.AboutInfo.ABOUT_GAME_HEADER);
        alert.setContentText(Constant.AboutInfo.ABOUT_GAME_TEXT);
        alert.showAndWait();
    }

    @FXML
    void handleDisconnectMenuItem() {
        network.shutdown();
    }

    private FieldCoord getShootCoordOnMessage(String line) {
        line = line.replaceAll("\\D+", Constant.NetworkMessage.EMPTY_STRING.toString());
        int xyInt = Integer.parseInt(line);
        //TODO смещение координат ????????
        // компенсация границ поля 12*12
        int x = xyInt / 10;
        int y = xyInt % 10 + 1;
        return new FieldCoord(x, y);
    }

    //resolve means client or server socket
    void resolveSocketAndProceedMassage(String line) {
        rcvdMsgsData.add(line);
        if (getIsClient()) {
            gameEngine.setFirstShot(true);
            proceedMessage(line, network.getSocketClient());
        } else {
            if (line.equals(Constant.NetworkMessage.CLIENT_READY) && gameEngine.getPhase() == GameEngine.Phase.READY && gameEngine.isFirstShot()) {
                gameEngine.setFirstShot(false);
                if (Math.random() < 0.5) {
                    gameEngine.setPhase(GameEngine.Phase.MAKE_SHOT);
                } else {
                    network.getSocketServer().sendMessage(Constant.NetworkMessage.YOU_TURN.toString());
                    gameEngine.setPhase(GameEngine.Phase.TAKE_SHOT);
                }
            }
            proceedMessage(line, network.getSocketServer());
        }
    }

    void proceedMessage(String line, GenericSocket socket) {
        gameEngingeProceed(line); //must be before gui
        guiProceed(line);
        sendAnswer(line, socket);
    }

    //must be before gui
    void gameEngingeProceed(String line) {
        Constant.NetworkMessage message = Constant.NetworkMessage.getType(line);
        switch (message) {
            case DESTROYED:
                gameEngine.setPhase(GameEngine.Phase.MAKE_SHOT);
                break;
            case SHOT:
                gameEngine.setShootCoord(getShootCoordOnMessage(line));
                if (gameEngine.getMyGameField().setCellAsDamaged(gameEngine.getShootCoord())) {
                    Ship ship = gameEngine.getShipsOnField().findShip(gameEngine.getShootCoord());
                    ship.tagShipCell(gameEngine.getShootCoord());
                    if (!gameEngine.getMyGameField().isShipsOnFieldAlive()) {
                        gameEngine.setPhase(GameEngine.Phase.END_GAME);
                    }
                }
                break;
            case YOU_TURN:
                gameEngine.setPhase(GameEngine.Phase.MAKE_SHOT);
                break;
            case YOU_LOSE:
                gameEngine.setPhase(GameEngine.Phase.END_GAME);
                break;
            case YOU_WIN:
                gameEngine.setPhase(GameEngine.Phase.END_GAME);
                break;
            case MISS:
                gameEngine.setPhase(GameEngine.Phase.TAKE_SHOT);
                break;
        }
    }

    void guiProceed(String line) {
        Constant.NetworkMessage message = Constant.NetworkMessage.getType(line);
        String[] param;
        switch (message) {
            case ENEMY_NAME:
                param = line.split("\\s+");
                StringBuffer result = new StringBuffer();
                for (int i = 1; i < param.length; i++) {
                    result.append(param[i]);
                }
                playerEnemyLabel.setText(new String(result));
                break;
            case DESTROYED:
                param = line.split("\\s+");
                try {
                    Draw.ShipEnemyField(canvasGeneralGraphicsContext, new FieldCoord(param[1], param[2]), Ship.Type.shipStrToType(param[3]), Ship.Orientation.strToOrientation(param[4]));
                } catch (Exception e) {
                    System.out.println("Can't handle orientation " + e);
                }
                break;
            case SHOT:
                if (gameEngine.getMyGameField().setCellAsDamaged(gameEngine.getShootCoord())) {
                    Draw.CrossMyPlayer(canvasGeneralGraphicsContext, gameEngine.getShootCoord());
                } else {
                    Draw.LineMyPlayer(canvasGeneralGraphicsContext, gameEngine.getShootCoord());
                }
                if (!gameEngine.getMyGameField().isShipsOnFieldAlive()) {
                    labelGameStatus.setText(Constant.NetworkMessage.YOU_LOSE.toString());
                }
                break;
            case YOU_LOSE:
                labelGameStatus.setText(Constant.NetworkMessage.YOU_LOSE.toString());
                break;
            case YOU_WIN:
                labelGameStatus.setText(Constant.NetworkMessage.YOU_WIN.toString());
                break;
            case HIT:
                Draw.CrossEnemyPlayer(canvasGeneralGraphicsContext, gameEngine.getShootCoord());
                break;
            case MISS:
                Draw.LineEnemyPlayer(canvasGeneralGraphicsContext, gameEngine.getShootCoord());
                break;
        }
    }

    void sendAnswer(String line, GenericSocket socket) {
        Constant.NetworkMessage message = Constant.NetworkMessage.getType(line);
        switch (message) {
            case SHOT:
                if (gameEngine.getMyGameField().setCellAsDamaged(gameEngine.getShootCoord())) {
                    Ship ship = gameEngine.getShipsOnField().findShip(gameEngine.getShootCoord());
                    socket.sendMessage(Constant.NetworkMessage.HIT.toString());
                    if (!ship.isAlive()) {
                        socket.sendMessage(Constant.NetworkMessage.DESTROYED.toString() + " " + ship.toString());
                    }
                } else {
                    socket.sendMessage(Constant.NetworkMessage.MISS.toString());
                }
                if (!gameEngine.getMyGameField().isShipsOnFieldAlive()) {
                    socket.sendMessage(Constant.NetworkMessage.YOU_WIN.toString());
                }
                break;
            case MISS:
                socket.sendMessage(Constant.NetworkMessage.YOU_TURN.toString());
                break;
        }
    }
}


