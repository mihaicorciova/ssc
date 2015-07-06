/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.controller.content.sumary;

import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.service.impl.DataProviderImpl;
import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
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
import org.joda.time.DateTime;
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
                        if (files != null) {
                            populateListView(files);

                            for (File file : files) {
                                if (file.getName().contains("mdb")) {

                                    DataProviderImpl.getInstance().saveMdbFile(file);

                                } else {
                                    DataProviderImpl.getInstance().importUserData(file);
                                }
                            }

                            if (!DataProviderImpl.getInstance().getUserData().isEmpty()) {
                                populateMyTable();
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

    public void populateMyTable() {

        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("one"));
        hourTableColumn.setCellValueFactory(new PropertyValueFactory<>("two"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("three"));
        cardNoTableColumn.setCellValueFactory(new PropertyValueFactory<>("four"));
        departmentTableColumn.setCellValueFactory(new PropertyValueFactory<>("five"));
        eventTableColumn.setCellValueFactory(new PropertyValueFactory<>("six"));

        dateTableColumn.setComparator(new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {

                DateTimeFormatter format = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
                DateTime d1 = DateTime.parse((String) o1, format);
                DateTime d2 = DateTime.parse((String) o2, format);
                return Long.compare(d1.getMillis(), d2.getMillis());

            }

        });
        dateTableColumn.setStyle("-fx-alignment:CENTER;");
        hourTableColumn.setStyle("-fx-alignment:CENTER;");
        nameTableColumn.setStyle("-fx-alignment:CENTER;");
        cardNoTableColumn.setStyle("-fx-alignment:CENTER;");
        departmentTableColumn.setStyle("-fx-alignment:CENTER;");
        eventTableColumn.setStyle("-fx-alignment:CENTER;");

        ObservableList data = FXCollections.observableArrayList(DataProviderImpl.getInstance().getUserData());

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
