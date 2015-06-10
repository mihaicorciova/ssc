package com.ro.ssc.app.client.controller.startpage;

import com.ro.ssc.app.client.model.commons.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public class StartPageController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(StartPageController.class);

    private static final String MENU_LAYOUT_FILE = "/fxml/Menu.fxml";

    @FXML
    public AnchorPane menuContainer;
    @FXML
    public AnchorPane statusBarContainer;
    @FXML
    public AnchorPane contentContainer;

    @FXML
    public Accordion accordion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initializing Dashboard");

        // load menu
        loadMenu();

        // TODO: recent projects should contain custom components
    }

    private void loadMenu() {
        try {
            final FXMLLoader menuLoader = new FXMLLoader();
            final MenuBar menuBar = menuLoader.load(getClass().getResourceAsStream(MENU_LAYOUT_FILE));
            AnchorPane.setLeftAnchor(menuBar, 0.0);
            AnchorPane.setRightAnchor(menuBar, 0.0);
            menuContainer.getChildren().add(menuBar);
        } catch (IOException ex) {
            log.error("Failed to load Dashboard menu", ex);
        }
    }

    /**
     * Update label on task end.
     *
     * @param result result string
     */
    public void loadProject(final String result) {
        // TODO
    }

}
