package com.asml.lis.client;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asml.lis.client.ui.common.DialogManager;

public class MainController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    private static final String MENU_LAYOUT_FILE = "/fxml/Menu.fxml";
    private static final String SIDE_MENU_LAYOUT_FILE = "/fxml/SideMenuNoImages.fxml";
    private static final String STATUS_BAR_LAYOUT_FILE = "/fxml/StatusBar.fxml";
    private static final String SIDE_MENU_CSS_FILE = "/styles/side_menu.css";
    private static final String STATUS_BAR_CSS_FILE = "/styles/status_bar.css";

    @FXML
    private AnchorPane menuContainer;
    @FXML
    private AnchorPane sideMenuContainer;
    @FXML
    private AnchorPane contentContainer;
    @FXML
    private AnchorPane statusBarContainer;
    @FXML
    private TreeView<?> sideMenuTree;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing main controller");

        // show login dialog
        Dialog<Pair<String, String>> loginDialog = DialogManager.createNewLoginDialog();
        Optional<Pair<String, String>> result = loginDialog.showAndWait();
        if (result.isPresent()) {
            log.info("username = " + result.get().getKey() + ", password = " + result.get().getValue());
        }

        // load components
        try {

            final FXMLLoader menuLoader = new FXMLLoader();
            final MenuBar menuBar = (MenuBar) menuLoader.load(getClass().getResourceAsStream(MENU_LAYOUT_FILE));
            AnchorPane.setLeftAnchor(menuBar, 0.0);
            AnchorPane.setRightAnchor(menuBar, 0.0);
            menuContainer.getChildren().add(menuBar);

            final FXMLLoader sideMenuLoader = new FXMLLoader();
            final AnchorPane sideMenu = (AnchorPane) sideMenuLoader.load(getClass().getResourceAsStream(SIDE_MENU_LAYOUT_FILE));
            AnchorPane.setLeftAnchor(sideMenu, 0.0);
            AnchorPane.setTopAnchor(sideMenu, 0.0);
            AnchorPane.setRightAnchor(sideMenu, 0.0);
            AnchorPane.setBottomAnchor(sideMenu, 0.0);
            sideMenuContainer.getChildren().add(sideMenu);
            sideMenuContainer.getStylesheets().add(SIDE_MENU_CSS_FILE);

            final FXMLLoader statusBarLoader = new FXMLLoader();
            final Label statusLabel = (Label) statusBarLoader.load(getClass().getResourceAsStream(STATUS_BAR_LAYOUT_FILE));
            AnchorPane.setRightAnchor(statusLabel, 10.0);
            statusBarContainer.getChildren().add(statusLabel);
            statusBarContainer.getStylesheets().add(STATUS_BAR_CSS_FILE);

        } catch (Exception ex) {
            log.error("failed to load components", ex);
        }
    }

}
