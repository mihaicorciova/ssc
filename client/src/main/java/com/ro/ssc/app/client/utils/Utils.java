/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.utils;

import com.ro.ssc.app.client.model.commons.Event;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public class Utils {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Utils.class);

    public static List<List<Event>> applyExcludeLogic(Set<String> excludedGates, List<Event> events) {
        List<List<Event>> result = new ArrayList<>();
        List<Event> trimedEvents = new ArrayList<>();
        List<Event> remainingEvents = new ArrayList<>();
        Boolean shouldAdd = false;
        for (int i = 0; i < events.size() - 1; i++) {

            if (!excludedGates.contains(events.get(i).getAddr()) && events.get(i).getPassed()) {
                if (events.get(i).getAddr().contains("In") && events.get(i + 1).getAddr().contains("Exit")) {
                    shouldAdd = true;
                    trimedEvents.add(events.get(i));
                } else if (events.get(i).getAddr().contains("Exit") && shouldAdd) {
                    shouldAdd = false;
                    trimedEvents.add(events.get(i));

                } else {
                    shouldAdd = false;
                    if (events.get(i + 1).getEventDateTime().getMillis() - events.get(i).getEventDateTime().getMillis() > 15 * 1000l) {

                        remainingEvents.add(events.get(i));

                    }
                }
            }
        }
        if (!excludedGates.contains(events.get(events.size() - 1).getAddr()) && events.get(events.size() - 1).getPassed()) {
            if (events.get(events.size() - 1).getAddr().contains("Exit") && shouldAdd) {
                shouldAdd = false;
                trimedEvents.add(events.get(events.size() - 1));

            } else {
                shouldAdd = false;
                if (!events.get(events.size() - 1).getDescription().contains("shift")) {
                  
                    remainingEvents.add(events.get(events.size() - 1));
                }
            }

        }
        result.add(trimedEvents);
        result.add(remainingEvents);
        return result;

    }

    public static Map<DateTime, List<Event>> splitPerDay(List<Event> events, DateTime iniDate, DateTime endDate) {
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

    public static Map<DateTime, List<Event>> splitPerDayNS(List<Event> events, DateTime iniDate, DateTime endDate) {
        Map<DateTime, List<Event>> result = new LinkedHashMap<>();
        List<Event> perDayList = new ArrayList<>();

        if (!events.isEmpty()) {
            DateTime dt = events.get(0).getEventDateTime().plusDays(1).withTimeAtStartOfDay();
            perDayList.add(new Event(dt.minusDays(1), "Intermediate in event for night shift", "In", Boolean.TRUE));
            for (Event ev : events) {
                if (ev.getEventDateTime().isAfter(dt)) {
                    if (iniDate == null || endDate == null || (dt.minusDays(1).isBefore(endDate) && iniDate != null && endDate != null && dt.isAfter(iniDate))) {
                        perDayList.add(new Event(dt.minusMillis(1), "Intermediate exit event for night shift", "Exit", Boolean.TRUE));
                        result.put(dt.minusDays(1), perDayList);
                    }
                    dt = ev.getEventDateTime().plusDays(1).withTimeAtStartOfDay();
                    perDayList = null;
                    perDayList = new ArrayList<>();
                    perDayList.add(new Event(dt.minusDays(1), "Intermediate in event for night shift", "In", Boolean.TRUE));
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

    public static String formatMillis(Long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return hms;
    }

}
