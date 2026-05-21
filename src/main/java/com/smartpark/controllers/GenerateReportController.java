package com.smartpark.controllers;
import billing.Bill;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
public class GenerateReportController implements Initializable {
    @FXML
    private ToggleButton btnDay;
    @FXML
    private ToggleButton btnWeek;
    @FXML
    private ToggleButton btnMonth;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button btnGenerate;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnBack;
    @FXML
    private Label statVehicles;
    @FXML
    private Label statEarnings;
    @FXML
    private Label statOccupied;
    @FXML
    private Label statEV;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea previewArea;
    private ToggleGroup periodGroup;
    private ArrayList<Bill> filteredHistory = new ArrayList<Bill>();
    @FXML
    private void handleGenerate(ActionEvent e) {
        String period = "day";
        if (periodGroup.getSelectedToggle() == btnWeek) {
            period = "week";
        } else if (periodGroup.getSelectedToggle() == btnMonth) {
            period = "month";
        }
        ArrayList<Bill> history = AppState.getBillingHistory();
        filteredHistory = new ArrayList<Bill>();
        long now = System.currentTimeMillis();
        long cutoff;
        if (period.equals("day")) {
            cutoff = now - (24L * 60 * 60 * 1000);
        } else if (period.equals("week")) {
            cutoff = now - (7L * 24 * 60 * 60 * 1000);
        } else {
            cutoff = now - (30L * 24 * 60 * 60 * 1000);
        }
        if (datePicker.getValue() != null) {
            // A selected date overrides the period buttons and starts at that day.
            java.time.LocalDate selected = datePicker.getValue();
            java.time.LocalDateTime startOfDay = selected.atStartOfDay();
            cutoff = startOfDay.atZone(
                java.time.ZoneId.systemDefault()
            ).toInstant().toEpochMilli();
            // Use the selected date in the report heading
            period = "custom (" + selected.toString() + ")";
        }
        int totalVehicles = 0;
        float totalRevenue = 0;
        int totalMinutes = 0;
        int evCount = 0;
        for (int i = 0; i < history.size(); i++) {
            Bill b = history.get(i);
            if (b.getExitTime() >= cutoff) {
                totalVehicles++;
                totalRevenue += b.getAmount();
                totalMinutes += b.getDuration();
                if ("EV".equals(b.getVehicleType())) {
                    evCount++;
                }
                filteredHistory.add(b);
            }
        }
        statVehicles.setText(String.valueOf(totalVehicles));
        statEarnings.setText("Rs " + String.format("%.2f", totalRevenue));
        statOccupied.setText(String.valueOf(totalMinutes));
        statEV.setText(String.valueOf(evCount));
        StringBuilder sb = new StringBuilder();
        sb.append("========== PARKING REPORT (").append(period.toUpperCase()).append(") ==========\n");
        sb.append("Generated: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
        sb.append("Total Vehicles: ").append(totalVehicles).append("\n");
        sb.append("Total Revenue : Rs ").append(totalRevenue).append("\n");
        sb.append("Total Minutes : ").append(totalMinutes).append("\n");
        sb.append("EV Exits      : ").append(evCount).append("\n");
        if (totalVehicles > 0) {
            sb.append("Avg Duration  : ").append(totalMinutes / totalVehicles).append(" mins\n");
            sb.append("Avg Revenue   : Rs ").append(totalRevenue / totalVehicles).append("\n");
        }
        sb.append("================================================\n\n");
        for (int i = 0; i < filteredHistory.size(); i++) {
            Bill b = filteredHistory.get(i);
            sb.append((i + 1)).append(". ").append(b.toString()).append("\n");
        }
        previewArea.setText(sb.toString());
        statusLabel.setText("Report generated");
    }
    @FXML
    private void handleSave(ActionEvent e) {
        if (previewArea.getText() == null || previewArea.getText().trim().isEmpty()) {
            handleGenerate(null);
        }
        String reportText = previewArea.getText();
        if (reportText == null || reportText.trim().isEmpty()) {
            statusLabel.setText("No report to save.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Parking Report");
        chooser.setInitialFileName("parking-report-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".txt");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = chooser.showSaveDialog(SceneManager.getStage());
        if (file == null) {
            statusLabel.setText("Save cancelled.");
            return;
        }
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(reportText);
            writer.close();
            statusLabel.setText("Report saved.");
        } catch (IOException ex) {
            statusLabel.setText("Save failed: " + ex.getMessage());
        }
    }
    @FXML
    private void handleBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/adminDashboard.fxml");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!AppState.getAdmin().isLoggedIn()) {
            SceneManager.switchTo("/fxml/Main/welcome.fxml");
            return;
        }
        periodGroup = new ToggleGroup();
        btnDay.setToggleGroup(periodGroup);
        btnWeek.setToggleGroup(periodGroup);
        btnMonth.setToggleGroup(periodGroup);
        btnDay.setSelected(true);
        previewArea.setEditable(false);
        datePicker.setValue(null);
        periodGroup.selectedToggleProperty().addListener(
            new javafx.beans.value.ChangeListener<Toggle>() {
                public void changed(
                    javafx.beans.value.ObservableValue<? extends Toggle> obs,
                    Toggle oldVal, Toggle newVal) {
                    if (newVal != null) {
                        datePicker.setValue(null);
                    }
                }
            }
        );
    }
}