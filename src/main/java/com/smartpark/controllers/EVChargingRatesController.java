package com.smartpark.controllers;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import parking.ParkingLot;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;
public class EVChargingRatesController implements Initializable {
    @FXML
    private TextField perMinRateField;
    @FXML
    private TextField perPercentRateField;
    @FXML
    private Button perMinModeBtn;
    @FXML
    private Button perPercentModeBtn;
    @FXML
    private Button saveEvRates;
    @FXML
    private Button backBtn;
    @FXML
    private Label currentModeLabel;
    @FXML
    private Label evRateErrorLabel;
    @FXML
    private void switchToPerMin(ActionEvent e) {
        ParkingLot lot = AppState.getLot();
        AppState.getAdmin().switchChargingMode(lot, "perMin");
        currentModeLabel.setText("Current mode: Per Minute");
        styleActiveMode(true);
        AppState.save();
    }
    @FXML
    private void switchToPerPercent(ActionEvent e) {
        ParkingLot lot = AppState.getLot();
        AppState.getAdmin().switchChargingMode(lot, "perPercent");
        currentModeLabel.setText("Current mode: Per Percent");
        styleActiveMode(false);
        AppState.save();
    }
    @FXML
    private void saveEvRates(ActionEvent e) {
        float perMinVal;
        float perPercentVal;
        try {
            perMinVal = Float.parseFloat(perMinRateField.getText().trim());
            if (perMinVal <= 0) {
                showError("Per-minute rate must be greater than 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Enter a valid per-minute rate.");
            return;
        }
        try {
            perPercentVal = Float.parseFloat(perPercentRateField.getText().trim());
            if (perPercentVal <= 0) {
                showError("Per-percent rate must be greater than 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Enter a valid per-percent rate.");
            return;
        }
        ParkingLot lot = AppState.getLot();
        AppState.getAdmin().updateEVChargingRate(lot, "perMin", perMinVal);
        AppState.getAdmin().updateEVChargingRate(lot, "perPercent", perPercentVal);
        AppState.save();
        evRateErrorLabel.setText("✅ Rates saved.");
        evRateErrorLabel.setStyle("-fx-text-fill: #00b894; -fx-font-size: 11; -fx-font-family: 'Segoe UI';");
        evRateErrorLabel.setVisible(true);
        evRateErrorLabel.setManaged(true);
    }
    @FXML
    private void goBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/adminDashboard.fxml");
    }
    private void showError(String msg) {
        evRateErrorLabel.setText(msg);
        evRateErrorLabel.setStyle("-fx-text-fill: #e17055; -fx-font-size: 11; -fx-font-family: 'Segoe UI';");
        evRateErrorLabel.setVisible(true);
        evRateErrorLabel.setManaged(true);
    }
    private void styleActiveMode(boolean perMinActive) {
        String activeStyle = "-fx-background-color: #6c5ce7; -fx-text-fill: white; -fx-font-size: 12; "
                + "-fx-font-weight: bold; -fx-font-family: 'Segoe UI'; -fx-background-radius: 8; "
                + "-fx-cursor: hand; -fx-border-width: 0;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #a29bfe; -fx-font-size: 12; "
                + "-fx-font-weight: bold; -fx-font-family: 'Segoe UI'; -fx-background-radius: 8; "
                + "-fx-cursor: hand; -fx-border-color: #a29bfe; -fx-border-radius: 8; -fx-border-width: 1.2;";
        if (perMinActive) {
            perMinModeBtn.setStyle(activeStyle);
            perPercentModeBtn.setStyle(inactiveStyle);
        } else {
            perMinModeBtn.setStyle(inactiveStyle);
            perPercentModeBtn.setStyle(activeStyle);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!AppState.getAdmin().isLoggedIn()) {
            SceneManager.switchTo("/fxml/Main/welcome.fxml");
            return;
        }
        ParkingLot lot = AppState.getLot();
        perMinRateField.setText(String.valueOf(lot.getChargingRatePerMin()));
        perPercentRateField.setText(String.valueOf(lot.getChargingRatePerPercent()));
        String mode = lot.getChargingMode();
        if (mode.equals("perMin")) {
            currentModeLabel.setText("Current mode: Per Minute");
            styleActiveMode(true);
        } else {
            currentModeLabel.setText("Current mode: Per Percent");
            styleActiveMode(false);
        }
        evRateErrorLabel.setVisible(false);
        evRateErrorLabel.setManaged(false);
        // Enter key: move to per-percent rate
        perMinRateField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    perPercentRateField.requestFocus();
                }
            }
        });
        // Enter key: save rates
        perPercentRateField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    saveEvRates(null);
                }
            }
        });
        Platform.runLater(new Runnable() {
            public void run() {
                perMinRateField.requestFocus();
            }
        });
    }
}