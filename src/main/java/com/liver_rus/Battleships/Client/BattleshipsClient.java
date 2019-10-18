package com.liver_rus.Battleships.Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class BattleshipsClient extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/FXMLDocumentMain.fxml"));
        Scene scene = new Scene(root);
        stage.getIcons().add(new Image("file:resources\\480px-icon.png"));
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
