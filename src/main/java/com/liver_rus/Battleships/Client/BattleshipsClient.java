package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.GUI.FXMLDocumentMainController;
import com.liver_rus.Battleships.Client.GameEngine.ClientGameEngine;
import com.liver_rus.Battleships.Network.Server.GameServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class BattleshipsClient extends Application {

    private ClientGameEngine gameEngine;
    private FXMLDocumentMainController controller;
    private GameServer gameServer;

    /*
      //create server
            if (startServer) {
                //TODO some magic
                //TODO убрать проверку на null (возможет ли тут null)
                if (netServer == null) {
                    try {
                        //TODO создать фабричный метод
                        netServer = new GameServer(ip, port);
                        netServer.start();
                    } catch (IOException e) {
                        controller.reset("Couldn't create server");
                        e.printStackTrace();
                    }

                } else {
                    netServer.startThread();
                }
            }
     */

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLDocumentMain.fxml"));
        Parent root = loader.load();

        gameEngine = new ClientGameEngine();
        //TODO gameServerThread перенести на этот уровень
        controller = loader.getController();
        controller.setClientEngine(gameEngine);
        controller.setGameServer(gameServer);
        gameEngine.setController(controller);
        //controller.setServerThreadClassHolder(serverThreadClassHolder)
        Scene scene = new Scene(root);
        stage.getIcons().add(new Image("/img/480px-icon.png"));
        stage.setResizable(false);
        stage.setScene(scene);

        stage.setTitle(this.getClass().getSimpleName());
        stage.show();
        stage.setOnCloseRequest(we -> System.exit(0));
    }

    public static void main(String[] args) {
        launch(args);
    }
}