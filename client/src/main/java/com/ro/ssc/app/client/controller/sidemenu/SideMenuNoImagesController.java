package com.ro.ssc.app.client.controller.sidemenu;

import com.ro.ssc.app.client.controller.MainController;
import com.ro.ssc.app.client.model.commons.Configuration;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

    @FXML
    private Label timeSideMenuLabel;

    @FXML
    private Label dateSideMenuLabel;

    @FXML
    private AnchorPane imageViewAnchorPane;

    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb resource bundle
     */
    @Override

    public void initialize(final URL url, final ResourceBundle rb) {
        log.debug("Initializing Side Menu Controller");

        if (!Configuration.HAS_LOGO.getAsBoolean()) {
            imageViewAnchorPane.getChildren().clear();
        }
        // hide root
        navigationTree.setShowRoot(false);
        navigationTree.getRoot().getChildren().stream().forEach((item) -> {
            item.setExpanded(true);
        });
        final Timeline digitalTime = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");
                                DateTimeFormatter dtf2 = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
                                dateSideMenuLabel.setText(DateTime.now().toLocalDate().toString(dtf2));
                                timeSideMenuLabel.setText(DateTime.now().toLocalTime().toString(dtf));

                            }
                        }
                ),
                new KeyFrame(Duration.seconds(5))
        );
        digitalTime.setCycleCount(Animation.INDEFINITE);
        digitalTime.play();

        // add side menu listener
        navigationTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Clicked on " + newValue.getValue());
            switch (newValue.getValue()) {
                case "Sumar":
                    try {
                        mainController.handleSumaryViewLaunch();
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(SideMenuNoImagesController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case "Raport Cumulativ":
                    try {
                        mainController.handleOverallReportViewLaunch();
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(SideMenuNoImagesController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case "Raport Individual":
                    try {
                        mainController.handleSingleReportViewLaunch();
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(SideMenuNoImagesController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case "Raport Absente Individual":
                    try {
                        mainController.handleSingleAbsViewLaunch();
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(SideMenuNoImagesController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case "Raport Absente Cumulativ":
                    try {
                        mainController.handleOverallAbsViewLaunch();
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(SideMenuNoImagesController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
            }

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
