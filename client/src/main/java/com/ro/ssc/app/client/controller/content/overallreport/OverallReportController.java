/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.controller.content.overallreport;

import com.ro.ssc.app.client.exporter.PptTableExporter;
import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.service.impl.DataProviderImpl;
import com.ro.ssc.app.client.ui.commons.UiCommonTools;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
public class OverallReportController implements Initializable {

    private static final UiCommonTools fxCommonTools = UiCommonTools.getInstance();
    private static final Logger log = LoggerFactory.getLogger(OverallReportController.class);
    private static final String ALL = "all";
    private DateTime iniDate;
    private DateTime endDate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
    private final org.joda.time.format.DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");

    @FXML
    private ChoiceBox departmentChoiceBox;
    @FXML
    private Button exportButton;
    @FXML
    private DatePicker iniDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TableView overallReportTableView;
    @FXML
    private TableColumn<GenericModel, Object> offTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> totalTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> nameTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> workTimeTableColumn;

    @FXML
    private TableColumn<GenericModel, Object> departmentTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> overtimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> absenceTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> lateTableColumn;
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

            iniDatePicker.setOnAction((final ActionEvent e) -> {
                iniDate = DateTime.parse(iniDatePicker.getValue().format(formatter), dtf);
                populateMyTable();
            });

            endDatePicker.setOnAction((final ActionEvent e) -> {
                endDate = DateTime.parse(endDatePicker.getValue().format(formatter), dtf);
                populateMyTable();
            });
            departmentChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

                @Override
                public void changed(ObservableValue observable, String oldValue, String newValue) {
                    populateMyTable();
                }
            });

            departmentChoiceBox.setItems(FXCollections.observableArrayList(DataProviderImpl.getInstance().getDepartments()));

            iniDate = DataProviderImpl.getInstance().getPossibleDateStart(ALL);
            endDate = DataProviderImpl.getInstance().getPossibleDateEnd(ALL);

            if (iniDate != null) {
                iniDatePicker.setValue(LocalDate.parse(iniDate.toString(dtf), formatter));
            }

            if (endDate != null) {
                endDatePicker.setValue(LocalDate.parse(endDate.toString(dtf), formatter));
            }

            populateMyTable();

        }

    }

    @FXML
    private void exportTableToPPT() {
        String[] ext = {".xls", ".ppt"};

        File file = fxCommonTools.getFileByChooser(exportButton.getContextMenu(), "PPT files (*.ppt);XLS files (*.xls)", Arrays.asList(ext));

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
                    content[rowNo][7] = (String) tableData.getEight();
                    content[rowNo][8] = (String) tableData.getNine();

                    rowNo++;
                }
                return content;
            }
        };
        if (!file.getPath().endsWith(ext[0])) {
            pptExporter.exportTableToPpt(overallReportTableView, file, "Raport cumulativ de la " + iniDatePicker.getValue().format(formatter) + " pana la " + endDatePicker.getValue().format(formatter));
            fxCommonTools.showInfoDialogStatus("Raport exportat", "Status-ul exportului", "Raportul s- a exportat cu succes in PPT.");
        } else {

            pptExporter.exportTableToXls(overallReportTableView, file, "Raport individual absente pentru ");
            fxCommonTools.showInfoDialogStatus("Raport exportat", "Status-ul exportului", "Raportul s- a exportat cu succes in XLS.");
        }

    }

    public void populateMyTable() {
        
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("one"));
        departmentTableColumn.setCellValueFactory(new PropertyValueFactory<>("two"));
        workTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("three"));
        offTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("four"));
        totalTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("five"));
        overtimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("six"));
        absenceTableColumn.setCellValueFactory(new PropertyValueFactory<>("seven"));
        lateTableColumn.setCellValueFactory(new PropertyValueFactory<>("eight"));
        earlyTableColumn.setCellValueFactory(new PropertyValueFactory<>("nine"));

        earlyTableColumn.setStyle("-fx-alignment:CENTER;");
        workTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        offTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        nameTableColumn.setStyle("-fx-alignment:CENTER;");
        totalTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        departmentTableColumn.setStyle("-fx-alignment:CENTER;");
        absenceTableColumn.setStyle("-fx-alignment:CENTER;");
        lateTableColumn.setStyle("-fx-alignment:CENTER;");
        overtimeTableColumn.setStyle("-fx-alignment:CENTER;");

        Comparator timeComparator = (Comparator<Object>) (Object o1, Object o2) -> {
            String[] s1 = ((String) o1).replace("!", "").split(":");
            String[] s2 = ((String) o2).replace("!", "").split(":");
            return Long.compare(Long.valueOf(s1[0].trim()) * 3600 + Long.valueOf(s1[1].trim()) * 60 + Long.valueOf(s1[2].trim()), Long.valueOf(s2[0]) * 3600 + Long.valueOf(s2[1].trim()) * 60 + Long.valueOf(s2[2].trim()));

        };

        workTimeTableColumn.setComparator(timeComparator);
        totalTimeTableColumn.setComparator(timeComparator);
        offTimeTableColumn.setComparator(timeComparator);
        overtimeTableColumn.setComparator(timeComparator);

        overallReportTableView.getItems().setAll(FXCollections.observableArrayList(DataProviderImpl.getInstance().getOverallTableData(iniDate, endDate, departmentChoiceBox.getSelectionModel().getSelectedItem() == null ? null : departmentChoiceBox.getSelectionModel().getSelectedItem().toString())));
    }

}
