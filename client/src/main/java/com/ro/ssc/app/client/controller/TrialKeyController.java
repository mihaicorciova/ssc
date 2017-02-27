package com.ro.ssc.app.client.controller;


import com.ro.ssc.app.client.licensing.TrialKeyGenerator;
import com.ro.ssc.app.client.licensing.TrialKeyValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TrialKeyController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(TrialKeyController.class);

    // main content containers
    @FXML
    private TextField outputField;
    @FXML
    private TextField inputField;
    @FXML
  

  
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing main controller");

        inputField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                try
                {
                String decoded= TrialKeyValidator.decodeKey(newValue);
                decoded=decoded.concat("0");
                log.debug(decoded);
              if(!decoded.equals("0")){
                outputField.setText(TrialKeyGenerator.generateKey(decoded));
              }else
              {
                 outputField.setText("Introduceti codul corect");
              }
                }catch(Exception e)
                {
                log.error("exceptie la decodare/encodare",e);
               
                }
                
            }
        });
    }

    /**
     * Get content tab pane component.
     *
     * @return content TabPane
     */
}
