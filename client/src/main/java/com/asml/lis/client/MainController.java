package com.asml.lis.client;

import com.asml.lis.client.metrologysetup.MultiYieldStarQualificationController;
import com.asml.lis.client.sidemenu.SideMenuNoImagesController;
import com.asml.lis.client.ui.common.DialogManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.TabPane;

public class MainController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    // main component fxml files
    private static final String MENU_LAYOUT_FILE = "/fxml/Menu.fxml";
    private static final String SIDE_MENU_LAYOUT_FILE = "/fxml/SideMenuNoImages.fxml";
    private static final String STATUS_BAR_LAYOUT_FILE = "/fxml/StatusBar.fxml";

    // phase component fxml files
    private static final String MULTI_YIELDSTAR_QUALIFICATION_LAYOUT_FILE = "/fxml/metrology_setup/MultiYieldStarQualification.fxml";

    // style sheet files
    private static final String SIDE_MENU_CSS_FILE = "/styles/side_menu.css";
    private static final String STATUS_BAR_CSS_FILE = "/styles/status_bar.css";

    @FXML
    private AnchorPane menuContainer;
    @FXML
    private AnchorPane sideMenuContainer;
    @FXML
    private AnchorPane contentContainer;
    @FXML
    private TabPane contentTabPane;
    @FXML
    private AnchorPane statusBarContainer;

    // controllers
    private MultiYieldStarQualificationController multiYieldStarQualificationController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing main controller");

        // show login dialog
        Dialog<Pair<String, String>> loginDialog = DialogManager.createNewLoginDialog();
        Optional<Pair<String, String>> result = loginDialog.showAndWait();
        if (result.isPresent()) {
            log.info("Logging in user " + result.get().getKey());
        }

        // load components
        try {

            // load menu
            final FXMLLoader menuLoader = new FXMLLoader();
            final MenuBar menuBar = menuLoader.load(getClass().getResourceAsStream(MENU_LAYOUT_FILE));
            AnchorPane.setLeftAnchor(menuBar, 0.0);
            AnchorPane.setRightAnchor(menuBar, 0.0);
            menuContainer.getChildren().add(menuBar);

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
            final Label statusLabel = statusBarLoader.load(getClass().getResourceAsStream(STATUS_BAR_LAYOUT_FILE));
            AnchorPane.setRightAnchor(statusLabel, 10.0);
            statusBarContainer.getChildren().add(statusLabel);
            statusBarContainer.getStylesheets().add(STATUS_BAR_CSS_FILE);

            // load Multi YieldStar Qualification
            final FXMLLoader multiYieldStarQaulifLoader = new FXMLLoader();
            contentTabPane = multiYieldStarQaulifLoader.load(getClass().getResourceAsStream(MULTI_YIELDSTAR_QUALIFICATION_LAYOUT_FILE));
            contentContainer.getChildren().add(contentTabPane);
            multiYieldStarQualificationController = multiYieldStarQaulifLoader.getController();
            ((SideMenuNoImagesController) sideMenuLoader.getController())
                    .setMultiYieldStarQualificationController(multiYieldStarQualificationController);

        } catch (Exception ex) {
            log.error("Failed to load components", ex);
        }
    }

    /**
     * Get content tab pane component.
     *
     * @return content TabPane
     */
    public TabPane getContentTabPane() {
        return contentTabPane;
    }

}
