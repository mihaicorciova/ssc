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
import java.util.HashMap;
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
            log.debug("User" + entry.getValue().getName());
            Map<DateTime, List<Event>> eventsPerDay = splitPerDay(applayExcludeLogic(entry.getValue().getEvents()).get(0));
            Long tduration = 0l;
            Long tpause = 0l;

            for (Map.Entry<DateTime, List<Event>> day : eventsPerDay.entrySet()) {
                List<Event> events = day.getValue();
            Long duration = 0l;
            Long pause = 0l;
            log.debug("Duration "+ formatMillis(duration)+ "  Pause "+ formatMillis(pause) +"  after day"+day.getKey().toString());
                DateTime firstevent = null;
                if (!events.isEmpty()) {
                    firstevent = events.get(0).getEventDateTime();
                    log.debug("First event"+firstevent.toString());
                

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
   log.debug("In event"+inevent.toString());
                    } else if (events.get(i).getAddr().contains("Exit")) {

                        if (inevent != null) {
                            duration += events.get(i).getEventDateTime().getMillis() - inevent.getMillis();

                            outevent = events.get(i).getEventDateTime();
                            log.debug("In event"+outevent.toString());
                        }

                    }
                }
                }
                log.debug("Duration "+ formatMillis(duration)+ "  Pause "+ formatMillis(pause) +"  after day"+day.getKey().toString());
                tduration+=duration;
                tpause+=pause;
            }
            data.add(new GenericModel(entry.getValue().getName(), entry.getValue().getDepartment(), formatMillis(tduration), formatMillis(tpause), formatMillis(tpause + tduration)));
        }

        overallReportTableView.getItems().setAll(data);
    }

    public static String formatMillis(Long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return hms;
    }

    public List<List<Event>> applayExcludeLogic(List<Event> events) {
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

    public Map<DateTime, List<Event>> splitPerDay(List<Event> events) {
        Map<DateTime, List<Event>> result = new HashMap<>();
        List<Event> perDayList = new ArrayList<>();
        if (!events.isEmpty())
        {
        DateTime dt = events.get(0).getEventDateTime().plusDays(1).withTimeAtStartOfDay();
        for (Event ev : events) {
            if (ev.getEventDateTime().isAfter(dt)) {

                result.put(dt.minusDays(1), perDayList);
                dt = ev.getEventDateTime().plusDays(1).withTimeAtStartOfDay();
                perDayList=null;
                perDayList = new ArrayList<>();
                perDayList.add(ev);

            } else {
                perDayList.add(ev);

            }
        }
        result.put(dt.minusDays(1),perDayList);
        }
        return result;
    }
}
