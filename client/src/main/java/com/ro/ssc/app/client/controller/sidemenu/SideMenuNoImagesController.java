package com.ro.ssc.app.client.controller.sidemenu;

import com.ro.ssc.app.client.controller.MainController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.joda.time.DateTime;

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
final Timeline digitalTime = new Timeline(
      new KeyFrame(Duration.seconds(0),
        new EventHandler<ActionEvent>() {
          @Override public void handle(ActionEvent actionEvent) {
           dateSideMenuLabel.setText(DateTime.now().toLocalDate().toString());
        timeSideMenuLabel.setText(DateTime.now().toLocalTime().toString());
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
