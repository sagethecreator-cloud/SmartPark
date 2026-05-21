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
import javafx.scene.layout.VBox;
import parking.ParkingLot;
import parking.ParkingSlot;
import vehicles.EV;
import vehicles.Vehicle;
import javafx.application.Platform;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
public class FindVehicleController implements Initializable {
    @FXML
    private TextField plateField;
    @FXML
    private Button searchBtn;
    @FXML
    private VBox resultCard;
    @FXML
    private Label plateResultLabel;
    @FXML
    private Label ownerResultLabel;
    @FXML
    private Label typeResultLabel;
    @FXML
    private Label slotLabel;
    @FXML
    private Label slotTypeLabel;
    @FXML
    private Label entryTimeLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label liveFeeLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private void handleSearch(ActionEvent e) {
        String input = plateField.getText().trim().toUpperCase();
        if (input.isEmpty()) {
            errorLabel.setText("Enter a plate number.");
            return;
        }
        ParkingLot lot = AppState.getLot();
        ParkingSlot found = lot.findVehicleByNumber(input);
        if (found == null) {
            found = lot.findSlotByEntryId(input);
        }
        if (found == null) {
            errorLabel.setText("Vehicle not found.");
            resultCard.setVisible(false);
            resultCard.setManaged(false);
            return;
        }
        Vehicle v = found.getParkedVehicle();
        plateResultLabel.setText(v.getVehicleNo());
        ownerResultLabel.setText(v.getOwnerName());
        typeResultLabel.setText(v.getType());
        slotLabel.setText("Slot " + found.getSlotId());
        slotTypeLabel.setText(found.getSlotType());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        entryTimeLabel.setText(sdf.format(new Date(v.getEntryTime())));
        long now = System.currentTimeMillis();
        int parkedMins = (int) ((now - v.getEntryTime()) / 60000);
        durationLabel.setText(parkedMins + " min");
        statusLabel.setText("Parked for " + parkedMins + " min");
        if (v instanceof EV) {
            EV ev = (EV) v;
            if (ev.isChargingRequested()) {
                float fee = lot.calculateLiveChargingFee(parkedMins, ev.getBatteryOnArrival());
                liveFeeLabel.setText("Rs " + fee);
            } else {
                liveFeeLabel.setText("—");
            }
        } else {
            liveFeeLabel.setText("—");
        }
        errorLabel.setText("");
        resultCard.setVisible(true);
        resultCard.setManaged(true);
    }
    @FXML
    private void handleBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/userDashboard.fxml");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorLabel.setText("");
        resultCard.setVisible(false);
        resultCard.setManaged(false);
        // Enter key: trigger search
        plateField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    handleSearch(null);
                }
            }
        });
        Platform.runLater(new Runnable() {
            public void run() {
                plateField.requestFocus();
            }
        });
    }
}