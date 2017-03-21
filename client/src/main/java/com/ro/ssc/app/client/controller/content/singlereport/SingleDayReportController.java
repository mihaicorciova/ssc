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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 *
 * @author DauBufu
 */
public class SingleDayReportController implements Initializable {

    private static final UiCommonTools fxCommonTools = UiCommonTools.getInstance();
    private static final Logger log = LoggerFactory.getLogger(SingleDayReportController.class);
    private static final String ALL = "all";
    private DateTime iniDate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
    private final org.joda.time.format.DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
    private final org.joda.time.format.DateTimeFormatter dtf2 = DateTimeFormat.forPattern("MM-yyyy");
    @FXML
    private ChoiceBox departmentChoiceBox;
    @FXML
    private Button exportButton;
    @FXML
    private DatePicker iniDatePicker;

    @FXML
    private TableView singleReportTableView;
    @FXML
    private TableColumn<GenericModel, Object> offTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> totalTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> nameTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> workTimeTableColumn;

    @FXML
    private TableColumn<GenericModel, Object> entryTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> exitTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> innertimeTableColumn;

    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb resource bundle
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        log.info("Initializing Sg rep controller");

        if (!DataProviderImpl.getInstance()
                .getUserData().isEmpty()) {

iniDate = DataProviderImpl.getInstance().getPossibleDateEnd(ALL).withTimeAtStartOfDay();

            iniDatePicker.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {

                    iniDate = DateTime.parse(iniDatePicker.getValue().format(formatter), dtf);
                    populateMyTable();
                }
            });

            departmentChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

                @Override
                public void changed(ObservableValue observable, String oldValue, String newValue) {
                    populateMyTable();
                }
            });

            departmentChoiceBox.setItems(FXCollections.observableArrayList(DataProviderImpl.getInstance().getDepartments()));

         
            log.debug("Date " + iniDate.toString());
            if (iniDate != null) {
                iniDatePicker.setValue(LocalDate.parse(iniDate.toString(dtf), formatter));
            }
              populateMyTable();
        }
 
    }

    public void populateMyTable() {

        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("one"));
        entryTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("two"));
        innertimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("three"));
        exitTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("four"));
        workTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("five"));
        offTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("six"));
        totalTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("seven"));

        workTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        offTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        entryTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        totalTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        exitTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        nameTableColumn.setStyle("-fx-alignment:CENTER;");
        innertimeTableColumn.setStyle("-fx-alignment:CENTER;");

        Comparator timeComparator = (Comparator<Object>) (Object o1, Object o2) -> {
            String[] s1 = ((String) o1).replace("!", "").split(":");
            String[] s2 = ((String) o2).replace("!", "").split(":");
            return Long.compare(Long.valueOf(s1[0].trim()) * 3600 + Long.valueOf(s1[1].trim()) * 60 + Long.valueOf(s1[2].trim()), Long.valueOf(s2[0]) * 3600 + Long.valueOf(s2[1].trim()) * 60 + Long.valueOf(s2[2].trim()));

        };

        Comparator dateComparator = (Comparator<Object>) (Object o1, Object o2) -> {
            org.joda.time.format.DateTimeFormatter format = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
            DateTime d1 = DateTime.parse((String) o1, format);
            DateTime d2 = DateTime.parse((String) o2, format);
            return Long.compare(d1.getMillis(), d2.getMillis());
        };

        workTimeTableColumn.setComparator(timeComparator);
        totalTimeTableColumn.setComparator(timeComparator);
        offTimeTableColumn.setComparator(timeComparator);

        log.debug("DAte " + iniDate.toString());
        List<GenericModel> ll = DataProviderImpl.getInstance().getDaySpecificTableData(departmentChoiceBox.getSelectionModel().getSelectedItem()==null?null:departmentChoiceBox.getSelectionModel().getSelectedItem().toString(), iniDate);
        
     //   ll.sort((o1,o2)->o1.getOne().toString().compareTo(o2.getOne().toString()));
        singleReportTableView.getItems().setAll(FXCollections.observableArrayList(ll));

    }

    @FXML
    private void exportTableToPPT() {
        String[] ext = {".xls"};

        File file = fxCommonTools.getFileByChooser(exportButton.getContextMenu(), "XLS files (*.xls)", Arrays.asList(ext), iniDate.toString(dtf2) , departmentChoiceBox.getSelectionModel().getSelectedItem()==null?"Toate Departamentele"+ "-"+iniDate.toString(dtf):departmentChoiceBox.getValue().toString()+ "-"+iniDate.toString(dtf));

        if (file == null) {
            return;
        }

        PptTableExporter pptExporter = new PptTableExporter() {

            @Override
            public String[][] getTableContent(TableView<?> fxTable) {
                String[][] content = new String[fxTable.getItems().size()][fxTable.getColumns().size()];

                int rowNo = 0;
                for (GenericModel tableData : ((TableView<GenericModel>) fxTable).getItems()) {
                    content[rowNo][0] = (String) tableData.getOne();
                    content[rowNo][1] = (String) tableData.getTwo();
                    content[rowNo][2] = (String) tableData.getThree();
                    content[rowNo][3] = (String) tableData.getFour();
                    content[rowNo][4] = (String) tableData.getFive();
                    content[rowNo][5] = (String) tableData.getSix();
                    content[rowNo][6] = (String) tableData.getSeven();

                    rowNo++;
                }
                return content;
            }
        };
        
            pptExporter.exportTableToXls(singleReportTableView, file, "Raport zilnic " , departmentChoiceBox.getSelectionModel().getSelectedItem()==null?"":departmentChoiceBox.getSelectionModel().getSelectedItem().toString() , iniDatePicker.getValue().format(formatter));
            fxCommonTools.showInfoDialogStatus("Raport exportat", "Status-ul exportului", "Raportul s- a exportat cu succes in XLS.");
        

    }

}
