/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.utils;

import com.ro.ssc.app.client.model.commons.Event;
import com.ro.ssc.app.client.model.commons.ShiftData;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
import static org.jooq.lambda.tuple.Tuple.tuple;
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

    public static Map<Pair<DateTime, DateTime>, List<Pair<Event,Event>>> splitPerDay(LocalTime time, List<Event> events, DateTime iniDate, DateTime endDate, boolean notWrong) {
        Map<Pair<DateTime, DateTime>, List<Pair<Event,Event>>> result = new LinkedHashMap<>();
          List<Pair<Event,Event>> pairedEvents = new LinkedList<>();
            List<Pair<Event,Event>> perDayList= new LinkedList<>();
              List<Pair<Event,Event>> additionalList= new LinkedList<>();
       if (!events.isEmpty()) {
       pairedEvents= Seq.seq(events.iterator())
   .window()
   .filter(w -> w.lead().isPresent() && w.value().getAddr().contains("In"))
   .map(w -> new Pair<Event,Event>(w.value(), w.lead().get())) // alternatively, use your new Pair() class
   .toList();
      
        }
       
       
           for (DateTime date = iniDate.withTimeAtStartOfDay(); date.isBefore(endDate.plusDays(1).withTimeAtStartOfDay()); date.plusDays(1)) {

       perDayList= pairedEvents.stream()
               .filter(o->o.getKey().getEventDateTime().withTimeAtStartOfDay().isEqual(date))
               .collect(Collectors.toList());
       perDayList.removeAll(additionalList);
       additionalList.clear();;

if(perDayList.get(perDayList.size()-1).getValue().getEventDateTime().isBefore(date.plusDays(1)))
{
    additionalList=pairedEvents.stream()
            .filter(o->o.getValue().getEventDateTime().withTimeAtStartOfDay().isAfter(date.plusDays(1)) && o.getValue().getEventDateTime().withTimeAtStartOfDay().isBefore(date.plusDays(1).plusHours(time.getHour())))
    .collect(Collectors.toList());
 perDayList.addAll(additionalList);

}
    result.put(new Pair(perDayList.get(0).getKey().getEventDateTime(),perDayList.get(perDayList.size()-1).getValue().getEventDateTime()), perDayList);
           }

       result.entrySet().forEach(o->log.debug("intrarea "+o.getKey().getKey().toString() + " si iesirea "+ o.getKey().getValue().toString() +" cu nr de perechi" + o.getValue().size()));
//    pairedEvents=  Seq.of(events).window().map(o-> new Pair(o,o)).;
//      
//            DateTime dt = events.get(0).getEventDateTime().plusDays(1).withTimeAtStartOfDay();
//            for (DateTime date = iniDate.withTimeAtStartOfDay(); date.isBefore(endDate.plusDays(1).withTimeAtStartOfDay()); date.plusDays(1)) {
//                perDayList = events.stream().filter(o -> o.getEventDateTime().isAfter(date) && o.getEventDateTime().isBefore(date.plusDays(1))).collect(Collectors.toList());
//                if (perDayList.get(0).getAddr().contains("In") && perDayList.get(perDayList.size() - 1).getAddr().contains("Exit")) {
//                    pair = new Pair(perDayList.get(0).getEventDateTime(), perDayList.get(perDayList.size() - 1).getEventDateTime());
//                
//                }
//                else
//                {
//               additional= events.stream().filter(o -> o.getEventDateTime().isAfter(date.plusDays(1)) && o.getEventDateTime().isBefore(date.plusDays(1))).collect(Collectors.toList());
//                }
//                result.put(pair,perDayList);
//            }
//        }
        return result;
    }

    public static String formatMillis(Long millis) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return hms;
    }

}
