package com.smartpark.controllers;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import exceptions.ParkingException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import parking.ParkingLot;
import parking.ParkingSlot;
import vehicles.Vehicle;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;
public class ManageSlotsController implements Initializable {
    @FXML
    private VBox slotGrid;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private ComboBox<String> editTypeCombo;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnRemove;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnBack;
    @FXML
    private TextField slotIdField;
    @FXML
    private Label statTotal;
    @FXML
    private Label statAvailable;
    @FXML
    private Label statOccupied;
    @FXML
    private Label statEV;
    @FXML
    private Label statusLabel;
    @FXML
    private void handleAddSlot(ActionEvent e) {
        String slotType = typeCombo.getValue();
        if (slotType == null || slotType.isEmpty()) {
            statusLabel.setText("Select a slot type.");
            return;
        }
        ParkingLot lot = AppState.getLot();
        lot.addSlot(slotType);
        statusLabel.setText("Slot added. Total: " + lot.getTotalSlots());
        refreshStats();
        renderSlotList();
        AppState.save();
    }
    @FXML
    private void handleRemoveSlot(ActionEvent e) {
        String idText = slotIdField.getText().trim();
        if (idText.isEmpty()) {
            statusLabel.setText("Enter a slot ID to remove.");
            return;
        }
        try {
            int slotId = Integer.parseInt(idText);
            ParkingLot lot = AppState.getLot();
            lot.removeSlot(slotId);
            statusLabel.setText("Slot removed. Total: " + lot.getTotalSlots());
            refreshStats();
            renderSlotList();
            slotIdField.clear();
            AppState.save();
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid slot ID.");
        } catch (ParkingException ex) {
            statusLabel.setText(ex.getMessage());
        }
    }
    @FXML
    private void handleEditSlot(ActionEvent e) {
        String idText = slotIdField.getText().trim();
        if (idText.isEmpty()) {
            statusLabel.setText("Enter a slot ID to edit.");
            return;
        }
        String newType = editTypeCombo.getValue();
        if (newType == null || newType.isEmpty()) {
            statusLabel.setText("Select a new slot type from the Edit combo.");
            return;
        }
        int slotId;
        try {
            slotId = Integer.parseInt(idText);
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid slot ID.");
            return;
        }
        ParkingLot lot = AppState.getLot();
        // Only editable empty slots can be changed
        ParkingSlot target = null;
        for (int i = 0; i < lot.getTotalSlots(); i++) {
            if (lot.getSlot(i).getSlotId() == slotId) {
                target = lot.getSlot(i);
                break;
            }
        }
        if (target == null) {
            statusLabel.setText("Slot " + slotId + " does not exist.");
            return;
        }
        if (target.isOccupied()) {
            statusLabel.setText(
                "Cannot edit an occupied slot. Vehicle must exit first."
            );
            return;
        }
        target.setSlotType(newType);
        statusLabel.setText("Slot " + slotId + " type changed to " + newType + ".");
        refreshStats();
        renderSlotList();
        slotIdField.clear();
        AppState.save();
    }
    @FXML
    private void handleSave(ActionEvent e) {
        AppState.save();
        statusLabel.setText("Slots saved.");
    }
    @FXML
    private void handleBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/adminDashboard.fxml");
    }
    private void refreshStats() {
        ParkingLot lot = AppState.getLot();
        int total = lot.getTotalSlots();
        int available = 0;
        int occupied = 0;
        int evSlots = 0;
        for (int i = 0; i < lot.getTotalSlots(); i++) {
            ParkingSlot slot = lot.getSlot(i);
            if (slot.getSlotType().equals("EV")) {
                evSlots++;
            }
            if (slot.isOccupied()) {
                occupied++;
            } else {
                available++;
            }
        }
        statTotal.setText(String.valueOf(total));
        statAvailable.setText(String.valueOf(available));
        statOccupied.setText(String.valueOf(occupied));
        statEV.setText(String.valueOf(evSlots));
    }
    private void renderSlotList() {
        slotGrid.getChildren().clear();
        ParkingLot lot = AppState.getLot();
        for (int i = 0; i < lot.getTotalSlots(); i++) {
            ParkingSlot slot = lot.getSlot(i);
            HBox row = new HBox(12);
            String bg = slot.isOccupied() ? "rgba(225,112,85,0.12)" : "rgba(0,184,148,0.10)";
            row.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 8; "
                    + "-fx-padding: 8 14; -fx-alignment: CENTER_LEFT;");
            Label idLbl = new Label("Slot " + slot.getSlotId());
            idLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");
            Label typeLbl = new Label(slot.getSlotType());
            typeLbl.setStyle("-fx-text-fill: #a29bfe; -fx-font-size: 12;");
            Label statusLbl = new Label(slot.isOccupied() ? "Occupied" : "Available");
            statusLbl.setStyle("-fx-text-fill: #c0c0d0; -fx-font-size: 11;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            row.getChildren().add(idLbl);
            row.getChildren().add(typeLbl);
            row.getChildren().add(statusLbl);
            row.getChildren().add(spacer);
            if (slot.isOccupied()) {
                Vehicle v = slot.getParkedVehicle();
                Label vLbl = new Label(v.getType() + " · " + v.getVehicleNo());
                vLbl.setStyle("-fx-text-fill: #8888aa; -fx-font-size: 11;");
                row.getChildren().add(vLbl);
            }
            slotGrid.getChildren().add(row);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!AppState.getAdmin().isLoggedIn()) {
            SceneManager.switchTo("/fxml/Main/welcome.fxml");
            return;
        }
        typeCombo.getItems().add("Compact");
        typeCombo.getItems().add("Motorcycle");
        typeCombo.getItems().add("Large");
        typeCombo.getItems().add("EV");
        typeCombo.setValue("Compact");
        editTypeCombo.getItems().add("Compact");
        editTypeCombo.getItems().add("Motorcycle");
        editTypeCombo.getItems().add("Large");
        editTypeCombo.getItems().add("EV");
        refreshStats();
        renderSlotList();
        // Enter key: trigger remove slot
        slotIdField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    handleRemoveSlot(null);
                }
            }
        });
        Platform.runLater(new Runnable() {
            public void run() {
                slotIdField.requestFocus();
            }
        });
    }
}