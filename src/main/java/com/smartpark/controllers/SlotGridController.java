package com.smartpark.controllers;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import parking.ParkingLot;
import parking.ParkingSlot;
import vehicles.Vehicle;
import java.net.URL;
import java.util.ResourceBundle;
public class SlotGridController implements Initializable {
    @FXML
    private FlowPane gridContainer;
    @FXML
    private ToggleButton filterAll;
    @FXML
    private ToggleButton filterCompact;
    @FXML
    private ToggleButton filterMotorcycle;
    @FXML
    private ToggleButton filterLarge;
    @FXML
    private ToggleButton filterEV;
    @FXML
    private ToggleGroup filterGroup;
    @FXML
    private Label statsLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Button refreshBtn;
    @FXML
    private void handleRefresh(ActionEvent e) {
        renderGrid();
    }
    @FXML
    private void handleBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/userDashboard.fxml");
    }
    private void renderGrid() {
        gridContainer.getChildren().clear();
        ParkingLot lot = AppState.getLot();
        String filter = getActiveFilter();
        int total = lot.getTotalSlots();
        int occupied = 0;
        int available = 0;
        for (int i = 0; i < lot.getTotalSlots(); i++) {
            ParkingSlot slot = lot.getSlot(i);
            if (slot.isOccupied()) {
                occupied++;
            } else {
                available++;
            }
            if (!matchesFilter(slot.getSlotType(), filter)) {
                continue;
            }
            HBox row = buildSlotRow(slot);
            gridContainer.getChildren().add(row);
        }
        statsLabel.setText(available + " / " + total + " Available");
        if (statusLabel != null) {
            statusLabel.setText(occupied + " occupied · " + available + " available");
        }
    }
    private String getActiveFilter() {
        Toggle selected = filterGroup.getSelectedToggle();
        if (selected == filterCompact) {
            return "Compact";
        }
        if (selected == filterMotorcycle) {
            return "Motorcycle";
        }
        if (selected == filterLarge) {
            return "Large";
        }
        if (selected == filterEV) {
            return "EV";
        }
        return "All";
    }
    private boolean matchesFilter(String slotType, String filter) {
        if ("All".equals(filter)) {
            return true;
        }
        return filter.equals(slotType);
    }
    private HBox buildSlotRow(ParkingSlot slot) {
        String bgColor;
        String borderColor;
        if (slot.getSlotType().equals("EV")) {
            bgColor = "rgba(108,92,231,0.15)";
            borderColor = "rgba(162,155,254,0.45)";
        } else if (slot.isOccupied()) {
            bgColor = "rgba(225,112,85,0.12)";
            borderColor = "rgba(225,112,85,0.35)";
        } else {
            bgColor = "rgba(0,184,148,0.10)";
            borderColor = "rgba(0,184,148,0.35)";
        }
        HBox row = new HBox(12);
        row.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; "
                + "-fx-border-color: " + borderColor + "; -fx-border-radius: 10; -fx-border-width: 1; "
                + "-fx-padding: 10 16; -fx-alignment: CENTER_LEFT;");
        Label idLabel = new Label("Slot " + slot.getSlotId());
        idLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        Label typeLabel = new Label(slot.getSlotType());
        typeLabel.setStyle("-fx-text-fill: #a29bfe; -fx-font-size: 12; -fx-font-family: 'Segoe UI';");
        String badgeText;
        String badgeColor;
        if (slot.isOccupied()) {
            badgeText = "OCCUPIED";
            badgeColor = "#e17055";
        } else {
            badgeText = "AVAILABLE";
            badgeColor = "#00b894";
        }
        Label badge = new Label(badgeText);
        badge.setStyle("-fx-text-fill: white; -fx-background-color: " + badgeColor
                + "; -fx-background-radius: 6; -fx-padding: 2 8; -fx-font-size: 10; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().add(idLabel);
        row.getChildren().add(typeLabel);
        row.getChildren().add(badge);
        row.getChildren().add(spacer);
        if (slot.isOccupied()) {
            Vehicle v = slot.getParkedVehicle();
            Label vehicleInfo = new Label(v.getType() + " · " + v.getVehicleNo() + " · " + v.getOwnerName());
            vehicleInfo.setStyle("-fx-text-fill: #c0c0d0; -fx-font-size: 11; -fx-font-family: 'Segoe UI';");
            row.getChildren().add(vehicleInfo);
        }
        row.setPrefWidth(1100);
        return row;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (filterGroup == null) {
            filterGroup = new ToggleGroup();
            filterAll.setToggleGroup(filterGroup);
            filterCompact.setToggleGroup(filterGroup);
            filterMotorcycle.setToggleGroup(filterGroup);
            filterLarge.setToggleGroup(filterGroup);
            filterEV.setToggleGroup(filterGroup);
        }
        filterAll.setSelected(true);
        filterGroup.selectedToggleProperty().addListener(new javafx.beans.value.ChangeListener<Toggle>() {
            public void changed(javafx.beans.value.ObservableValue<? extends Toggle> obs, Toggle oldVal, Toggle newVal) {
                renderGrid();
            }
        });
        renderGrid();
    }
}