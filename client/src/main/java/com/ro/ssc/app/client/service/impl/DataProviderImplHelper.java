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

                if (DateTime.parse(day, dtf3).plusDays(1).isAfter(DataProviderImpl.getInstance().getPossibleDateStart(user)) && DateTime.parse(day, dtf3).isBefore(DataProviderImpl.getInstance().getPossibleDateEnd(user))) {
                    result.add(day);
                }
            }
        }

        return result;
    }

    public static List<DailyData> getListPerDay(Map<String, User> userData, LocalTime time, Map<String, Map<String, ShiftData>> shiftData, Set<String> excludedGates, String user, DateTime iniDate, DateTime endDate) {
        List result = new ArrayList();
        Collections.sort(userData.get(user).getEvents(), (c1, c2) -> c1.getEventDateTime().compareTo(c2.getEventDateTime()));
        Map<Pair<DateTime, DateTime>, List<Pair<Event, Event>>> eventsPerDay;
        Map<DateTime, List<Event>> wrongPerDay;
        String userId = userData.get(user).getUserId().trim();
        eventsPerDay = splitPerDay(time, applyExcludeLogic(excludedGates, userData.get(user).getEvents()).get(0), iniDate, endDate);
        wrongPerDay = splitPerDayWrong(time, applyExcludeLogic(excludedGates, userData.get(user).getEvents()).get(1), iniDate, endDate);
        Set<String> neededPresence = getNeededPresence(userData, shiftData, user, iniDate, endDate);

        for (DateTime date = iniDate.withTimeAtStartOfDay(); date.isBefore(endDate.plusDays(1).withTimeAtStartOfDay()); date = date.plusDays(1)) {
            final DateTime dd = date;

            for (Map.Entry<Pair<DateTime, DateTime>, List<Pair<Event, Event>>> entry : eventsPerDay.entrySet()) {
                if (entry.getKey().getKey().withTimeAtStartOfDay().isEqual(dd)) {

                    Long duration = 0l;
                    for (Pair<Event, Event> pair : entry.getValue()) {
                        duration = duration + pair.getValue().getEventDateTime().getMillis() - pair.getKey().getEventDateTime().getMillis();
                    }
                    Long pause = entry.getKey().getValue().getMillis() - entry.getKey().getKey().getMillis() - duration;
                    Long overtime = 0l;
                    Long latetime = 0l;
                    Long earlytime = 0l;
                    if (shiftData.containsKey(userId)) {
                        if (shiftData.get(userId).containsKey(dd.toString(dtf3))) {

                            ShiftData sh = shiftData.get(userId).get(dd.toString(dtf3));
                            if (sh.getShiftId().equals("0")) {
                                overtime = duration;
                            } else {
                                LocalTime officialStart = LocalTime.from(dtf4.parse(sh.getShiftStartHour()));
                                LocalTime officialEnd = LocalTime.from(dtf4.parse(sh.getShiftEndHour()));
                                long dailyHours = officialEnd.isAfter(officialStart)
                                        ? 1000 * (officialEnd.toSecondOfDay() - officialStart.toSecondOfDay())
                                        : 1000 * (officialEnd.toSecondOfDay() + (24 * 60 * 60 - officialStart.toSecondOfDay()));
                                if (sh.isHasOvertime()) {
                                    overtime = duration > dailyHours ? duration - dailyHours : 0l;
                                }
                                earlytime = entry.getKey().getValue().getSecondOfDay() < officialEnd.toSecondOfDay() ? 1000 * (officialEnd.toSecondOfDay() - entry.getKey().getValue().getSecondOfDay()) : 0l;
                                latetime = entry.getKey().getKey().getSecondOfDay() > officialStart.toSecondOfDay() ? 1000 * (entry.getKey().getKey().getSecondOfDay() - officialStart.toSecondOfDay()) : 0l;
                            }
                        }
                    }
                    result.add(new DailyData(userId, date, entry.getKey().getKey().toString(dtf), entry.getKey().getValue().toString(dtf), earlytime, duration, duration, pause, overtime, latetime, wrongPerDay.get(dd)));
                }
            }
        }
        return result;
    }

}
