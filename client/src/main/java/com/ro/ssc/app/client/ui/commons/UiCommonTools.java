/**
 *
 */
package com.ro.ssc.app.client.ui.commons;

import java.io.File;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * @author bbenga
 *
 */
public class UiCommonTools {

    private static UiCommonTools instance;
   
  
    
    private UiCommonTools() {
        //Singleton
    }

    public static UiCommonTools getInstance() {
        if (instance == null) {
            instance = new UiCommonTools();
        }
        return instance;
    }

    

    /**
     * Opens a FileChooser to let the user select a file to save to.
     */
    public File getFileByChooser(ContextMenu context, String description, List<String> fileExtension) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                description, fileExtension);
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(context);

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

    public  Optional<String> showExpDialogStatus(String title, String text, String value) {

        Dialog<String> alert = new Dialog<>();
        alert.setTitle(title);
        alert.setHeaderText(text);
 
        
        
        
        TextField output = new TextField();
output.setPromptText("Codul de trimis catre furnizor: ");
output.setText(value);
TextField input = new TextField();
input.setPromptText("Codul primit de la furnizor: ");

GridPane grid = new GridPane();
grid.setHgap(10);
grid.setVgap(10);
grid.setPadding(new Insets(20, 150, 10, 10));

grid.add(new Label("Codul de trimis catre furnizor: "), 0, 0);
grid.add(output, 1, 0);
grid.add(new Label("Codul primit de la furnizor:"), 0, 1);
grid.add(input, 1, 1);

alert.setResultConverter(dialogButton -> {
    if (dialogButton == ButtonType.OK) {
        return input.getText();
    }
    return null;
});

alert.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

alert.getDialogPane().setContent(grid);
Platform.runLater(() -> input.requestFocus());
// Traditional way to get the response value.
        Optional<String> res = alert.showAndWait();
        return res;
    }
}
