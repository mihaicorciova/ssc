/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.controller.content.singlereport;

import com.ro.ssc.app.client.exporter.PptTableExporter;
import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.service.impl.DataProviderImpl;
import com.ro.ssc.app.client.ui.commons.UiCommonTools;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public class SingleAbsController implements Initializable {

    private static final UiCommonTools fxCommonTools = UiCommonTools.getInstance();
    private static final Logger log = LoggerFactory.getLogger(SingleAbsController.class);
    private static final String ALL = "all";
    private DateTime iniDate;
    private DateTime endDate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
    private final org.joda.time.format.DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");

    @FXML
    private ChoiceBox userChoiceBox;
    @FXML
    private Button exportButton;
    @FXML
    private DatePicker iniDatePicker;
    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableView singleReportTableView;

    @FXML
    private TableColumn<GenericModel, Object> dateTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> absTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> entryTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> exitTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> delayTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> earlyTableColumn;

    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb resource bundle
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        log.info("Initializing Sumary controller");

        if (!DataProviderImpl.getInstance()
                .getUserData().isEmpty()) {

              iniDate = DataProviderImpl.getInstance().getPossibleDateStart(ALL);
            endDate = DataProviderImpl.getInstance().getPossibleDateEnd(ALL);

            iniDatePicker.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {

                    iniDate = DateTime.parse(iniDatePicker.getValue().format(formatter), dtf);
                    populateMyTable();
                }
            });

            endDatePicker.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {

                    endDate = DateTime.parse(endDatePicker.getValue().format(formatter), dtf);
                    populateMyTable();

                }
            });
            userChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

                @Override
                public void changed(ObservableValue observable, String oldValue, String newValue) {
                    populateMyTable();
                }
            });

            userChoiceBox.setItems(FXCollections.observableArrayList(DataProviderImpl.getInstance().getUsers()));
            userChoiceBox.getSelectionModel().selectFirst();
          
            if (iniDate != null) {
                iniDatePicker.setValue(LocalDate.parse(iniDate.toString(dtf), formatter));
            }

            if (endDate != null) {
                endDatePicker.setValue(LocalDate.parse(endDate.toString(dtf), formatter));
            }

        }

    }

    public void populateMyTable() {

        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("one"));
        entryTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("two"));
        exitTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("three"));
        absTableColumn.setCellValueFactory(new PropertyValueFactory<>("eight"));
        delayTableColumn.setCellValueFactory(new PropertyValueFactory<>("nine"));
        earlyTableColumn.setCellValueFactory(new PropertyValueFactory<>("ten"));

        exitTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        earlyTableColumn.setStyle("-fx-alignment:CENTER;");
        dateTableColumn.setStyle("-fx-alignment:CENTER;");
        absTableColumn.setStyle("-fx-alignment:CENTER;");
        delayTableColumn.setStyle("-fx-alignment:CENTER;");
        entryTimeTableColumn.setStyle("-fx-alignment:CENTER;");

        Comparator dateComparator = (Comparator<Object>) (Object o1, Object o2) -> {
            org.joda.time.format.DateTimeFormatter format = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
            DateTime d1 = DateTime.parse((String) o1, format);
            DateTime d2 = DateTime.parse((String) o2, format);
            return Long.compare(d1.getMillis(), d2.getMillis());
        };

        dateTableColumn.setComparator(dateComparator);

        singleReportTableView.getItems().setAll(FXCollections.observableArrayList(DataProviderImpl.getInstance().getUserSpecificTableData(userChoiceBox.getSelectionModel().getSelectedItem().toString(), iniDate, endDate)));
    }

    @FXML
    private void exportTableToPPT() {
         String[] ext = { ".xls" ,".ppt"};

        File file = fxCommonTools.getFileByChooser(exportButton.getContextMenu(), "PPT files (*.ppt);XLS files (*.xls)", Arrays.asList(ext));

        PptTableExporter pptExporter = new PptTableExporter() {

            @Override
            public String[][] getTableContent(TableView<?> fxTable) {
                String[][] content = new String[fxTable.getItems().size()][fxTable.getColumns().size()];

                int rowNo = 0;
                for (GenericModel tableData : ((TableView<GenericModel>) fxTable).getItems()) {
                    content[rowNo][0] = (String) tableData.getOne();
                    content[rowNo][1] = (String) tableData.getTwo();
                    content[rowNo][2] = (String) tableData.getThree();
                    content[rowNo][3] = (String) tableData.getEight();
                    content[rowNo][4] = (String) tableData.getNine();
                    content[rowNo][5] = (String) tableData.getTen();

                    rowNo++;
                }
                return content;
            }
        };

        if (file == null) {
            return;
        }
        if (!file.getPath().endsWith(ext[0])) {
            pptExporter.exportTableToPpt(singleReportTableView, file, "Raport individual absente pentru " + userChoiceBox.getSelectionModel().getSelectedItem().toString() + " de la " + endDatePicker.getValue().format(formatter) + " pana la " + endDatePicker.getValue().format(formatter));
  fxCommonTools.showInfoDialogStatus("Raport exportat", "Status-ul exportului", "Raportul s- a exportat cu succes in PPT.");
        } else {
            
            pptExporter.exportTableToXls(singleReportTableView, file, "Raport individual absente pentru " + userChoiceBox.getSelectionModel().getSelectedItem().toString() + " de la " + endDatePicker.getValue().format(formatter) + " pana la " + endDatePicker.getValue().format(formatter));
  fxCommonTools.showInfoDialogStatus("Raport exportat", "Status-ul exportului", "Raportul s- a exportat cu succes in XLS.");
        }
      
    }

}
