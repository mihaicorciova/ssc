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
import com.ro.ssc.app.client.model.commons.ShiftData;
import com.ro.ssc.app.client.model.commons.User;
import com.ro.ssc.app.client.service.api.DataProvider;
import static com.ro.ssc.app.client.utils.ExcelReader.readExcel;
import static com.ro.ssc.app.client.utils.AccessReader.updateUserMap;
import static com.ro.ssc.app.client.utils.AccessReader.getShiftData;
import static com.ro.ssc.app.client.utils.Utils.formatMillis;
import static com.ro.ssc.app.client.utils.Utils.splitPerDay;
import static com.ro.ssc.app.client.utils.Utils.splitPerDayNS;
import static com.ro.ssc.app.client.utils.Utils.applyExcludeLogic;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
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
import java.util.stream.Collectors;
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
                private Map<String, Map<String, ShiftData>> shiftData;
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
                            if (department == null || (department != null && entry.getValue().getDepartment().equals(department))) {

                                Long tduration = 0l;
                                Long tpause = 0l;
                                Long tovertime = 0l;
                                int tabsent = 0;
                                int tlate = 0;
                                List<DailyData> dailyList = getListPerDay(entry.getKey(), iniDate, endDate);
                                for (DailyData day : dailyList) {
                                    long workTime = day.getWorkTime() - (day.getPauseTime() > day.getDailyPause() ? day.getPauseTime() - day.getDailyPause() : 0);
                                    long overTime = day.getWorkTime() > day.getShiftHours() * 3600 * 1000 ? (day.getWorkTime() - day.getShiftHours() * 3660 * 1000) : 0l;

                                    if (!day.getFirstInEvent().equals("") && !day.getStartDayTime().equals("")) {
                                        if (DateTime.parse(day.getStartDayTime().trim(), dtf).hourOfDay().get() > (DateTime.parse(day.getFirstInEvent().trim(), dtf).hourOfDay().get())) {
                                            tlate++;
                                        }
                                    }

                                    if (day.getFirstInEvent().equals("") && day.getLastOutEvent().equals("")) {

                                        tabsent++;

                                    }
                                    tduration += workTime;
                                    tpause += day.getPauseTime();
                                    tovertime += overTime;
                                }

                                data.add(new GenericModel(entry.getValue().getName().toUpperCase(), entry.getValue().getDepartment(), formatMillis(tduration), formatMillis(tpause), formatMillis(tpause + tduration),formatMillis(tovertime),tabsent,tlate));
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
                        for (DailyData day : dailyList) {
                            log.debug("Ziua " + day.getDate().toString(dtf2) + " timp lucru " + formatMillis(day.getWorkTime()) + " timp pauza " + formatMillis(day.getPauseTime()) + " pauza zilnica " + formatMillis(day.getDailyPause())+ "norma de lucru"+formatMillis(day.getShiftHours() * 3600 * 1000l));
                            long workTime = day.getWorkTime() - (day.getPauseTime() > day.getDailyPause() ? day.getPauseTime() - day.getDailyPause() : 0);
                            long overTime = day.getWorkTime() > day.getShiftHours() * 3600 * 1000 ? (day.getWorkTime() - day.getShiftHours() * 3600 * 1000) : 0l;
                            boolean late = false;
                            int absent = 0;
                            if (!day.getFirstInEvent().equals("") && !day.getStartDayTime().equals("")) {
                                if (DateTime.parse(day.getStartDayTime().trim(), dtf).hourOfDay().get() > (DateTime.parse(day.getFirstInEvent().trim(), dtf).hourOfDay().get())) {
                                    late = true;
                                }
                            }

                            if (day.getFirstInEvent().equals("") && day.getLastOutEvent().equals("")) {
                                if (day.getWrongEvents().size() > 0) {
                                    absent = 2;
                                } else {
                                    absent = 1;
                                }
                            }

                            data.add(new GenericModel(day.getDate().toString(dtf2), day.getFirstInEvent(), day.getLastOutEvent(), formatMillis(workTime), formatMillis(day.getPauseTime()), formatMillis(day.getWorkTime()  + day.getPauseTime()), formatMillis(overTime), absent == 2 ? "Da***" : absent == 1 ? "Da" : "", late == true ? "Da" : ""));
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

                private Set<String> getNeededPresence(String user) {
                    Set<String> result = new HashSet<>();
                    String userId = userData.get(user).getUserId().trim();
                 
                    if (shiftData.containsKey(userId)) {
                        for (String day : shiftData.get(userId).keySet()) {
                            if (DateTime.parse(day, dtf3).plusDays(1).isAfter(getPossibleDateStart(user)) && DateTime.parse(day, dtf3).isBefore(getPossibleDateEnd(user))) {
                                result.add(day);
                            }
                        }
                    }
                   
                    return result;

                }

                private List<DailyData> getListPerDay(String user, DateTime iniDate, DateTime endDate) {
                    List result = new ArrayList();
                    Collections.sort(userData.get(user).getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));
                    Map<DateTime, List<Event>> eventsPerDay;
                    Map<DateTime, List<Event>> wrongPerDay;
                    if (user.contains("*")) {
                        eventsPerDay = splitPerDayNS(applyExcludeLogic(excludedGates, userData.get(user).getEvents()).get(0), iniDate, endDate);
                        wrongPerDay = splitPerDayNS(applyExcludeLogic(excludedGates, userData.get(user).getEvents()).get(1), iniDate, endDate);
                    } else {
                        eventsPerDay = splitPerDay(applyExcludeLogic(excludedGates, userData.get(user).getEvents()).get(0), iniDate, endDate);
                        wrongPerDay = splitPerDay(applyExcludeLogic(excludedGates, userData.get(user).getEvents()).get(1), iniDate, endDate);
                    }
                    String userId = userData.get(user).getUserId().trim();

                    getNeededPresence(user).stream().forEach((String dd) -> {
                      
                        DateTime day = DateTime.parse(dd, dtf3);
     log.debug("incepe"+dd);
                        if (eventsPerDay.containsKey(day)) {
                       
                            List<Event> events = applyExcludeLogic(excludedGates, eventsPerDay.get(day)).get(0);
                            List<Event> exEvents = applyExcludeLogic(excludedGates, eventsPerDay.get(day)).get(1).stream().filter(event -> (!event.getDescription().contains("night shift"))).collect(Collectors.toList());;

                            if (exEvents.size() > 0) {
                                if (wrongPerDay.containsKey(day)) {
                                    wrongPerDay.get(day).addAll(exEvents);
                                } else {
                                    wrongPerDay.put(day, exEvents);
                                }
                            }

                            Integer jumpIndex = 0;
  Long allowedPause = 60 * 1000 * Long.valueOf(shiftData.get(userId).get(dd).getShiftBreakTime());
                            LocalTime officialStart = LocalTime.from(dtf4.parse(shiftData.get(userId).get(dd).getShiftStartHour()));
                            LocalTime officialEnd = LocalTime.from(dtf4.parse(shiftData.get(userId).get(dd).getShiftEndHour()));
                       String     startOfDay = officialStart.format(dtf5);
                       int     dailyHours = officialEnd.getHour() > officialStart.getHour()? officialEnd.getHour() - officialStart.getHour():officialStart.getHour() - officialEnd.getHour() ;

                         
                            Long duration = 0l;
                            Long pause = 0l;
                           
                          
                            DateTime firstevent = null;
                            DateTime outevent = null;
                            if (!events.isEmpty()) {
                                firstevent = events.get(0).getEventDateTime();
                                DateTime inevent = null;
                                for (Event event : events) {
                                   if (event.getAddr().contains("In")) {
                                            if (event.getDescription().contains("night shift") && !eventsPerDay.containsKey(day.plusDays(1))) {
                                                continue;
                                            }
                                            inevent = event.getEventDateTime();
                                            if (inevent != null) {
                                                if (inevent.isAfter(firstevent.plusHours(dailyHours)) && jumpIndex == 0) {
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
                                                if (outevent.isAfter(firstevent.plusHours(dailyHours))) {
                                                  pause = outevent.getMillis() - firstevent.getMillis() - duration;
                                                    log.debug("user"+userId+" zi "+day+"DAILY HOURS"+dailyHours);
                                                  result.add(new DailyData(userId,day, firstevent != null ? firstevent.toString(dtf) : "", outevent != null ? outevent.toString(dtf) : "", startOfDay, duration, pause, allowedPause, dailyHours, wrongPerDay.get(day)));
                                                    duration = 0l;
                                                    jumpIndex = 0;
                                                }
                                            }
                                        }
                                }
                            }
                            if (firstevent != null && outevent != null) {
                                pause = outevent.getMillis() - firstevent.getMillis() - duration;
                            }

                           if(jumpIndex>0){
                               
                                log.debug("user"+userId+" zi "+day+"DAILY HOURS"+dailyHours);
                            result.add(new DailyData(userId, day, firstevent != null ? firstevent.toString(dtf) : "", outevent != null ? outevent.toString(dtf) : "", startOfDay, duration, pause, allowedPause, dailyHours, wrongPerDay.get(day)));
                           }
                           } else if (wrongPerDay.containsKey(day)) {
                                log.debug("user with wrong"+userId+" zi "+day);
                            result.add(new DailyData(userId, day, "", "", "", 0, 0, 0, 0, wrongPerDay.get(day)));

                        } else {
                               log.debug("user"+userId+" zi "+day);
                            result.add(new DailyData(userId, day, "", "", "", 0, 0, 0, 0, new ArrayList<>()));
                        }
 log.debug("sfarseste"+dd);
                    });

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

                        excludedGates = updateUserMap(dir.listFiles()[0]).get(1);
                        excludedUsers = updateUserMap(dir.listFiles()[0]).get(2);
                        shiftData = getShiftData(dir.listFiles()[0]);
                    }
                }

            };

    public static DataProvider getInstance() {
        return DataProviderImpl.INSTANCE;
    }

}
