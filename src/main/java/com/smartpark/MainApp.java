package com.smartpark;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        AppState.init();
        SceneManager.init(primaryStage);
        primaryStage.setTitle("SmartPark — Parking Management System");
        // Keep the window resizable so the maximize control stays available.
        primaryStage.setResizable(true);
        primaryStage.setOnCloseRequest(new EventHandler<javafx.stage.WindowEvent>() {
            public void handle(javafx.stage.WindowEvent e) {
                AppState.shutdown();
            }
        });
        SceneManager.switchTo("/fxml/Main/welcome.fxml");
        // Show the stage before requesting the maximized state.
        primaryStage.show();
        // Start the app maximized and let the window manager handle restore.
        primaryStage.setMaximized(true);
    }
    @Override
    public void stop() {
        AppState.shutdown();
    }
    public static void main(String[] args) {
        launch(args);
    }
}