package com.asml.lis.client.ui.common;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asml.lis.client.controller.content.metrologysetup.MultiYieldStarQualificationController;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * @author bbenga
 *
 */
public class UiCommonTools {
    private static final Logger log = LoggerFactory.getLogger(UiCommonTools.class);

    
    private static UiCommonTools instance;
    
    private UiCommonTools() {
       //Singleton
    }
    
    public static UiCommonTools getInstance(){
        if (instance == null){
            instance = new UiCommonTools();
        }
        return instance;
    }
    
    /**
     * Opens a FileChooser to let the user select a file to save to.
     */
    public File getFileByChooser(Window window, String description, String fileExtension) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                description, "*" + fileExtension);
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            // The file must be a *.pdf file
            if (!file.getPath().endsWith(fileExtension)) {
                file = new File(file.getPath() + fileExtension);
            }
        }
        return file;
    }
    
    /**
     * Opens an information pop-up dialog
     */
    public void showAlertDialog(String title, String header, String text, AlertType alertType) {
        log.info("Show Allert Dialog of type " + alertType + " - text: " + text);
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(text);
            alert.showAndWait();
    }
}
