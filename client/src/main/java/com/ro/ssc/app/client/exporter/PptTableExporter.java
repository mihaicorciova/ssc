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

/**
 * @author bbenga
 *
 */
public abstract class PptTableExporter {

    public static final String POTX_PATH = "/template/template.potm";

    public void exportTableToPpt(TableView<?> fxTable, File file, String title) {
        try {

            XMLSlideShow pptx = new XMLSlideShow();

            XSLFSlideMaster slideMaster = pptx.getSlideMasters()[0];

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

            r1.setFontSize(24);

            XSLFTable tbl = slide.createTable();
            tbl.setAnchor(new Rectangle(15, 100, 650, 250));

            int colNo = fxTable.getColumns().size();
            int rowNo = fxTable.getItems().size();

            XSLFTableRow headerRow = tbl.addRow();
            headerRow.setHeight(20);
            // Create Table Header

            String[][] content = getTableContent(fxTable);

            for (int i = 0; i < colNo; i++) {
                XSLFTableCell th = headerRow.addCell();
                XSLFTextParagraph p = th.addNewTextParagraph();
                p.setTextAlign(TextAlign.CENTER);
                XSLFTextRun r = p.addNewTextRun();
                r.setText(fxTable.getColumns().get(i).getText());
                r.setBold(true);
                r.setFontColor(Color.white);
                th.setFillColor(new Color(79, 129, 189));
                th.setBorderBottom(2);
                th.setBorderBottomColor(Color.white);
                tbl.setColumnWidth(i, 120); // all columns are equally sized
            }

            for (int row = 0; row < rowNo; row++) {
                XSLFTableRow tr = tbl.addRow();
                tr.setHeight(15);
                // header
                for (int col = 0; col < colNo; col++) {
                    XSLFTableCell cell = tr.addCell();
                    XSLFTextParagraph p = cell.addNewTextParagraph();
                    p.setTextAlign(TextAlign.CENTER);
                    XSLFTextRun r = p.addNewTextRun();

                    r.setText(content[row][col]);
                    r.setFontSize(16);

                    if (row % 2 == 0) {
                        cell.setFillColor(new Color(208, 216, 232));
                    } else {
                        cell.setFillColor(new Color(233, 247, 244));
                    }
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

    public abstract String[][] getTableContent(TableView<?> fxTable);

//    private String[][] getTableContent(TableView<TableData> fxTable) {
//        String[][] content = new String[fxTable.getItems().size()][fxTable.getColumns().size()];
//
//        int rowNo = 0;
//        for (TableData tableData : fxTable.getItems()) {
//            content[rowNo][0] = tableData.getId();
//            content[rowNo][1] = tableData.getStringTwo();
//            content[rowNo][2] = "" + tableData.getDoubleValue();
//            content[rowNo][3] = DateParser.format(tableData.getDate());
//            rowNo++;
//        }
//        return content;
//    }
}
