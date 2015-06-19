/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.controller.content.singlereport;

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

    @FXML
    private void exportTableToPDF() throws IOException, COSVisitorException {
        exportTableBySnapshoot(singleReportTableView, null);
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

    private void exportTableBySnapshoot(TableView<GenericModel> javaFxTableComponent, String filePath) throws IOException, COSVisitorException {
        WritableImage demoTableSnapshot = javaFxTableComponent.snapshot(null, null);

        int width = (int) demoTableSnapshot.getWidth();
        int height = (int) demoTableSnapshot.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        File file = getFile(filePath);
        if (file != null) {
            PDDocument doc = null;
            try {
                doc = new PDDocument();

                PDPage page = new PDPage();
                doc.addPage(page);

                PDXObjectImage ximage = null;
                ximage = new PDPixelMap(doc, SwingFXUtils.fromFXImage(demoTableSnapshot, bufferedImage));

                PDPageContentStream contentStream = new PDPageContentStream(doc, page);

                float xMargin = 50;
                float yMargin = 100;
                float scale = 0.46f;
                float xCoordinate = xMargin;
                float yCoordinate = PDPage.PAGE_SIZE_A4.getUpperRightY() - yMargin - scale * (float) demoTableSnapshot.getHeight();

                // reduce this value if the image is too large
                contentStream.drawXObject(ximage, xCoordinate, yCoordinate, ximage.getWidth() * scale, ximage.getHeight() * scale);

                contentStream.close();
                doc.save(file);

                fxCommonTools.showInfoDialogStatus("PDF Exported", "Export Status", "Exported succesfully to PDF file.");

            } finally {
                if (doc != null) {
                    doc.close();
                }
            }
        }
    }

    private File getFile(String filePath) {
        File file;
        if (filePath == null) {
            file = fxCommonTools.getFileByChooser(exportButton.getContextMenu(), "PDF files (*.pdf)", ".pdf");
        } else {
            file = new File(filePath);
        }
        return file;
    }

}
