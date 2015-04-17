package com.asml.lis.client.sidemenu;

import com.asml.lis.client.MainController;
import com.asml.lis.client.metrologysetup.MultiYieldStarQualificationController;
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
    private MultiYieldStarQualificationController multiYieldStarQualificationController;

    @FXML
    private TreeView<String> sideMenuTree;

    /**
     * Initializes the controller class.
     *
     * @param url
     *            URL
     * @param rb
     *            resource bundle
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        log.debug("Initializing Side Menu Controller");

        // add side menu listener
        sideMenuTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Clicked on " + newValue.getValue());
            if (multiYieldStarQualificationController == null) {
                log.warn("Null content TabPane");
                return;
            }
            multiYieldStarQualificationController.setPlaceholderLabel(newValue.getValue());
        });
    }

    /**
     * Set Main Controller.
     *
     * @param pController
     *            application main controller
     */
    public void setMainController(final MainController pController) {
        mainController = pController;
    }

    /**
     * Set Multi YieldStar Qualification Controller.
     *
     * @param pController
     *            multi YieldStar qualification controller
     */
    public void setMultiYieldStarQualificationController(final MultiYieldStarQualificationController pController) {
        multiYieldStarQualificationController = pController;
    }

}
