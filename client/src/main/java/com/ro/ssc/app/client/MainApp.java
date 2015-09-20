package com.ro.ssc.app.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

;

public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);
    private static final String ROOT_LAYOUT_FILE = "/fxml/RootLayout.fxml";
    private static final String MAIN_CSS_FILE = "/styles/Main.css";
    private static final double SCENE_MIN_WIDTH = 1280;
    private static final double SCENE_MIN_HEIGHT = 720;
    private Stage stage;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        log.info("Starting Litho InSight");
        this.stage = stage;

        log.debug("Loading FXML for main view from: {}", ROOT_LAYOUT_FILE);
        FXMLLoader loader = new FXMLLoader();

        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(ROOT_LAYOUT_FILE));

        log.debug("Showing JFX scene");
        Scene scene = new Scene(rootNode, SCENE_MIN_WIDTH, SCENE_MIN_HEIGHT);
        // scene.getStylesheets().add(MAIN_CSS_FILE);
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/images/icon.png")));
        stage.setTitle("Soft Pontaj v2.0");
        stage.setMinWidth(SCENE_MIN_WIDTH);
        stage.setMinHeight(SCENE_MIN_HEIGHT);
        stage.setScene(scene);

        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

}
