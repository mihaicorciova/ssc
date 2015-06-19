/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.service.impl;

import com.ro.ssc.app.client.controller.content.overallreport.OverallReportController;
import com.ro.ssc.app.client.model.commons.Event;
import com.ro.ssc.app.client.model.commons.GenericModel;
import com.ro.ssc.app.client.model.commons.User;
import com.ro.ssc.app.client.service.api.DataProvider;
import static com.ro.ssc.app.client.utils.ExcelReader.readExcel;
import static com.ro.ssc.app.client.utils.AccessReader.updateUserMap;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
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
                private Map<String, User> userData;
                private final Logger log = LoggerFactory.getLogger(DataProviderImpl.class);

                @Override
                public Map<String, User> getUserData() {
                    return userData;
                }

                @Override
                public void importUserData(File file) {
                    userData = readExcel(file);
                }

                @Override
                public void enrichUserData(File file) {
                    userData = updateUserMap(userData, file);
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

                @Override
                public List<GenericModel> getTableData(DateTime iniDate, DateTime endDate,String department) {

                    List<GenericModel> data = new ArrayList<>();
                    for (Map.Entry<String, User> entry : userData.entrySet()) {
                      
                        if(department==null||(department!=null&& entry.getValue().getDepartment().equals(department))){
                              Collections.sort(entry.getValue().getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));
                        Map<DateTime, List<Event>> eventsPerDay = splitPerDay(applyExcludeLogic(entry.getValue().getEvents()).get(0), iniDate, endDate);
                        Long tduration = 0l;
                        Long tpause = 0l;

                        for (Map.Entry<DateTime, List<Event>> day : eventsPerDay.entrySet()) {
                            List<Event> events = applyExcludeLogic(day.getValue()).get(0);
                            Long duration = 0l;
                            Long pause = 0l;
                            DateTime firstevent = null;
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
                            }
                            // log.debug("Duration " + formatMillis(duration) + "  Pause " + formatMillis(pause) + "  after day" + day.getKey().toString());
                            tduration += duration;
                            tpause += pause;
                        }
                        data.add(new GenericModel(entry.getValue().getName(), entry.getValue().getDepartment(), formatMillis(tduration), formatMillis(tpause), formatMillis(tpause + tduration)));
                    }
                    }
                    return data;
                }

                private String formatMillis(Long millis) {
                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                    return hms;
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
         Set<String> result=new LinkedHashSet<>();
            for (Map.Entry<String, User> entry : userData.entrySet()) {
            result.add(entry.getValue().getDepartment());
            }
            return new ArrayList<>(result);
        }

        @Override
        public List<GenericModel> getUTableData(String user, DateTime iniDate, DateTime endDate) {
           
                    List<GenericModel> data = new ArrayList<>();
                            DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");
        DateTimeFormatter dtf2 = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
                      
                        if(user!=null){
                              Collections.sort(userData.get(user).getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));
                        Map<DateTime, List<Event>> eventsPerDay = splitPerDay(applyExcludeLogic(userData.get(user).getEvents()).get(0), iniDate, endDate);
                     
                        for (Map.Entry<DateTime, List<Event>> day : eventsPerDay.entrySet()) {
                            List<Event> events = applyExcludeLogic(day.getValue()).get(0);
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
                            }
                             log.debug("Duration " + formatMillis(duration) + "  Pause " + formatMillis(pause) + "  after day" + day.getKey().toString());
                            
                             data.add(new GenericModel(day.getKey().toString(dtf2),firstevent!=null? firstevent.toString(dtf):"", outevent!=null?outevent.toString(dtf):"",formatMillis(duration), formatMillis(pause), formatMillis(pause + duration)));
                
                        }
                         }
                    
                    return data;
        }

            };

    public static DataProvider getInstance() {
        return DataProviderImpl.INSTANCE;
    }

}
