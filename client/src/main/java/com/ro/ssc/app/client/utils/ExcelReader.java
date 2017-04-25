/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.utils;

import com.ro.ssc.app.client.model.commons.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

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
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
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
                        if (row.getCell(ExcelEnum.USER_ID.getAsInteger()) == null || row.getCell(ExcelEnum.USER_ID.getAsInteger()).getCellType() == Cell.CELL_TYPE_BLANK) {


                        } else {
                            String i = row.getCell(ExcelEnum.USER_ID.getAsInteger()).toString().trim().contains(".") ? row.getCell(ExcelEnum.USER_ID.getAsInteger()).toString().trim().split("\\.")[0] : row.getCell(ExcelEnum.USER_ID.getAsInteger()).toString().trim();
                            String user = WordUtils.capitalizeFully(row.getCell(ExcelEnum.USER_NAME.getAsInteger()).toString().trim()) + "#" + i;
                            if (row.getCell(ExcelEnum.PASSED.getAsInteger()).toString().trim().equals("1.0")) {
                                if (result.containsKey(user)) {
                                    events = result.get(user).getEvents();
                                    final DateTimeZone dtz = DateTimeZone.getDefault();
                                    LocalDateTime dateTime= dtf.parseLocalDateTime(row.getCell(ExcelEnum.TIMESTAMP.getAsInteger()).toString());
                                    if(dtz.isLocalDateTimeGap(dateTime)){
                                        dateTime=dateTime.withHourOfDay(4);
                                    }

                                    final Event event = new Event(dateTime.toDateTime(), row.getCell(ExcelEnum.DESCRIPTION.getAsInteger()).toString(), row.getCell(ExcelEnum.ADDRESS.getAsInteger()).toString().trim(), row.getCell(ExcelEnum.PASSED.getAsInteger()).toString().trim().equals("1.0"));

                                    events.add(event);
                                    result.get(user).setEvents(events);

                                } else {
                                    events = new ArrayList();
                                    events.add(new Event(DateTime.parse(row.getCell(ExcelEnum.TIMESTAMP.getAsInteger()).toString(), dtf), row.getCell(ExcelEnum.DESCRIPTION.getAsInteger()).toString().trim(), row.getCell(ExcelEnum.ADDRESS.getAsInteger()).toString().trim(), row.getCell(ExcelEnum.PASSED.getAsInteger()).toString().trim().equals("1.0")));
                                    String id = row.getCell(ExcelEnum.USER_ID.getAsInteger()).toString().trim();
                                    result.put(user, new User(WordUtils.capitalizeFully(row.getCell(ExcelEnum.USER_NAME.getAsInteger()).toString().trim()), id.contains(".") ? id.split("\\.")[0] : id, row.getCell(ExcelEnum.CARD_NO.getAsInteger()).toString().trim(), WordUtils.capitalizeFully(row.getCell(ExcelEnum.DEPARTMENT.getAsInteger()).toString().trim()), events));
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("Exception" + e.getMessage());
                    }

                }}
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

              
                            

                            final String date = sheet.getRow(3).getCell(2).toString();
            
                 //           log.debug(dep+" "+date);
                            
            DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");
            DateTimeFormatter dtf2 = DateTimeFormat.forPattern("HH:mm:ss");
            final java.time.format.DateTimeFormatter dtf4 = java.time.format.DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z YYYY");
            DateTimeFormatter dtf3 = DateTimeFormat.forPattern("HH:mm");
            FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
            formulaEvaluator.evaluateAll();
            for (int r = 1; r <= rows+1; r++) {
                row = sheet.getRow(r);

                if (row != null) {
                    try {

                        if (row.getCell(ExcelEnum2.I.getAsInteger()) == null || row.getCell(ExcelEnum2.I.getAsInteger()).getCellType() == Cell.CELL_TYPE_BLANK) {
 final String user = row.getCell(ExcelEnum2.USER_NAME.getAsInteger()).toString().trim();
                       
                        } else if (!row.getCell(ExcelEnum2.I.getAsInteger()).toString().isEmpty()) {

                            final String user = row.getCell(ExcelEnum2.USER_NAME.getAsInteger()).toString().trim();
                        final String dep =row.getCell(ExcelEnum2.DEP.getAsInteger()).toString().trim();
                        
                      //  log.debug(user+" "+dep+" "+row.getCell(ExcelEnum2.IN.getAsInteger()).toString().trim()+" "+row.getCell(ExcelEnum2.OUT.getAsInteger()).toString().trim()+" "+row.getCell(ExcelEnum2.I.getAsInteger()).toString().trim()+
                       //        " "+row.getCell(ExcelEnum2.O.getAsInteger()).toString().trim()+" "+row.getCell(ExcelEnum2.W.getAsInteger()).toString().trim()+" " );
                        
                            long in = 0;
                            if (!row.getCell(ExcelEnum2.IN.getAsInteger()).toString().contains("1899")) {
                                in = LocalTime.parse(row.getCell(ExcelEnum2.IN.getAsInteger()).toString().trim(), dtf2).getMillisOfDay();
                                                  //    log.debug("in1 "+ in);

                            } else {
                               
                                in = java.time.LocalTime.from(dtf4.parse(row.getCell(ExcelEnum2.IN.getAsInteger()).getDateCellValue().toString())).toNanoOfDay() / 1000000;
                         //    log.debug("in2 "+ in);
 
                            }
                            long out = 0;
                            if (!row.getCell(ExcelEnum2.OUT.getAsInteger()).toString().contains("1899")) {
                                out = LocalTime.parse(row.getCell(ExcelEnum2.OUT.getAsInteger()).toString().trim(), dtf2).getMillisOfDay();
                        //    log.debug("out1 "+ out);
                            } else {
                           //     log.debug("out "+row.getCell(ExcelEnum2.OUT.getAsInteger()).getDateCellValue().toString());

                                out = java.time.LocalTime.from(dtf4.parse(row.getCell(ExcelEnum2.OUT.getAsInteger()).getDateCellValue().toString())).toNanoOfDay() / 1000000;
                        //     log.debug("out2 "+ out);

                            }
                            long work = -1;

                            if (row.getCell(ExcelEnum2.WORK.getAsInteger()).toString().contains("1899")) {
                         //       log.debug("work"+row.getCell(ExcelEnum2.WORK.getAsInteger()).getDateCellValue().toString());

                                work = java.time.LocalTime.from(dtf4.parse(row.getCell(ExcelEnum2.WORK.getAsInteger()).getDateCellValue().toString())).toNanoOfDay() / 1000000;
                                                      // log.debug("work"+ work);

                            }

                            long pen;
                            if (!row.getCell(ExcelEnum2.PEN.getAsInteger()).toString().contains("1899")) {
                                pen = LocalTime.parse(row.getCell(ExcelEnum2.PEN.getAsInteger()).toString().trim(), dtf3).getMillisOfDay();
                   //        log.debug("pen1"+ pen);
                            } else {
                             //   log.debug("pen"+row.getCell(ExcelEnum2.PEN.getAsInteger()).getDateCellValue().toString());
                                pen = java.time.LocalTime.from(dtf4.parse(row.getCell(ExcelEnum2.PEN.getAsInteger()).getDateCellValue().toString())).toNanoOfDay() / 1000000;
                    //         log.debug("pen2"+ pen);

                            }


                            final LocalTime in2 = LocalTime.parse(row.getCell(ExcelEnum2.I.getAsInteger()).toString().trim(), dtf2);
                            final LocalTime out2 = LocalTime.parse(row.getCell(ExcelEnum2.O.getAsInteger()).toString().trim(), dtf2);
                            final LocalTime w = LocalTime.parse(row.getCell(ExcelEnum2.W.getAsInteger()).toString().trim(), dtf3);

                            long wtime = w.getMillisOfDay() - in + in2.getMillisOfDay() + out - out2.getMillisOfDay() - pen;
                            if (work != -1) {
                                wtime = work;
                            }

                            long ptime = 0;
                            if (!row.getCell(ExcelEnum2.PAUSE.getAsInteger()).toString().contains("1899")) {
                                ptime = LocalTime.parse(row.getCell(ExcelEnum2.PAUSE.getAsInteger()).toString().trim(), dtf3).getMillisOfDay();
                            } else {
                        //        log.debug("paus"+row.getCell(ExcelEnum2.PAUSE.getAsInteger()).getDateCellValue().toString());

                                ptime = java.time.LocalTime.from(dtf4.parse(row.getCell(ExcelEnum2.PAUSE.getAsInteger()).getDateCellValue().toString())).toNanoOfDay() / 1000000;

                            }

                            if(wtime+ptime>24*3600*1000){
                                wtime=-1;
                                ptime=-1;
                            }
                            final DailyData dai = new DailyData(WordUtils.capitalizeFully(user), DateTime.parse(date, dtf), row.getCell(ExcelEnum2.IN.getAsInteger()).toString().trim(), row.getCell(ExcelEnum2.OUT.getAsInteger()).toString().trim(), 0, wtime, ptime, 0, 0, new ArrayList(), dep);
                            result.add(dai);
                        

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
