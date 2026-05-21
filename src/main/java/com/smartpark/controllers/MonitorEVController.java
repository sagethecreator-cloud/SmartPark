package com.smartpark.controllers;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import parking.ParkingLot;
import parking.ParkingSlot;
import vehicles.EV;
import vehicles.Vehicle;
import java.net.URL;
import java.util.ResourceBundle;
public class MonitorEVController implements Initializable {
    @FXML
    private VBox evCardsContainer;
    @FXML
    private VBox emptyStateBox;
    @FXML
    private ScrollPane evScrollPane;
    @FXML
    private Button refreshBtn;
    @FXML
    private Button backBtn;
    @FXML
    private Label evCountLabel;
    @FXML
    private void refreshMonitor(ActionEvent e) {
        loadEVCards();
    }
    @FXML
    private void goBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/adminDashboard.fxml");
    }
    private void loadEVCards() {
        for (int i = evCardsContainer.getChildren().size() - 1; i >= 0; i--) {
            javafx.scene.Node node = evCardsContainer.getChildren().get(i);
            if (node != emptyStateBox) {
                evCardsContainer.getChildren().remove(i);
            }
        }
        ParkingLot lot = AppState.getLot();
        long now = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; i < lot.getTotalSlots(); i++) {
            ParkingSlot slot = lot.getSlot(i);
            if (!slot.isOccupied()) {
                continue;
            }
            Vehicle v = slot.getParkedVehicle();
            if (!(v instanceof EV)) {
                continue;
            }
            EV ev = (EV) v;
            if (!ev.isChargingRequested()) {
                continue;
            }
            count++;
            int elapsedMins = (int) ((now - ev.getEntryTime()) / 60000);
            double currentBattery = ev.getBatteryOnArrival() + (elapsedMins * 1.8);
            if (currentBattery > 100) {
                currentBattery = 100;
            }
            int charged = (int) currentBattery - ev.getBatteryOnArrival();
            double minutesToFull = ((100 - ev.getBatteryOnArrival()) / 1.8) - elapsedMins;
            if (minutesToFull < 0) {
                minutesToFull = 0;
            }
            float feeSoFar = lot.calculateLiveChargingFee(elapsedMins, ev.getBatteryOnArrival());
            String status;
            String statusColor;
            if (currentBattery >= 100) {
                status = "FULLY CHARGED ✔";
                statusColor = "#00b894";
            } else {
                status = "CHARGING ⚡";
                statusColor = "#a29bfe";
            }
            String modeLabel;
            if (lot.getChargingMode().equals("perPercent")) {
                modeLabel = "Per Percent (Rs " + lot.getChargingRatePerPercent() + "/%)";
            } else {
                modeLabel = "Per Minute (Rs " + lot.getChargingRatePerMin() + "/min)";
            }
            VBox card = buildEvCard(ev, slot, (int) currentBattery, charged, status, statusColor,
                    elapsedMins, minutesToFull, feeSoFar, modeLabel);
            evCardsContainer.getChildren().add(0, card);
        }
        if (count == 0) {
            emptyStateBox.setVisible(true);
            emptyStateBox.setManaged(true);
        } else {
            emptyStateBox.setVisible(false);
            emptyStateBox.setManaged(false);
        }
        evCountLabel.setText(count + " EV" + (count == 1 ? "" : "s") + " Charging");
    }
    private VBox buildEvCard(EV ev, ParkingSlot slot, int currentBattery, int charged,
                             String status, String statusColor, int elapsedMins,
                             double minutesToFull, float feeSoFar, String modeLabel) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #1e1a2e; -fx-background-radius: 12; "
                + "-fx-border-color: rgba(108,92,231,0.3); -fx-border-radius: 12; -fx-border-width: 1; "
                + "-fx-padding: 16;");
        HBox header = new HBox(12);
        Label plateLbl = new Label(ev.getVehicleNo());
        plateLbl.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
        Label ownerLbl = new Label(ev.getOwnerName());
        ownerLbl.setStyle("-fx-text-fill: #c0c0d0; -fx-font-size: 12;");
        Label slotLbl = new Label("Slot " + slot.getSlotId());
        slotLbl.setStyle("-fx-text-fill: #a29bfe; -fx-font-size: 12;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label badge = new Label(status);
        badge.setStyle("-fx-text-fill: white; -fx-background-color: " + statusColor
                + "; -fx-background-radius: 6; -fx-padding: 4 10; -fx-font-size: 11; -fx-font-weight: bold;");
        header.getChildren().add(plateLbl);
        header.getChildren().add(ownerLbl);
        header.getChildren().add(slotLbl);
        header.getChildren().add(spacer);
        header.getChildren().add(badge);
        Label batteryLbl = new Label("Battery: " + ev.getBatteryOnArrival() + "% → " + currentBattery
                + "% (+" + charged + "%)");
        batteryLbl.setStyle("-fx-text-fill: #c0c0d0; -fx-font-size: 12;");
        ProgressBar bar = new ProgressBar(currentBattery / 100.0);
        bar.setPrefWidth(800);
        bar.setStyle("-fx-accent: #6c5ce7;");
        Label modeLbl = new Label("Mode: " + modeLabel);
        modeLbl.setStyle("-fx-text-fill: #8888aa; -fx-font-size: 11;");
        Label timeLbl = new Label("Elapsed: " + elapsedMins + " min · To full: " + (int) minutesToFull
                + " min · Fee: Rs " + feeSoFar);
        timeLbl.setStyle("-fx-text-fill: #c0c0d0; -fx-font-size: 12;");
        card.getChildren().add(header);
        card.getChildren().add(batteryLbl);
        card.getChildren().add(bar);
        card.getChildren().add(modeLbl);
        card.getChildren().add(timeLbl);
        return card;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!AppState.getAdmin().isLoggedIn()) {
            SceneManager.switchTo("/fxml/Main/welcome.fxml");
            return;
        }
        loadEVCards();
    }
}