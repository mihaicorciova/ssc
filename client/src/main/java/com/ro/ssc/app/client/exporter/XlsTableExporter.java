/**
 *
 */
package com.ro.ssc.app.client.exporter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.controlsfx.control.spreadsheet.Grid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author bbenga
 */
public class XlsTableExporter {

    public static final String POTX_PATH = "/template/template.potm";


    public void exportTableToXls(Grid fxTable, File file, String title, String department, String iniDate, String outDate) {
        try {

            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("new sheet");

            int colNo = fxTable.getColumnCount();
            int rowNo = fxTable.getRowCount();
            // Create a row and put some cells in it. Rows are 0 based.

            HSSFRow row = sheet.createRow(0);
            row = sheet.createRow(1);
            for (int i = 0; i < 3; i++) {
                HSSFCell cell = row.createCell(i);
                if (i == 2) {
                    cell.setCellValue(title);
                }
            }

            row = sheet.createRow(2);
            if (!department.equals("")) {
                for (int i = 0; i < 4; i++) {
                    HSSFCell cell = row.createCell(i);
                    if (i == 3) {
                        cell.setCellValue(department);
                    } else if (i == 1) {
                        cell.setCellValue("Departament");
                    }
                }
            }

            row = sheet.createRow(3);
            for (int i = 0; i < 6; i++) {
                HSSFCell cell = row.createCell(i);
                if (i == 2) {
                    cell.setCellValue("de la ");
                } else if (i == 3) {
                    cell.setCellValue(iniDate);
                } else if (i == 4) {
                    cell.setCellValue("pana la ");
                } else if (i == 5) {
                    cell.setCellValue(outDate);
                } else if (i == 1) {
                    cell.setCellValue("Perioada");
                }
            }
            row = sheet.createRow(4);
            row = sheet.createRow(5);
            if (department.equals("")) {
                HSSFCell cell = row.createCell(0);
                cell.setCellValue("Departament");
                for (int i = 1; i <= colNo; i++) {

                    cell = row.createCell(i);
                    cell.setCellValue(fxTable.getColumnHeaders().get(i - 1));

                }
            } else {
                for (int i = 0; i < colNo; i++) {

                    HSSFCell cell = row.createCell(i);
                    cell.setCellValue(fxTable.getColumnHeaders().get(i));

                }
            }

            for (int r = 6; r <= rowNo+5; r++) {
                row = sheet.createRow(r);

                // header
        String idepartment = "";

                if (department.equals("")) {
                    for (int col = 0; col <= colNo; col++) {

                        if (col == 0) {
                           
                                row.createCell(col).setCellValue(fxTable.getRowHeaders().get(r - 6));
                        } else {
                            row.createCell(col).setCellValue(fxTable.getRows().get(r - 6).get(col - 1).getText());
                        }
                    }
                } else {
                    for (int col = 0; col < colNo; col++) {

                        row.createCell(col).setCellValue(fxTable.getRows().get(r - 6).get(col).getText());

                    }
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
