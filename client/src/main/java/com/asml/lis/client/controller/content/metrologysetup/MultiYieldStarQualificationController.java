package com.asml.lis.client.controller.content.metrologysetup;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FXML Controller class
 *
 * @author Catalin Tudorache <catalin.tudorache@asml.com>
 */
public class MultiYieldStarQualificationController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MultiYieldStarQualificationController.class);

    @FXML
    private Label contentPlaceholderLabel;

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
        log.info("Initializing MultiYieldStarQualification controller");
    }

    public void setPlaceholderLabel(final String text) {
        log.debug("Setting new label value to " + text);
        contentPlaceholderLabel.setText(text + " content");
        log.info("New label value is " + contentPlaceholderLabel.getText());
    }

}
