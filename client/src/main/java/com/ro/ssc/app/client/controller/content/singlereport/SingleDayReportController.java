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
import java.util.stream.Collectors;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 *
 * @author DauBufu
 */
public class SingleDayReportController implements Initializable {

    private static final UiCommonTools fxCommonTools = UiCommonTools.getInstance();
    private static final Logger log = LoggerFactory.getLogger(SingleDayReportController.class);
    private static final String ALL = "all";
    private DateTime iniDate;
    private DateTime endDate;

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
    private DatePicker endDatePicker;

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
    private TableColumn<GenericModel, Object> depTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> entryTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> exitTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> innertimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> overtimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> absenceTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> lateTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> earlyTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> dateTableColumn;
      @FXML
    private TableColumn<GenericModel, Object> nightTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> dayTimeTableColumn;

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

            endDatePicker.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {

                    endDate = DateTime.parse(endDatePicker.getValue().format(formatter), dtf);
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
        nameTableColumn.setCellFactory(new Callback<TableColumn<GenericModel, Object>, TableCell<GenericModel, Object>>() {
            public TableCell call(TableColumn<GenericModel, Object> param) {
                return new TableCell<GenericModel, Object>() {

                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {

                            // Get fancy and change color based on data
                            if (DataProviderImpl.getInstance().getDepartments().contains(item.toString())) {
                                log.debug(item.toString());
                                this.setTextFill(Color.RED);
                            } else {
                                this.setTextFill(Color.BLACK);
                            }
                            setText(item.toString());
                        }
                    }
                };
            }
        });
        entryTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("two"));
        innertimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("three"));
        exitTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("four"));
        workTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("five"));
        offTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("six"));
        totalTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("seven"));
        depTableColumn.setCellValueFactory(new PropertyValueFactory<>("eight"));
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("nine"));
        overtimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("ten"));
        absenceTableColumn.setCellValueFactory(new PropertyValueFactory<>("eleven"));
        lateTableColumn.setCellValueFactory(new PropertyValueFactory<>("twelve"));
        earlyTableColumn.setCellValueFactory(new PropertyValueFactory<>("thirteen"));
         nightTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("fourteen"));
         dayTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("fifteen"));
        workTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        offTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        entryTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        totalTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        exitTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        nameTableColumn.setStyle("-fx-alignment:CENTER;");
        innertimeTableColumn.setStyle("-fx-alignment:CENTER;");
        depTableColumn.setStyle("-fx-alignment:CENTER;");
        overtimeTableColumn.setStyle("-fx-alignment:CENTER;");
        dateTableColumn.setStyle("-fx-alignment:CENTER;");
        earlyTableColumn.setStyle("-fx-alignment:CENTER;");
        lateTableColumn.setStyle("-fx-alignment:CENTER;");
        absenceTableColumn.setStyle("-fx-alignment:CENTER;");
          nightTimeTableColumn.setStyle("-fx-alignment:CENTER;");
          dayTimeTableColumn.setStyle("-fx-alignment:CENTER;");
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
 nightTimeTableColumn.setComparator(timeComparator);
        workTimeTableColumn.setComparator(timeComparator);
        totalTimeTableColumn.setComparator(timeComparator);
        offTimeTableColumn.setComparator(timeComparator);
        overtimeTableColumn.setComparator(timeComparator);
        earlyTableColumn.setComparator(timeComparator);
        lateTableColumn.setComparator(timeComparator);
        dateTableColumn.setComparator(dateComparator);
        dayTimeTableColumn.setComparator(dateComparator);
        List<GenericModel> ll = new ArrayList();
        if (endDate != null) {
            log.debug("aici"+endDate.toString(dtf));
            for (DateTime date = iniDate.withTimeAtStartOfDay(); date.isBefore(endDate.plusDays(1).withTimeAtStartOfDay()); date = date.plusDays(1)) {
                ll.addAll(DataProviderImpl.getInstance().getDaySpecificTableData(departmentChoiceBox.getSelectionModel().getSelectedItem() == null ? null : departmentChoiceBox.getSelectionModel().getSelectedItem().toString(), date));

            }
        } else {

            ll.addAll(DataProviderImpl.getInstance().getDaySpecificTableData(departmentChoiceBox.getSelectionModel().getSelectedItem() == null ? null : departmentChoiceBox.getSelectionModel().getSelectedItem().toString(), iniDate));
        }
        
        List<GenericModel> result = new ArrayList<>();
                    Map<String, List<GenericModel>> tm = new TreeMap();
                    tm.putAll(ll.stream().collect(Collectors.groupingBy(o -> DataProviderImpl.getInstance().getDepartmentFromUser(DataProviderImpl.getInstance().getKeyFromUser(o.getOne().toString())))));
                    for (Map.Entry<String, List<GenericModel>> d : tm.entrySet()) {

                        List<GenericModel> r = new ArrayList<>();
                        r = d.getValue();
                        r.sort((o1, o2) -> o1.getOne().toString().compareTo(o2.getOne().toString()));
                        result.addAll(r);
                    }
        //   ll.sort((o1,o2)->o1.getOne().toString().compareTo(o2.getOne().toString()));
        singleReportTableView.getItems().setAll(FXCollections.observableArrayList(result));

    }

    @FXML
    private void exportTableToPPT() {
        String[] ext = {".xls"};

        File file = fxCommonTools.getFileByChooser(exportButton.getContextMenu(), "XLS files (*.xls)", Arrays.asList(ext), iniDate.toString(dtf2), departmentChoiceBox.getSelectionModel().getSelectedItem() == null ? "Toate Departamentele" + "-" + iniDate.toString(dtf) : departmentChoiceBox.getValue().toString() + "-" + iniDate.toString(dtf));

        if (file == null) {
            return;
        }

        PptTableExporter pptExporter = new PptTableExporter() {

            @Override
            public String[][] getTableContent(TableView<?> fxTable) {
                String[][] content = new String[fxTable.getItems().size()][fxTable.getColumns().size()];

                int rowNo = 0;
                for (GenericModel tableData : ((TableView<GenericModel>) fxTable).getItems()) {
                    content[rowNo][0] = (String) tableData.getEight();
                    content[rowNo][1] = (String) tableData.getOne();
                    content[rowNo][2] = (String) tableData.getTwo();
                    content[rowNo][3] = (String) tableData.getThree();
                    content[rowNo][4] = (String) tableData.getFour();
                    content[rowNo][5] = (String) tableData.getFive();
                    content[rowNo][6] = (String) tableData.getSix();
                    content[rowNo][7] = (String) tableData.getSeven();
                    content[rowNo][8] = (String) tableData.getNine();
                    content[rowNo][9] = (String) tableData.getFourteen();
                    content[rowNo][10] = (String) tableData.getFifteen();
                    content[rowNo][11] = (String) tableData.getTen();
                    content[rowNo][12] = (String) tableData.getEleven();
                    content[rowNo][13] = (String) tableData.getTwelve();
                    content[rowNo][14] = (String) tableData.getThirteen();
                    rowNo++;
                }
                return content;
            }
        };

        pptExporter.exportTableToXls(singleReportTableView, file, "Raport zilnic ", departmentChoiceBox.getSelectionModel().getSelectedItem() == null ? "" : departmentChoiceBox.getSelectionModel().getSelectedItem().toString(), iniDatePicker.getValue().format(formatter));
        fxCommonTools.showInfoDialogStatus("Raport exportat", "Status-ul exportului", "Raportul s- a exportat cu succes in XLS.");

    }

}
