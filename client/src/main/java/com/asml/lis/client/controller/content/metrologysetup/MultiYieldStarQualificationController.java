package com.asml.lis.client.controller.content.metrologysetup;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private SwingNode waferPlotContainer;

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
        
        
        
        waferPlotContainer.setContent();
    }

    public void setPlaceholderLabel(final String text) {
      
    }

}
