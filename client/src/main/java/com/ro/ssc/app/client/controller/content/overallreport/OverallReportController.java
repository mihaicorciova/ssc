/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.controller.content.overallreport;

import com.ro.ssc.app.client.exporter.Column;
import com.ro.ssc.app.client.exporter.PDFTableGenerator;
import com.ro.ssc.app.client.exporter.Table;
import com.ro.ssc.app.client.exporter.TableBuilder;
import com.ro.ssc.app.client.model.commons.Event;
import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.model.commons.User;
import com.ro.ssc.app.client.service.impl.DataProviderImpl;
import com.ro.ssc.app.client.ui.commons.UiCommonTools;
import com.sun.javafx.scene.control.skin.DatePickerContent;
import com.sun.javafx.scene.control.skin.DatePickerSkin;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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
public class OverallReportController implements Initializable {

    private static final UiCommonTools fxCommonTools = UiCommonTools.getInstance();
    private static final Logger log = LoggerFactory.getLogger(OverallReportController.class);
    private DateCell iniCell = null;
    private DateCell endCell = null;

    private LocalDate iniDate;
    private LocalDate endDate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
    ;
    @FXML
    private Button exportButton;
    @FXML
    private DatePicker iniDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ListView filesListView;
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
            populateMyTable(DataProviderImpl.getInstance().getUserData(), iniDate, endDate);

            log.debug("not emp");
        }

    }

    private void populateListView(List<File> files) {
        List<String> ls = new ArrayList<>();
        files.stream().forEach((file) -> {
            ls.add(file.getName());
        });

        filesListView.setItems(FXCollections.observableArrayList(ls));

    }

    @FXML
    private void exportTableToPDF() throws IOException, COSVisitorException {

        exportTableBySnapshoot(overallReportTableView, null);

    }

    public void populateMyTable(Map<String, User> pair, LocalDate iniDate, LocalDate endDate) {

        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("one"));
        departmentTableColumn.setCellValueFactory(new PropertyValueFactory<>("two"));
        workTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("three"));
        offTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("four"));
        totalTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("five"));

        workTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        offTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        nameTableColumn.setStyle("-fx-alignment:CENTER;");
        totalTimeTableColumn.setStyle("-fx-alignment:CENTER;");
        departmentTableColumn.setStyle("-fx-alignment:CENTER;");

        org.joda.time.format.DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");
        org.joda.time.format.DateTimeFormatter dtf2 = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
        DecimalFormat df = new DecimalFormat();

        ObservableList data = FXCollections.observableArrayList();
        for (Map.Entry<String, User> entry : pair.entrySet()) {
            Collections.sort(entry.getValue().getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));
            log.debug("User" + entry.getValue().getName());
            Map<DateTime, List<Event>> eventsPerDay = splitPerDay(applyExcludeLogic(entry.getValue().getEvents()).get(0), iniDate, endDate);
            Long tduration = 0l;
            Long tpause = 0l;

            for (Map.Entry<DateTime, List<Event>> day : eventsPerDay.entrySet()) {
                List<Event> events = day.getValue();
                Long duration = 0l;
                Long pause = 0l;
                log.debug("Duration " + formatMillis(duration) + "  Pause " + formatMillis(pause) + "  after day" + day.getKey().toString());
                DateTime firstevent = null;
                if (!events.isEmpty()) {
                    firstevent = events.get(0).getEventDateTime();
                    log.debug("First event" + firstevent.toString());

                    DateTime inevent = null;
                    DateTime outevent = null;
                    Integer k = 0;

                    for (int i = 0; i < events.size(); i++) {

                        if (events.get(i).getAddr().contains("In")) {
                            if (inevent != null && outevent != null) {
                                if (inevent.getMillis() - firstevent.getMillis() < 8 * 60 * 60 * 1000) {
                                    pause += outevent.getMillis() - inevent.getMillis();

                                } else {
                                    firstevent = inevent;
                                }
                            }

                            inevent = events.get(i).getEventDateTime();
                            log.debug("In event" + inevent.toString());
                        } else if (events.get(i).getAddr().contains("Exit")) {

                            if (inevent != null) {
                                duration += events.get(i).getEventDateTime().getMillis() - inevent.getMillis();

                                outevent = events.get(i).getEventDateTime();
                                log.debug("In event" + outevent.toString());
                            }

                        }
                    }
                }
                log.debug("Duration " + formatMillis(duration) + "  Pause " + formatMillis(pause) + "  after day" + day.getKey().toString());
                tduration += duration;
                tpause += pause;
            }
            data.add(new GenericModel(entry.getValue().getName(), entry.getValue().getDepartment(), formatMillis(tduration), formatMillis(tpause), formatMillis(tpause + tduration)));
        }

        overallReportTableView.getItems().setAll(data);
    }

    private void exportTableBySnapshoot(TableView<GenericModel> javaFxTableComponent, String filePath) throws IOException, COSVisitorException {
        WritableImage demoTableSnapshot = javaFxTableComponent.snapshot(null, null);

        int width = (int) demoTableSnapshot.getWidth();
        int height = (int) demoTableSnapshot.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        File file = getFile(filePath);

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

    public static String formatMillis(Long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return hms;
    }

    public List<List<Event>> applyExcludeLogic(List<Event> events) {
        List<List<Event>> result = new ArrayList<>();
        List<Event> trimedEvents = new ArrayList<>();
        List<Event> remainingEvents = new ArrayList<>();
        Boolean shouldAdd = false;
        for (int i = 0; i < events.size() - 1; i++) {

            if (events.get(i).getAddr().contains("In") && events.get(i + 1).getAddr().contains("Exit")) {
                shouldAdd = true;
                trimedEvents.add(events.get(i));

            } else if (events.get(i).getAddr().contains("Exit") && shouldAdd) {

                shouldAdd = false;
                trimedEvents.add(events.get(i));

            } else {
                shouldAdd = false;
                remainingEvents.add(events.get(i));
                //  log.debug("Adding " + events.get(i).getAddr() + "to rem events");
            }

        }
        if (events.get(events.size() - 1).getAddr().contains("Exit") && shouldAdd) {

            shouldAdd = false;
            trimedEvents.add(events.get(events.size() - 1));

        } else {
            shouldAdd = false;
            remainingEvents.add(events.get(events.size() - 1));
            //  log.debug("Adding " + events.get(events.size() - 1).getAddr() + "to rem events");
        }

        result.add(trimedEvents);
        result.add(remainingEvents);
        return result;
    }

    public Map<DateTime, List<Event>> splitPerDay(List<Event> events, LocalDate iniDate, LocalDate endDate) {
        Map<DateTime, List<Event>> result = new HashMap<>();
        List<Event> perDayList = new ArrayList<>();
        if (!events.isEmpty()) {
            DateTime dt = events.get(0).getEventDateTime().plusDays(1).withTimeAtStartOfDay();

           

            for (Event ev : events) {
                if (ev.getEventDateTime().isAfter(dt)) {

                    result.put(dt.minusDays(1), perDayList);
                    dt = ev.getEventDateTime().plusDays(1).withTimeAtStartOfDay();
                    perDayList = null;
                    perDayList = new ArrayList<>();
                    perDayList.add(ev);

                } else {
                    perDayList.add(ev);

                }
            }

            if (iniDate != null && endDate != null && dt.minusDays(1).isAfter(DateTime.parse(iniDate.toString())) && dt.minusDays(1).isBefore(DateTime.parse(endDate.toString()))) {
                result.put(dt.minusDays(1), perDayList);
            } else if (iniDate == null || endDate == null) {
                result.put(dt.minusDays(1), perDayList);
            }
        }
        return result;
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
