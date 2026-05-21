package com.smartpark.controllers;
import billing.Bill;
import billing.EVBill;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;
public class BillingHistoryController implements Initializable {
    @FXML
    private TableView<Bill> historyTable;
    @FXML
    private TableColumn<Bill, String> colBillId;
    @FXML
    private TableColumn<Bill, String> colVehicle;
    @FXML
    private TableColumn<Bill, String> colDuration;
    @FXML
    private TableColumn<Bill, String> colAmount;
    @FXML
    private TableColumn<Bill, String> colPaymentTime;
    @FXML
    private TableColumn<Bill, String> colExtra;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> periodCombo;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private ComboBox<String> amountCombo;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnExport;
    @FXML
    private Button btnRefresh;
    @FXML
    private Label countLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label statusLabel;
    private final ObservableList<Bill> tableData = FXCollections.observableArrayList();
    private ArrayList<Bill> allHistory = new ArrayList<Bill>();
    @FXML
    private void handleExport(ActionEvent e) {
        ArrayList<Bill> history = new ArrayList<Bill>();
        for (int i = 0; i < tableData.size(); i++) {
            history.add(tableData.get(i));
        }
        if (history.isEmpty()) {
            history = AppState.getBillingHistory();
            Collections.sort(history, new Bill.AmountComparator());
        }
        if (history.isEmpty()) {
            showExportMessage("Export", "No billing records available to export.");
            statusLabel.setText("No records to export.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Billing History");
        chooser.setInitialFileName("billing-history-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".csv");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showSaveDialog(SceneManager.getStage());
        if (file == null) {
            statusLabel.setText("Export cancelled.");
            return;
        }
        try {
            writeBillingHistoryCsv(file, history);
            showExportMessage("Export Complete", "Billing history exported to:\n" + file.getAbsolutePath());
            statusLabel.setText("Exported " + history.size() + " record(s).");
        } catch (IOException ex) {
            showExportMessage("Export Failed", "Could not export billing history:\n" + ex.getMessage());
            statusLabel.setText("Export failed.");
        }
    }
    @FXML
    private void handleRefresh(ActionEvent e) {
        reloadTable();
    }
    @FXML
    private void handleBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/adminDashboard.fxml");
    }
    private void reloadTable() {
        allHistory = AppState.getBillingHistory();
        applyFilters();
    }
    private void writeBillingHistoryCsv(File file, ArrayList<Bill> history) throws IOException {
        FileWriter writer = new FileWriter(file);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        writer.write("Bill ID,Vehicle No,Owner,Type,Duration Minutes,Parking Amount,Payment Time,Extra Charges,Total Amount\n");
        for (int i = 0; i < history.size(); i++) {
            Bill b = history.get(i);
            float extra = 0.0f;
            if (b instanceof EVBill) {
                extra = ((EVBill) b).getChargingFee();
            }
            writer.write(csv(b.getEntryId()) + ",");
            writer.write(csv(b.getVehicleNo()) + ",");
            writer.write(csv(b.getOwnerName()) + ",");
            writer.write(csv(b.getVehicleType()) + ",");
            writer.write(b.getDuration() + ",");
            writer.write(b.getAmount() + ",");
            writer.write(csv(sdf.format(new Date(b.getExitTime()))) + ",");
            writer.write(extra + ",");
            writer.write(String.valueOf(b.getAmount() + extra));
            writer.write("\n");
        }
        writer.close();
    }
    private String csv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    private void showExportMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void applyFilters() {
        ArrayList<Bill> filtered = new ArrayList<Bill>();
        String search = searchField.getText().trim().toLowerCase();
        String typeFilter = typeCombo.getValue();
        if (typeFilter == null || "All Types".equals(typeFilter)) {
            typeFilter = "";
        }
        long now = System.currentTimeMillis();
        long cutoff = 0;
        String period = periodCombo.getValue();
        if (period != null && !"All Time".equals(period)) {
            if ("Today".equals(period)) {
                cutoff = now - (24L * 60 * 60 * 1000);
            } else if ("This Week".equals(period)) {
                cutoff = now - (7L * 24 * 60 * 60 * 1000);
            } else if ("This Month".equals(period)) {
                cutoff = now - (30L * 24 * 60 * 60 * 1000);
            }
        }
        for (int i = 0; i < allHistory.size(); i++) {
            Bill b = allHistory.get(i);
            if (cutoff > 0 && b.getExitTime() < cutoff) {
                continue;
            }
            if (!typeFilter.isEmpty() && !b.getVehicleType().equals(typeFilter)) {
                continue;
            }
            if (!search.isEmpty()) {
                boolean matches = b.getVehicleNo().toLowerCase().contains(search)
                        || b.getOwnerName().toLowerCase().contains(search)
                        || b.getVehicleType().toLowerCase().contains(search)
                        || b.getEntryId().toLowerCase().contains(search);
                if (!matches) {
                    continue;
                }
            }
            filtered.add(b);
        }
        String sortMode = amountCombo.getValue();
        if ("Highest Amount".equals(sortMode)) {
            Collections.sort(filtered, new Bill.AmountComparator());
        } else if ("Lowest Amount".equals(sortMode)) {
            Collections.sort(filtered, new Bill.AmountComparator());
            Collections.reverse(filtered);
        }
        tableData.clear();
        for (int i = 0; i < filtered.size(); i++) {
            tableData.add(filtered.get(i));
        }
        float total = 0;
        for (int i = 0; i < filtered.size(); i++) {
            Bill b = filtered.get(i);
            total += b.getAmount();
            if (b instanceof EVBill) {
                total += ((EVBill) b).getChargingFee();
            }
        }
        countLabel.setText(String.valueOf(filtered.size()));
        totalLabel.setText("Total: Rs " + String.format("%.2f", total));
        statusLabel.setText(filtered.size() + " record(s) shown");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!AppState.getAdmin().isLoggedIn()) {
            SceneManager.switchTo("/fxml/Main/welcome.fxml");
            return;
        }
        periodCombo.getItems().add("All Time");
        periodCombo.getItems().add("Today");
        periodCombo.getItems().add("This Week");
        periodCombo.getItems().add("This Month");
        periodCombo.setValue("All Time");
        typeCombo.getItems().add("All Types");
        typeCombo.getItems().add("Car");
        typeCombo.getItems().add("Bike");
        typeCombo.getItems().add("Truck");
        typeCombo.getItems().add("EV");
        typeCombo.setValue("All Types");
        amountCombo.getItems().add("Default Order");
        amountCombo.getItems().add("Highest Amount");
        amountCombo.getItems().add("Lowest Amount");
        amountCombo.setValue("Highest Amount");
        colBillId.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> param) {
                return new javafx.beans.property.SimpleStringProperty(param.getValue().getEntryId());
            }
        });
        colVehicle.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> param) {
                Bill b = param.getValue();
                return new javafx.beans.property.SimpleStringProperty(
                        b.getVehicleNo() + " · " + b.getOwnerName() + " (" + b.getVehicleType() + ")");
            }
        });
        colDuration.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> param) {
                return new javafx.beans.property.SimpleStringProperty(param.getValue().getDuration() + " min");
            }
        });
        colAmount.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> param) {
                return new javafx.beans.property.SimpleStringProperty("Rs " + param.getValue().getAmount());
            }
        });
        colPaymentTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> param) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                return new javafx.beans.property.SimpleStringProperty(
                        sdf.format(new Date(param.getValue().getExitTime())));
            }
        });
        colExtra.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> param) {
                Bill b = param.getValue();
                StringBuilder extra = new StringBuilder();
                if (b instanceof EVBill) {
                    EVBill evb = (EVBill) b;
                    extra.append("EV Charging: Rs ");
                    extra.append(evb.getChargingFee());
                }
                if (extra.length() == 0) {
                    extra.append("—");
                }
                return new javafx.beans.property.SimpleStringProperty(
                    extra.toString()
                );
            }
        });
        historyTable.setItems(tableData);
        // Enter key: move focus to results table
        searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    historyTable.requestFocus();
                }
            }
        });
        reloadTable();
        searchField.textProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String o, String n) {
                applyFilters();
            }
        });
        periodCombo.valueProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String o, String n) {
                applyFilters();
            }
        });
        typeCombo.valueProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String o, String n) {
                applyFilters();
            }
        });
        amountCombo.valueProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String o, String n) {
                applyFilters();
            }
        });
    }
}