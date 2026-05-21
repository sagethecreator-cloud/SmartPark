package com.smartpark.controllers;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import billing.Bill;
import billing.EVBill;
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
public class ExitVehicleController implements Initializable {
    @FXML
    private TextField lookupField;
    @FXML
    private Button lookupBtn;
    @FXML
    private Button exitBtn;
    @FXML
    private VBox vehicleCard;
    @FXML
    private VBox receiptCard;
    @FXML
    private VBox receiptPlaceholder;
    @FXML
    private Label ownerLabel;
    @FXML
    private Label plateLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label entryIdLabel;
    @FXML
    private Label entryTimeLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label rateLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label thankYouLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label statusLabel;
    private ParkingSlot currentSlot;
    @FXML
    private void handleLookup(ActionEvent e) {
        String input = lookupField.getText().trim().toUpperCase();
        if (input.isEmpty()) {
            errorLabel.setText("Enter a plate number or Entry ID.");
            if (statusLabel != null) {
                statusLabel.setText("Enter plate or Entry ID.");
            }
            return;
        }
        ParkingLot lot = AppState.getLot();
        ParkingSlot slot = lot.findVehicleByNumber(input);
        if (slot == null) {
            slot = lot.findSlotByEntryId(input);
        }
        if (slot == null || !slot.isOccupied()) {
            errorLabel.setText("Vehicle not found.");
            if (statusLabel != null) {
                statusLabel.setText("Vehicle not found.");
            }
            vehicleCard.setVisible(false);
            vehicleCard.setManaged(false);
            currentSlot = null;
            exitBtn.setDisable(true);
            return;
        }
        Vehicle v = slot.getParkedVehicle();
        currentSlot = slot;
        ownerLabel.setText("Owner: " + v.getOwnerName());
        plateLabel.setText("Plate: " + v.getVehicleNo());
        typeLabel.setText("Type: " + v.getType());
        entryIdLabel.setText("Entry ID: " + v.getEntryId());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        entryTimeLabel.setText("Entry: " + sdf.format(new Date(v.getEntryTime())));
        vehicleCard.setVisible(true);
        vehicleCard.setManaged(true);
        exitBtn.setDisable(false);
        errorLabel.setText("");
        if (statusLabel != null) {
            statusLabel.setText("Vehicle found — ready to exit.");
        }
    }
    @FXML
    private void handleExit(ActionEvent e) {
        if (currentSlot == null) {
            return;
        }
        Vehicle v = currentSlot.getParkedVehicle();
        if (v == null) {
            return;
        }
        ParkingLot lot = AppState.getLot();
        long exitTime = System.currentTimeMillis();
        float rate = lot.getRate(v.getType());
        Bill bill;
        if (v instanceof EV && ((EV) v).isChargingRequested()) {
            EV ev = (EV) v;
            EVBill evBill = new EVBill(v, exitTime, rate, ev.getBatteryOnArrival(),
                    lot.getChargingRatePerMin(), lot.getChargingRatePerPercent(), lot.getChargingMode());
            evBill.calculate();
            bill = evBill;
        } else {
            bill = new Bill(v, exitTime, rate);
            bill.calculate();
        }
        bill.exportToFile();
        durationLabel.setText("Duration: " + bill.getDuration() + " min");
        rateLabel.setText("Rate: Rs " + rate + "/min");
        float parkingAmount = bill.getAmount();
        if (bill instanceof EVBill) {
            EVBill evBill = (EVBill) bill;
            subtotalLabel.setText("Parking Fee: Rs " + parkingAmount + " + Charging: Rs " + evBill.getChargingFee());
            parkingAmount = parkingAmount + evBill.getChargingFee();
        } else {
            subtotalLabel.setText("Parking Fee: Rs " + bill.getAmount());
        }
        totalLabel.setText("Rs " + parkingAmount);
        currentSlot.vacate();
        currentSlot = null;
        AppState.save();
        receiptCard.setVisible(true);
        receiptCard.setManaged(true);
        receiptPlaceholder.setVisible(false);
        receiptPlaceholder.setManaged(false);
        vehicleCard.setVisible(false);
        vehicleCard.setManaged(false);
        exitBtn.setDisable(true);
        if (thankYouLabel != null) {
            thankYouLabel.setText("Thank you! Drive safely. 👋");
            thankYouLabel.setVisible(true);
            thankYouLabel.setManaged(true);
        }
        if (statusLabel != null) {
            statusLabel.setText("Exit complete — bill generated.");
        }
    }
    @FXML
    private void handleBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/userDashboard.fxml");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vehicleCard.setVisible(false);
        vehicleCard.setManaged(false);
        receiptCard.setVisible(false);
        receiptCard.setManaged(false);
        exitBtn.setDisable(true);
        errorLabel.setText("");
        currentSlot = null;
        if (statusLabel != null) {
            statusLabel.setText("Ready");
        }
        if (thankYouLabel != null) {
            thankYouLabel.setVisible(false);
            thankYouLabel.setManaged(false);
        }
        // Enter key: trigger vehicle lookup
        lookupField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    handleLookup(null);
                }
            }
        });
        Platform.runLater(new Runnable() {
            public void run() {
                lookupField.requestFocus();
            }
        });
    }
}