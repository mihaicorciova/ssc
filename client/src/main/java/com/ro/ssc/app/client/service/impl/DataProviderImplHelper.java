/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.service.impl;

import com.ro.ssc.app.client.model.commons.Configuration;
import com.ro.ssc.app.client.model.commons.DailyData;
import com.ro.ssc.app.client.model.commons.Event;
import com.ro.ssc.app.client.model.commons.ShiftData;
import com.ro.ssc.app.client.model.commons.User;
import static com.ro.ssc.app.client.utils.Utils.applyExcludeLogic;
import static com.ro.ssc.app.client.utils.Utils.applyExcludeLogic2;
import static com.ro.ssc.app.client.utils.Utils.splitPerDay;
import static com.ro.ssc.app.client.utils.Utils.splitPerDayWrong;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public class DataProviderImplHelper {

    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");
    private static final DateTimeFormatter dtf2 = DateTimeFormat.forPattern("EEE dd-MMM-yyyy");
    private static final DateTimeFormatter dtf3 = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final java.time.format.DateTimeFormatter dtf4 = java.time.format.DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z YYYY");
    private static final java.time.format.DateTimeFormatter dtf5 = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final DecimalFormat df = new DecimalFormat();
    private static final Logger log = LoggerFactory.getLogger(DataProviderImplHelper.class);

    private static Set<String> getNeededPresence(Map<String, User> userData, Map<String, Map<String, ShiftData>> shiftData, String user, DateTime inidate, DateTime endate) {
        Set<String> result = new HashSet<>();
        String userId = userData.get(user).getUserId().trim();

        if (shiftData.containsKey(userId)) {

            for (String day : shiftData.get(userId).keySet()) {

                if (!shiftData.get(userId).get(day).getShiftId().equals("0") && DateTime.parse(day, dtf3).plusDays(1).isAfter(inidate) && DateTime.parse(day, dtf3).isBefore(endate.plusDays(1))) {
                    result.add(day);
                }
            }
        }

        return result;
    }

    public static List<DailyData> getListPerDay(Map<String, User> userData, LocalTime time, Map<String, Map<String, ShiftData>> shiftData, Set<String> excludedGates, String userName, DateTime iniDate, DateTime endDate) {
        List<DailyData> result = new ArrayList();
        Collections.sort(userData.get(userName).getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));
        Map<Pair<DateTime, DateTime>, List<Pair<Event, Event>>> eventsPerDay;
        Map<DateTime, List<Event>> wrongPerDay;
        Set<String> usedDates = new HashSet<>();
        String userId = userData.get(userName).getUserId().trim();
        if (Configuration.IS_EXPIRED.getAsBoolean()) {
            eventsPerDay = splitPerDay(time, applyExcludeLogic(excludedGates, userData.get(userName).getEvents()).get(0), iniDate, endDate);
            wrongPerDay = splitPerDayWrong(time, applyExcludeLogic(excludedGates, userData.get(userName).getEvents()).get(1), iniDate, endDate);
        } else {
            eventsPerDay = splitPerDay(time, applyExcludeLogic2(excludedGates, userData.get(userName).getEvents()).get(0), iniDate, endDate);
            wrongPerDay = splitPerDayWrong(time, applyExcludeLogic2(excludedGates, userData.get(userName).getEvents()).get(1), iniDate, endDate);

        }

        for (DateTime date = iniDate.withTimeAtStartOfDay(); date.isBefore(endDate.plusDays(1).withTimeAtStartOfDay()); date = date.plusDays(1)) {
            final DateTime currentDateAsDateTime = date;

            for (Map.Entry<Pair<DateTime, DateTime>, List<Pair<Event, Event>>> entry : eventsPerDay.entrySet()) {
                if (entry.getKey().getKey().withTimeAtStartOfDay().isEqual(currentDateAsDateTime)) {

                    Long duration = 0l;
                    for (Pair<Event, Event> pair : entry.getValue()) {
                        duration = duration + pair.getValue().getEventDateTime().getMillis() - pair.getKey().getEventDateTime().getMillis();
                    }
                    Long pause = entry.getKey().getValue().getMillis() - entry.getKey().getKey().getMillis() - duration;
                    Long overtime = 0l;
                    Long latetime = 0l;
                    Long earlytime = 0l;
                    if (shiftData.containsKey(userId)) {
                        final Map<String, ShiftData> shiftDataMapForUser = shiftData.get(userId);
                        if (shiftDataMapForUser.containsKey(currentDateAsDateTime.toString(dtf3))) {

                            ShiftData shiftDataInCurrentDate = shiftDataMapForUser.get(currentDateAsDateTime.toString(dtf3));
                            if (shiftDataInCurrentDate.getShiftId().equals("0")) {
                                if (shiftDataInCurrentDate.isHasOvertime()) {
                                    overtime = duration;
                                }
                            } else {
                                LocalTime officialStart = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getShiftStartHour()));
                                LocalTime officialEnd = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getShiftEndHour()));
                                long dailyPause = Long.valueOf(shiftDataInCurrentDate.getShiftBreakTime()) * 1000 * 60l;
                                long dailyHours = officialEnd.isAfter(officialStart)
                                        ? 1000 * (officialEnd.toSecondOfDay() - officialStart.toSecondOfDay())
                                        : 1000 * (officialEnd.toSecondOfDay() + (24 * 60 * 60 - officialStart.toSecondOfDay()));
                                if (shiftDataInCurrentDate.isHasOvertime()) {
                                    
                                    if (pause > dailyPause) {
                                        overtime = duration - dailyHours + dailyPause;
                                    } else {
                                        overtime = duration + pause - dailyHours;
                                        duration = duration - (dailyPause - pause) > 0 ? duration - (dailyPause - pause) : 0;
                                        pause = dailyPause;
                                    }
                                    log.debug(userName+" data "+ currentDateAsDateTime.toString(dtf3) +" overtime "+overtime);
                                } else {
                                    if (duration < dailyHours-dailyPause) {
                                        overtime = duration - dailyHours+dailyPause;
                                    }
                                }
                                earlytime = entry.getKey().getValue().getSecondOfDay() < officialEnd.toSecondOfDay() ? 1000 * (officialEnd.toSecondOfDay() - entry.getKey().getValue().getSecondOfDay()) : 0l;
                                latetime = entry.getKey().getKey().getSecondOfDay() > officialStart.toSecondOfDay() ? 1000 * (entry.getKey().getKey().getSecondOfDay() - officialStart.toSecondOfDay()) : 0l;
                            }
                        }
                    }
                    result.add(new DailyData(userId, date, entry.getKey().getKey().toString(dtf), entry.getKey().getValue().toString(dtf), earlytime,  duration, pause, overtime, latetime, wrongPerDay.get(currentDateAsDateTime),""));
                    usedDates.add(currentDateAsDateTime.toString(dtf3));
                }
                
                
            }

        }

        for (String day : getNeededPresence(userData, shiftData, userName, iniDate, endDate)) {
            
            if (!usedDates.contains(day)) {
                ShiftData shiftDataInCurrentDate= shiftData.get(userId).get(day);
                         LocalTime officialStart = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getShiftStartHour()));
                                LocalTime officialEnd = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getShiftEndHour()));
                                long dailyPause = Long.valueOf(shiftDataInCurrentDate.getShiftBreakTime()) * 1000 * 60l;
                                long dailyHours = officialEnd.isAfter(officialStart)
                                        ? 1000 * (officialEnd.toSecondOfDay() - officialStart.toSecondOfDay())
                                        : 1000 * (officialEnd.toSecondOfDay() + (24 * 60 * 60 - officialStart.toSecondOfDay()));
                     
                if (wrongPerDay.containsKey(DateTime.parse(day, dtf3))) {
                    if (wrongPerDay.get(DateTime.parse(day, dtf3)).size() > 0) {
                        String ev = wrongPerDay.get(DateTime.parse(day, dtf3)).get(0).getEventDateTime().toString(dtf);
                        Boolean isIn = wrongPerDay.get(DateTime.parse(day, dtf3)).get(0).getAddr().contains("In");

                        result.add(new DailyData(userId, DateTime.parse(day, dtf3), isIn == true ? ev : "", isIn == false ? ev : "", 0, 0, 0, dailyPause-dailyHours, 0, wrongPerDay.get(DateTime.parse(day, dtf3)),""));
                    } else {
                        result.add(new DailyData(userId, DateTime.parse(day, dtf3), "", "", 0,  0, 0, dailyPause-dailyHours, 0, new ArrayList<>(),""));

                    }
                } else {
                    result.add(new DailyData(userId, DateTime.parse(day, dtf3), "", "", 0 , 0, 0, dailyPause-dailyHours, 0, new ArrayList<>(),""));

                }
            }
        }

        return result;
    }

    public static List<DailyData> getListOfDay(String userName,Map<String,User> userData, DateTime dateTime,LocalTime time, Set<String> excludedGates){
        List<DailyData> result = new ArrayList();
        Collections.sort(userData.get(userName).getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));
        Map<Pair<DateTime, DateTime>, List<Pair<Event, Event>>> eventsPerDay;
        Map<DateTime, List<Event>> wrongPerDay;
        Set<String> usedDates = new HashSet<>();
        String userId = userData.get(userName).getUserId().trim();
        if (Configuration.IS_EXPIRED.getAsBoolean()) {
            eventsPerDay = splitPerDay(time, applyExcludeLogic(excludedGates, userData.get(userName).getEvents()).get(0), dateTime, dateTime);
        } else {
            eventsPerDay = splitPerDay(time, applyExcludeLogic2(excludedGates, userData.get(userName).getEvents()).get(0), dateTime, dateTime);
        }


        for (Map.Entry<Pair<DateTime, DateTime>, List<Pair<Event, Event>>> entry : eventsPerDay.entrySet()) {
            if (entry.getKey().getKey().withTimeAtStartOfDay().isEqual(dateTime)) {
                Long duration = 0l;
                String aditional="";
                for (Pair<Event, Event> pair : entry.getValue()) {
                    duration = duration + pair.getValue().getEventDateTime().getMillis() - pair.getKey().getEventDateTime().getMillis();
                    if(!pair.getKey().getEventDateTime().equals(entry.getKey().getKey())) {
                        aditional = aditional + " in " + pair.getKey().getEventDateTime().toString(dtf) + "\n";
                    }

                    if(!pair.getValue().getEventDateTime().equals(entry.getKey().getValue())) {
                        aditional = aditional  +" out "+pair.getValue().getEventDateTime().toString(dtf) + "\n";
                    }


                }
                Long pause = entry.getKey().getValue().getMillis() - entry.getKey().getKey().getMillis() - duration;

                result.add(new DailyData(userId, dateTime, entry.getKey().getKey().toString(dtf), entry.getKey().getValue().toString(dtf), 0,  duration, pause, 0, 0, new ArrayList<>(),aditional));


            }
        }


        return result;
    }

}
