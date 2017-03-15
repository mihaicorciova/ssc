/**
 *
 */
package com.ro.ssc.app.client.exporter;

import javafx.scene.control.TableView;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xslf.usermodel.*;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author bbenga
 */
public abstract class PptTableExporter {

    public static final String POTX_PATH = "/template/template.potm";

    public void exportTableToPpt(TableView<?> fxTable, File file, String title) {
        try {

            XMLSlideShow pptx = new XMLSlideShow();

            XSLFSlideMaster slideMaster = pptx.getSlideMasters()[0];
            pptx.setPageSize(new java.awt.Dimension(1024, 768));

            // XSLFSlide#createSlide() with no arguments creates a blank slide
            XSLFSlideLayout slidelayout = slideMaster.getLayout(SlideLayout.BLANK);

            //creating a slide with title and content layout
            XSLFSlide slide = pptx.createSlide(slidelayout);
            //selection of title place holder
            XSLFTextBox shape = slide.createTextBox();
            shape.setAnchor(new Rectangle(15, 15, 650, 250));
            XSLFTextParagraph pr = shape.addNewTextParagraph();

            //setting the title in it
            XSLFTextRun r1 = pr.addNewTextRun();
            r1.setText(title);
            r1.setBold(true);
            r1.setFontSize(24);

            XSLFTable tbl = slide.createTable();
            tbl.setAnchor(new Rectangle(15, 100, 650, 250));

            int colNo = fxTable.getColumns().size();
            int rowNo = fxTable.getItems().size();

            XSLFTableRow headerRow = tbl.addRow();
            headerRow.setHeight(15);
            // Create Table Header

            String[][] content = getTableContent(fxTable);

            for (int i = 0; i < colNo; i++) {
                XSLFTableCell th = headerRow.addCell();
                XSLFTextParagraph p = th.addNewTextParagraph();
                p.setTextAlign(TextAlign.CENTER);
                XSLFTextRun r = p.addNewTextRun();
                r.setText(fxTable.getColumns().get(i).getText());
                r.setBold(true);
                r.setFontSize(18);
                r.setFontColor(Color.white);
                th.setFillColor(new Color(79, 129, 189));
                th.setBorderBottom(2);
                th.setBorderBottomColor(Color.white);
                tbl.setColumnWidth(i, 100); // all columns are equally sized
            }
            int i = 0;
            for (int row = 0; row < rowNo; row++) {
                XSLFTableRow tr = tbl.addRow();
                tr.setHeight(12);
                i++;
                // header
                for (int col = 0; col < colNo; col++) {
                    XSLFTableCell cell = tr.addCell();
                    XSLFTextParagraph p = cell.addNewTextParagraph();
                    p.setTextAlign(TextAlign.CENTER);
                    XSLFTextRun r = p.addNewTextRun();

                    r.setText(content[row][col]);
                    r.setFontSize(14);

                    if (row % 2 == 0) {
                        cell.setFillColor(new Color(208, 216, 232));
                    } else {
                        cell.setFillColor(new Color(233, 247, 244));
                    }
                }
                if (i > 10) {
                    if (row + 1 == rowNo) {
                        break;
                    }
                    System.out.println("aici");
                    XSLFSlide slide2 = pptx.createSlide(slidelayout);
                    tbl = slide2.createTable();
                    tbl.setAnchor(new Rectangle(15, 100, 650, 250));
                    headerRow = tbl.addRow();
                    headerRow.setHeight(15);
                    for (int ij = 0; ij < colNo; ij++) {
                        XSLFTableCell th = headerRow.addCell();
                        XSLFTextParagraph p = th.addNewTextParagraph();
                        p.setTextAlign(TextAlign.CENTER);
                        XSLFTextRun r = p.addNewTextRun();
                        r.setText(fxTable.getColumns().get(ij).getText());
                        r.setBold(true);
                        r.setFontSize(18);
                        r.setFontColor(Color.white);
                        th.setFillColor(new Color(79, 129, 189));
                        th.setBorderBottom(2);
                        th.setBorderBottomColor(Color.white);
                        tbl.setColumnWidth(ij, 100); // all columns are equally sized
                    }
                    i = 0;

                }
            }

            FileOutputStream out = new FileOutputStream(file);

            // save the changes in a file
            pptx.write(out);
            out.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void exportTableToXls(TableView<?> fxTable, File file, String title, String department, String date) {
        try {

            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("new sheet");

            int colNo = fxTable.getColumns().size();
            int rowNo = fxTable.getItems().size();
            // Create a row and put some cells in it. Rows are 0 based.
            String[][] content = getTableContent(fxTable);

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
            for (int i = 0; i < 3; i++) {
                HSSFCell cell = row.createCell(i);
                if (i == 2) {
                    cell.setCellValue(date);
                } else if (i == 1) {
                    cell.setCellValue("Data ");
                }
            }
            row = sheet.createRow(4);
            row = sheet.createRow(5);

            for (int i = 0; i <= colNo; i++) {

                if(i<colNo) {
                    HSSFCell cell = row.createCell(i);
                    cell.setCellValue(fxTable.getColumns().get(i).getText());
                }else
                {
                    HSSFCell cell = row.createCell(i);
                    cell.setCellValue("Penalizari");
                }
            }
            CellStyle cellStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            cellStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("h:mm"));


            for (int r = 6; r <= rowNo + 5; r++) {
                row = sheet.createRow(r);
                int a=r+1;
                for (int col = 0; col <= colNo; col++) {
                    HSSFCell cell = row.createCell(col);
                    if(col==4){
                        cell.setCellStyle(cellStyle);
                       cell.setCellType(Cell.CELL_TYPE_FORMULA);
                       cell.setCellFormula("TIME(HOUR(CY"+a+")+HOUR(CW"+a+")-HOUR(B"+a+")+HOUR(D"+a+")-HOUR(CX"+a+")-HOUR(H"+a+"),MINUTE(CY"+a+")+MINUTE(CW"+a+")+MINUTE(D"+a+")-MINUTE(B"+a+")-MINUTE(CX"+a+")-MINUTE(H"+a+"),0)");
                    }else if(col==6){
                        cell.setCellStyle(cellStyle);
                        cell.setCellType(Cell.CELL_TYPE_FORMULA);
                        cell.setCellFormula("TIME(HOUR(E"+a+")+HOUR(F"+a+"),MINUTE(E"+a+")+MINUTE(F"+a+"),0)");
                    }
                    else if(col==7){
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue("00:00");
                    }
                    else{
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(content[r - 6][col]);
                }
                }

                for (int col = 100; col < 104; col++) {
                    HSSFCell cell = row.createCell(col);
                    if (col == 100) {
                        cell.setCellValue(content[r - 6][1]);
                    } else if (col == 101) {
                        cell.setCellValue(content[r - 6][3]);
                    } else if (col == 102) {
                        cell.setCellValue(content[r - 6][4]);
                    } else if (col == 103) {
                        cell.setCellValue(content[r - 6][5]);
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

    public void exportTableToXls(TableView<?> fxTable, File file, String title) {
        try {

            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("new sheet");

            int colNo = fxTable.getColumns().size();
            int rowNo = fxTable.getItems().size();
            // Create a row and put some cells in it. Rows are 0 based.
            String[][] content = getTableContent(fxTable);

            HSSFRow row = sheet.createRow(0);
            for (int i = 0; i < colNo; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(fxTable.getColumns().get(i).getText());

            }

            for (int r = 1; r <= rowNo; r++) {
                row = sheet.createRow(r);

                // header
                for (int col = 0; col < colNo; col++) {

                    row.createCell(col).setCellValue(content[r - 1][col]);

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

    public abstract String[][] getTableContent(TableView<?> fxTable);


}
