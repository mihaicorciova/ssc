package com.asml.lis.client.controller.sidemenu;

import com.asml.lis.client.controller.MainController;
import com.asml.lis.client.controller.content.metrologysetup.MultiYieldStarQualificationController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Catalin Tudorache <catalin.tudorache@asml.com>
 */
public class SideMenuNoImagesController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(SideMenuNoImagesController.class);

    // content controllers
    private MainController mainController;
   
    @FXML
    private TreeView<String> navigationTree;

    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb resource bundle
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        log.debug("Initializing Side Menu Controller");

        // hide root
        navigationTree.setShowRoot(false);
        navigationTree.getRoot().getChildren().stream().forEach((item) -> {
            item.setExpanded(true);
        });

        // add side menu listener
        navigationTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Clicked on " + newValue.getValue());
           mainController.handleMYSQViewLaunch();
        });
    }

    /**
     * Set Main Controller.
     *
     * @param pController application main controller
     */
    public void setMainController(final MainController pController) {
        mainController = pController;
    }

  
}
