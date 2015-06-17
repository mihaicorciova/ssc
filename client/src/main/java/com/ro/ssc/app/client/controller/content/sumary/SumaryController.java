/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.controller.content.sumary;

import com.ro.ssc.app.client.model.commons.Event;
import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.model.commons.User;
import com.ro.ssc.app.client.service.impl.DataProviderImpl;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javax.inject.Inject;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public class SumaryController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(SumaryController.class);
    @FXML
    private Button selectButton;
    @FXML
    private ListView filesListView;
    @FXML
    private TableView sumaryTableView;
    @FXML
    private TableColumn<GenericModel, Object> dateTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> hourTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> nameTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> cardNoTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> departmentTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> eventTableColumn;


    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb resource bundle
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        log.info("Initializing Sumary controller");

        final FileChooser fileChooser = new FileChooser();

      
        selectButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        configureFileChooser(fileChooser);
                        List<File> files = fileChooser.showOpenMultipleDialog(selectButton.getContextMenu());
                       if(files!=null){
                        populateListView(files);
                        
                        for (File file : files) {
                            if(file.getName().contains("mdb"))
                            {
                                DataProviderImpl.getInstance().enrichUserData(file); 
                            }
                            else                            {
                            DataProviderImpl.getInstance().importUserData(file);
                        }
                        }
                        
                        if (! DataProviderImpl.getInstance().getUserData().isEmpty()) {
                            populateMyTable( DataProviderImpl.getInstance().getUserData());
                            log.debug("not emp");
                        }
                    }
                    }
                });

    }

    private void populateListView(List<File> files) {
        List<String> ls = new ArrayList<>();
        files.stream().forEach((file) -> {
            ls.add(file.getName());
        });

        filesListView.setItems(FXCollections.observableArrayList(ls));

    }

    public void populateMyTable(Map<String, User> pair) {

        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("one"));
        hourTableColumn.setCellValueFactory(new PropertyValueFactory<>("two"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("three"));
        cardNoTableColumn.setCellValueFactory(new PropertyValueFactory<>("four"));
        departmentTableColumn.setCellValueFactory(new PropertyValueFactory<>("five"));
        eventTableColumn.setCellValueFactory(new PropertyValueFactory<>("six"));

        dateTableColumn.setStyle("-fx-alignment:CENTER;");
        hourTableColumn.setStyle("-fx-alignment:CENTER;");
        nameTableColumn.setStyle("-fx-alignment:CENTER;");
        cardNoTableColumn.setStyle("-fx-alignment:CENTER;");
        departmentTableColumn.setStyle("-fx-alignment:CENTER;");
        eventTableColumn.setStyle("-fx-alignment:CENTER;");

        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");
        DateTimeFormatter dtf2 = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
        DecimalFormat df = new DecimalFormat();

        ObservableList data = FXCollections.observableArrayList();
        for (Map.Entry<String, User> entry : pair.entrySet()) {
                      for (Event ev : entry.getValue().getEvents()) {
                try {
                    
                    data.add(new GenericModel(ev.getEventDateTime().toString(dtf2), ev.getEventDateTime().toString(dtf), entry.getValue().getName().toUpperCase(), df.parse(entry.getValue().getCardNo()), entry.getValue().getDepartment(), ev.getAddr().contains("In") ? "Intrare" : "Iesire"));
                } catch (ParseException ex) {
                    java.util.logging.Logger.getLogger(SumaryController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        sumaryTableView.getItems().setAll(data);
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        FileChooser.ExtensionFilter extFilter
                = new FileChooser.ExtensionFilter("Files (*.xls,*.mdb)", "*.xls;*.mdb");
        fileChooser.getExtensionFilters().add(extFilter);
    }
}
