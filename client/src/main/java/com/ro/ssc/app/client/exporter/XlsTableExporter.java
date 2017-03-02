/**
 *
 */
package com.ro.ssc.app.client.exporter;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javafx.scene.control.TableView;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xslf.usermodel.SlideLayout;

import org.apache.poi.xslf.usermodel.TextAlign;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.controlsfx.control.spreadsheet.Grid;

/**
 * @author bbenga
 *
 */
public class XlsTableExporter {

    public static final String POTX_PATH = "/template/template.potm";


    public void exportTableToXls(Grid fxTable, File file, String title) {
        try {

            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("new sheet");

            int colNo = fxTable.getColumnCount();
            int rowNo = fxTable.getRowCount();
            // Create a row and put some cells in it. Rows are 0 based.
          
            HSSFRow row = sheet.createRow(0);
            for (int i = 0; i < colNo; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(fxTable.getColumnHeaders().get(i));

            }

            for (int r = 1; r <= rowNo; r++) {
                row = sheet.createRow(r);

                // header
                for (int col = 0; col <=colNo; col++) {

                    if(col==0)
                    {
                         row.createCell(col).setCellValue(fxTable.getRowHeaders().get(r-1));

                    }else{
                    row.createCell(col).setCellValue(fxTable.getRows().get(r-1).get(col-1).getText());
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
