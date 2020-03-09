package com.liver_rus.Battleships.Client.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class FXMLDocumentConnectGame {
    private static final Pattern IPv4Pattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final int MIN_VALUE_PORT = 0;
    private static final int MAX_VALUE_PORT = 65535;
    private static String ip;
    private static String port;
    private static String myName;
    private boolean isStartClient;

    @FXML
    private TextField portTextField;

    @FXML
    private TextField ipTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private CheckBox checkBox;

    @FXML
    void handleButtonConnect(ActionEvent event) {
        isStartClient = false;
        ip = ipTextField.getText();
        port = portTextField.getText();
        myName = nameTextField.getText();
        if (isIPAddress(ip) && isPort(port)) {
            isStartClient = true;
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid pair IP and Port");
            alert.setHeaderText("Check IP address and Port");
            alert.setContentText("");
            alert.showAndWait();
        }
    }

    String getHost() {
        return ip;
    }

    int getPort() {
        return Integer.parseInt(port);
    }

    String getMyName() {
        return myName;
    }

    void setIPAndPortFields() {
        if (ip != null) {
            ipTextField.setText(ip);
        }
        if (port != null) {
            portTextField.setText(port);
        }
    }

    void setMyName() {
        myName = nameTextField.getText();
    }

    boolean isStartServer() {
        return checkBox.isSelected();
    }

    boolean isStartClient() {
        return isStartClient;
    }

    private static boolean isNumeric(String strNum) {
        boolean isNum = true;
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException e) {
            isNum = false;
        }
        return isNum;
    }

    private static boolean isPort(String strPort) {
        if (isNumeric(strPort)) {
            int intPort = Integer.parseInt(port);
            if (intPort > MIN_VALUE_PORT && intPort <= MAX_VALUE_PORT) {
                isNumeric(strPort);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean isIPAddress(final String ip) {
        return IPv4Pattern.matcher(ip).matches() || ip.equals("localhost");
    }
}