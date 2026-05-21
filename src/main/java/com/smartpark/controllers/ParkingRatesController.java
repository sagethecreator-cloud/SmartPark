package com.smartpark.controllers;
import com.smartpark.AppState;
import com.smartpark.SceneManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import parking.ParkingLot;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;
public class ParkingRatesController implements Initializable {
    @FXML
    private TextField carRateField;
    @FXML
    private TextField bikeRateField;
    @FXML
    private TextField truckRateField;
    @FXML
    private TextField evRateField;
    @FXML
    private Button saveCarRate;
    @FXML
    private Button saveBikeRate;
    @FXML
    private Button saveTruckRate;
    @FXML
    private Button saveEvRate;
    @FXML
    private Button saveAllRates;
    @FXML
    private Button backBtn;
    @FXML
    private Label carRateError;
    @FXML
    private Label bikeRateError;
    @FXML
    private Label truckRateError;
    @FXML
    private Label evRateError;
    @FXML
    private void saveCarRate(ActionEvent e) {
        saveSingleRate("Car", carRateField, carRateError, saveCarRate);
    }
    @FXML
    private void saveBikeRate(ActionEvent e) {
        saveSingleRate("Bike", bikeRateField, bikeRateError, saveBikeRate);
    }
    @FXML
    private void saveTruckRate(ActionEvent e) {
        saveSingleRate("Truck", truckRateField, truckRateError, saveTruckRate);
    }
    @FXML
    private void saveEvRate(ActionEvent e) {
        saveSingleRate("EV", evRateField, evRateError, saveEvRate);
    }
    @FXML
    private void saveAllRates(ActionEvent e) {
        hideAllErrors();
        float car = parseRate(carRateField, carRateError);
        float bike = parseRate(bikeRateField, bikeRateError);
        float truck = parseRate(truckRateField, truckRateError);
        float ev = parseRate(evRateField, evRateError);
        if (car < 0 || bike < 0 || truck < 0 || ev < 0) {
            return;
        }
        ParkingLot lot = AppState.getLot();
        lot.updateRate("Car", car);
        lot.updateRate("Bike", bike);
        lot.updateRate("Truck", truck);
        lot.updateRate("EV", ev);
        flashSuccess(saveAllRates);
        AppState.save();
    }
    @FXML
    private void goBack(ActionEvent e) {
        SceneManager.switchTo("/fxml/Main/adminDashboard.fxml");
    }
    private void saveSingleRate(String type, TextField field, Label errorLabel, Button btn) {
        hideAllErrors();
        float val = parseRate(field, errorLabel);
        if (val < 0) {
            return;
        }
        ParkingLot lot = AppState.getLot();
        lot.updateRate(type, val);
        flashSuccess(btn);
        AppState.save();
    }
    private float parseRate(TextField field, Label errorLabel) {
        try {
            float val = Float.parseFloat(field.getText().trim());
            if (val <= 0) {
                showError(errorLabel, "Rate must be greater than 0.");
                return -1;
            }
            return val;
        } catch (NumberFormatException e) {
            showError(errorLabel, "Enter a valid number.");
            return -1;
        }
    }
    private void showError(Label label, String msg) {
        label.setText(msg);
        label.setVisible(true);
        label.setManaged(true);
    }
    private void hideAllErrors() {
        carRateError.setVisible(false);
        carRateError.setManaged(false);
        bikeRateError.setVisible(false);
        bikeRateError.setManaged(false);
        truckRateError.setVisible(false);
        truckRateError.setManaged(false);
        evRateError.setVisible(false);
        evRateError.setManaged(false);
    }
    private void flashSuccess(Button btn) {
        final String original = btn.getStyle();
        btn.setStyle("-fx-background-color: #00b894; -fx-text-fill: white;");
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(400),
                        new javafx.event.EventHandler<javafx.event.ActionEvent>() {
                            public void handle(javafx.event.ActionEvent ev) {
                                btn.setStyle(original);
                            }
                        }));
        timeline.play();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!AppState.getAdmin().isLoggedIn()) {
            SceneManager.switchTo("/fxml/Main/welcome.fxml");
            return;
        }
        ParkingLot lot = AppState.getLot();
        carRateField.setText(String.valueOf(lot.getRate("Car")));
        bikeRateField.setText(String.valueOf(lot.getRate("Bike")));
        truckRateField.setText(String.valueOf(lot.getRate("Truck")));
        evRateField.setText(String.valueOf(lot.getRate("EV")));
        hideAllErrors();
        // Enter key: move to bike rate
        carRateField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    bikeRateField.requestFocus();
                }
            }
        });
        // Enter key: move to truck rate
        bikeRateField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    truckRateField.requestFocus();
                }
            }
        });
        // Enter key: move to EV rate
        truckRateField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    evRateField.requestFocus();
                }
            }
        });
        // Enter key: save all rates
        evRateField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    saveAllRates(null);
                }
            }
        });
        Platform.runLater(new Runnable() {
            public void run() {
                carRateField.requestFocus();
            }
        });
    }
}