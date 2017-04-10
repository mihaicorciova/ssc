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
import com.ro.ssc.app.client.model.commons.ShiftData;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.WordUtils;
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
                idMapping.add(WordUtils.capitalizeFully(String.format("%s",row.get("f_ConsumerName")).trim())+"#"+String.format("%s",row.get("f_ConsumerNO")).trim()+"~"+String.format("%s",row.get("f_ConsumerID")).trim());
                
                if(!String.format("%s",row.get("f_AttendEnabled")).contains("1"))
                {
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
            shiftMap.put("0",new ShiftData("0", "weekend", "", "", "", true));
            for (Row row : cursor.newIterable()) {

                shiftMap.put(String.format("%s", row.get("f_ShiftID")).trim(), new ShiftData(String.format("%s", row.get("f_ShiftID")), String.format("%s", row.get("f_ShiftName")).contains(":") ? String.format("%s", row.get("f_ShiftName")).split(":")[0] : String.format("%s", row.get("f_ShiftName")),
                        String.format("%s", row.get("f_ShiftName")).contains(":") ? String.format("%s", row.get("f_ShiftName")).split(":")[1] : "0",
                        String.format("%s", row.get("f_OnDuty1")), String.format("%s", row.get("f_OffDuty1")), String.format("%s", row.get("f_bOvertimeShift")).contains("1")));

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
                            result.get(userId).put(String.format("%s", row.get("f_DateYM")) + "-0" + String.valueOf(i), shiftMap.get(key));
                        }
                    } else {
                        String key = String.format("%s", row.get("f_ShiftID_" + i));
                        if (shiftMap.containsKey(key)) {
                            result.get(userId).put(String.format("%s", row.get("f_DateYM")) + "-" + String.valueOf(i), shiftMap.get(key));
                        }
                    }
                }

            }

        } catch (IOException ex) {
          log.error("Exceptie", ex);
        }
      

        return result;
    }

}
