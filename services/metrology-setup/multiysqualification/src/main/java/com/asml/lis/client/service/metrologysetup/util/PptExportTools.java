/**
 * 
 */
package com.asml.lis.client.service.metrologysetup.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javafx.scene.control.TableView;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.poi.xslf.usermodel.TextAlign;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

/**
 * @author bbenga
 *
 */
public class PptExportTools {

    /**
     * Creates a snapshoot of a JPanel and save it into an Byte Array Image
     * 
     * @param panel
     *            - the panel to be saved as an image
     * @return array of bytes representing the snapshoot image
     * @throws IOException
     */

    public static byte[] getPictureFromJComponent(JPanel panel) throws IOException {
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        panel.printAll(g);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    /**
     * Creates a new slide in the given PPT and saves the given image. The slide title will be passed as an argument.
     * 
     * @param ppt
     *            - the {@link XMLSlideShow} where the new slide is created
     * @param picture
     *            - the picture to be added to the newly created slide
     * @param scale
     *            - the scale used to compute the dimension of the picture in the slide
     * @param slideTitle
     *            - the slide title message in the newly created slide
     */
    public static void addPictureToNewPptSlide(XMLSlideShow ppt, byte[] picture, double scale, String slideTitle) {
        // Export Image to selected PPT file
        XSLFSlide slide = ppt.getSlides()[ppt.getSlides().length - 1];
        ppt.createSlide(slide.getMasterSheet()).importContent(slide);

        slide.getPlaceholder(0).setText(slideTitle);

        int idx = ppt.addPicture(picture, XSLFPictureData.PICTURE_TYPE_JPEG);

        XSLFPictureShape pic = slide.createPicture(idx);
        pic.setAnchor(new Rectangle(100, 100, (int) (pic.getAnchor().getWidth() * scale), (int) (pic.getAnchor().getHeight() * scale)));
    }

    /**
     * * Creates a new slide in the given PPT and exports the given table. The slide title will be passed as an argument.
     * 
     * @param ppt
     *            - - the {@link XMLSlideShow} where the new slide is created
     * @param fxTable
     *            - The JavaFX Table to be exported
     * @param content
     *            - The content of the table
     * @param slideTitle
     *            - the slide title message in the newly created slide
     */
    public static void exportTableToSlide(XMLSlideShow ppt, TableView<?> fxTable, String[][] content, String slideTitle) {

        // Export Table to selected PPT file
        XSLFSlide slide = ppt.getSlides()[ppt.getSlides().length - 1];
        ppt.createSlide(slide.getMasterSheet()).importContent(slide);

        slide.getPlaceholder(0).setText(slideTitle);

        XSLFTable tbl = slide.createTable();
        tbl.setAnchor(new Rectangle(75, 100, 450, 380));

        int colNo = fxTable.getColumns().size();
        int rowNo = fxTable.getItems().size();

        XSLFTableRow headerRow = tbl.addRow();
        headerRow.setHeight(50);
        // Create Table Header

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
            tbl.setColumnWidth(i, 110); // all columns are equally sized
        }

        for (int row = 0; row < rowNo; row++) {
            XSLFTableRow tr = tbl.addRow();
            tr.setHeight(25);
            // header
            for (int col = 0; col < colNo; col++) {
                XSLFTableCell cell = tr.addCell();
                XSLFTextParagraph p = cell.addNewTextParagraph();
                XSLFTextRun r = p.addNewTextRun();

                r.setText(content[row][col]);
                r.setFontSize(16);

                if (row % 2 == 0)
                    cell.setFillColor(new Color(208, 216, 232));
                else
                    cell.setFillColor(new Color(233, 247, 244));
            }
        }
    }
}
