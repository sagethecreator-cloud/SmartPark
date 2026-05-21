package com.smartpark.controllers;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import parking.ParkingLot;
import parking.ParkingSlot;
import vehicles.EV;
import vehicles.Vehicle;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;
public class EVChargingController implements Initializable {
    @FXML
    private TextField evPlateField;
    @FXML
    private Button loadBtn;
    @FXML
    private VBox statusCard;
    @FXML
    private Label evPlateDisplay;
    @FXML
    private Label evOwnerLabel;
    @FXML
    private Label batteryArrivalLabel;
    @FXML
    private Label currentBatteryLabel;
    @FXML
    private ProgressBar batteryBar;
    @FXML
    private Label elapsedLabel;
    @FXML
    private Label estFullLabel;
    @FXML
    private Label liveFeeLabel;
    @FXML
    private HBox modeBadge;
    @FXML
    private Label modeLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Button refreshLiveBtn;
    @FXML
    private VBox rateCard;
    @FXML
    private Label ratePerMinLabel;
    @FXML
    private Label ratePerPctLabel;
    private EV currentEv;
    @FXML
    private void handleLoad(ActionEvent e) {
        String plate = evPlateField.getText().trim().toUpperCase();
        if (plate.isEmpty()) {
            errorLabel.setText("Enter your vehicle plate number.");
            return;
        }
        ParkingLot lot = AppState.getLot();
        ParkingSlot found = lot.findVehicleByNumber(plate);
        if (found == null) {
            errorLabel.setText("Vehicle not found.");
            statusCard.setVisible(false);
            statusCard.setManaged(false);
            return;
        }
        Vehicle v = found.getParkedVehicle();
        if (!(v instanceof EV)) {
            errorLabel.setText("This vehicle is not an EV or not connected to charging.");
            statusCard.setVisible(false);
            statusCard.setManaged(false);
            return;
        }
        EV ev = (EV) v;
        if (!ev.isChargingRequested()) {
            errorLabel.setText("This vehicle is not an EV or not connected to charging.");
            statusCard.setVisible(false);
            statusCard.setManaged(false);
            return;
        }
        currentEv = ev;
        errorLabel.setText("");
        refreshStatus();
        statusCard.setVisible(true);
        statusCard.setManaged(true);
    }
    @FXML
    private void handleRefreshLive(ActionEvent e) {
        if (currentEv != null) {
            refreshStatus();
        }
    }
    @FXML
    private void handleBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/userDashboard.fxml");
    }
    private void refreshStatus() {
        if (currentEv == null) {
            return;
        }
        ParkingLot lot = AppState.getLot();
        long now = System.currentTimeMillis();
        int elapsedMins = (int) ((now - currentEv.getEntryTime()) / 60000);
        double currentBattery = currentEv.getBatteryOnArrival() + (elapsedMins * 1.8);
        if (currentBattery > 100) {
            currentBattery = 100;
        }
        int chargedAmt = (int) currentBattery - currentEv.getBatteryOnArrival();
        double minsToFull = ((100 - currentEv.getBatteryOnArrival()) / 1.8) - elapsedMins;
        if (minsToFull < 0) {
            minsToFull = 0;
        }
        float feeSoFar = lot.calculateLiveChargingFee(elapsedMins, currentEv.getBatteryOnArrival());
        String evStatus;
        if (currentBattery >= 100) {
            evStatus = "FULLY CHARGED ✔";
        } else {
            evStatus = "CHARGING ⚡";
        }
        evPlateDisplay.setText(currentEv.getVehicleNo());
        evOwnerLabel.setText(currentEv.getOwnerName());
        batteryArrivalLabel.setText(currentEv.getBatteryOnArrival() + "%");
        currentBatteryLabel.setText((int) currentBattery + "%");
        batteryBar.setProgress(currentBattery / 100.0);
        elapsedLabel.setText(elapsedMins + " mins");
        estFullLabel.setText((int) minsToFull + " mins");
        liveFeeLabel.setText("Rs " + feeSoFar);
        statusLabel.setText(evStatus + " · Charged " + chargedAmt + "%");
        String mode = lot.getChargingMode();
        if (mode.equals("perPercent")) {
            modeLabel.setText("Per Percent (Rs " + lot.getChargingRatePerPercent() + "/%)");
        } else {
            modeLabel.setText("Per Minute (Rs " + lot.getChargingRatePerMin() + "/min)");
        }
        modeBadge.setVisible(true);
        modeBadge.setManaged(true);
        rateCard.setVisible(true);
        rateCard.setManaged(true);
        ratePerMinLabel.setText("Rs " + lot.getChargingRatePerMin() + "/min");
        ratePerPctLabel.setText("Rs " + lot.getChargingRatePerPercent() + "/%");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorLabel.setText("");
        statusCard.setVisible(false);
        statusCard.setManaged(false);
        currentEv = null;
        // Enter key: trigger load/check status
        evPlateField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    handleLoad(null);
                }
            }
        });
        Platform.runLater(new Runnable() {
            public void run() {
                evPlateField.requestFocus();
            }
        });
    }
}