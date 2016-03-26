/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.service.impl;

import com.ro.ssc.app.client.controller.content.sumary.SumaryController;
import com.ro.ssc.app.client.model.commons.DailyData;
import com.ro.ssc.app.client.model.commons.Event;
import com.ro.ssc.app.client.model.commons.GenericModel;

import com.ro.ssc.app.client.model.commons.User;
import com.ro.ssc.app.client.service.api.DataProvider;

import static com.ro.ssc.app.client.utils.AccessReader.updateUserMap;
import static com.ro.ssc.app.client.utils.ExcelReader.readExcel;
import static com.ro.ssc.app.client.utils.Utils.applyExcludeLogic;
import static com.ro.ssc.app.client.utils.Utils.formatMillis;
import static com.ro.ssc.app.client.utils.Utils.splitPerDay;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
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
                private Map<String, User> userData = new HashMap();

                private Set<String> excludedGates;
                private Set<String> excludedUsers;
                private DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");
                private DateTimeFormatter dtf2 = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
                private DateTimeFormatter dtf3 = DateTimeFormat.forPattern("yyyy-MM-dd");
                private java.time.format.DateTimeFormatter dtf4 = java.time.format.DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z YYYY");
                private java.time.format.DateTimeFormatter dtf5 = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");

                private DecimalFormat df = new DecimalFormat();
                private final Logger log = LoggerFactory.getLogger(DataProviderImpl.class);

                @Override
                public List<GenericModel> getUserData() {
                    List<GenericModel> data = new ArrayList<>();
                    for (Map.Entry<String, User> entry : userData.entrySet()) {
                        for (Event ev : entry.getValue().getEvents()) {
                            try {
                                if (entry.getKey().contains("*")) {

                                    data.add(new GenericModel(ev.getEventDateTime().toString(dtf2), ev.getEventDateTime().toString(dtf), entry.getValue().getName().toUpperCase(), df.parse(entry.getValue().getCardNo()), entry.getValue().getDepartment(), ev.getAddr().contains("In") ? "Intrare" : "Iesire"));
                                } else {

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
                public List<GenericModel> getOverallTableData(DateTime iniDate, DateTime endDate, String department) {

                    List<GenericModel> data = new ArrayList<>();
                    for (Map.Entry<String, User> entry : userData.entrySet()) {
                        if (!excludedUsers.contains(entry.getKey())) {
                            if (department == null || (entry.getValue().getDepartment().equals(department))) {

                                Long tduration = 0L;

                                Long tpause = 0L;

                                boolean withWrongEv = false;
                                List<DailyData> dailyList = getListPerDay(entry.getKey(), iniDate, endDate);
                                for (DailyData day : dailyList) {

                                    if (day.getFirstInEvent().equals("") || day.getLastOutEvent().equals("")) {
                                        if (day.getWrongEvents().size() > 0) {
                                            withWrongEv = true;
                                        }

                                    }
                                    tduration += day.getWorkTime();

                                    tpause += day.getPauseTime();

                                }

                                data.add(new GenericModel(entry.getValue().getName().toUpperCase(), entry.getValue().getDepartment(), formatMillis(tduration), formatMillis(tpause), withWrongEv == false ? formatMillis(tpause + tduration) : formatMillis(tpause + tduration) + "!"));
                            }
                        }

                    }

                    return data;
                }

                @Override
                public List<GenericModel> getUserSpecificTableData(String user, DateTime iniDate, DateTime endDate) {

                    List<GenericModel> data = new ArrayList<>();

                    if (user != null && !excludedUsers.contains(user)) {

                        List<DailyData> dailyList = getListPerDay(user, iniDate, endDate);
                        for (int i = 0; i < dailyList.size(); i++) {

                            int absent = 0;

                            if (dailyList.get(i).getFirstInEvent().equals("") || dailyList.get(i).getLastOutEvent().equals("")) {
                                if (dailyList.get(i).getWrongEvents().size() > 0) {
                                    absent = 2;
                                } else {
                                    absent = 1;
                                }
                            }
                            if (!user.contains("*")) {
                                data.add(new GenericModel(dailyList.get(i).getDate().toString(dtf2), dailyList.get(i).getFirstInEvent(), dailyList.get(i).getLastOutEvent(), formatMillis(dailyList.get(i).getWorkTime()), formatMillis(dailyList.get(i).getPauseTime()), absent == 0 ? formatMillis(dailyList.get(i).getWorkTime() + dailyList.get(i).getPauseTime()) : formatMillis(dailyList.get(i).getWorkTime() + dailyList.get(i).getPauseTime()) + "!"));
                            } else if (i + 1 != dailyList.size()) {
                                if (dailyList.get(i).getDate().plusDays(1).equals(dailyList.get(i + 1).getDate())) {
                                    log.debug(user + " " + dailyList.get(i).getDate().toString(dtf) + "");
                                    if (dailyList.get(i + 1).getFirstInEvent().equals("00:00:00") && dailyList.get(i).getLastOutEvent().equals("23:59:59")) {
                                        log.debug(user + " " + dailyList.get(i) + "in if");
                                        data.add(new GenericModel(dailyList.get(i).getDate().toString(dtf2), dailyList.get(i).getFirstInEvent(), dailyList.get(i + 1).getLastOutEvent(), formatMillis(dailyList.get(i).getWorkTime() + dailyList.get(i + 1).getWorkTime()), formatMillis(dailyList.get(i).getPauseTime() + dailyList.get(i + 1).getPauseTime()), absent == 0 ? formatMillis(dailyList.get(i).getWorkTime() + dailyList.get(i).getPauseTime() + dailyList.get(i + 1).getWorkTime() + dailyList.get(i + 1).getPauseTime()) : formatMillis(dailyList.get(i).getWorkTime() + dailyList.get(i).getPauseTime() + dailyList.get(i + 1).getWorkTime() + dailyList.get(i + 1).getPauseTime()) + "!"));
                                        i++;
                                    } else {
                                        log.debug(user + " " + dailyList.get(i) + "in else");
                                        data.add(new GenericModel(dailyList.get(i).getDate().toString(dtf2), dailyList.get(i).getFirstInEvent(), dailyList.get(i).getLastOutEvent(), formatMillis(dailyList.get(i).getWorkTime()), formatMillis(dailyList.get(i).getPauseTime()), absent == 0 ? formatMillis(dailyList.get(i).getWorkTime() + dailyList.get(i).getPauseTime()) : formatMillis(dailyList.get(i).getWorkTime() + dailyList.get(i).getPauseTime()) + "!"));

                                    }
                                } else {
                                    data.add(new GenericModel(dailyList.get(i).getDate().toString(dtf2), dailyList.get(i).getFirstInEvent(), dailyList.get(i).getLastOutEvent(), formatMillis(dailyList.get(i).getWorkTime()), formatMillis(dailyList.get(i).getPauseTime()), absent == 0 ? formatMillis(dailyList.get(i).getWorkTime() + dailyList.get(i).getPauseTime()) : formatMillis(dailyList.get(i).getWorkTime() + dailyList.get(i).getPauseTime()) + "!"));

                                }
                            }
                        }
                    }

                    return data;
                }

                @Override
                public DateTime getPossibleDateEnd(String user) {
                    DateTime result = new DateTime().withYear(1970);

                    if (user.equals("all")) {
                        for (Map.Entry<String, User> entry : userData.entrySet()) {
                            for (Event ev : entry.getValue().getEvents()) {
                                if (ev.getEventDateTime().isAfter(result)) {
                                    result = ev.getEventDateTime();
                                }
                            }
                        }
                    } else if (userData.containsKey(user)) {
                        for (Event ev : userData.get(user).getEvents()) {
                            if (ev.getEventDateTime().isAfter(result)) {
                                result = ev.getEventDateTime();
                            }
                        }
                    }

                    return result;
                }

                @Override
                public DateTime getPossibleDateStart(String user) {
                    DateTime result = DateTime.now();

                    if (user.equals("all")) {
                        for (Map.Entry<String, User> entry : userData.entrySet()) {
                            for (Event ev : entry.getValue().getEvents()) {
                                if (ev.getEventDateTime().isBefore(result)) {
                                    result = ev.getEventDateTime();
                                }
                            }
                        }
                    } else if (userData.containsKey(user)) {
                        for (Event ev : userData.get(user).getEvents()) {
                            if (ev.getEventDateTime().isBefore(result)) {
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

                private List<DailyData> getListPerDay(String user, DateTime iniDate, DateTime endDate) {
                    List result = new ArrayList();

                    Collections.sort(userData.get(user).getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));
                    Map<DateTime, List<Event>> eventsPerDay;
                    Map<DateTime, List<Event>> wrongPerDay;
                    String userId = userData.get(user).getUserId().trim();
                    eventsPerDay = splitPerDay(user, applyExcludeLogic(excludedGates, userData.get(user).getEvents()).get(0), iniDate, endDate, true);
                    wrongPerDay = splitPerDay(user, applyExcludeLogic(excludedGates, userData.get(user).getEvents()).get(1), iniDate, endDate, false);

                    for (DateTime day : eventsPerDay.keySet()) {
                        if (eventsPerDay.containsKey(day)) {

                            List<Event> events = applyExcludeLogic(excludedGates, eventsPerDay.get(day)).get(0);
                            List<Event> exEvents = applyExcludeLogic(excludedGates, eventsPerDay.get(day)).get(1);

                            if (exEvents.size() > 0) {
                                if (wrongPerDay.containsKey(day)) {
                                    wrongPerDay.get(day).addAll(exEvents);
                                } else {
                                    wrongPerDay.put(day, exEvents);
                                }
                            }

                            Integer jumpIndex = 0;

                            Long duration = 0l;
                            Long pause = 0l;

                            DateTime firstevent = null;
                            DateTime outevent = null;
                            if (!events.isEmpty()) {
                                firstevent = events.get(0).getEventDateTime();
                                DateTime inevent = null;
                                for (Event event : events) {
                                    if (event.getAddr().contains("In")) {

                                        inevent = event.getEventDateTime();
                                        if (inevent != null) {
                                            if (inevent.isAfter(firstevent.plusHours(8)) && jumpIndex == 0) {
                                                firstevent = inevent;
                                            }
                                        }
                                    } else if (event.getAddr().contains("Exit")) {
                                        if (event.getDescription().contains("night shift") && !eventsPerDay.containsKey(day.plusDays(1))) {

                                            continue;
                                        }
                                        if (inevent != null) {
                                            duration += event.getEventDateTime().getMillis() - inevent.getMillis();
                                            jumpIndex++;
                                            outevent = event.getEventDateTime();
                                        }
                                        if (firstevent != null && outevent != null) {
                                            if (outevent.isAfter(firstevent.plusHours(1))) {
                                                pause = outevent.getMillis() - firstevent.getMillis() - duration;

                                                result.add(new DailyData(user, day, firstevent.toString(dtf), outevent.toString(dtf), duration, pause, wrongPerDay.get(day)));

                                                duration = 0l;
                                                jumpIndex = 0;
                                            }
                                        }
                                    }
                                }
                            }

                            if (firstevent != null && outevent != null) {
                                pause = outevent.getMillis() - firstevent.getMillis() - duration;
                                if (jumpIndex > 0) {
                                    result.add(new DailyData(userId, day, firstevent.toString(dtf), outevent.toString(dtf), duration, pause, wrongPerDay.get(day)));
                                }
                            }

                        } else if (wrongPerDay.containsKey(day)) {

                            result.add(new DailyData(userId, day, wrongPerDay.get(day).get(0).getAddr().contains("In") ? wrongPerDay.get(day).get(0).getEventDateTime().toString(dtf) : "", wrongPerDay.get(day).get(0).getAddr().contains("Exit") ? wrongPerDay.get(day).get(0).getEventDateTime().toString(dtf) : "", 0, 0, wrongPerDay.get(day)));

                        } else {

                            result.add(new DailyData(userId, day, "", "", 0, 0, new ArrayList<>()));

                        }

                    }

                    Comparator dateComparator = (Comparator<DailyData>) (DailyData o1, DailyData o2) -> {
                        return Long.compare(o1.getDate().getMillis(), o2.getDate().getMillis());
                    };
                    Collections.sort(result, dateComparator);
                    return result;
                }

                private void enrichUserData() {

                    File dir = new File(MDB_PATH);
                    if (dir.exists()) {
                        log.debug("File from " + MDB_PATH + "  " + dir.listFiles()[0].getName());

                        updateUserMap(dir.listFiles()[0]).get(0).stream().forEach(p -> {
                            String userId = p.split("-")[0];
                            String userName = p.split("-")[1];
                            userData.get(userName).setUserId(userId);
                        });
                        excludedGates = updateUserMap(dir.listFiles()[0]).get(1);
                        excludedUsers = updateUserMap(dir.listFiles()[0]).get(2);

                    }
                }

            };

    public static DataProvider getInstance() {
        return DataProviderImpl.INSTANCE;
    }

}
