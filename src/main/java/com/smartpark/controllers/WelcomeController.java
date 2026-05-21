package com.smartpark.controllers;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;
public class WelcomeController implements Initializable {
    @FXML
    private Button adminBtn;
    @FXML
    private Button userBtn;
    @FXML
    private void goToAdminLogin(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/login.fxml");
    }
    @FXML
    private void goToUserMenu(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/userDashboard.fxml");
    }
    @FXML
    private void handleExit(ActionEvent e) {
        Platform.exit();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}