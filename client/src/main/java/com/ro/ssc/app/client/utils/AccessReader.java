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
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DauBufu
 */
public class AccessReader {
    
    public static Map<String,User> updateUserMap(Map<String,User> map,File file)
    {
        
        /*
          --normal/night shift 
--SELECT f_ConsumerID FROM t_b_consumer_other where f_note='Night'
Database db = DatabaseBuilder.open(new File("iCCard3000.mdb"));
Table table = db.getTable("t_b_consumer_other");
IndexCursor cursor = CursorBuilder.createCursor(table.getIndex("f_note"));



for(Row row : cursor.newEntryIterable("Night")) {
  System.out.println(String.format("ID=%d", row.get("f_ConsumerID")));
  // post processing Razvan
}
--usi de pontaj -->doar aceste usi se iau in considerare
--select f_ReaderName from t_b_reader where f_attend=1

Table table = db.getTable("t_b_reader");
IndexCursor cursor = CursorBuilder.createCursor(table.getIndex("f_attend"));
for(Row row : cursor.newEntryIterable("1")) {
  System.out.println(String.format("ReaderName=%s", row.get("f_ReaderName")));
  // post processing Razvan
}




-- useri care sunt luati in considerare la pontaj
--select f_ConsumerNo from t_b_consumer where
--f_AttendEnabled=1
Table table = db.getTable("t_b_reader");
IndexCursor cursor = CursorBuilder.createCursor(table.getIndex("f_AttendEnabled"));
for(Row row : cursor.newEntryIterable("1")) {
  System.out.println(String.format("f_ConsumerNo=%d", row.get("f_ConsumerNo")));
  // post processing Razvan
}


Exemple:

  Database db = DatabaseBuilder.open(new File("iCCard3000.mdb"));
  Table table = db.getTable("t_b_consumer_other");

  Row row = ...;
  for(Column column : table.getColumns()) {
    String columnName = column.getName();
    Object value = row.get(columnName);
    System.out.println("Column " + columnName + "(" + column.getType() + "): "
                       + value + " (" + value.getClass() + ")");
  }

  // Example Output:
  //
  // Column ID(LONG): 27 (java.lang.Integer)
  // Column Name(TEXT): Bob Smith (java.lang.String)
  // Column Salary(MONEY): 50000.00 (java.math.BigDecimal)
  // Column StartDate(SHORT_DATE_TIME): Mon Jan 05 09:00:00 EDT 2010 (java.util.Date)

  Table table = db.getTable("Test");
 IndexCursor cursor = CursorBuilder.createCursor(table.getIndex("NameIndex"));
 for(Row row : cursor.newEntryIterable("bob")) {
   System.out.println(String.format("ID=%d, Name='%s'.", row.get("ID"), row.get("Name")));
 }
 */

    Table table;
        try {
            table = DatabaseBuilder.open(file).getTable("t_b_reader");
            Cursor cursor = CursorBuilder.createCursor(table);
        for (Row row : cursor.newIterable().addMatchPattern("1", "2")) {
    System.out.println(String.format(
            "a='%s', SomeFieldName='%s'.", 
            row.get("a"), 
            row.get("SomeFieldName")));
}
        } catch (IOException ex) {
            Logger.getLogger(AccessReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
}
