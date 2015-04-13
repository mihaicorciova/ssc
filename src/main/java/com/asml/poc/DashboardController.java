package com.asml.poc;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label nodeLabel;
    private Dashboard dashboard;

    public DashboardController() {
    }

    @FXML private void initialize() {
        //called after fxml has been loaded
        System.out.println("q");

    }

    @FXML public void handle(Event event) {
        String buttonLabel = ((Button) event.getSource()).getText();
        System.out.println("buttonLabel = " + buttonLabel);
    }

}
