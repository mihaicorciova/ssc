package com.ro.ssc.app.client.controller.content.overallreport;

import com.ro.ssc.app.client.exporter.XlsTableExporter;
import com.ro.ssc.app.client.service.impl.DataImportImpl;
import com.ro.ssc.app.client.service.impl.DataProviderImpl;
import com.ro.ssc.app.client.ui.commons.UiCommonTools;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import org.controlsfx.control.spreadsheet.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import javafx.stage.DirectoryChooser;

/**
 * @author DauBufu
 */
public class MonthlyReportController<T> implements Initializable {

    private static final UiCommonTools fxCommonTools = UiCommonTools.getInstance();
    private static final Logger log = LoggerFactory.getLogger(MonthlyReportController.class);
    private static final String ALL = "all";
    private static final String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private static final String[] YEARS = {"2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022"};
    private final java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
    private final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
    private final DateTimeFormatter dtf2 = DateTimeFormat.forPattern("dd/MM");
    private DateTime iniDate;
    private DateTime endDate;
    @FXML
    private ChoiceBox departmentChoiceBox;
    @FXML
    private Button exportButton;
    @FXML
    private DatePicker iniDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private SpreadsheetView monthlySpreadsheetView;

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

            if (endDate.minusMonths(1).isAfter(iniDate)) {
                iniDate = endDate.minusMonths(1);
            }

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
    private void importFiles() {
        DirectoryChooser dc = new DirectoryChooser();
        File dir = dc.showDialog(exportButton.getContextMenu());
        DataImportImpl.getInstance().importData(dir);

        populateMyTable();

    }

    @FXML
    private void exportTableToPPT() {
        String[] ext = {".xls"};

        File file = fxCommonTools.getFileByChooser(exportButton.getContextMenu(), "XLS files (*.xls)", Arrays.asList(ext));

        if (file == null) {
            return;
        }

        XlsTableExporter pptExporter = new XlsTableExporter();

        pptExporter.exportTableToXls(monthlySpreadsheetView.getGrid(), file, "Raport lunar ",
                departmentChoiceBox.getSelectionModel().getSelectedItem() == null ? "" : departmentChoiceBox.getSelectionModel().getSelectedItem().toString(), iniDate.toString(dtf), endDate.toString(dtf));
        fxCommonTools.showInfoDialogStatus("Raport exportat", "Status-ul exportului", "Raportul s- a exportat cu succes in XLS.");

    }

    public void populateMyTable() {

        int in = 1;

        monthlySpreadsheetView.setGrid(getGridBase(departmentChoiceBox.getSelectionModel().getSelectedItem() == null ? ALL : departmentChoiceBox.getSelectionModel().getSelectedItem().toString(), in));
        monthlySpreadsheetView.setRowHeaderWidth(150);
        for (int i = 0; i < monthlySpreadsheetView.getColumns().size(); i++) {
            if (i > 0 && i < monthlySpreadsheetView.getColumns().size() - 3) {
                SpreadsheetColumn col = monthlySpreadsheetView.getColumns().get(i);
                col.setPrefWidth(50);
            } else if (i == 0) {
                SpreadsheetColumn col = monthlySpreadsheetView.getColumns().get(i);
                col.setPrefWidth(150);
            } else {
                SpreadsheetColumn col = monthlySpreadsheetView.getColumns().get(i);
                col.setPrefWidth(100);
            }

        }
    }

    private GridBase getGridBase(String department, int p) {

        List<String> users = new ArrayList();

        users.addAll(DataProviderImpl.getInstance().getUsersDep(department, 1));

        List<DateTime> dates = getDatesForMonth();

        final GridBase grid = new GridBase(users.size(), dates.size() + 7);

        grid.getColumnHeaders().clear();
        grid.getRowHeaders().clear();

        for (String entry : users) {

            if (entry.contains("$1")) {
                department = DataProviderImpl.getInstance().getDepartmentFromUser(entry.substring(0, entry.length() - 2));

            } else {
                department = DataProviderImpl.getInstance().getDepartmentFromUser(entry);
            }
            grid.getRowHeaders().add(department);
        }

        for (int column = 0; column < grid.getColumnCount(); ++column) {

            if (column == 0) {
                grid.getColumnHeaders().add("Nume");
            } else if (column == grid.getColumnCount() - 6) {
                grid.getColumnHeaders().add("Timp lucrat");
            } else if (column == grid.getColumnCount() - 5) {
                grid.getColumnHeaders().add("Timp pauza");
            } else if (column == grid.getColumnCount() - 4) {
                grid.getColumnHeaders().add("Timp total");
            } else if (column == grid.getColumnCount() - 3) {
                grid.getColumnHeaders().add("Timp suplimentar");
            } else if (column == grid.getColumnCount() - 2) {
                grid.getColumnHeaders().add("Timp noapte");
            } else if (column == grid.getColumnCount() - 1) {
                grid.getColumnHeaders().add("Timp zi");
            } else {
                grid.getColumnHeaders().add(dates.get(column - 1).toString(dtf2));

            }

        }

        final ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        for (int row = 0; row < grid.getRowCount(); ++row) {

            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();

            for (int column = 0; column < grid.getColumnCount(); ++column) {

                if (p == 1) {

                    if (column == 0) {
                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, users.get(row).split("#")[0]));
                    } else if (column == grid.getColumnCount() - 1) {
                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
                                DataProviderImpl.getInstance().getCellData(users.get(row), iniDate, endDate, 1, 1)));
                    } else if (column == grid.getColumnCount() - 2) {
                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
                                DataProviderImpl.getInstance().getCellData(users.get(row), iniDate, endDate, 2, 1)));
                    } else if (column == grid.getColumnCount() - 3) {
                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
                                DataProviderImpl.getInstance().getCellData(users.get(row), iniDate, endDate, 3, 1)));
                    } else if (column == grid.getColumnCount() - 4) {
                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
                                DataProviderImpl.getInstance().getCellData(users.get(row), iniDate, endDate, 4, 1)));

                    } else if (column == grid.getColumnCount() - 5) {
                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
                                DataProviderImpl.getInstance().getCellData(users.get(row), iniDate, endDate, 5, 1)));
                    } else if (column == grid.getColumnCount() - 6) {
                        list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
                                DataProviderImpl.getInstance().getCellData(users.get(row), iniDate, endDate, 6, 1)));
                    } else {
                        String user = users.get(row);
                        if (user.contains("$1")) {
                            user = user.substring(0, user.length() - 2);
                        }
                        final SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(row, column, 1, 1,
                                DataProviderImpl.getInstance().getCellData(user, dates.get(column - 1), dates.get(column - 1), 0, users.get(row).contains("$1") ? 0 : 1));
                        list.add(cell);
                    }
                }
            }
            rows.add(list);
        }
        grid.setRows(rows);

        return grid;
    }

    private List<DateTime> getDatesForMonth() {
        final List<DateTime> result = new ArrayList<>();
        for (DateTime dd = iniDate.withTimeAtStartOfDay(); dd.isBefore(endDate.withTimeAtStartOfDay().plusDays(1)); dd = dd.plusDays(1)) {
            result.add(dd);
        }
        return result;

    }

}
