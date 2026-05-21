package com.smartpark.controllers;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import exceptions.ParkingException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import parking.ParkingLot;
import parking.ParkingSlot;
import vehicles.Bike;
import vehicles.Car;
import vehicles.EV;
import vehicles.Truck;
import vehicles.Vehicle;
import java.net.URL;
import java.util.ResourceBundle;
public class ParkVehicleController implements Initializable {
    @FXML
    private TextField plateField;
    @FXML
    private TextField ownerNameField;
    @FXML
    private ComboBox<String> vehicleTypeCombo;
    @FXML
    private VBox evSection;
    @FXML
    private Slider batterySlider;
    @FXML
    private Label batteryValueLabel;
    @FXML
    private CheckBox chargingCheckBox;
    @FXML
    private Button parkBtn;
    @FXML
    private Label errorLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label slotIdLabel;
    @FXML
    private Label entryIdLabel;
    @FXML
    private Label slotTypeLabel;
    @FXML
    private Label recommendLabel;
    @FXML
    private void handleVehicleTypeChange(ActionEvent e) {
        String selected = vehicleTypeCombo.getValue();
        if ("EV".equals(selected)) {
            evSection.setVisible(true);
            evSection.setManaged(true);
        } else {
            evSection.setVisible(false);
            evSection.setManaged(false);
        }
        errorLabel.setText("");
    }
    @FXML
    private void handlePark(ActionEvent e) {
        String plate = plateField.getText().trim().toUpperCase();
        String owner = ownerNameField.getText().trim();
        if (plate.isEmpty() || owner.isEmpty()) {
            errorLabel.setText("Plate number and owner name are required.");
            return;
        }
        if (plate.length() < 3 || plate.length() > 20) {
            errorLabel.setText("Plate number must be between 3 and 20 characters.");
            return;
        }
        ParkingLot lot = AppState.getLot();
        if (lot.findVehicleByNumber(plate) != null) {
            errorLabel.setText("Vehicle already parked");
            return;
        }
        String type = vehicleTypeCombo.getValue();
        Vehicle vehicle = null;
        if ("Car".equals(type)) {
            vehicle = new Car(plate, owner);
        } else if ("Bike".equals(type)) {
            vehicle = new Bike(plate, owner);
        } else if ("Truck".equals(type)) {
            vehicle = new Truck(plate, owner);
        } else if ("EV".equals(type)) {
            EV ev = new EV(plate, owner);
            if (chargingCheckBox.isSelected()) {
                ev.setChargingRequested(true);
                ev.setBatteryOnArrival((int) batterySlider.getValue());
            } else {
                ev.setChargingRequested(false);
            }
            vehicle = ev;
        } else {
            errorLabel.setText("Unsupported vehicle type.");
            return;
        }
        vehicle.generateEntryId();
        try {
            ParkingSlot slot = lot.assignSlot(vehicle);
            slotIdLabel.setText("Slot: " + slot.getSlotId());
            slotTypeLabel.setText("Type: " + slot.getSlotType());
            entryIdLabel.setText("Entry ID: " + vehicle.getEntryId());
            recommendLabel.setText(slot.getSlotType() + " slot assigned");
            statusLabel.setText("✅ Parked successfully!");
            statusLabel.setStyle("-fx-text-fill: #00b894; -fx-font-size: 13; -fx-font-weight: bold;");
            clearInputs();
            errorLabel.setText("");
            AppState.save();
        } catch (ParkingException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }
    @FXML
    private void handleBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/userDashboard.fxml");
    }
    private void clearInputs() {
        plateField.clear();
        ownerNameField.clear();
        vehicleTypeCombo.setValue("Car");
        // Reset the EV section after changing the vehicle type programmatically
        evSection.setVisible(false);
        evSection.setManaged(false);
        batterySlider.setValue(50);
        batteryValueLabel.setText("50%");
        chargingCheckBox.setSelected(false);
        errorLabel.setText("");
        Platform.runLater(new Runnable() {
            public void run() {
                plateField.requestFocus();
            }
        });
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vehicleTypeCombo.getItems().add("Car");
        vehicleTypeCombo.getItems().add("Bike");
        vehicleTypeCombo.getItems().add("Truck");
        vehicleTypeCombo.getItems().add("EV");
        vehicleTypeCombo.setValue("Car");
        evSection.setVisible(false);
        evSection.setManaged(false);
        errorLabel.setText("");
        batteryValueLabel.setText("50%");
        batterySlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
                batteryValueLabel.setText((int) newVal.doubleValue() + "%");
            }
        });
        // Enter key: move to owner name
        plateField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    ownerNameField.requestFocus();
                }
            }
        });
        // Enter key: move focus to vehicle type combo
        ownerNameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    vehicleTypeCombo.requestFocus();
                }
            }
        });
        // Enter key: trigger park action directly
        vehicleTypeCombo.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    handlePark(null);
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