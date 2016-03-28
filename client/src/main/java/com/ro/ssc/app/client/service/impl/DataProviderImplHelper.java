/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.service.impl;

import com.ro.ssc.app.client.model.commons.DailyData;
import com.ro.ssc.app.client.model.commons.Event;
import com.ro.ssc.app.client.model.commons.ShiftData;
import com.ro.ssc.app.client.model.commons.User;
import static com.ro.ssc.app.client.utils.Utils.applyExcludeLogic;
import static com.ro.ssc.app.client.utils.Utils.splitPerDay;
import static com.ro.ssc.app.client.utils.Utils.splitPerDayWrong;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jooq.lambda.Seq;
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

                if (DateTime.parse(day, dtf3).plusDays(1).isAfter(DataProviderImpl.getInstance().getPossibleDateStart(user)) && DateTime.parse(day, dtf3).isBefore(DataProviderImpl.getInstance().getPossibleDateEnd(user))) {
                    result.add(day);
                }
            }
        }

        return result;
    }
    
     private static Set<String> getPossiblePresence(Map<String, User> userData, Map<String, Map<String, ShiftData>> shiftData, String user, DateTime inidate, DateTime endate) {
        Set<String> result = new HashSet<>();
        String userId = userData.get(user).getUserId().trim();

        if (shiftData.containsKey(userId)) {

            for (String day : shiftData.get(userId).keySet()) {

                if (DateTime.parse(day, dtf3).plusDays(1).isAfter(DataProviderImpl.getInstance().getPossibleDateStart(user)) && DateTime.parse(day, dtf3).isBefore(DataProviderImpl.getInstance().getPossibleDateEnd(user))) {
                    result.add(day);
                }
            }
        }

        return result;
    }
    

    public static List<DailyData> getListPerDay(Map<String, User> userData,LocalTime time, Map<String, Map<String, ShiftData>> shiftData, Set<String> excludedGates, String user, DateTime iniDate, DateTime endDate) {
        List result = new ArrayList();
      Collections.sort(userData.get(user).getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));
        Map<Pair<DateTime,DateTime>, List<Pair<Event,Event>>> eventsPerDay;
        Map<DateTime, List<Event>> wrongPerDay;
        String userId = userData.get(user).getUserId().trim();
        eventsPerDay = splitPerDay(time, applyExcludeLogic(excludedGates, userData.get(user).getEvents()).get(0), iniDate, endDate);
        wrongPerDay = splitPerDayWrong(time, applyExcludeLogic(excludedGates, userData.get(user).getEvents()).get(1), iniDate, endDate);
        Set<String> neededPresence = getNeededPresence(userData, shiftData, user, iniDate, endDate);
       
        for (DateTime date = iniDate.withTimeAtStartOfDay(); date.isBefore(endDate.plusDays(1).withTimeAtStartOfDay()); date = date.plusDays(1)) {
       final DateTime dd=date;
      
       for(Map.Entry<Pair<DateTime,DateTime>, List<Pair<Event,Event>>> entry : eventsPerDay.entrySet()) {
            if(entry.getKey().getKey().withTimeAtStartOfDay().isEqual(dd)) {
             
                Long duration= 0l;
                for(Pair<Event,Event> pair:entry.getValue())
                {
                duration=duration+pair.getValue().getEventDateTime().getMillis() - pair.getKey().getEventDateTime().getMillis();
                }
                Long pause= entry.getKey().getValue().getMillis()-entry.getKey().getKey().getMillis()-duration;
                result.add(new DailyData(userId, date, user, user, 0, duration, duration, pause, 0, 0, wrongPerDay.get(dd)));
            }
        }
        }
        
