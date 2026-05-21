package com.smartpark;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
public class SceneManager {
    private static final Color APP_BG = Color.web("#0f0f1a");
    private static Stage primaryStage;
    public static void init(Stage stage) {
        primaryStage = stage;
    }
    public static void switchTo(String fxmlPath) {
        try {
            double savedX      = primaryStage.getX();
            double savedY      = primaryStage.getY();
            double savedWidth  = primaryStage.getWidth();
            double savedHeight = primaryStage.getHeight();
            URL resource = SceneManager.class.getResource(fxmlPath);
            if (resource == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText("Could not load: " + fxmlPath);
                alert.setContentText("FXML resource not found at path: " + fxmlPath);
                alert.showAndWait();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent newRoot = loader.load();
            if (newRoot instanceof Region) {
                ((Region) newRoot).setMaxWidth(Double.MAX_VALUE);
                ((Region) newRoot).setMaxHeight(Double.MAX_VALUE);
                ((Region) newRoot).setPrefWidth(savedWidth);
                ((Region) newRoot).setPrefHeight(savedHeight);
            }
            Scene newScene = new Scene(newRoot, savedWidth, savedHeight);
            newScene.setFill(APP_BG);
            primaryStage.setScene(newScene);
            if (!primaryStage.isMaximized()) {
                primaryStage.setX(savedX);
                primaryStage.setY(savedY);
                primaryStage.setWidth(savedWidth);
                primaryStage.setHeight(savedHeight);
            }
            newScene.widthProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(
                        ObservableValue<? extends Number> obs,
                        Number oldVal, Number newVal) {
                        if (newScene.getRoot() instanceof Region) {
                            ((Region) newScene.getRoot())
                                .setPrefWidth(newVal.doubleValue());
                        }
                    }
                }
            );
            newScene.heightProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(
                        ObservableValue<? extends Number> obs,
                        Number oldVal, Number newVal) {
                        if (newScene.getRoot() instanceof Region) {
                            ((Region) newScene.getRoot())
                                .setPrefHeight(newVal.doubleValue());
                        }
                    }
                }
            );
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not load: " + fxmlPath);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    public static Stage getStage() {
        return primaryStage;
    }
}