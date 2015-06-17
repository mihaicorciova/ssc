/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.controller.content.overallreport;

import com.ro.ssc.app.client.model.commons.Event;
import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.model.commons.User;
import com.ro.ssc.app.client.service.impl.DataProviderImpl;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public class OverallReportController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(OverallReportController.class);
    @FXML
    private Button selectButton;
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

        final FileChooser fileChooser = new FileChooser();

        if (!DataProviderImpl.getInstance().getUserData().isEmpty()) {
            populateMyTable(DataProviderImpl.getInstance().getUserData());
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

    public void populateMyTable(Map<String, User> pair) {

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

        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");
        DateTimeFormatter dtf2 = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
        DecimalFormat df = new DecimalFormat();

        ObservableList data = FXCollections.observableArrayList();
        for (Map.Entry<String, User> entry : pair.entrySet()) {
            Collections.sort(entry.getValue().getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));
            List<Event> events = entry.getValue().getEvents();
            DateTime firstevent = entry.getValue().getEvents().get(0).getEventDateTime();
            DateTime inevent = null;
            DateTime outevent = null;
            Integer k = 0;
            Long duration = 0l;
            Long pause = 0l;
            for (int i = 0; i < events.size(); i++) {

                if (events.get(i).getAddr().contains("In")) {
                    inevent = events.get(i).getEventDateTime();
                    if (inevent.getMillis() - firstevent.getMillis() < 8 * 60 * 60 * 1000) {
                        pause += events.get(k).getEventDateTime().getMillis() - firstevent.getMillis();
                    } else {
                        firstevent = inevent;
                    }
                } else if (events.get(i).getAddr().contains("Exit")) {

                    if (events.get(i + 1).getAddr().contains("Exit")) {
                        i++;
                    } else if (inevent != null) {
                        duration += events.get(i).getEventDateTime().getMillis() - inevent.getMillis();
                        k = i;
                    }

                }
            }

            data.add(new GenericModel(entry.getValue().getName(), entry.getValue().getDepartment(), formatMillis(duration), formatMillis(pause), formatMillis(6000000l)));
        }

        overallReportTableView.getItems().setAll(data);
    }

    public static String formatMillis(Long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return hms;
    }

}
