package com.smartpark.controllers;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import parking.ParkingLot;
import parking.ParkingSlot;
import java.net.URL;
import java.util.ResourceBundle;
public class UserDashboardController implements Initializable {
    @FXML
    private Label statLabel;
    @FXML
    private Button backToWelcomeBtn;
    @FXML
    private Label statusLabel;
    @FXML
    private Label occupiedStatLabel;
    @FXML
    private void handleParkVehicle(ActionEvent e) {
        SceneManager.switchTo("/fxml/User Features/parkVehicle.fxml");
    }
    @FXML
    private void handleExitVehicle(ActionEvent e) {
        SceneManager.switchTo("/fxml/User Features/exitVehicle.fxml");
    }
    @FXML
    private void handleViewSlotGrid(ActionEvent e) {
        SceneManager.switchTo("/fxml/User Features/slotGridView.fxml");
    }
    @FXML
    private void handleFindVehicle(ActionEvent e) {
        SceneManager.switchTo("/fxml/User Features/findVehicle.fxml");
    }
    @FXML
    private void handleCheckEVStatus(ActionEvent e) {
        SceneManager.switchTo("/fxml/User Features/evCharging.fxml");
    }
    @FXML
    private void handleBackToWelcome(ActionEvent event) {
        SceneManager.switchTo("/fxml/Main/welcome.fxml");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ParkingLot lot = AppState.getLot();
        int available = 0;
        int occupied = 0;
        for (int i = 0; i < lot.getTotalSlots(); i++) {
            ParkingSlot slot = lot.getSlot(i);
            if (slot.isOccupied()) {
                occupied++;
            } else {
                available++;
            }
        }
        statLabel.setText(String.valueOf(available));
        occupiedStatLabel.setText(String.valueOf(occupied));
        if (statusLabel != null) {
            statusLabel.setText("Ready");
        }
    }
}