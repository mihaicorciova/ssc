/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.utils;

import com.ro.ssc.app.client.model.commons.Event;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.LoggerFactory;
import org.jooq.lambda.Seq;

/**
 *
 * @author DauBufu
 */
public class Utils {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Utils.class);

    private static final DateTimeFormatter dtf3 = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final java.time.format.DateTimeFormatter dtf4 = java.time.format.DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z YYYY");

    public static List<List<Event>> applyExcludeLogic(Set<String> excludedGates, List<Event> events) {
        List<List<Event>> result = new ArrayList<>();
        List<Event> trimedEvents = new ArrayList<>();
        List<Event> remainingEvents = new ArrayList<>();
        Boolean shouldAdd = false;
        for (int i = 0; i < events.size() - 1; i++) {

            if (!excludedGates.contains(events.get(i).getAddr()) && events.get(i).getPassed()) {
                if (events.get(i).getAddr().contains("In") && events.get(i + 1).getAddr().contains("Exit") && events.get(i + 1).getEventDateTime().minus(events.get(i).getEventDateTime().getMillis()).getMillis() < 24 * 3600 * 1000) {
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

    public static List<List<Event>> applyExcludeLogic2(Set<String> excludedGates, List<Event> events) {
        List<List<Event>> result = new ArrayList<>();
        List<Event> trimedEvents = new ArrayList<>();
        List<Event> remainingEvents = new ArrayList<>();
        Boolean shouldAddExit = false;
        Boolean shouldAddIn = true;
        Event in = null;
        for (int i = 0; i < events.size() - 1; i++) {

            if (!excludedGates.contains(events.get(i).getAddr()) && events.get(i).getPassed()) {
                if (shouldAddExit && events.get(i).getAddr().contains("Exit") && events.get(i + 1).getAddr().contains("In")) {
                    shouldAddIn = true;
                    shouldAddExit = false;
                    trimedEvents.add(events.get(i));

                } else if (events.get(i).getAddr().contains("In") && shouldAddIn) {
                    shouldAddIn = false;
                    shouldAddExit = true;
                    in = events.get(i);
                    trimedEvents.add(events.get(i));

                } else if (events.get(i + 1).getEventDateTime().getMillis() - events.get(i).getEventDateTime().getMillis() > 15 * 1000l) {

                    remainingEvents.add(events.get(i));

                }
            }
        }
        if (!excludedGates.contains(events.get(events.size() - 1).getAddr()) && events.get(events.size() - 1).getPassed()) {
            if (events.get(events.size() - 1).getAddr().contains("Exit") && shouldAddExit) {
                shouldAddIn = false;
                shouldAddExit = false;
                trimedEvents.add(events.get(events.size() - 1));

            } else {
                shouldAddIn = false;
                shouldAddExit = false;
                if (!events.get(events.size() - 1).getDescription().contains("shift")) {

                    remainingEvents.add(events.get(events.size() - 1));
                }
            }

        }

        result.add(trimedEvents);
        result.add(remainingEvents);
        List<Pair<Event, Event>> pairedEvents = Seq.seq(trimedEvents.iterator())
                .window()
                .filter(w -> w.lead().isPresent() && w.value().getAddr().contains("In"))
                .map(w -> new Pair<>(w.value(), w.lead().get())) // alternatively, use your new Pair() class
                .toList();
        List<Pair<Event, Event>> ll = pairedEvents.stream().filter(o -> (o.getValue().getEventDateTime().getMillis() - o.getKey().getEventDateTime().getMillis() > 24 * 3600 * 1000)).collect(Collectors.toList());

        if(ll.size()>0){
        for (ListIterator<Event> it =trimedEvents.listIterator();it.hasNext();) {
            for (Pair<Event, Event> pair : ll) {
                if (it.next().equals(pair.getKey())) {
                    Event newEv=remainingEvents.stream().filter(o->o.getEventDateTime().withTimeAtStartOfDay().isEqual(pair.getValue().getEventDateTime().withTimeAtStartOfDay())).collect(Collectors.toList()).get(0);
                    it.set(newEv);
                }
            }
        }
        }
        return result;

    }

    public static Map<Pair<DateTime, DateTime>, List<Pair<Event, Event>>> splitPerDay(LocalTime time, List<Event> events, DateTime iniDate, DateTime endDate) {
        Map<Pair<DateTime, DateTime>, List<Pair<Event, Event>>> result = new LinkedHashMap<>();
        List<Pair<Event, Event>> pairedEvents = new LinkedList<>();
        List<Pair<Event, Event>> perDayList = new LinkedList<>();
        List<Pair<Event, Event>> additionalList = new LinkedList<>();

        if (!events.isEmpty()) {
            pairedEvents = Seq.seq(events.iterator())
                    .window()
                    .filter(w -> w.lead().isPresent() && w.value().getAddr().contains("In"))
                    .map(w -> new Pair<>(w.value(), w.lead().get())) // alternatively, use your new Pair() class
                    .toList();

            pairedEvents.stream().forEach(o -> log.debug("aici" + o.getKey().getEventDateTime().toString() + o.getKey().getAddr() + " " + o.getValue().getEventDateTime().toString() + o.getValue().getAddr()));

            for (DateTime date = iniDate.withTimeAtStartOfDay(); date.isBefore(endDate.plusDays(1).withTimeAtStartOfDay()); date = date.plusDays(1)) {
                final DateTime dd = date;
                perDayList = pairedEvents.stream()
                        .filter(o -> o.getKey().getEventDateTime().withTimeAtStartOfDay().isEqual(dd))
                        .collect(Collectors.toList());
                perDayList.removeAll(additionalList);
                additionalList.clear();
                if (!perDayList.isEmpty()) {
                    final DateTime evt = perDayList.get(perDayList.size() - 1).getValue().getEventDateTime();
                    if (evt.isAfter(date.plusDays(1))) {
                        additionalList = pairedEvents.stream()
                                .filter(o -> o.getValue().getEventDateTime().isAfter(evt) && o.getValue().getEventDateTime().isBefore(dd.plusDays(1).plusHours(time.getHour()).plusMinutes(time.getMinute())))
                                .collect(Collectors.toList());
                        additionalList.forEach(o -> log.debug("asda" + o.getValue().getEventDateTime()));
                        perDayList.addAll(additionalList);
                        if (!additionalList.isEmpty()) {
                            result.put(new Pair(perDayList.get(0).getKey().getEventDateTime(), additionalList.get(additionalList.size() - 1).getValue().getEventDateTime()), perDayList);
                        } else {
                            result.put(new Pair(perDayList.get(0).getKey().getEventDateTime(), perDayList.get(perDayList.size() - 1).getValue().getEventDateTime()), perDayList);

                        }
                    } else {
                        result.put(new Pair(perDayList.get(0).getKey().getEventDateTime(), perDayList.get(perDayList.size() - 1).getValue().getEventDateTime()), perDayList);
                    }
                }
            }
        }

        return result;
    }

    public static Map<DateTime, List<Event>> splitPerDayWrong(LocalTime time, List<Event> events, DateTime iniDate, DateTime endDate) {
        Map<DateTime, List<Event>> result = new LinkedHashMap<>();

        List<Event> perDayList = new LinkedList<>();

        if (!events.isEmpty()) {
            for (DateTime date = iniDate.withTimeAtStartOfDay(); date.isBefore(endDate.plusDays(1).withTimeAtStartOfDay()); date = date.plusDays(1)) {
                final DateTime dd = date;
                perDayList = events.stream().filter(o -> o.getEventDateTime().withTimeAtStartOfDay().isEqual(dd)).collect(Collectors.toList());
                result.put(dd, perDayList);
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
