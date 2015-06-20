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
import com.ro.ssc.app.client.model.commons.User;
import com.ro.ssc.app.client.service.impl.DataProviderImpl;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        Set<String> nightShifts = new LinkedHashSet<>();
        List<Set<String>> result = new LinkedList<>();

        Table table;
        try {
            log.debug("In access reader");
            table = DatabaseBuilder.open(file).getTable("t_b_Reader");
            Cursor cursor = CursorBuilder.createCursor(table);
            for (Row row : cursor.newIterable().addMatchPattern("f_Attend", 0)) {

                excludedGates.add(String.format("%s", row.get("f_ReaderName")));
            }
            table = DatabaseBuilder.open(file).getTable("t_b_Consumer");
            cursor = CursorBuilder.createCursor(table);
            for (Row row : cursor.newIterable().addMatchPattern("f_AttendEnabled", 0)) {

                excludedUsers.add(WordUtils.capitalizeFully(String.format("%s", row.get("f_ConsumerName"))));
            }
            table = DatabaseBuilder.open(file).getTable("t_b_Consumer_Other");
            cursor = CursorBuilder.createCursor(table);
            for (Row row : cursor.newIterable().addMatchPattern("f_Note", "Night")) {

                nightShifts.add(String.format("%s", row.get("f_ConsumerID")));
            }

        } catch (IOException ex) {
            Logger.getLogger(AccessReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        result.add(nightShifts);
        result.add(excludedGates);
        result.add(excludedUsers);
log.debug("First id"+nightShifts.iterator().next());
        return result;
    }

}
