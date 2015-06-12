/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.controller.content.sumary;

import com.ro.ssc.app.client.model.commons.Event;
import com.ro.ssc.app.client.model.commons.ExcelEnum;
import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.model.commons.User;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
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
    private Map<String, User> pair = new HashMap<>();
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

                        // populate selected files list view
                        populateListView(files);

                        // parse file   
                        //      
                        for (File file : files) {

                            pair = readExcel(file);
                        }

                        if (!pair.isEmpty()) {
                            populateMyTable();
                            log.debug("not emp");
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
        
        dateTableColumn.setStyle("-fx-alignment:CENTER;");
        hourTableColumn.setStyle("-fx-alignment:CENTER;");
        nameTableColumn.setStyle("-fx-alignment:CENTER;");
        cardNoTableColumn.setStyle("-fx-alignment:CENTER;");
        departmentTableColumn.setStyle("-fx-alignment:CENTER;");
        eventTableColumn.setStyle("-fx-alignment:CENTER;");
        
        DateTimeFormatter dtf =  DateTimeFormat.forPattern("HH:mm:ss");
         DateTimeFormatter dtf2 =  DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
         DecimalFormat df = new DecimalFormat();
         
        ObservableList data = FXCollections.observableArrayList();
        for (Map.Entry<String, User> entry : pair.entrySet()) {
            for (Event ev : entry.getValue().getEvents()) {
                try {
                    data.add(new GenericModel(ev.getEventDateTime().toString(dtf2), ev.getEventDateTime().toString(dtf), entry.getValue().getName().toUpperCase(), df.parse(entry.getValue().getCardNo()), entry.getValue().getDepartment(), ev.getAddr().contains("In")?"Intrare":"Iesire"));
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
    }

    public Map<String, User> readExcel(File file) {
        Map<String, User> result = new HashMap<>();
        List<Event> events = new ArrayList();
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;
            HSSFCell cell;

            int rows; // No of rows
            rows = sheet.getPhysicalNumberOfRows();

            int cols = 0; // No of columns
            int tmp = 0;

            // This trick ensures that we get the data properly even if it doesn't start from first few rows
            for (int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) {
                        cols = tmp;
                    }
                }
            }

            DateTimeFormatter dtf =  DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss EEEE");
            for (int r = 1; r < rows; r++) {
                row = sheet.getRow(r);

                if (row != null) {
                    try {
                        
                        if (result.containsKey(row.getCell(ExcelEnum.USER_NAME.getAsInteger()).toString())) {
                            events = result.get(row.getCell(ExcelEnum.USER_NAME.getAsInteger()).toString()).getEvents();
                            events.add(new Event(DateTime.parse(row.getCell(ExcelEnum.TIMESTAMP.getAsInteger()).toString(), dtf), row.getCell(ExcelEnum.DESCRIPTION.getAsInteger()).toString(), row.getCell(ExcelEnum.ADDRESS.getAsInteger()).toString(), Boolean.valueOf(row.getCell(ExcelEnum.PASSED.getAsInteger()).toString())));
                            result.get(row.getCell(ExcelEnum.USER_NAME.getAsInteger()).toString()).setEvents(events);
                            log.debug(ExcelEnum.USER_NAME.getAsInteger().toString());
                        } else {

                            events.add(new Event(DateTime.parse(row.getCell(ExcelEnum.TIMESTAMP.getAsInteger()).toString(), dtf), row.getCell(ExcelEnum.DESCRIPTION.getAsInteger()).toString(), row.getCell(ExcelEnum.ADDRESS.getAsInteger()).toString(), Boolean.valueOf(row.getCell(ExcelEnum.PASSED.getAsInteger()).toString())));
                            result.put(row.getCell(ExcelEnum.USER_NAME.getAsInteger()).toString(), new User(row.getCell(ExcelEnum.USER_NAME.getAsInteger()).toString().toLowerCase(), row.getCell(ExcelEnum.USER_ID.getAsInteger()).toString(), row.getCell(ExcelEnum.CARD_NO.getAsInteger()).toString(), row.getCell(ExcelEnum.DEPARTMENT.getAsInteger()).toString(), events));
                        }
                    } catch (Exception e) {
  log.error("Exception" + e.getMessage());
                    }
                }
            }
        } catch (Exception ioe) {
            log.error("Exception" + ioe.getMessage());
        }
        return result;
    }

}