//        Set<String> eventDays = eventsPerDay.keySet().stream().map(e -> e.toString(dtf3)).collect(Collectors.toSet());
//        eventDays.removeAll(neededPresence);
//
//        long tovertime = 0L;s
//        Long allowedPause = 60 * 1000 * 30l;
//        int dailyHours = 8;
//        LocalTime officialStart = null;
//        LocalTime officialEnd = null;
//        Boolean hasOvertime = true;
//        for (String dd : neededPresence) {
//
//            DateTime day = DateTime.parse(dd, dtf3);
//
//            if (eventsPerDay.containsKey(day)) {
//);
//                List<Event> exEvents = applyExcludeLogic(excludedGates, eventsPerDay.get(day)).get(1);
//                List<Event> events = applyExcludeLogic(excludedGates, eventsPerDay.get(day)).get(0
//
//                if (exEvents.size() > 0) {
//                    if (wrongPerDay.containsKey(day)) {
//                        wrongPerDay.get(day).addAll(exEvents);
//                    } else {
//                        wrongPerDay.put(day, exEvents);
//                    }
//                }
//
//                Integer jumpIndex = 0;
//
//                allowedPause = 60 * 1000 * Long.valueOf(shiftData.get(userId).get(dd).getShiftBreakTime());
//                officialStart = LocalTime.from(dtf4.parse(shiftData.get(userId).get(dd).getShiftStartHour()));
//                officialEnd = LocalTime.from(dtf4.parse(shiftData.get(userId).get(dd).getShiftEndHour()));
//                hasOvertime = shiftData.get(userId).get(dd).isHasOvertime();
//                dailyHours = officialEnd.isAfter(officialStart) ? 1000 * (officialEnd.toSecondOfDay() - officialStart.toSecondOfDay()) : 1000 * (officialEnd.toSecondOfDay() + (24 * 60 * 60 - officialStart.toSecondOfDay()));
//
//                Long duration = 0l;
//                Long pause;
//
//                DateTime firstevent = null;
//                DateTime outevent = null;
//                if (!events.isEmpty()) {
//                    firstevent = events.get(0).getEventDateTime();
//                    DateTime inevent = null;
//                    for (Event event : events) {
//                        if (event.getAddr().contains("In")) {
//
//                            inevent = event.getEventDateTime();
//                            if (inevent != null) {
//                                if (inevent.isAfter(firstevent.plusMillis(dailyHours)) && jumpIndex == 0) {
//                                    firstevent = inevent;
//                                }
//                            }
//                        } else if (event.getAddr().contains("Exit")) {
//                            if (event.getDescription().contains("night shift") && !eventsPerDay.containsKey(day.plusDays(1))) {
//
//                                continue;
//                            }
//                            if (inevent != null) {
//                                duration += event.getEventDateTime().getMillis() - inevent.getMillis();
//                                jumpIndex++;
//                                outevent = event.getEventDateTime();
//                            }
//                            if (firstevent != null && outevent != null) {
//                                if (outevent.isAfter(firstevent.plusMillis(officialEnd.isAfter(officialStart) ? dailyHours : dailyHours / 2))) {
//                                    pause = outevent.getMillis() - firstevent.getMillis() - duration;
//                                    long cduration = duration - (pause > allowedPause ? pause - allowedPause : 0);
//
//                                    long early = outevent.getSecondOfDay() < officialEnd.toSecondOfDay() ? 1000 * (officialEnd.toSecondOfDay() - outevent.getSecondOfDay()) : 0;
//                                    long overtime = 0l;
//
//                                    long latetime = firstevent.getSecondOfDay() > officialStart.toSecondOfDay() ? 1000 * (firstevent.getSecondOfDay() - officialStart.toSecondOfDay()) : 0;
//
//                                    if (officialEnd.isAfter(officialStart)) {
//
//                                        overtime = duration > dailyHours - allowedPause && hasOvertime ? duration - dailyHours + allowedPause : 0;
//
//                                        result.add(new DailyData(userId, day, firstevent.toString(dtf), outevent.toString(dtf), early, duration, cduration, pause, overtime, latetime, wrongPerDay.get(day)));
//                                    } else {
//
//                                        if (firstevent.millisOfDay().get() == 0) {
//                                            overtime = hasOvertime ? duration - (1000 * officialEnd.toSecondOfDay()) : 0;
//                                        }
//                                        if (outevent.millisOfDay().get() == 24 * 60 * 60 * 1000 - 1) {
//                                            overtime = hasOvertime ? duration + allowedPause - (24 * 3600 * 1000 - 1000 * officialStart.toSecondOfDay()) : 0;
//
//                                        }
//                                        tovertime += overtime;
//
//                                        result.add(new DailyData(userId, day, firstevent.toString(dtf), outevent.toString(dtf), early, duration, cduration, pause, firstevent.millisOfDay().get() == 0 && tovertime > 0 ? tovertime : 0, latetime, wrongPerDay.get(day)));
//                                        log.debug("User " + user + " day " + dd + "  " + firstevent.toString(dtf) + "  " + outevent.toString(dtf));
//
//                                    }
//                                    duration = 0l;
//                                    jumpIndex = 0;
//                                }
//                            }
//                        }
//                    }
//                }
//                if (firstevent != null && outevent != null) {
//                    pause = outevent.getMillis() - firstevent.getMillis() - duration;
//
//                    if (jumpIndex > 0) {
//
//                        long cduration = duration - (pause > allowedPause ? pause - allowedPause : 0);
//                        long early = outevent.getSecondOfDay() < officialEnd.toSecondOfDay() ? 1000 * (officialEnd.toSecondOfDay() - outevent.getSecondOfDay()) : 0;
//                        long overtime = 0L;
//                        long latetime = firstevent.getSecondOfDay() > officialStart.toSecondOfDay() ? 1000 * (firstevent.getSecondOfDay() - officialStart.toSecondOfDay()) : 0;
//
//                        if (officialEnd.isAfter(officialStart)) {
//                            overtime = duration > dailyHours - allowedPause && hasOvertime ? duration - dailyHours + allowedPause : 0;
//
//                            result.add(new DailyData(userId, day, firstevent.toString(dtf), outevent.toString(dtf), early, duration, cduration, pause, overtime > 0 ? overtime : 0, latetime, wrongPerDay.get(day)));
//
//                        } else {
//
//                            if (firstevent.millisOfDay().get() == 0) {
//                                overtime = hasOvertime ? duration - (1000 * officialEnd.toSecondOfDay()) : 0;
//                            }
//                            if (outevent.millisOfDay().get() == 24 * 60 * 60 * 1000 - 1) {
//                                overtime = hasOvertime ? duration + allowedPause - (24 * 3600 * 1000 - 1000 * officialStart.toSecondOfDay()) : 0;
//
//                            }
//                            tovertime += overtime;
//
//                            result.add(new DailyData(userId, day, firstevent.toString(dtf), outevent.toString(dtf), early, duration, cduration, pause, firstevent.millisOfDay().get() == 0 && tovertime > 0 ? tovertime : 0, latetime, wrongPerDay.get(day)));
//                            log.debug("User " + user + " day " + dd + "  " + firstevent.toString(dtf) + "  " + outevent.toString(dtf));
//
//                        }
//
//                    }
//                }
//            } else if (wrongPerDay.containsKey(day)) {
//
//                result.add(new DailyData(userId, day, wrongPerDay.get(day).get(0).getAddr().contains("In") ? wrongPerDay.get(day).get(0).getEventDateTime().toString(dtf) : "", wrongPerDay.get(day).get(0).getAddr().contains("Exit") ? wrongPerDay.get(day).get(0).getEventDateTime().toString(dtf) : "", 0, 0, 0, 0, 0, 0, wrongPerDay.get(day)));
//                tovertime = 0l;
//            } else if (day.isAfter(iniDate) && day.isBefore(endDate.plusDays(1))) {
//
//                result.add(new DailyData(userId, day, "", "", 0, 0, 0, 0, 0, 0, new ArrayList<>()));
//                tovertime = 0l;
//            }
//
//        }
//
//        tovertime = 0L;
//        for (String dd : eventDays) {
//
//            DateTime day = DateTime.parse(dd, dtf3);
//
//            if (eventsPerDay.containsKey(day)) {
//
//                List<Event> events = applyExcludeLogic(excludedGates, eventsPerDay.get(day)).get(0);
//                List<Event> exEvents = applyExcludeLogic(excludedGates, eventsPerDay.get(day)).get(1);
//
//                if (exEvents.size() > 0) {
//                    if (wrongPerDay.containsKey(day)) {
//                        wrongPerDay.get(day).addAll(exEvents);
//                    } else {
//                        wrongPerDay.put(day, exEvents);
//                    }
//                }
//
//                Integer jumpIndex = 0;
//
//                dailyHours = officialEnd.isAfter(officialStart) ? 1000 * (officialEnd.toSecondOfDay() - officialStart.toSecondOfDay()) : 1000 * (officialEnd.toSecondOfDay() + (24 * 60 * 60 - officialStart.toSecondOfDay()));
//
//                Long duration = 0l;
//                Long pause;
//
//                DateTime firstevent = null;
//                DateTime outevent = null;
//                if (!events.isEmpty()) {
//                    firstevent = events.get(0).getEventDateTime();
//                    DateTime inevent = null;
//                    for (Event event : events) {
//                        if (event.getAddr().contains("In")) {
//
//                            inevent = event.getEventDateTime();
//                            if (inevent != null) {
//                                if (inevent.isAfter(firstevent.plusMillis(dailyHours)) && jumpIndex == 0) {
//                                    firstevent = inevent;
//                                }
//                            }
//                        } else if (event.getAddr().contains("Exit")) {
//                            if (event.getDescription().contains("night shift") && !eventsPerDay.containsKey(day.plusDays(1))) {
//
//                                continue;
//                            }
//                            if (inevent != null) {
//                                duration += event.getEventDateTime().getMillis() - inevent.getMillis();
//                                jumpIndex++;
//                                outevent = event.getEventDateTime();
//                            }
//                            if (firstevent != null && outevent != null) {
//                                if (outevent.isAfter(firstevent.plusMillis(officialEnd.isAfter(officialStart) ? dailyHours : dailyHours / 2))) {
//                                    pause = outevent.getMillis() - firstevent.getMillis() - duration;
//                                    long cduration = duration - (pause > allowedPause ? pause - allowedPause : 0);
//
//                                    long early = outevent.getSecondOfDay() < officialEnd.toSecondOfDay() ? 1000 * (officialEnd.toSecondOfDay() - outevent.getSecondOfDay()) : 0;
//                                    long overtime = 0l;
//
//                                    long latetime = firstevent.getSecondOfDay() > officialStart.toSecondOfDay() ? 1000 * (firstevent.getSecondOfDay() - officialStart.toSecondOfDay()) : 0;
//
//                                    if (officialEnd.isAfter(officialStart)) {
//
//                                        overtime = duration > dailyHours - allowedPause && hasOvertime ? duration - dailyHours + allowedPause : 0;
//
//                                        result.add(new DailyData(userId, day, firstevent.toString(dtf), outevent.toString(dtf), early, duration, cduration, pause, overtime, latetime, wrongPerDay.get(day)));
//                                    } else {
//
//                                        if (firstevent.millisOfDay().get() == 0) {
//                                            overtime = hasOvertime ? duration - (1000 * officialEnd.toSecondOfDay()) : 0;
//                                        }
//                                        if (outevent.millisOfDay().get() == 24 * 60 * 60 * 1000 - 1) {
//                                            overtime = hasOvertime ? duration + allowedPause - (24 * 3600 * 1000 - 1000 * officialStart.toSecondOfDay()) : 0;
//
//                                        }
//                                        tovertime += overtime;
//
//                                        result.add(new DailyData(userId, day, firstevent.toString(dtf), outevent.toString(dtf), early, duration, cduration, pause, firstevent.millisOfDay().get() == 0 && tovertime > 0 ? tovertime : 0, latetime, wrongPerDay.get(day)));
//                                        log.debug("User " + user + " day " + dd + "  " + firstevent.toString(dtf) + "  " + outevent.toString(dtf));
//
//                                    }
//                                    duration = 0l;
//                                    jumpIndex = 0;
//                                }
//                            }
//                        }
//                    }
//                }
//                if (firstevent != null && outevent != null) {
//                    pause = outevent.getMillis() - firstevent.getMillis() - duration;
//
//                    if (jumpIndex > 0) {
//
//                        long cduration = duration - (pause > allowedPause ? pause - allowedPause : 0);
//                        long early = outevent.getSecondOfDay() < officialEnd.toSecondOfDay() ? 1000 * (officialEnd.toSecondOfDay() - outevent.getSecondOfDay()) : 0;
//                        long overtime = 0L;
//                        long latetime = firstevent.getSecondOfDay() > officialStart.toSecondOfDay() ? 1000 * (firstevent.getSecondOfDay() - officialStart.toSecondOfDay()) : 0;
//
//                        if (officialEnd.isAfter(officialStart)) {
//                            overtime = duration > dailyHours - allowedPause && hasOvertime ? duration - dailyHours + allowedPause : 0;
//
//                            result.add(new DailyData(userId, day, firstevent.toString(dtf), outevent.toString(dtf), early, duration, cduration, pause, overtime > 0 ? overtime : 0, latetime, wrongPerDay.get(day)));
//
//                        } else {
//
//                            if (firstevent.millisOfDay().get() == 0) {
//                                overtime = hasOvertime ? duration - (1000 * officialEnd.toSecondOfDay()) : 0;
//                            }
//                            if (outevent.millisOfDay().get() == 24 * 60 * 60 * 1000 - 1) {
//                                overtime = hasOvertime ? duration + allowedPause - (24 * 3600 * 1000 - 1000 * officialStart.toSecondOfDay()) : 0;
//
//                            }
//                            tovertime += overtime;
//
//                            result.add(new DailyData(userId, day, firstevent.toString(dtf), outevent.toString(dtf), early, duration, cduration, pause, firstevent.millisOfDay().get() == 0 && tovertime > 0 ? tovertime : 0, latetime, wrongPerDay.get(day)));
//                            log.debug("User " + user + " day " + dd + "  " + firstevent.toString(dtf) + "  " + outevent.toString(dtf));
//
//                        }
//
//                    }
//                }
//            } else if (wrongPerDay.containsKey(day)) {
//
//                result.add(new DailyData(userId, day, wrongPerDay.get(day).get(0).getAddr().contains("In") ? wrongPerDay.get(day).get(0).getEventDateTime().toString(dtf) : "", wrongPerDay.get(day).get(0).getAddr().contains("Exit") ? wrongPerDay.get(day).get(0).getEventDateTime().toString(dtf) : "", 0, 0, 0, 0, 0, 0, wrongPerDay.get(day)));
//                tovertime = 0l;
//            } else if (day.isAfter(iniDate) && day.isBefore(endDate.plusDays(1))) {
//
//                result.add(new DailyData(userId, day, "", "", 0, 0, 0, 0, 0, 0, new ArrayList<>()));
//                tovertime = 0l;
//            }
//
//        }
//        Comparator dateComparator = (Comparator<DailyData>) (DailyData o1, DailyData o2) -> {
//            return Long.compare(o1.getDate().getMillis(), o2.getDate().getMillis());
//        };
//        Collections.sort(result, dateComparator);
        return result;
    }

}
