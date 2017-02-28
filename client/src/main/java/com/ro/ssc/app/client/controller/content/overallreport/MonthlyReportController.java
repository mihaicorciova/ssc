package com.ro.ssc.app.client.controller.content.overallreport;

import com.ro.ssc.app.client.exporter.PptTableExporter;
import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.service.impl.DataProviderImpl;
import com.ro.ssc.app.client.ui.commons.UiCommonTools;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public class MonthlyReportController implements Initializable {
    
    private static final UiCommonTools fxCommonTools = UiCommonTools.getInstance();
    private static final Logger log = LoggerFactory.getLogger(MonthlyReportController.class);
    private static final String ALL = "all";
    private static final String[] MONTHS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private static final String[] YEARS = {"2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022"};
    private String selectedMonth;
    private String selectedYear;
    
    private final DateTimeFormatter dtf = DateTimeFormat.forPattern("MM");
    private final DateTimeFormatter dtf2 = DateTimeFormat.forPattern("YYYY");
    
    @FXML
    private ChoiceBox departmentChoiceBox;
    @FXML
    private Button exportButton;
    @FXML
    private ChoiceBox monthChoiceBox;
    @FXML
    private ChoiceBox yearChoiceBox;
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
            monthChoiceBox.setItems(FXCollections.observableArrayList(MONTHS));
            yearChoiceBox.setItems(FXCollections.observableArrayList(YEARS));
            monthChoiceBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                populateMyTable();
            });
            
            yearChoiceBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                populateMyTable();
            });
            
            departmentChoiceBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                populateMyTable();
            });
            
            departmentChoiceBox.setItems(FXCollections.observableArrayList(DataProviderImpl.getInstance().getDepartments()));
            
            selectedMonth = DataProviderImpl.getInstance().getPossibleDateStart(ALL).toString(dtf);
            selectedYear = DataProviderImpl.getInstance().getPossibleDateStart(ALL).toString(dtf2);
            
            if (selectedMonth != null) {
                monthChoiceBox.setValue(selectedMonth);
            }
            
            if (selectedYear != null) {
                yearChoiceBox.setValue(selectedYear);
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
            //     pptExporter.exportTableToPpt(overallReportTableView, file, "Raport cumulativ de la " + iniDatePicker.getValue().format(formatter) + " pana la " + endDatePicker.getValue().format(formatter));
            fxCommonTools.showInfoDialogStatus("Raport exportat", "Status-ul exportului", "Raportul s- a exportat cu succes in PPT.");
        } else {

            //  pptExporter.exportTableToXls(overallReportTableView, file, "Raport individual absente pentru ");
            fxCommonTools.showInfoDialogStatus("Raport exportat", "Status-ul exportului", "Raportul s- a exportat cu succes in XLS.");
        }
        
    }
    
    public void populateMyTable() {
        monthlySpreadsheetView.setGrid(getGridBase(ALL));
        monthlySpreadsheetView.setRowHeaderWidth(150);
    }
    
    private GridBase getGridBase(String date) {
        
        List<String> users = DataProviderImpl.getInstance().getUsersDep();
        
        List<String> dates = getDatesForMonth(date);
        int i = 0;
        
        final GridBase grid = new GridBase(users.size(), dates.size() + 4);
        
        grid.getColumnHeaders().clear();
        grid.getRowHeaders().clear();
        String department = "££££";
        
        for (String entry : users) {
            
            if (!department.equals(DataProviderImpl.getInstance().getDepartmentFromUser(entry))) {
                department = DataProviderImpl.getInstance().getDepartmentFromUser(entry);
                grid.getRowHeaders().add(department);
            } else {
                grid.getRowHeaders().add("");
            }
            
        }
        
        for (int column = 0; column < grid.getColumnCount()-4; ++column) {
            grid.getColumnHeaders().add(dates.get(column));
        }
        final ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        for (int row = 0; row < grid.getRowCount(); ++row) {
            
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
            
            for (int column = 0; column < grid.getColumnCount(); ++column) {
//
////                    final SpreadsheetCell cell
////                            = SpreadsheetCellType.STRING
////                           .createCell(row, column, 1, 1, DataProviderImpl.getInstance().getCellData(users.get(row), dates.get(column)));
                //      list.add(cell);
                if (column == 0) {
                    list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, users.get(row)));
                } else if(column==grid.getColumnCount()){
                    list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "r" + row + "c" + column));
                }else{
                       list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, ""));  
                        }
            }
            rows.add(list);
        }
        grid.setRows(rows);
        
        return grid;
    }
    
    private List<String> getDatesForMonth(String date) {
        return Arrays.asList(MONTHS);
    }
    
}
