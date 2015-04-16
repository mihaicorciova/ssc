/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asml.lis.client.sidemenu;

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
 * @author catudora
 */
public class SideMenuNoImagesController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(SideMenuNoImagesController.class);

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
        sideMenuTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            log.info("value=" + newValue.getValue());

            final SideMenuTreeType selected = SideMenuTreeType.parse(newValue.getValue());

            switch (selected) {
                case NODE_PROJECT:
                    log.info("node project selected");
                    break;
                default:
                    log.warn("unknown tree item selected");
            }
        });

    }

}
