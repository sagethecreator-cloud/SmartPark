package com.smartpark.controllers;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;
public class LoginController implements Initializable {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private void handleLogin(ActionEvent e) {
        boolean ok = AppState.getAdmin().login(
                usernameField.getText().trim(),
                passwordField.getText().trim());
        if (ok) {
            SceneManager.switchTo("/fxml/Main/adminDashboard.fxml");
        } else {
            errorLabel.setText("Invalid credentials. Try again.");
            shakeErrorLabel();
        }
    }
    @FXML
    private void handleBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/welcome.fxml");
    }
    private void shakeErrorLabel() {
        final double originalX = errorLabel.getTranslateX();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(0), new EventHandler<javafx.event.ActionEvent>() {
                    public void handle(javafx.event.ActionEvent ev) {
                        errorLabel.setTranslateX(originalX);
                    }
                }),
                new KeyFrame(Duration.millis(50), new EventHandler<javafx.event.ActionEvent>() {
                    public void handle(javafx.event.ActionEvent ev) {
                        errorLabel.setTranslateX(originalX + 8);
                    }
                }),
                new KeyFrame(Duration.millis(100), new EventHandler<javafx.event.ActionEvent>() {
                    public void handle(javafx.event.ActionEvent ev) {
                        errorLabel.setTranslateX(originalX - 8);
                    }
                }),
                new KeyFrame(Duration.millis(150), new EventHandler<javafx.event.ActionEvent>() {
                    public void handle(javafx.event.ActionEvent ev) {
                        errorLabel.setTranslateX(originalX + 4);
                    }
                }),
                new KeyFrame(Duration.millis(200), new EventHandler<javafx.event.ActionEvent>() {
                    public void handle(javafx.event.ActionEvent ev) {
                        errorLabel.setTranslateX(originalX);
                    }
                })
        );
        timeline.play();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorLabel.setText("");
        // Enter key: move focus to password field
        usernameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
        // Enter key: submit login
        passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    handleLogin(null);
                }
            }
        });
        Platform.runLater(new Runnable() {
            public void run() {
                usernameField.requestFocus();
            }
        });
    }
}