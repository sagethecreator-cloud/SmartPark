package com.smartpark.controllers;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.util.Callback;
import parking.ParkingLot;
import parking.ParkingSlot;
import vehicles.Vehicle;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;
public class ParkedVehiclesController implements Initializable {
    public static class ParkedRow {
        private final Vehicle vehicle;
        private final ParkingSlot slot;
        public ParkedRow(Vehicle vehicle, ParkingSlot slot) {
            this.vehicle = vehicle;
            this.slot = slot;
        }
        public Vehicle getVehicle() {
            return vehicle;
        }
        public ParkingSlot getSlot() {
            return slot;
        }
    }
    @FXML
    private TableView<ParkedRow> vehicleTable;
    @FXML
    private TableColumn<ParkedRow, String> colType;
    @FXML
    private TableColumn<ParkedRow, String> colPlate;
    @FXML
    private TableColumn<ParkedRow, String> colEntryTime;
    @FXML
    private TableColumn<ParkedRow, String> colSlot;
    @FXML
    private TableColumn<ParkedRow, String> colDuration;
    @FXML
    private TableColumn<ParkedRow, String> colStatus;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterCombo;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnRefresh;
    @FXML
    private Label countLabel;
    private final ObservableList<ParkedRow> tableData = FXCollections.observableArrayList();
    @FXML
    private void handleRefresh(ActionEvent e) {
        loadData();
    }
    @FXML
    private void handleBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/adminDashboard.fxml");
    }
    private void loadData() {
        tableData.clear();
        ParkingLot lot = AppState.getLot();
        ArrayList<ParkedRow> rows = new ArrayList<ParkedRow>();
        String search = searchField.getText().trim().toLowerCase();
        String typeFilter = filterCombo.getValue();
        if (typeFilter == null || "All Types".equals(typeFilter)) {
            typeFilter = "";
        }
        for (int i = 0; i < lot.getTotalSlots(); i++) {
            ParkingSlot slot = lot.getSlot(i);
            if (slot.isOccupied()) {
                Vehicle v = slot.getParkedVehicle();
                if (!typeFilter.isEmpty() && !v.getType().equals(typeFilter)) {
                    continue;
                }
                if (!search.isEmpty()) {
                    boolean matches = v.getVehicleNo().toLowerCase().contains(search)
                            || v.getOwnerName().toLowerCase().contains(search)
                            || v.getType().toLowerCase().contains(search);
                    if (!matches) {
                        continue;
                    }
                }
                rows.add(new ParkedRow(v, slot));
            }
        }
        Collections.sort(rows, new java.util.Comparator<ParkedRow>() {
            public int compare(ParkedRow a, ParkedRow b) {
                return a.getVehicle().compareTo(b.getVehicle());
            }
        });
        for (int i = 0; i < rows.size(); i++) {
            tableData.add(rows.get(i));
        }
        countLabel.setText(String.valueOf(tableData.size()));
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!AppState.getAdmin().isLoggedIn()) {
            SceneManager.switchTo("/fxml/Main/welcome.fxml");
            return;
        }
        filterCombo.getItems().add("All Types");
        filterCombo.getItems().add("Car");
        filterCombo.getItems().add("Bike");
        filterCombo.getItems().add("Truck");
        filterCombo.getItems().add("EV");
        filterCombo.setValue("All Types");
        colType.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ParkedRow, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ParkedRow, String> param) {
                return new javafx.beans.property.SimpleStringProperty(param.getValue().getVehicle().getType());
            }
        });
        colPlate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ParkedRow, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ParkedRow, String> param) {
                return new javafx.beans.property.SimpleStringProperty(param.getValue().getVehicle().getVehicleNo());
            }
        });
        colSlot.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ParkedRow, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ParkedRow, String> param) {
                return new javafx.beans.property.SimpleStringProperty(String.valueOf(param.getValue().getSlot().getSlotId()));
            }
        });
        colEntryTime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ParkedRow, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ParkedRow, String> param) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                return new javafx.beans.property.SimpleStringProperty(
                        sdf.format(new Date(param.getValue().getVehicle().getEntryTime())));
            }
        });
        colDuration.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ParkedRow, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ParkedRow, String> param) {
                long now = System.currentTimeMillis();
                int mins = (int) ((now - param.getValue().getVehicle().getEntryTime()) / 60000);
                return new javafx.beans.property.SimpleStringProperty(mins + " min");
            }
        });
        colStatus.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ParkedRow, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ParkedRow, String> param) {
                return new javafx.beans.property.SimpleStringProperty("Parked");
            }
        });
        vehicleTable.setItems(tableData);
        searchField.textProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String o, String n) {
                loadData();
            }
        });
        filterCombo.valueProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            public void changed(javafx.beans.value.ObservableValue<? extends String> obs, String o, String n) {
                loadData();
            }
        });
        loadData();
    }
}