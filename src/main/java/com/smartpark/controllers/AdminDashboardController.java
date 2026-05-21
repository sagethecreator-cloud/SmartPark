package com.smartpark.controllers;
import billing.Bill;
import billing.EVBill;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import parking.ParkingLot;
import parking.ParkingSlot;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;
public class AdminDashboardController implements Initializable {
    @FXML
    private Label statLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label availableStatLabel;
    @FXML
    private void handleViewParkedVehicles(ActionEvent e) {
        SceneManager.switchTo("/fxml/Admin Features/parkedVehicles.fxml");
    }
    @FXML
    private void handleViewHistory(ActionEvent e) {
        SceneManager.switchTo("/fxml/Admin Features/billingHistory.fxml");
    }
    @FXML
    private void handleGenerateReport(ActionEvent e) {
        SceneManager.switchTo("/fxml/Admin Features/generateReport.fxml");
    }
    @FXML
    private void handleManageRates(ActionEvent e) {
        SceneManager.switchTo("/fxml/Admin Features/parking_rates.fxml");
    }
    @FXML
    private void handleEVRates(ActionEvent e) {
        SceneManager.switchTo("/fxml/Admin Features/ev_charging_rates.fxml");
    }
    @FXML
    private void handleManageSlots(ActionEvent e) {
        SceneManager.switchTo("/fxml/Admin Features/manageSlots.fxml");
    }
    @FXML
    private void handleMonitorEV(ActionEvent e) {
        SceneManager.switchTo("/fxml/Admin Features/monitor_ev.fxml");
    }
    @FXML
    private void handleExportReport(ActionEvent e) {
        ArrayList<Bill> history = new ArrayList<Bill>(AppState.getBillingHistory());
        if (history.isEmpty()) {
            showExportMessage("Export Report", "No history records to export.");
            if (statusLabel != null) {
                statusLabel.setText("No records to export.");
            }
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Parking Report");
        chooser.setInitialFileName("parking-report-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".txt");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = chooser.showSaveDialog(SceneManager.getStage());
        if (file == null) {
            if (statusLabel != null) {
                statusLabel.setText("Export cancelled.");
            }
            return;
        }
        try {
            writeReportFile(file, history);
            showExportMessage("Export Complete", "Report exported to:\n" + file.getAbsolutePath());
            if (statusLabel != null) {
                statusLabel.setText("Report exported.");
            }
        } catch (IOException ex) {
            showExportMessage("Export Failed", "Could not export report:\n" + ex.getMessage());
            if (statusLabel != null) {
                statusLabel.setText("Export failed.");
            }
        }
    }
    private void writeReportFile(File file, ArrayList<Bill> history) throws IOException {
        Collections.sort(history, new Bill.AmountComparator());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        float totalRevenue = 0;
        float totalExtra = 0;
        FileWriter writer = new FileWriter(file);
        writer.write("========== PARKING MANAGEMENT REPORT ==========\n");
        writer.write("Generated: " + sdf.format(new Date()) + "\n");
        writer.write("Total Records: " + history.size() + "\n");
        writer.write("================================================\n\n");
        for (int i = 0; i < history.size(); i++) {
            Bill b = history.get(i);
            float extra = 0.0f;
            if (b instanceof EVBill) {
                extra = ((EVBill) b).getChargingFee();
            }
            totalRevenue += b.getAmount();
            totalExtra += extra;
            writer.write((i + 1) + ". " + b.getEntryId()
                    + " | " + b.getVehicleNo()
                    + " | " + b.getOwnerName()
                    + " | " + b.getVehicleType()
                    + " | Duration: " + b.getDuration() + " mins"
                    + " | Rs " + b.getAmount());
            if (extra > 0) {
                writer.write(" | Extra: Rs " + extra);
            }
            writer.write("\n");
        }
        writer.write("\n");
        writer.write("================================================\n");
        writer.write("TOTAL REVENUE: Rs " + totalRevenue + "\n");
        writer.write("EXTRA CHARGES: Rs " + totalExtra + "\n");
        writer.write("GRAND TOTAL  : Rs " + (totalRevenue + totalExtra) + "\n");
        writer.close();
    }
    private void showExportMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleClearHistory(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear History");
        alert.setHeaderText("Clear all billing history?");
        alert.setContentText("This action cannot be undone.");
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            AppState.getAdmin().clearHistory();
            AppState.save();
            if (statusLabel != null) {
                statusLabel.setText("Billing history cleared.");
            }
        }
    }
    @FXML
    private void handleLogout(ActionEvent e) {
        AppState.getAdmin().logout();
        SceneManager.switchTo("/fxml/Main/welcome.fxml");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!AppState.getAdmin().isLoggedIn()) {
            SceneManager.switchTo("/fxml/Main/welcome.fxml");
            return;
        }
        ParkingLot lot = AppState.getLot();
        int occupied = 0;
        int available = 0;
        for (int i = 0; i < lot.getTotalSlots(); i++) {
            ParkingSlot slot = lot.getSlot(i);
            if (slot.isOccupied()) {
                occupied++;
            } else {
                available++;
            }
        }
        statLabel.setText(String.valueOf(occupied));
        availableStatLabel.setText(String.valueOf(available));
        if (statusLabel != null) {
            statusLabel.setText("Ready");
        }
    }
}