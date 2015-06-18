/**
 * 
 */
package com.ro.ssc.app.client.ui.commons;

import java.io.File;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author bbenga
 *
 */
public class UiCommonTools {
    
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
    public File getFileByChooser(ContextMenu context, String description, String fileExtension) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                description, "*" + fileExtension);
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(context);

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
    public void showInfoDialogStatus(String title, String header, String text) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }

}
