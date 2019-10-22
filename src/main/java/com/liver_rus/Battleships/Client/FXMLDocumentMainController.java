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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private GameEngine gameEngine;

    private Client network;

    Thread serverThread;

    private ObservableList<String> networkInbox;

    public enum ConnectionDisplayState {
        DISCONNECTED, ATTEMPTING, CONNECTED
    }

    private FieldCoord lastMyFieldCoord;
    private FieldCoord lastEnemyFieldCoord;

    private GraphicsContext canvasOverlayGraphicsContext;
    private GraphicsContext canvasGeneralGraphicsContext;

    private boolean isFirstChangeLastFieldCoord = true;
    private boolean isServerReadyFirst = true;

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
            proceedMessage(received_msg);
            networkInbox.clear();
        });
    }


    private void initGameEngine() {
        gameEngine = new GameEngine();
        //TODO lastMyFieldCoord lastEnemyFieldCoord move on GameEngine class
        lastMyFieldCoord = new FieldCoord((byte) Constants.NONE_SELECTED_FIELD_COORD, (byte) Constants.NONE_SELECTED_FIELD_COORD);
        lastEnemyFieldCoord = new FieldCoord((byte) Constants.NONE_SELECTED_FIELD_COORD, (byte) Constants.NONE_SELECTED_FIELD_COORD);
    }

    private void initGUI() {
        resetFleetButton.setDisable(true);

        statusListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        canvasGeneralGraphicsContext = canvasGeneral.getGraphicsContext2D();
        canvasOverlayGraphicsContext = canvasOverlay.getGraphicsContext2D();

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
                    if (gameEngine.getGamePhase() == GameEngine.Phase.DEPLOYING_FLEET) {
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
        if (gameEngine.getPhase() == GameEngine.Phase.DEPLOYING_FLEET) {
            gameEngine.setCurrentState(
                    new FieldCoord(
                            event.getSceneX(), event.getSceneY(), true),
                    gameEngine.getShipType(),
                    gameEngine.getShipOrientation()
            );
            //System.out.println("handleOverlayCanvasMouseMoved gameEngine.getCurrentState()" + gameEngine.getCurrentState());
            if (gameEngine.isShipSelected() &&
                    PixelCoord.isFromMyPlayerField(event.getSceneX(), event.getSceneY()) &&
                    gameEngine.getMyField().isNotIntersectShipWithBorder(gameEngine.getCurrentState())) {
                if (gameEngine.getMyField().isPossibleLocateShip(gameEngine.getCurrentState())) {
                    canvasOverlayGraphicsContext.setStroke(Color.BLACK);
                    Draw.ShipOnMyField(canvasOverlayGraphicsContext, gameEngine.getCurrentState());
                } else {
                    canvasOverlayGraphicsContext.setStroke(Color.RED);
                    Draw.ShipOnMyField(canvasOverlayGraphicsContext, gameEngine.getCurrentState());
                }
                if (lastMyFieldCoord.equals(gameEngine.getCurrentState().getFieldCoord()) && !isFirstChangeLastFieldCoord) {
                    clearCanvas(canvasOverlayGraphicsContext);
                    lastMyFieldCoord = gameEngine.getCurrentState().getFieldCoord();
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
            if (PixelCoord.isCoordFromEnemyPlayerField(event.getSceneX(), event.getSceneY())) {
                canvasOverlayGraphicsContext.setStroke(Color.BLACK);
                FieldCoord fieldCoord = new FieldCoord(event.getSceneX(), event.getSceneY(), false);
                Draw.MissCellOnEnemyField(canvasOverlayGraphicsContext, fieldCoord);
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
        context.clearRect(0, 0, Constants.Window.WIDTH, Constants.Window.HEIGHT);
    }

    @FXML
    void handleResetFleetButton() {
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
        //меняем расположения корабля с горизонтального на вертикальное (или наоборот) на среднюю клавишу мыши
        if (event.getButton().equals(MouseButton.MIDDLE)) {
            if (gameEngine.getShipType() != Ship.Type.SUBMARINE) {
                gameEngine.changeShipOrientation();
//                System.out.println("Current state MouseButton.MIDDLE" + gameEngine.getCurrentState());
                //перерисовываем после смены
                canvasOverlayGraphicsContext.clearRect(0, 0, Constants.Window.WIDTH, Constants.Window.HEIGHT);
                if (gameEngine.getMyField().isNotIntersectShipWithBorder(gameEngine.getCurrentState()) &&
                        gameEngine.getMyField().isPossibleLocateShip(gameEngine.getCurrentState())) {
                    Draw.ShipOnMyField(canvasOverlayGraphicsContext, gameEngine.getCurrentState());
                }
            }
        }

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            //Размещение корабля, после выбора
            if (gameEngine.getGamePhase() == GameEngine.Phase.DEPLOYING_FLEET) {
                if (PixelCoord.isFromMyPlayerField(event.getSceneX(), event.getSceneY()) &&
                        gameEngine.isShipSelected() &&
                        gameEngine.getMyField().getShips().getShipsLeft() >= 0) {
                    resetFleetButton.setDisable(false);

//                    log.info("CurrentState MouseButton.PRIMARY" + gameEngine.getCurrentState());

                    if (gameEngine.getMyField().isPossibleLocateShip(gameEngine.getCurrentState()) &&
                            gameEngine.getMyField().getShips().getShipsLeft() >= 0) {
                        gameEngine.addShipOnField(new Ship(gameEngine.getCurrentState()));
                        Draw.ShipOnMyField(canvasGeneralGraphicsContext, gameEngine.getCurrentState());
                        gameEngine.getMyField().markFieldByShip(gameEngine.getCurrentState());
                        gameEngine.setShipSelected(false);
                    } else {
                        log.log(Level.SEVERE, "Failed to deploy Ship.");
                    }
                }
                //Флот расставлен. Объявление о готовности игрока.. ################handleToMouseClick###################
                if (!gameEngine.isShipSelected() && gameEngine.getMyField().getShips().getShipsLeft() == 0 && isServerReadyFirst) {
                    gameEngine.setPhase(GameEngine.Phase.FLEET_IS_DEPLOYED);
                    network.sendMessage(Constants.NetworkMessage.READY_TO_GAME.toString());
                    resetFleetButton.setDisable(true);
                    labelGameStatus.setText("Fleet is deployed. Waiting for second player");
                }
            }
            //выстрел в поле противника
            if (gameEngine.getGamePhase() == GameEngine.Phase.MAKE_SHOT) {
                if (PixelCoord.isCoordFromEnemyPlayerField(event.getSceneX(), event.getSceneY())) {
                    FieldCoord shootFieldCoord = new FieldCoord(event.getSceneX(), event.getSceneY(), false);
                    Draw.MissCellOnEnemyField(canvasGeneralGraphicsContext, shootFieldCoord);
                    network.sendMessage(Constants.NetworkMessage.SHOT.toString() + shootFieldCoord);
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
                    gameEngine.setPhase(GameEngine.Phase.DEPLOYING_FLEET);
                    labelGameStatus.setText("Deploy fleet. Select ship");

                    //TODO FOR debug DELETE AFTERWARDS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    testShipsDeployment();
                    //TODO FOR debug DELETE AFTERWARDS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                } else {
                    close.consume();
                }
                playerMyLabel.setText(controller.getMyName());
            });
            stage.show();
            numRoundLabel.setText(Integer.toString(gameEngine.newNumRound()));

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
        if (serverThread == null) {
            serverThread.interrupt();
        }
    }

    private FieldCoord getShootCoordOnMessage(String line) {
        line = line.replaceAll("\\D+", "");
        int xyInt = Integer.parseInt(line);
        //TODO найти причину смещение координат ????????
        // компенсация границ поля 12*12
        int x = xyInt / 10;
        int y = xyInt % 10 + 1;
        return new FieldCoord(x, y);
    }

    private void proceedMessage(String line) {
        gameEngineProceed(line); //must be before gui and sensAnswer
        guiProceed(line);
        sendAnswer(line);
    }

    //must be before all
    private void gameEngineProceed(String line) {
        //SHOTXX
        if (checkShotLine(line)) {
            gameEngine.setShootCoord(getShootCoordOnMessage(line));
            if (gameEngine.getMyField().setCellAsDamaged(gameEngine.getShootCoord())) {
                Ship ship = gameEngine.getMyField().getShips().findShip(gameEngine.getShootCoord());
                ship.tagShipCell(gameEngine.getShootCoord());
                if (!gameEngine.getMyField().isShipsOnFieldAlive()) {
                    gameEngine.setPhase(GameEngine.Phase.END_GAME);
                }
            }
        } else {
            Constants.NetworkMessage message = Constants.NetworkMessage.getType(line);
            switch (message) {
                case DESTROYED:
                    gameEngine.setPhase(GameEngine.Phase.MAKE_SHOT);
                    break;
                case SHOT:

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
    }

    //TODO Trasnsform line in someone readable for statusListView
    String convertForReadableView(String line) {
        return line;
    }

    private void guiProceed(String line) {
        //TODO DISPLAY STATE CHANGE WITH PROCEEDING MESSAGE
        //displayState(state);
        statusListView.getItems().add(convertForReadableView(line));

        String[] param;
        //SHOTXX
        if (checkShotLine(line)) {
            if (gameEngine.getMyField().setCellAsDamaged(gameEngine.getShootCoord())) {
                Draw.HitCellOnMyField(canvasGeneralGraphicsContext, gameEngine.getShootCoord());
            } else {
                Draw.MissCellOnMyField(canvasGeneralGraphicsContext, gameEngine.getShootCoord());
            }
            if (!gameEngine.getMyField().isShipsOnFieldAlive()) {
                labelGameStatus.setText(Constants.NetworkMessage.YOU_LOSE.toString());
            }
        }
        else {
            Constants.NetworkMessage message = Constants.NetworkMessage.getType(line);
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
                        Draw.ShipOnEnemyField(canvasGeneralGraphicsContext, new FieldCoord(param[1], param[2]), Ship.Type.shipStrToType(param[3]), Ship.Orientation.strToOrientation(param[4]));
                    } catch (Exception e) {
                        System.out.println("Can't handle orientation " + e);
                    }
                    break;
                case YOU_LOSE:
                    labelGameStatus.setText(Constants.NetworkMessage.YOU_LOSE.toString());
                    break;
                case YOU_WIN:
                    labelGameStatus.setText(Constants.NetworkMessage.YOU_WIN.toString());
                    break;
                case HIT:
                    Draw.HitCellOnEnemyField(canvasGeneralGraphicsContext, gameEngine.getShootCoord());
                    break;
                case MISS:
                    Draw.MissCellOnEnemyField(canvasGeneralGraphicsContext, gameEngine.getShootCoord());
                    break;
            }
        }
    }

    //SHOTXX check
    private boolean checkShotLine(String line){
        Pattern p = Pattern.compile("^SHOT\\d\\d$");
        Matcher m = p.matcher(line);
        return m.matches();
    }

    private void sendAnswer(String line) {
        //SHOTXX
        if (checkShotLine(line)) {
            if (gameEngine.getMyField().setCellAsDamaged(gameEngine.getShootCoord())) {
                Ship ship = gameEngine.getMyField().getShips().findShip(gameEngine.getShootCoord());
                network.sendMessage(Constants.NetworkMessage.HIT.toString());
                if (!ship.isAlive()) {
                    network.sendMessage(Constants.NetworkMessage.DESTROYED.toString() + " " + ship.toString());
                }
            } else {
                network.sendMessage(Constants.NetworkMessage.MISS.toString());
            }
            if (!gameEngine.getMyField().isShipsOnFieldAlive()) {
                network.sendMessage(Constants.NetworkMessage.YOU_WIN.toString());
            }
        } else {
            Constants.NetworkMessage message = Constants.NetworkMessage.getType(line);
            switch (message) {
                case MISS:
                    network.sendMessage(Constants.NetworkMessage.YOU_TURN.toString());
                    break;
            }
        }

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

    void testShipsDeployment() {

        gameEngine.addShipOnField(new Ship(new FieldCoord(1, 8), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        gameEngine.addShipOnField(new Ship(new FieldCoord(3, 2), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL));
        gameEngine.addShipOnField(new Ship(new FieldCoord(1, 1), Ship.Type.DESTROYER, Ship.Orientation.VERTICAL));
        gameEngine.addShipOnField(new Ship(new FieldCoord(3, 4), Ship.Type.DESTROYER, Ship.Orientation.HORIZONTAL));
        gameEngine.addShipOnField(new Ship(new FieldCoord(2, 6), Ship.Type.CRUISER, Ship.Orientation.HORIZONTAL));
        gameEngine.addShipOnField(new Ship(new FieldCoord(7, 4), Ship.Type.BATTLESHIP, Ship.Orientation.VERTICAL));
        gameEngine.addShipOnField(new Ship(new FieldCoord(9, 1), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.VERTICAL));

        Draw.ShipOnMyField(canvasGeneralGraphicsContext, new FieldCoord(1, 8), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL);
        Draw.ShipOnMyField(canvasGeneralGraphicsContext, new FieldCoord(3, 2), Ship.Type.SUBMARINE, Ship.Orientation.HORIZONTAL);
        Draw.ShipOnMyField(canvasGeneralGraphicsContext, new FieldCoord(1, 1), Ship.Type.DESTROYER, Ship.Orientation.VERTICAL);
        Draw.ShipOnMyField(canvasGeneralGraphicsContext, new FieldCoord(3, 4), Ship.Type.DESTROYER, Ship.Orientation.HORIZONTAL);
        Draw.ShipOnMyField(canvasGeneralGraphicsContext, new FieldCoord(2, 6), Ship.Type.CRUISER, Ship.Orientation.HORIZONTAL);
        Draw.ShipOnMyField(canvasGeneralGraphicsContext, new FieldCoord(7, 4), Ship.Type.BATTLESHIP, Ship.Orientation.VERTICAL);
        Draw.ShipOnMyField(canvasGeneralGraphicsContext, new FieldCoord(9, 1), Ship.Type.AIRCRAFT_CARRIER, Ship.Orientation.VERTICAL);

        gameEngine.setPhase(GameEngine.Phase.FLEET_IS_DEPLOYED);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        network.sendMessage(Constants.NetworkMessage.READY_TO_GAME.toString());
        resetFleetButton.setDisable(true);
        labelGameStatus.setText("Fleet is deployed. Waiting for second player");

    }
}


