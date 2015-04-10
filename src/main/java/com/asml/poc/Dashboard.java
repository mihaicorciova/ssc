package com.asml.poc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Dashboard, main entry point
 */
public class Dashboard extends Application {
    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setTitle("POC of Project overview");

        initRootLayout();

        initCentralContent();
    }

    private void initCentralContent() {
        //TODO load central content here...
    }

    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Dashboard.class.getResource("../../../skeleton.fxml"));
            BorderPane root = (BorderPane) loader.load();

            DashboardController controller = loader.getController();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}