/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.utils;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.ro.ssc.app.client.model.commons.ShiftCorrection;
import com.ro.ssc.app.client.model.commons.ShiftData;
import com.ro.ssc.app.client.model.commons.ShiftHours;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public class AccessReader {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AccessReader.class);

    public static List<Set<String>> updateUserMap(File file) {

        Set<String> excludedGates = new LinkedHashSet<>();
        Set<String> excludedUsers = new LinkedHashSet<>();
        Set<String> idMapping = new LinkedHashSet<>();
        List<Set<String>> result = new LinkedList<>();

        Table table;
        try {

            table = DatabaseBuilder.open(file).getTable("t_b_Reader");
            Cursor cursor = CursorBuilder.createCursor(table);
            for (Row row : cursor.newIterable().addMatchPattern("f_Attend", 0)) {

                excludedGates.add(String.format("%s", row.get("f_ReaderName")));
            }
            table = DatabaseBuilder.open(file).getTable("t_b_Consumer");
            cursor = CursorBuilder.createCursor(table);
            for (Row row : cursor.newIterable()) {
                idMapping.add(WordUtils.capitalizeFully(String.format("%s", row.get("f_ConsumerName")).trim()) + "#" + String.format("%s", row.get("f_ConsumerNO")).trim() + "~" + String.format("%s", row.get("f_ConsumerID")).trim());

                if (!String.format("%s", row.get("f_AttendEnabled")).contains("1")) {
                    excludedUsers.add(WordUtils.capitalizeFully(String.format("%s", row.get("f_ConsumerName"))));
                }
            }

        } catch (IOException ex) {
            log.error("Exceptie", ex);
        }

        result.add(idMapping);
        result.add(excludedGates);
        result.add(excludedUsers);

        return result;
    }

    public static Map<String, Map<String, ShiftData>> getShiftData(File file) {

        final Map<String, ShiftData> shiftMap = new HashMap<>();

        final Map<String, Map<String, ShiftData>> result = new HashMap<>();
        Table table;
        try {

            table = DatabaseBuilder.open(file).getTable("t_b_ShiftSet");
            Cursor cursor = CursorBuilder.createCursor(table);
            shiftMap.put("0", new ShiftData("0", "weekend", new ShiftHours("", "", "", "", ""), new ShiftCorrection("", "", "", "", "", ""), true));
            for (Row row : cursor.newIterable()) {

                final String shiftId = String.format("%s", row.get("f_ShiftID")).trim();
                final String shiftNameInitial = String.format("%s", row.get("f_ShiftName")).trim();
                final String shiftName = shiftNameInitial.contains(":")
                        ? shiftNameInitial.split(":")[0] : shiftNameInitial.contains("#") ? shiftNameInitial.split("#")[0]
                                : shiftNameInitial.contains("£") ? shiftNameInitial.split("£")[0] : shiftNameInitial;

                final String shiftPauseInitial = shiftNameInitial.contains(":") ? shiftNameInitial.split(":")[1] : "0";
                final String shiftPause = shiftPauseInitial.contains("#") ? shiftPauseInitial.split("#")[0] : shiftPauseInitial.contains("£") ? shiftPauseInitial.split("£")[0] : shiftPauseInitial;
                final String shiftAdjust = shiftNameInitial.contains("#") ? shiftNameInitial.split("#")[1] : "0";
                final String shiftAdjustIn = shiftAdjust.contains("£") ? shiftAdjust.split("£")[0] : shiftAdjust;
                final String shiftAdjustOut = shiftNameInitial.contains("£") ? shiftNameInitial.split("£")[1] : "0";

                final String onDuty = String.format("%s", row.get("f_OnDuty1"));
                final String offDuty = String.format("%s", row.get("f_OffDuty1"));

                final String onDuty2 = String.format("%s", row.get("f_OnDuty2"));
                final String offDuty2 = String.format("%s", row.get("f_OffDuty2"));
                final String onDuty3 = String.format("%s", row.get("f_OnDuty3"));
                final String offDuty3 = String.format("%s", row.get("f_OffDuty3"));
                final String onDuty4 = String.format("%s", row.get("f_OnDuty4"));
                final String offDuty4 = String.format("%s", row.get("f_OffDuty4"));

                final ShiftCorrection sc = new ShiftCorrection(shiftAdjustIn, shiftAdjustOut, onDuty2 == null || onDuty2.equals("null")? "" : onDuty2, offDuty2 == null || offDuty2.equals("null") ? "" : offDuty2, onDuty3 == null|| onDuty3.equals("null") ? "" : onDuty3, offDuty3  == null|| offDuty3.equals("null") ? "" : offDuty3);
                final ShiftHours sh = new ShiftHours(shiftPause, onDuty == null || onDuty.equals("null")? "" : onDuty, offDuty == null  || offDuty.equals("null")? "" : offDuty, onDuty4 == null  || onDuty4.equals("null") ? "" : onDuty4, offDuty4 == null  || offDuty4.equals("null")? "" : offDuty4);
                shiftMap.put(shiftId, new ShiftData(shiftId, shiftName,
                        sh, sc, String.format("%s", row.get("f_bOvertimeShift")).contains("1")));

            }

            final DateTimeFormatter dtf3 = DateTimeFormat.forPattern("yyyy-MM-dd");

            final Map<String, Map<String, String>> lMap = new HashMap<>();
            table = DatabaseBuilder.open(file).getTable("t_d_Leave");
            cursor = CursorBuilder.createCursor(table);

            for (Row row : cursor.newIterable()) {
                String userId = String.format("%s", row.get("f_ConsumerID")).trim();
                if (!lMap.containsKey(userId)) {
                    lMap.put(userId, new HashMap<>());
                }

                final LocalDate startDate = LocalDate.parse(String.format("%s", row.get("f_Value")).trim(), dtf3);
                final LocalDate endDate = LocalDate.parse(String.format("%s", row.get("f_Value2")).trim(), dtf3);
                final Map<String, String> usd = lMap.get(userId);
                for (LocalDate d = startDate; d.isBefore(endDate.plusDays(1)); d = d.plusDays(1)) {
                    usd.put(d.toString(dtf3), String.format("%s", row.get("f_HolidayType")).trim());
                }

            }

            table = DatabaseBuilder.open(file).getTable("t_a_Holiday");
            cursor = CursorBuilder.createCursor(table);
            final Map<String, String> hMap = new HashMap<>();
            for (Row row : cursor.newIterable()) {

                final String sd = String.format("%s", row.get("f_Value")).trim();
                if (!sd.equals("0")) {
                    final DateTime startDate = DateTime.parse(sd, dtf3);
                    final DateTime endDate = DateTime.parse(String.format("%s", row.get("f_Value2")).trim(), dtf3);

                    for (DateTime d = startDate.withTimeAtStartOfDay(); d.isBefore(endDate.plusDays(1).withTimeAtStartOfDay()); d = d.plusDays(1)) {

                        hMap.put(d.toString(dtf3), String.format("%s", row.get("f_Name")).trim());
                    }

                }
            }

            table = DatabaseBuilder.open(file).getTable("t_d_ShiftData");
            cursor = CursorBuilder.createCursor(table);

            for (Row row : cursor.newIterable()) {
                String userId = String.format("%s", row.get("f_ConsumerID")).trim();

                if (!result.containsKey(userId)) {

                    result.put(userId, new HashMap<>());

                }
                for (int i = 1; i <= 31; i++) {
                    if (i < 10) {
                        String key = String.format("%s", row.get("f_ShiftID_0" + i));
                        if (shiftMap.containsKey(key)) {
                            String date = String.format("%s", row.get("f_DateYM")) + "-0" + String.valueOf(i);
                            result.get(userId).put(date, shiftMap.get(key));

                            if (lMap.containsKey(userId)) {
                                final Map<String, String> usd = lMap.get(userId);
                                if (usd.containsKey(date)) {
                                    result.get(userId).put(date, new ShiftData(result.get(userId).get(date), usd.get(date)));
                                }
                            }
                            if (hMap.containsKey(date)) {
                                result.get(userId).put(date, new ShiftData(result.get(userId).get(date), hMap.get(date)));
                            }
                        }

                    } else {
                        String key = String.format("%s", row.get("f_ShiftID_" + i));

                        if (shiftMap.containsKey(key)) {
                            String date = String.format("%s", row.get("f_DateYM")) + "-" + String.valueOf(i);
                            result.get(userId).put(date, shiftMap.get(key));

                            if (lMap.containsKey(userId)) {
                                final Map<String, String> usd = lMap.get(userId);
                                if (usd.containsKey(date)) {
                                    result.get(userId).put(date, new ShiftData(result.get(userId).get(date), usd.get(date)));
                                }
                            }
                            if (hMap.containsKey(date)) {
                                result.get(userId).put(date, new ShiftData(result.get(userId).get(date), hMap.get(date)));

                            }
                        }
                    }
                }
            }

//              table = DatabaseBuilder.open(file).getTable("t_d_ShiftData");
//            cursor = CursorBuilder.createCursor(table);
//
//            for (Row row : cursor.newIterable()) {
//                String userId = String.format("%s", row.get("f_ConsumerID")).trim();
//
//                if (!result.containsKey(userId)) {
//
//                    result.put(userId, new HashMap<>());
//
//                }
//
//                for (int i = 1; i <= 31; i++) {
//                    if (i < 10) {
//                        String key = String.format("%s", row.get("f_ShiftID_0" + i));
//                        if (shiftMap.containsKey(key)) {
//                            result.get(userId).put(String.format("%s", row.get("f_DateYM")) + "-0" + String.valueOf(i), shiftMap.get(key));
//                        }
//                    } else {
//                        String key = String.format("%s", row.get("f_ShiftID_" + i));
//                        if (shiftMap.containsKey(key)) {
//                            result.get(userId).put(String.format("%s", row.get("f_DateYM")) + "-" + String.valueOf(i), shiftMap.get(key));
//                        }
//                    }
//                }
//
//            }
//          
//
//            table = DatabaseBuilder.open(file).getTable("t_d_Leave");
//            cursor = CursorBuilder.createCursor(table);
//            for (Row row : cursor.newIterable()) {
//                String userId = String.format("%s", row.get("f_ConsumerID")).trim();
//                if (result.containsKey(userId)) {
//                    final LocalDate startDate = LocalDate.parse(String.format("%s", row.get("f_Value")).trim(), dtf3);
//                    final LocalDate endDate = LocalDate.parse(String.format("%s", row.get("f_Value2")).trim(), dtf3);
//                    final Map<String, ShiftData> usd = result.get(userId);
//                    for ( LocalDate d = startDate; d.isBefore(endDate.plusDays(1)); d= d.plusDays(1)) {
//                        if (usd.containsKey(d.toString(dtf3))) {
//                            usd.get(d.toString(dtf3)).setHoliday(String.format("%s", row.get("f_HolidayType")).trim());
//                        }
//                    }
//                }
//            }
//            
//            table = DatabaseBuilder.open(file).getTable("t_a_Holiday");
//            cursor = CursorBuilder.createCursor(table);
//            for (Row row : cursor.newIterable()) {
//
//                final String sd = String.format("%s", row.get("f_Value")).trim();
//                if (!sd.equals("0")) {
//                    final DateTime startDate = DateTime.parse(sd, dtf3);
//                    final DateTime endDate = DateTime.parse(String.format("%s", row.get("f_Value2")).trim(), dtf3);
//
//                    for (Map.Entry<String, Map<String,ShiftData>> usd : result.entrySet()) {
//                        for ( DateTime d = startDate.withTimeAtStartOfDay(); d.isBefore(endDate.plusDays(1).withTimeAtStartOfDay()); d=d.plusDays(1)) {
//                            if (usd.getValue().containsKey(d.toString(dtf3))) {
//                                usd.getValue().get(d.toString(dtf3)).setHoliday(String.format("%s", row.get("f_Name")).trim());
//                            }
//                        }
//                    }
//                }
//            }
            log.debug("aici");
        } catch (IOException ex) {
            log.error("Exceptie", ex);
        }

        return result;
    }

}
