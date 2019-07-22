package com.liver_rus.Battleships.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLDocumentCreateGame {

    private static String port = null;
    private static String myName = null;

    String getPort() {
        return port;
    }

    String getMyName() {
        return myName;
    }

    final private static int MIN_VALUE_PORT = 0;
    final private static int MAX_VAULE_PORT = 65535;

    private static boolean isPort(String str) {
        if (CheckNumeric.isNumeric(str)) {
            int intPort = Integer.parseInt(port);
            if (intPort > MIN_VALUE_PORT && intPort <= MAX_VAULE_PORT) {
                CheckNumeric.isNumeric(str);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean isMyName(String str) {
        return (!str.equals(""));
    }

    @FXML
    private TextField portTextField;

    @FXML
    private TextField nameTextFieldHost;

    @FXML
    void handleButtonCreate(ActionEvent event) {
        if (!portTextField.getText().isEmpty() &&
                !portTextField.getText().isEmpty() &&
                !nameTextFieldHost.getText().isEmpty()) {
            port = portTextField.getText();
            myName = nameTextFieldHost.getText();
            if (isPort(port)) {
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Not Port");
                alert.setHeaderText("Input port");
                alert.setContentText("");
                alert.showAndWait();
            }
            if (isMyName(myName)) {
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Empty Name");
                alert.setHeaderText("Enter Name");
                alert.setContentText("");
                alert.showAndWait();
            }
        }
    }

    void setPortField() {
        if (port != null) {
            portTextField.setText(port);
        }
    }

    void setMyName() {
        myName = nameTextFieldHost.getText();
    }
}

