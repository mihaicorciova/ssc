/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.utils;

import com.ro.ssc.app.client.model.commons.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.WordUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DauBufu
 */
public class ExcelReader {

    private static final Logger log = LoggerFactory.getLogger(ExcelReader.class);

    /**
     *
     * @param file
     * @return
     */
    public static Map<String, User> readExcel(File file) {
        Map<String, User> result = new HashMap<>();
        List<Event> events = new ArrayList();
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;
            HSSFCell cell;

            int rows; // No of rows
            rows = sheet.getPhysicalNumberOfRows();

            int cols = 0; // No of columns
            int tmp = 0;

            // This trick ensures that we get the data properly even if it doesn't start from first few rows
            for (int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) {
                        cols = tmp;
                    }
                }
            }

            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss EEEE");
            for (int r = 1; r < rows; r++) {
                row = sheet.getRow(r);

                if (row != null) {
                    try {
                        String user = WordUtils.capitalizeFully(row.getCell(ExcelEnum.USER_NAME.getAsInteger()).toString().trim());
                        if (row.getCell(ExcelEnum.PASSED.getAsInteger()).toString().trim().equals("1.0")) {
                            if (result.containsKey(user)) {
                                events = result.get(user).getEvents();

                                events.add(new Event(DateTime.parse(row.getCell(ExcelEnum.TIMESTAMP.getAsInteger()).toString(), dtf), row.getCell(ExcelEnum.DESCRIPTION.getAsInteger()).toString(), row.getCell(ExcelEnum.ADDRESS.getAsInteger()).toString().trim(), row.getCell(ExcelEnum.PASSED.getAsInteger()).toString().trim().equals("1.0")));
                                result.get(user).setEvents(events);

                            } else {
                                events = new ArrayList();
                                events.add(new Event(DateTime.parse(row.getCell(ExcelEnum.TIMESTAMP.getAsInteger()).toString(), dtf), row.getCell(ExcelEnum.DESCRIPTION.getAsInteger()).toString().trim(), row.getCell(ExcelEnum.ADDRESS.getAsInteger()).toString().trim(), row.getCell(ExcelEnum.PASSED.getAsInteger()).toString().trim().equals("1.0")));
                                String id = row.getCell(ExcelEnum.USER_ID.getAsInteger()).toString().trim();
                                result.put(user, new User(user, id.contains(".") ? id.split("\\.")[0] : id, row.getCell(ExcelEnum.CARD_NO.getAsInteger()).toString().trim(), WordUtils.capitalizeFully(row.getCell(ExcelEnum.DEPARTMENT.getAsInteger()).toString().trim()), events));
                            }
                        }
                    } catch (Exception e) {
                        log.error("Exception" + e.getMessage());
                    }
                }
            }
        } catch (Exception ioe) {
            log.error("Exception" + ioe.getMessage());
        }
        return result;
    }

    public static List<DailyData> readFile(File file) {
        final List<DailyData> result = new ArrayList<>();
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;
            HSSFCell cell;
FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

            int rows; // No of rows
            rows = sheet.getPhysicalNumberOfRows();

            int cols = 0; // No of columns
            int tmp = 0;

            // This trick ensures that we get the data properly even if it doesn't start from first few rows
            for (int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) {
                        cols = tmp;
                    }
                }
            }

            DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
            DateTimeFormatter dtf2 = DateTimeFormat.forPattern("HH:mm:ss");
 DateTimeFormatter dtf3 = DateTimeFormat.forPattern("HH:mm");
            for (int r = 1; r < rows; r++) {
                row = sheet.getRow(r);

                if (row != null) {
                    try {

                        if (row.getCell(ExcelEnum2.I.getAsInteger()) == null || row.getCell(ExcelEnum2.I.getAsInteger()).getCellType() == Cell.CELL_TYPE_BLANK) {
        
    } else  

                        if (!row.getCell(ExcelEnum2.I.getAsInteger()).toString().isEmpty()) {
                           log.info(file.getName());
                          DataFormatter df = new DataFormatter(); 
df.formatCellValue(row.getCell(ExcelEnum2.IN.getAsInteger()));
                            final String user = row.getCell(ExcelEnum2.USER_NAME.getAsInteger()).toString().trim();
                            final String dep = file.getName().split("-")[0];
                            final String date = file.getName().substring(dep.length()+1, file.getName().replace(".xls","").length());
                             log.debug(user+ " "+ dep+" "+ date);
                            log.debug(row.getCell(ExcelEnum2.IN.getAsInteger()).toString()+ " "+ row.getCell(ExcelEnum2.OUT.getAsInteger()).toString().trim()+" "+ date); 
                            final LocalTime in = LocalTime.parse(row.getCell(ExcelEnum2.IN.getAsInteger()).toString().trim(),dtf2);
                           final LocalTime out = LocalTime.parse(row.getCell(ExcelEnum2.OUT.getAsInteger()).toString().trim(),dtf2);
                           final LocalTime in2 = LocalTime.parse(row.getCell(ExcelEnum2.I.getAsInteger()).toString().trim(),dtf2);
                           final LocalTime out2 = LocalTime.parse(row.getCell(ExcelEnum2.O.getAsInteger()).toString().trim(),dtf2);
                           final LocalTime w = LocalTime.parse(row.getCell(ExcelEnum2.W.getAsInteger()).toString().trim(),dtf3);
                                                        

                           log.debug(user+ " "+ dep+" "+ date+ in.toString()+" "+ out.toString()+" "+ in2.toString()+" "+ out2.toString()+" "+ w.toString());
                           
                            final String pt = row.getCell(ExcelEnum2.PAUSE.getAsInteger()).toString().trim();
                            
                            final long wtime =w.getMillisOfDay()-in.getMillisOfDay()+in2.getMillisOfDay()+out.getMillisOfDay()-out2.getMillisOfDay();
                            final long ptime = LocalTime.parse(pt, dtf3).getMillisOfDay();

                            
                            result.add(new DailyData(user, DateTime.parse(date, dtf),row.getCell(ExcelEnum2.IN.getAsInteger()).toString().trim(), row.getCell(ExcelEnum2.OUT.getAsInteger()).toString().trim(), 0, wtime, ptime, 0, 0, new ArrayList(), dep));

                        }
                    } catch (Exception e) {
                        log.error("Exception" + e.getMessage());
                    }
                }
            }
        } catch (Exception ioe) {
            log.error("Exception" + ioe.getMessage());
        }
        return result;
    }

}
