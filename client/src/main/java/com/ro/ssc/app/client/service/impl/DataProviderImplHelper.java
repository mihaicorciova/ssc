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
import java.util.*;

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

        if (userData.containsKey(userName)) {
            final List<Event> events = userData.get(userName).getEvents();
            Collections.sort(events, Comparator.comparing(Event::getEventDateTime));

            Map<Pair<DateTime, DateTime>, List<Pair<Event, Event>>> eventsPerDay;
            Map<DateTime, List<Event>> wrongPerDay;
            Set<String> usedDates = new HashSet<>();
            String userId = userData.get(userName).getUserId().trim();

            //aici schimb tipurile
//        if (Configuration.IS_EXPIRED.getAsBoolean()) {
            eventsPerDay = splitPerDay(time, applyExcludeLogic(excludedGates, events).get(0), iniDate, endDate);
            wrongPerDay = splitPerDayWrong(time, applyExcludeLogic(excludedGates, events).get(1), iniDate, endDate);
//        } else {
            ////      eventsPerDay = splitPerDay(time, applyExcludeLogic2(excludedGates, events).get(0), iniDate, endDate);
            //      wrongPerDay = splitPerDayWrong(time, applyExcludeLogic2(excludedGates, events).get(1), iniDate, endDate);

            //  }
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
                        DateTime start = entry.getKey().getKey();
                        DateTime end = entry.getKey().getValue();
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
                                    LocalTime penTimeIn = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getSc().getPenaltyTimeIn()));
                                    LocalTime penTimeOut = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getSc().getPenaltyTimeOut()));
                                    LocalTime penIn = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getSc().getPenaltyAmountIn()));
                                    LocalTime penOut = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getSc().getPenaltyAmountOut()));
                                    Long correction = 0L;
                                    if (start.getMillisOfDay() < officialStart.toSecondOfDay() * 1000) {
                                        if (start.getMillisOfDay() > officialStart.toSecondOfDay() * 1000 - Long.valueOf(shiftDataInCurrentDate.getSc().getAdjustIn()) * 60 * 1000) {
                                            log.debug("aici" + start.toString());
                                            correction = correction + officialStart.toSecondOfDay() * 1000 - start.getMillisOfDay();
                                            start = start.withMillisOfDay(officialStart.toSecondOfDay() * 1000);

                                            log.debug("aici dupa" + start.toString());
                                        }
                                    }
                                    if (end.getMillisOfDay() > officialEnd.toSecondOfDay() * 1000) {
                                        if (end.getMillisOfDay() < officialEnd.toSecondOfDay() * 1000 + Long.valueOf(shiftDataInCurrentDate.getSc().getAdjustOut()) * 60 * 1000) {
                                            correction = correction + end.getMillisOfDay() - officialEnd.toSecondOfDay() * 1000;
                                            end = end.withMillisOfDay(officialEnd.toSecondOfDay() * 1000);

                                        }
                                    }

                                    if (start.getMillisOfDay() > officialStart.toSecondOfDay() * 1000 + penTimeIn.toSecondOfDay() * 1000) {
                                        correction = correction + penIn.toSecondOfDay() * 1000;
                                        start = start.plusSeconds(penIn.toSecondOfDay());

                                    }

                                    if (end.getMillisOfDay() < officialEnd.toSecondOfDay() * 1000 - penTimeOut.toSecondOfDay() * 1000) {
                                        correction = correction + penOut.toSecondOfDay() * 1000;
                                        end = end.minusSeconds(penOut.toSecondOfDay());

                                    }
                                    duration = duration - correction;
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
                                    } else {
                                        if (duration < dailyHours - dailyPause) {
                                            overtime = duration - dailyHours + dailyPause;
                                        }
                                    }
                                    if (entry.getKey().getValue().isAfter(date.plusDays(1)) && officialStart.isBefore(officialEnd)) {
                                        earlytime = entry.getKey().getValue().getSecondOfDay() + 24 * 3600 < officialEnd.toSecondOfDay() ? 1000 * (officialEnd.toSecondOfDay() - entry.getKey().getValue().getSecondOfDay()) : 0l;

                                    } else {
                                        earlytime = entry.getKey().getValue().getSecondOfDay() < officialEnd.toSecondOfDay() ? 1000 * (officialEnd.toSecondOfDay() - entry.getKey().getValue().getSecondOfDay()) : 0l;
                                    }
                                    latetime = entry.getKey().getKey().getSecondOfDay() > officialStart.toSecondOfDay() ? 1000 * (entry.getKey().getKey().getSecondOfDay() - officialStart.toSecondOfDay()) : 0l;
                                }
                            }
                        }
                        result.add(new DailyData(userId, date, start.toString(dtf), end.toString(dtf), earlytime, duration, pause, overtime, latetime, "", ""));
                        usedDates.add(currentDateAsDateTime.toString(dtf3));
                    }

                }

            }

            for (String day : getNeededPresence(userData, shiftData, userName, iniDate, endDate)) {

                if (!usedDates.contains(day)) {
                    ShiftData shiftDataInCurrentDate = shiftData.get(userId).get(day);
                    LocalTime officialStart = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getShiftStartHour()));
                    LocalTime officialEnd = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getShiftEndHour()));
                    long dailyPause = Long.valueOf(shiftDataInCurrentDate.getShiftBreakTime()) * 1000 * 60l;
                    long dailyHours = officialEnd.isAfter(officialStart)
                            ? 1000 * (officialEnd.toSecondOfDay() - officialStart.toSecondOfDay())
                            : 1000 * (officialEnd.toSecondOfDay() + (24 * 60 * 60 - officialStart.toSecondOfDay()));
                    String holiday = shiftDataInCurrentDate.getHoliday();

                    if (!holiday.equals("")) {
                        result.add(new DailyData(userId, DateTime.parse(day, dtf3), "", "", 0, 0, 0, 0, 0, holiday, ""));

                    } else {
                        if (wrongPerDay.containsKey(DateTime.parse(day, dtf3))) {
                            if (wrongPerDay.get(DateTime.parse(day, dtf3)).size() > 0) {
                                String ev = wrongPerDay.get(DateTime.parse(day, dtf3)).get(0).getEventDateTime().toString(dtf);
                                Boolean isIn = wrongPerDay.get(DateTime.parse(day, dtf3)).get(0).getAddr().contains("In");

                                result.add(new DailyData(userId, DateTime.parse(day, dtf3), isIn == true ? ev : "", isIn == false ? ev : "", 0, 0, 0, dailyPause - dailyHours, 0, "Da***", ""));
                            } else {
                                result.add(new DailyData(userId, DateTime.parse(day, dtf3), "", "", 0, 0, 0, dailyPause - dailyHours, 0, "Da", ""));

                            }
                        } else {
                            result.add(new DailyData(userId, DateTime.parse(day, dtf3), "", "", 0, 0, 0, dailyPause - dailyHours, 0, "Da", ""));

                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<DailyData> getListOfDay(String userName, Map<String, User> userData, DateTime dateTime, LocalTime time, Set<String> excludedGates, Map<String, Map<String, ShiftData>> shiftData) {
        List<DailyData> result = new ArrayList();
        final List<Event> events = userData.get(userName).getEvents();
        Collections.sort(events, Comparator.comparing(Event::getEventDateTime));

        Map<Pair<DateTime, DateTime>, List<Pair<Event, Event>>> eventsPerDay;
        String userId = userData.get(userName).getUserId().trim();
        Set<String> usedDates = new HashSet<>();
        eventsPerDay = splitPerDay(time, applyExcludeLogic(excludedGates, events).get(0), dateTime, dateTime);
        Map<DateTime, List<Event>> wrongPerDay = splitPerDayWrong(time, applyExcludeLogic(excludedGates, events).get(1), dateTime, dateTime);
        for (Map.Entry<Pair<DateTime, DateTime>, List<Pair<Event, Event>>> entry : eventsPerDay.entrySet()) {
            if (entry.getKey().getKey().withTimeAtStartOfDay().isEqual(dateTime)) {

                Long duration = 0l;
                String aditional = "";
                for (Pair<Event, Event> pair : entry.getValue()) {
                    duration = duration + pair.getValue().getEventDateTime().getMillis() - pair.getKey().getEventDateTime().getMillis();
                    if (!pair.getKey().getEventDateTime().equals(entry.getKey().getKey())) {
                        aditional = aditional + " in " + pair.getKey().getEventDateTime().toString(dtf) + "\n";
                    }

                    if (!pair.getValue().getEventDateTime().equals(entry.getKey().getValue())) {
                        aditional = aditional + " out " + pair.getValue().getEventDateTime().toString(dtf) + "\n";
                    }

                }
                Long pause = entry.getKey().getValue().getMillis() - entry.getKey().getKey().getMillis() - duration;
                Long overtime = 0l;
                Long latetime = 0l;
                Long earlytime = 0l;
                DateTime start = entry.getKey().getKey();
                DateTime end = entry.getKey().getValue();
                String holiday = "";
                if (shiftData.containsKey(userId)) {
                    final Map<String, ShiftData> shiftDataMapForUser = shiftData.get(userId);
                    if (shiftDataMapForUser.containsKey(dateTime.toString(dtf3))) {

                        ShiftData shiftDataInCurrentDate = shiftDataMapForUser.get(dateTime.toString(dtf3));
                        holiday = shiftDataInCurrentDate.getHoliday();
                        if (shiftDataInCurrentDate.getShiftId().equals("0")) {
                            if (shiftDataInCurrentDate.isHasOvertime()) {
                                overtime = duration;
                            }
                        } else {
                            LocalTime officialStart = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getShiftStartHour()));
                            LocalTime officialEnd = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getShiftEndHour()));
                            LocalTime penTimeIn = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getSc().getPenaltyTimeIn()));
                            LocalTime penTimeOut = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getSc().getPenaltyTimeOut()));
                            LocalTime penIn = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getSc().getPenaltyAmountIn()));
                            LocalTime penOut = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getSc().getPenaltyAmountOut()));
                            Long correction = 0L;
                            if (start.getMillisOfDay() < officialStart.toSecondOfDay() * 1000) {
                                if (start.getMillisOfDay() > officialStart.toSecondOfDay() * 1000 - Long.valueOf(shiftDataInCurrentDate.getSc().getAdjustIn()) * 60 * 1000) {
                                    correction = correction + officialStart.toSecondOfDay() * 1000 - start.getMillisOfDay();
                                    start = start.withMillisOfDay(officialStart.toSecondOfDay() * 1000);

                                }
                            }
                            if (start.getMillisOfDay() > officialStart.toSecondOfDay() * 1000 + penTimeIn.toSecondOfDay() * 1000) {
                                correction = correction + penIn.toSecondOfDay() * 1000;
                                start = start.plusSeconds(penIn.toSecondOfDay());

                            }

                            if (end.getMillisOfDay() < officialEnd.toSecondOfDay() * 1000 - penTimeOut.toSecondOfDay() * 1000) {
                                correction = correction + penOut.toSecondOfDay() * 1000;
                                end = end.minusSeconds(penOut.toSecondOfDay());

                            }

                            if (end.getHourOfDay() == 0 && officialEnd.getHour() == 23) {
                                if (end.getMillisOfDay() + 24 * 3600 * 1000 > officialEnd.toSecondOfDay() * 1000) {
                                    if (end.getMillisOfDay() + 24 * 3600 * 1000 < officialEnd.toSecondOfDay() * 1000 + Long.valueOf(shiftDataInCurrentDate.getSc().getAdjustOut()) * 60 * 1000) {
                                        correction = correction + end.getMillisOfDay() + 24 * 3600 * 1000 - officialEnd.toSecondOfDay() * 1000;
                                        end = end.withMillisOfDay(officialEnd.toSecondOfDay() * 1000);

                                    }
                                }
                            } else {
                                if (end.getMillisOfDay() > officialEnd.toSecondOfDay() * 1000) {
                                    if (end.getMillisOfDay() < officialEnd.toSecondOfDay() * 1000 + Long.valueOf(shiftDataInCurrentDate.getSc().getAdjustOut()) * 60 * 1000) {
                                        correction = correction + end.getMillisOfDay() - officialEnd.toSecondOfDay() * 1000;
                                        end = end.withMillisOfDay(officialEnd.toSecondOfDay() * 1000);

                                    }
                                }
                            }
                            duration = duration - correction;
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
                            } else {
                                if (duration < dailyHours - dailyPause) {
                                    overtime = duration - dailyHours + dailyPause;
                                }
                            }
                            if (entry.getKey().getValue().isAfter(dateTime.plusDays(1)) && officialStart.isBefore(officialEnd)) {
                                earlytime = entry.getKey().getValue().getSecondOfDay() + 24 * 3600 < officialEnd.toSecondOfDay() ? 1000 * (officialEnd.toSecondOfDay() - entry.getKey().getValue().getSecondOfDay()) : 0l;

                            } else {
                                earlytime = entry.getKey().getValue().getSecondOfDay() < officialEnd.toSecondOfDay() ? 1000 * (officialEnd.toSecondOfDay() - entry.getKey().getValue().getSecondOfDay()) : 0l;
                            }
                            latetime = entry.getKey().getKey().getSecondOfDay() > officialStart.toSecondOfDay() ? 1000 * (entry.getKey().getKey().getSecondOfDay() - officialStart.toSecondOfDay()) : 0l;
                        }
                        usedDates.add(dateTime.toString(dtf3));
                    }
                }
                result.add(new DailyData(userId, dateTime, start.toString(dtf), end.toString(dtf), earlytime, duration, pause, overtime, latetime, "", aditional));
            }

        }

        for (String day : getNeededPresence(userData, shiftData, userName, dateTime, dateTime)) {

            if (!usedDates.contains(day)) {
                ShiftData shiftDataInCurrentDate = shiftData.get(userId).get(day);
                LocalTime officialStart = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getShiftStartHour()));
                LocalTime officialEnd = LocalTime.from(dtf4.parse(shiftDataInCurrentDate.getShiftEndHour()));
                long dailyPause = Long.valueOf(shiftDataInCurrentDate.getShiftBreakTime()) * 1000 * 60l;
                long dailyHours = officialEnd.isAfter(officialStart)
                        ? 1000 * (officialEnd.toSecondOfDay() - officialStart.toSecondOfDay())
                        : 1000 * (officialEnd.toSecondOfDay() + (24 * 60 * 60 - officialStart.toSecondOfDay()));
                String holiday = shiftDataInCurrentDate.getHoliday();

                if (!holiday.equals("")) {
                    result.add(new DailyData(userId, DateTime.parse(day, dtf3), "", "", 0, 0, 0, 0, 0, holiday, ""));

                } else {
                    if (wrongPerDay.containsKey(DateTime.parse(day, dtf3))) {
                        if (wrongPerDay.get(DateTime.parse(day, dtf3)).size() > 0) {
                            String ev = wrongPerDay.get(DateTime.parse(day, dtf3)).get(0).getEventDateTime().toString(dtf);
                            Boolean isIn = wrongPerDay.get(DateTime.parse(day, dtf3)).get(0).getAddr().contains("In");

                            result.add(new DailyData(userId, DateTime.parse(day, dtf3), isIn == true ? ev : "", isIn == false ? ev : "", 0, 0, 0, dailyPause - dailyHours, 0, "Da***", ""));
                        } else {
                            result.add(new DailyData(userId, DateTime.parse(day, dtf3), "", "", 0, 0, 0, dailyPause - dailyHours, 0, "Da", ""));

                        }
                    } else {
                        result.add(new DailyData(userId, DateTime.parse(day, dtf3), "", "", 0, 0, 0, dailyPause - dailyHours, 0, "Da", ""));

                    }
                }
            }
        }
        return result;
    }

}
