package com.liver_rus.Battleships.Client;

import com.liver_rus.Battleships.Client.GUI.FXMLDocumentMainController;
import com.liver_rus.Battleships.Client.GameEngine.ClientGameEngine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class BattleshipsClient extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLDocumentMain.fxml"));
        Parent root = loader.load();
        ClientGameEngine gameEngine = new ClientGameEngine();
        //TODO gameServerThread перенести на этот уровень
        FXMLDocumentMainController controller = loader.getController();
        controller.setClientEngine(gameEngine);
        gameEngine.setController(controller);
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