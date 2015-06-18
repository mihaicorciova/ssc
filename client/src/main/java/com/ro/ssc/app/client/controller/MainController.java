package com.ro.ssc.app.client.controller;

import com.ro.ssc.app.client.controller.sidemenu.SideMenuNoImagesController;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    // main component fxml files
    private static final String OVERALLREPORT_LAYOUT_FILE = "/fxml/OverallReport.fxml";
    private static final String SIDE_MENU_LAYOUT_FILE = "/fxml/SideMenuNoImages.fxml";
    private static final String STATUS_BAR_LAYOUT_FILE = "/fxml/StatusBar.fxml";
    private static final String SUMARY_FILE = "/fxml/Sumary.fxml";
    // style sheet files
    private static final String SIDE_MENU_CSS_FILE = "/styles/SideMenu.css";
    private static final String STATUS_BAR_CSS_FILE = "/styles/StatusBar.css";

    // main content containers
    @FXML
    private AnchorPane sideMenuContainer;
    @FXML
    private AnchorPane contentContainer;
    @FXML
    private AnchorPane statusBarContainer;
    @FXML
    private AnchorPane contentTabPane;

    private AnchorPane sumaryPane;

    private AnchorPane overallReportPane;
    // controllers

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing main controller");

        // load components
        try {

            // load side menu
            final FXMLLoader sideMenuLoader = new FXMLLoader();
            final AnchorPane sideMenu = sideMenuLoader.load(getClass().getResourceAsStream(SIDE_MENU_LAYOUT_FILE));
            AnchorPane.setLeftAnchor(sideMenu, 0.0);
            AnchorPane.setTopAnchor(sideMenu, 0.0);
            AnchorPane.setRightAnchor(sideMenu, 0.0);
            AnchorPane.setBottomAnchor(sideMenu, 0.0);
            sideMenuContainer.getChildren().add(sideMenu);
            sideMenuContainer.getStylesheets().add(SIDE_MENU_CSS_FILE);
            ((SideMenuNoImagesController) sideMenuLoader.getController()).setMainController(this);

            // load status bar
            final FXMLLoader statusBarLoader = new FXMLLoader();
            final AnchorPane statusBar = statusBarLoader.load(getClass().getResourceAsStream(STATUS_BAR_LAYOUT_FILE));
            AnchorPane.setRightAnchor(statusBar, 10.0);
            statusBarContainer.getChildren().add(statusBar);
            statusBarContainer.getStylesheets().add(STATUS_BAR_CSS_FILE);

        } catch (Exception ex) {
            log.error("Failed to load components", ex);
        }
    }

    /**
     * Get content tab pane component.
     *
     * @return content TabPane
     */
    public AnchorPane getContentTabPane() {
        return contentTabPane;
    }

    public void handleSumaryViewLaunch() throws IOException {

        // load side menu
        if (sumaryPane == null) {
            final FXMLLoader sumaryPaneLoader = new FXMLLoader();
            sumaryPane = sumaryPaneLoader.load(getClass().getResourceAsStream(SUMARY_FILE));
            AnchorPane.setLeftAnchor(sumaryPane, 0.0);
            AnchorPane.setTopAnchor(sumaryPane, 0.0);
            AnchorPane.setRightAnchor(sumaryPane, 0.0);
            AnchorPane.setBottomAnchor(sumaryPane, 0.0);
        }
        contentContainer.getChildren().setAll(sumaryPane);
    }

    public void handleOverallReportViewLaunch() throws IOException {
        // load side menu
        
            final FXMLLoader overallReportPaneLoader = new FXMLLoader();
            overallReportPane = overallReportPaneLoader.load(getClass().getResourceAsStream(OVERALLREPORT_LAYOUT_FILE));
            AnchorPane.setLeftAnchor(overallReportPane, 0.0);
            AnchorPane.setTopAnchor(overallReportPane, 0.0);
            AnchorPane.setRightAnchor(overallReportPane, 0.0);
            AnchorPane.setBottomAnchor(overallReportPane, 0.0);
        
        contentContainer.getChildren().setAll(overallReportPane);
    }

}
