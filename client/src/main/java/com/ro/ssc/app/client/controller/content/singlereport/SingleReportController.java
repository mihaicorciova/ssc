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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public class SingleReportController implements Initializable {

    private static final UiCommonTools fxCommonTools = UiCommonTools.getInstance();
    private static final Logger log = LoggerFactory.getLogger(SingleReportController.class);

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
    private TableColumn<GenericModel, Object> offTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> totalTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> dateTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> workTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> entryTimeTableColumn;
    @FXML
    private TableColumn<GenericModel, Object> exitTimeTableColumn;

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
            iniDate = DataProviderImpl.getInstance().getPossibleDateStart();
            endDate = DataProviderImpl.getInstance().getPossibleDateEnd();

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
        workTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("four"));
        offTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("five"));
        totalTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("six"));
        dateTableColumn.setStyle("-fx-alignment:CENTER;");
        workTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        offTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        entryTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        totalTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        exitTimeTableColumn.setStyle("-fx-alignment:CENTER;");

        singleReportTableView.getItems().setAll(FXCollections.observableArrayList(DataProviderImpl.getInstance().getUTableData(userChoiceBox.getSelectionModel().getSelectedItem().toString(), iniDate, endDate)));
    }

   @FXML
    private void exportTableToPPT() {
        File file = fxCommonTools.getFileByChooser(exportButton.getContextMenu(), "PPT files (*.ppt)", ".ppt");

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
                    rowNo++;
                }
                return content;
            }
        };

        pptExporter.exportTableToPpt(singleReportTableView, file, "Raport individual pentru "+userChoiceBox.getSelectionModel().getSelectedItem().toString()+" de la "+endDatePicker.getValue().format(formatter)+" pana la "+endDatePicker.getValue().format(formatter));
        fxCommonTools.showInfoDialogStatus("Raport exportat", "Status-ul exportului", "Raportul s- a exportat cu succes in PPT.");
    }

}
