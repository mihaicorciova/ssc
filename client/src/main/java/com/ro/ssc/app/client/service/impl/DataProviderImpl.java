/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.service.impl;

import com.ro.ssc.app.client.controller.content.overallreport.OverallReportController;
import com.ro.ssc.app.client.controller.content.sumary.SumaryController;
import com.ro.ssc.app.client.model.commons.Event;
import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.model.commons.User;
import com.ro.ssc.app.client.service.api.DataProvider;
import static com.ro.ssc.app.client.utils.ExcelReader.readExcel;
import static com.ro.ssc.app.client.utils.AccessReader.updateUserMap;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public enum DataProviderImpl implements DataProvider {

    INSTANCE {
                private String MDB_PATH = "mdb";
                private Map<String, User> userData;
                private Set<String> nighShiftUsers;
                private Set<String> excludedGates;
                private Set<String> excludedUsers;
                private DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");
                private DateTimeFormatter dtf2 = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
                private DecimalFormat df = new DecimalFormat();
                private final Logger log = LoggerFactory.getLogger(DataProviderImpl.class);

                @Override
                public List<GenericModel> getUserData() {
                    List<GenericModel> data = new ArrayList<>();
                    for (Map.Entry<String, User> entry : userData.entrySet()) {
                        for (Event ev : entry.getValue().getEvents()) {
                            try {
                                if (nighShiftUsers.contains(entry.getValue().getUserId())) {
                                    log.debug("night shift" + entry.getValue().getUserId());
                                    data.add(new GenericModel(ev.getEventDateTime().toString(dtf2), ev.getEventDateTime().toString(dtf), entry.getValue().getName().toUpperCase() + "*", df.parse(entry.getValue().getCardNo()), entry.getValue().getDepartment(), ev.getAddr().contains("In") ? "Intrare" : "Iesire"));
                                } else {
                                    log.debug("normal shift" + entry.getValue().getUserId() + "\\n");
                                    data.add(new GenericModel(ev.getEventDateTime().toString(dtf2), ev.getEventDateTime().toString(dtf), entry.getValue().getName(), df.parse(entry.getValue().getCardNo()), entry.getValue().getDepartment(), ev.getAddr().contains("In") ? "Intrare" : "Iesire"));

                                }
                            } catch (ParseException ex) {
                                java.util.logging.Logger.getLogger(SumaryController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    return data;
                }

                @Override
                public List<GenericModel> getTableData(DateTime iniDate, DateTime endDate, String department) {

                    List<GenericModel> data = new ArrayList<>();
                    for (Map.Entry<String, User> entry : userData.entrySet()) {
                        if (!excludedUsers.contains(entry.getKey())) {
                            if (department == null || (department != null && entry.getValue().getDepartment().equals(department))) {
                                if (!nighShiftUsers.contains(entry.getValue().getUserId())) {
                                    Collections.sort(entry.getValue().getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));
                                    Map<DateTime, List<Event>> eventsPerDay = splitPerDay(applyExcludeLogic(entry.getValue().getEvents()).get(0), iniDate, endDate);
                                    Long tduration = 0l;
                                    Long tpause = 0l;
                                    Boolean wrongEvent = false;
                                    if (applyExcludeLogic(entry.getValue().getEvents()).get(1).size() > 0) {
                                        wrongEvent = true;
                                    }
                                    for (Map.Entry<DateTime, List<Event>> day : eventsPerDay.entrySet()) {
                                        List<Event> events = applyExcludeLogic(day.getValue()).get(0);
                                        if (applyExcludeLogic(day.getValue()).get(1).size() > 0) {
                                            wrongEvent = true;
                                        }
                                        Long duration = 0l;
                                        Long pause = 0l;
                                        DateTime firstevent = null;
                                        if (!events.isEmpty()) {
                                            firstevent = events.get(0).getEventDateTime();
                                            DateTime inevent = null;
                                            DateTime outevent = null;
                                            for (int i = 0; i < events.size(); i++) {

                                                if (events.get(i).getAddr().contains("In")) {
                                                    inevent = events.get(i).getEventDateTime();
                                                } else if (events.get(i).getAddr().contains("Exit")) {
                                                    if (inevent != null) {
                                                        duration += events.get(i).getEventDateTime().getMillis() - inevent.getMillis();
                                                        outevent = events.get(i).getEventDateTime();
                                                    }
                                                }
                                            }
                                            if (firstevent != null && outevent != null) {
                                                pause = outevent.getMillis() - firstevent.getMillis() - duration;
                                            }
                                        }
                                        // log.debug("Duration " + formatMillis(duration) + "  Pause " + formatMillis(pause) + "  after day" + day.getKey().toString());
                                        tduration += duration;
                                        tpause += pause;
                                    }

                                    data.add(new GenericModel(entry.getValue().getName(), entry.getValue().getDepartment(), formatMillis(tduration), formatMillis(tpause), wrongEvent == true ? formatMillis(tpause + tduration) + " !" : formatMillis(tpause + tduration)));

                                } else if (nighShiftUsers.contains(entry.getValue().getUserId())) {
                                    Collections.sort(entry.getValue().getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));

                                    List<Event> events = applyExcludeLogic(entry.getValue().getEvents()).get(0);
                                    Long duration = 0l;
                                    Long pause = 0l;
                                    DateTime firstevent = null;
                                    Boolean wrongEvent = false;
                                    if (applyExcludeLogic(entry.getValue().getEvents()).get(1).size() > 0) {
                                        wrongEvent = true;
                                    }
                                    if (!events.isEmpty()) {
                                        firstevent = events.get(0).getEventDateTime();
                                        DateTime inevent = null;
                                        DateTime outevent = null;
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
                                            } else if (events.get(i).getAddr().contains("Exit")) {
                                                if (inevent != null) {
                                                    duration += events.get(i).getEventDateTime().getMillis() - inevent.getMillis();
                                                    outevent = events.get(i).getEventDateTime();
                                                }
                                            }
                                        }
                                        if (firstevent != null && outevent != null) {
                                            //    pause = outevent.getMillis() - firstevent.getMillis() - duration;
                                        }
                                    }
                                    data.add(new GenericModel(entry.getValue().getName().toUpperCase() + "*", entry.getValue().getDepartment(), formatMillis(duration), formatMillis(pause), wrongEvent == true ? formatMillis(pause + duration) + " !" : formatMillis(pause + duration)));

                                }
                            }

                        }
                    }
                    return data;
                }

                @Override
                public DateTime getPossibleDateStart() {
                    DateTime result = DateTime.now();
                    for (Map.Entry<String, User> entry : userData.entrySet()) {
                        for (Event ev : entry.getValue().getEvents()) {
                            if (ev.getEventDateTime().isBefore(result)) {
                                result = ev.getEventDateTime();
                            }
                        }
                    }

                    return result;
                }

                @Override
                public DateTime getPossibleDateEnd() {
                    DateTime result = new DateTime().withYear(1970);
                    for (Map.Entry<String, User> entry : userData.entrySet()) {
                        for (Event ev : entry.getValue().getEvents()) {
                            if (ev.getEventDateTime().isAfter(result)) {
                                result = ev.getEventDateTime();
                            }
                        }
                    }

                    return result;
                }

                @Override
                public List<String> getUsers() {
                    return new ArrayList<>(userData.keySet());
                }

                @Override
                public List<String> getDepartments() {
                    Set<String> result = new LinkedHashSet<>();
                    for (Map.Entry<String, User> entry : userData.entrySet()) {
                        result.add(entry.getValue().getDepartment());
                    }
                    return new ArrayList<>(result);
                }

                @Override
                public List<GenericModel> getUTableData(String user, DateTime iniDate, DateTime endDate) {

                    List<GenericModel> data = new ArrayList<>();

                    if (user != null && !excludedUsers.contains(user)) {
                        if (!nighShiftUsers.contains(userData.get(user).getUserId())) {
                            Collections.sort(userData.get(user).getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));
                            Map<DateTime, List<Event>> eventsPerDay = splitPerDay(applyExcludeLogic(userData.get(user).getEvents()).get(0), iniDate, endDate);

                            Map<DateTime, List<Event>> wrongPerDay = splitPerDay(applyExcludeLogic(userData.get(user).getEvents()).get(1), iniDate, endDate);
                            for (Map.Entry<DateTime, List<Event>> day : eventsPerDay.entrySet()) {
                                Boolean wrongEvent = false;
                                List<Event> events = applyExcludeLogic(day.getValue()).get(0);
                                if (applyExcludeLogic(userData.get(user).getEvents()).get(1).size() > 0) {
                                    wrongEvent = true;
                                }
                                if (wrongPerDay.containsKey(day.getKey())) {
                                    if (wrongPerDay.get(day.getKey()).size() > 0) {
                                        wrongEvent = true;
                                    }
                                }
                                Long duration = 0l;
                                Long pause = 0l;
                                DateTime firstevent = null;
                                DateTime outevent = null;
                                if (!events.isEmpty()) {
                                    firstevent = events.get(0).getEventDateTime();

                                    DateTime inevent = null;

                                    for (int i = 0; i < events.size(); i++) {

                                        if (events.get(i).getAddr().contains("In")) {
                                            if (inevent != null && outevent != null) {
                                                if (inevent.getMillis() - firstevent.getMillis() < 8 * 60 * 60 * 1000) {

                                                } else {
                                                    firstevent = inevent;
                                                }
                                            }

                                            inevent = events.get(i).getEventDateTime();

                                        } else if (events.get(i).getAddr().contains("Exit")) {

                                            if (inevent != null) {
                                                duration += events.get(i).getEventDateTime().getMillis() - inevent.getMillis();

                                                outevent = events.get(i).getEventDateTime();

                                            }

                                        }
                                    }
                                }
                                log.debug("Duration " + formatMillis(duration) + "  Pause " + formatMillis(pause) + "  after day" + day.getKey().toString());
                                if (firstevent != null && outevent != null) {
                                    pause = outevent.getMillis() - firstevent.getMillis() - duration;
                                }
                                data.add(new GenericModel(day.getKey().toString(dtf2), firstevent != null ? firstevent.toString(dtf) : "", outevent != null ? outevent.toString(dtf) : "", formatMillis(duration), formatMillis(pause), wrongEvent == true ? formatMillis(pause + duration) + " !" : formatMillis(pause + duration)));

                            }
                        } else if (nighShiftUsers.contains(userData.get(user).getUserId())) {
                            Collections.sort(userData.get(user).getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));

                            Map<DateTime, List<Event>> eventsPerDay = splitPerDayNS(applyExcludeLogic(userData.get(user).getEvents()).get(0), iniDate, endDate);

                            Map<DateTime, List<Event>> wrongPerDay = splitPerDayNS(applyExcludeLogic(userData.get(user).getEvents()).get(1), iniDate, endDate);
                             for (Map.Entry<DateTime, List<Event>> day : eventsPerDay.entrySet()) {
                                Boolean wrongEvent = false;
                                List<Event> events = applyExcludeLogic(day.getValue()).get(0);
                                if (applyExcludeLogic(userData.get(user).getEvents()).get(1).size() > 0) {
                                    wrongEvent = true;
                                }
                                if (wrongPerDay.containsKey(day.getKey())) {
                                    if (wrongPerDay.get(day.getKey()).size() > 0) {
                                        wrongEvent = true;
                                    }
                                }
                                Long duration = 0l;
                                Long pause = 0l;
                                DateTime firstevent = null;
                                DateTime outevent = null;
                                if (!events.isEmpty()) {
                                    firstevent = events.get(0).getEventDateTime();

                                    DateTime inevent = null;

                                    for (int i = 0; i < events.size(); i++) {

                                        if (events.get(i).getAddr().contains("In")) {
                                            if (inevent != null && outevent != null) {
                                                if (inevent.getMillis() - firstevent.getMillis() < 8 * 60 * 60 * 1000) {

                                                } else {
                                                    firstevent = inevent;
                                                }
                                            }

                                            inevent = events.get(i).getEventDateTime();

                                        } else if (events.get(i).getAddr().contains("Exit")) {

                                            if (inevent != null) {
                                                duration += events.get(i).getEventDateTime().getMillis() - inevent.getMillis();

                                                outevent = events.get(i).getEventDateTime();

                                            }

                                        }
                                    }
                                }
                                log.debug("Duration " + formatMillis(duration) + "  Pause " + formatMillis(pause) + "  after day" + day.getKey().toString());
                                if (firstevent != null && outevent != null) {
                                    pause = outevent.getMillis() - firstevent.getMillis() - duration;
                                }
                                data.add(new GenericModel(day.getKey().toString(dtf2), firstevent != null ? firstevent.toString(dtf) : "", outevent != null ? outevent.toString(dtf) : "", formatMillis(duration), formatMillis(pause), wrongEvent == true ? formatMillis(pause + duration) + " !" : formatMillis(pause + duration)));

                            }
                        }
                    }
                    return data;
                }

                @Override
                public void saveMdbFile(File srcFile) {
                    File destDir = new File(MDB_PATH);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                    }

                    try {
                        FileUtils.cleanDirectory(destDir);
                        FileUtils.copyFileToDirectory(srcFile, destDir);
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(DataProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                @Override
                public void importUserData(File file) {
                    userData = readExcel(file);
                    enrichUserData();
                }

                public void enrichUserData() {

                    File dir = new File(MDB_PATH);
                    if (dir.exists()) {
                        log.debug("File from " + MDB_PATH + "  " + dir.listFiles()[0].getName());
                        nighShiftUsers = updateUserMap(dir.listFiles()[0]).get(0);
                        excludedGates = updateUserMap(dir.listFiles()[0]).get(1);
                        excludedUsers = updateUserMap(dir.listFiles()[0]).get(2);
                    }
                }

                public List<List<Event>> applyExcludeLogic(List<Event> events) {
                    List<List<Event>> result = new ArrayList<>();
                    List<Event> trimedEvents = new ArrayList<>();
                    List<Event> remainingEvents = new ArrayList<>();
                    Boolean shouldAdd = false;
                    for (int i = 0; i < events.size() - 1; i++) {
                        if (!excludedGates.contains(events.get(i).getAddr())) {
                            if (events.get(i).getAddr().contains("In") && events.get(i + 1).getAddr().contains("Exit")) {
                                shouldAdd = true;
                                trimedEvents.add(events.get(i));
                            } else if (events.get(i).getAddr().contains("Exit") && shouldAdd) {
                                shouldAdd = false;
                                trimedEvents.add(events.get(i));

                            } else {
                                shouldAdd = false;
                                if (events.get(i + 1).getEventDateTime().getMillis() - events.get(i + 1).getEventDateTime().getMillis() > 15 * 1000l) {
                                    remainingEvents.add(events.get(i));
                                }//  log.debug("Adding " + events.get(i).getAddr() + "to rem events");
                            }
                        }
                    }
                    if (!excludedGates.contains(events.get(events.size() - 1).getAddr())) {
                        if (events.get(events.size() - 1).getAddr().contains("Exit") && shouldAdd) {
                            shouldAdd = false;
                            trimedEvents.add(events.get(events.size() - 1));

                        } else {
                            shouldAdd = false;

                            remainingEvents.add(events.get(events.size() - 1));

                        }

                    }
                    result.add(trimedEvents);
                    result.add(remainingEvents);
                    return result;

                }

                public Map<DateTime, List<Event>> splitPerDay(List<Event> events, DateTime iniDate, DateTime endDate) {
                    Map<DateTime, List<Event>> result = new LinkedHashMap<>();
                    List<Event> perDayList = new ArrayList<>();
                    if (!events.isEmpty()) {
                        DateTime dt = events.get(0).getEventDateTime().plusDays(1).withTimeAtStartOfDay();

                        for (Event ev : events) {
                            if (ev.getEventDateTime().isAfter(dt)) {
                                if (iniDate == null || endDate == null || (dt.minusDays(1).isBefore(endDate) && iniDate != null && endDate != null && dt.isAfter(iniDate))) {

                                    result.put(dt.minusDays(1), perDayList);
                                }
                                dt = ev.getEventDateTime().plusDays(1).withTimeAtStartOfDay();
                                perDayList = null;
                                perDayList = new ArrayList<>();
                                perDayList.add(ev);

                            } else {
                                perDayList.add(ev);

                            }
                        }

                        if (iniDate == null || endDate == null || (dt.minusDays(1).isBefore(endDate) && iniDate != null && endDate != null && dt.isAfter(iniDate))) {

                            result.put(dt.minusDays(1), perDayList);
                        }
                    }
                    return result;
                }

                public Map<DateTime, List<Event>> splitPerDayNS(List<Event> events, DateTime iniDate, DateTime endDate) {
                    Map<DateTime, List<Event>> result = new LinkedHashMap<>();
                    List<Event> perDayList = new ArrayList<>();

                    if (!events.isEmpty()) {
                        DateTime dt = events.get(0).getEventDateTime().plusDays(1).withTimeAtStartOfDay();
                        perDayList.add(new Event(dt.minusDays(1).minusMillis(1), "Intermediate in event for night shift", "In", Boolean.TRUE));
                        for (Event ev : events) {
                            if (ev.getEventDateTime().isAfter(dt)) {
                                if (iniDate == null || endDate == null || (dt.minusDays(1).isBefore(endDate) && iniDate != null && endDate != null && dt.isAfter(iniDate))) {
                                    perDayList.add(new Event(dt.minusMillis(1), "Intermediate exit event for night shift", "Exit", Boolean.TRUE));
                                    result.put(dt.minusDays(1), perDayList);
                                }
                                dt = ev.getEventDateTime().plusDays(1).withTimeAtStartOfDay();
                                perDayList = null;
                                perDayList = new ArrayList<>();
                                perDayList.add(new Event(dt.minusDays(1).minusMillis(1), "Intermediate in event for night shift", "In", Boolean.TRUE));
                                perDayList.add(ev);
                            } else {
                                perDayList.add(ev);
                            }
                        }

                        if (iniDate == null || endDate == null || (dt.minusDays(1).isBefore(endDate) && iniDate != null && endDate != null && dt.isAfter(iniDate))) {

                            result.put(dt.minusDays(1), perDayList);
                        }
                    }
                    return result;
                }

                private String formatMillis(Long millis) {
                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                    return hms;
                }
            };

    public static DataProvider getInstance() {
        return DataProviderImpl.INSTANCE;
    }

}
