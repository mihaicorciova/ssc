package com.asml.lis.client.controller.statusbar;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Catalin Tudorache <catalin.tudorache@asml.com>
 */
public class StatusBarController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(StatusBarController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initializing Status Bar Controller");
    }

    /**
     * Update label on task end.
     *
     * @param result result string
     */
    public void showTaskResult(final String result) {
        // TODO
    }

}
