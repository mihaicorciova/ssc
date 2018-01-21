/**
 *
 */
package com.ro.ssc.app.client.exporter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author bbenga
 */
public class XlsxTableExporter {

    public void exportTableToXls(Grid fxTable, File file, String title, String department, String iniDate, String outDate) {
        try {

            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("new sheet");

            int colNo = fxTable.getColumnCount();
            int rowNo = fxTable.getRowCount();
            // Create a row and put some cells in it. Rows are 0 based.
            XSSFRow   row  =sheet.createRow(0);
            for (int i = 0; i < colNo; i++) {

                XSSFCell cell = row.createCell(i);
                cell.setCellValue(fxTable.getColumnHeaders().get(i));

            }
            for (int r = 1; r <= rowNo; r++) {

                   boolean emp =true;
                   for(SpreadsheetCell cont: fxTable.getRows().get(r-1)){

                           if(cont.getColumn()>4){
                               if(!cont.getText().contains("0") && !cont.getText().isEmpty()){
                                   emp=false;
                               }
                           }

                   }
                   if(emp){
                       continue;
                   }
                row = sheet.createRow(r);
                 for (int col = 0; col < colNo; col++) {

                        row.createCell(col).setCellValue(fxTable.getRows().get(r-1).get(col).getText());
                }
            }

            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(file);
            wb.write(fileOut);
            fileOut.close();
            wb = null;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
