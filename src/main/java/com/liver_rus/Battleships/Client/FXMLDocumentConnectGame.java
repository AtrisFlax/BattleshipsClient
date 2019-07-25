package com.liver_rus.Battleships.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class FXMLDocumentConnectGame {

    private static String host = null;
    private static String port = null;
    private static String myName = null;

    String getHost() {
        return host;
    }

    String getPort() {
        return port;
    }

    String getMyName() {
        return myName;
    }

    private static final Pattern IPv4Pattern = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private static boolean isIPAddress(final String ip) {
        return IPv4Pattern.matcher(ip).matches() || ip.equals("localhost");
    }

    final private static int MIN_VALUE_PORT = 0;
    final private static int MAX_VALUE_PORT = 65535;

    private static boolean isPort(String strPort) {
        if (CheckNumeric.isNumeric(strPort)) {
            int intPort = Integer.parseInt(port);
            if (intPort > MIN_VALUE_PORT && intPort <= MAX_VALUE_PORT) {
                CheckNumeric.isNumeric(strPort);
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
    private TextField ipTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    void handleButtonConnect(ActionEvent event) {
        if (portTextField.getText() != null && !portTextField.getText().isEmpty() &&
                ipTextField.getText() != null && !ipTextField.getText().isEmpty()) {
            host = ipTextField.getText();
            port = portTextField.getText();
            myName = nameTextField.getText();
            if (isIPAddress(host)) {
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
                    if (isMyName(myName)) {
                        Node source = (Node) event.getSource();
                        Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
                    } else {
                        Alert alertName = new Alert(Alert.AlertType.WARNING);
                        alertName.setTitle("Empty Name");
                        alertName.setHeaderText("Enter Name");
                        alertName.setContentText("");
                        alertName.showAndWait();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Not valid IP");
                alert.setHeaderText("Input Valid ip");
                alert.setContentText("");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Field");
            alert.setHeaderText("Wrong value");
            alert.setContentText("Input IP address and Pass");
            alert.showAndWait();
        }
    }

    void setIPAndPortFields() {
        if (host != null) {
            ipTextField.setText(host);
        }
        if (port != null) {
            portTextField.setText(port);
        }
    }

    void setMyName() {
        myName = nameTextField.getText();
    }
}