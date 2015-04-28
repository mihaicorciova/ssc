package com.asml.wfa.metrotools.tooltotoolmatching.gui.widgets;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.DP2DCoordinateUtils;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.DP2DDimension2D;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.standardObjects.BaseDataPlot2DObject;

/**
 * 
 * @author Roel Coset
 * @version 2015-02-23, RCPL, Initial version.
 */
public class DirectionalFieldObject extends BaseDataPlot2DObject {

    private static final Color EDGE_COLOR = new Color(150, 150, 150);
    private static final Color DIE_EDGE_COLOR = new Color(235, 235, 235);

    private final Point2D centerPosition;
    private final double fieldWidth;
    private final double fieldHeight;

    private final int nrDiesX;
    private final int nrDiesY;
    private final double dieWidth;
    private final double dieHeight;

    private final Area fieldTemplate;
    private final Area dieTemplate;

    /**
     * Creates a new Field object with the given center position and field dimensions.
     * 
     * @param xPos
     *            The x of the field center position.
     * @param yPos
     *            The y of the field center position.
     * @param width
     *            The field width.
     * @param height
     *            The field height.
     */
    public DirectionalFieldObject(final double xPos, final double yPos, final double width, final double height) {
        this(xPos, yPos, width, height, 0, 0);
    }

    /**
     * Creates a new Field object with the given center position and field dimensions.
     * 
     * @param xPos
     *            The x of the field center position.
     * @param yPos
     *            The y of the field center position.
     * @param width
     *            The field width.
     * @param height
     *            The field height.
     * @param nrDiesX
     *            The number of dies in the x direction.
     * @param nrDiesY
     *            The number of dies in the y direction.
     */
    public DirectionalFieldObject(final double xPos, final double yPos, final double width, final double height,
            final int nrDiesX, final int nrDiesY) {
        this.centerPosition = new Point2D.Double(xPos, yPos);

        this.fieldWidth = width - 0.25;
        this.fieldHeight = height - 0.25;
        this.fieldTemplate = new Area(new Rectangle2D.Double(0, 0, fieldWidth, fieldHeight));

        if (nrDiesX > 0 && nrDiesY > 0) {
            // there is die information available.
            this.nrDiesX = nrDiesX;
            this.nrDiesY = nrDiesY;

            // the size of a die is determined by the number of dies that fit in a field.
            this.dieWidth = fieldWidth / nrDiesX;
            this.dieHeight = fieldHeight / nrDiesY;
            this.dieTemplate = new Area(new Rectangle2D.Double(0, 0, dieWidth, dieHeight));
        } else {
            this.nrDiesX = 0;
            this.nrDiesY = 0;

            this.dieWidth = 0.0;
            this.dieHeight = 0.0;
            this.dieTemplate = null;
        }
    }

    @Override
    public Point2D getPosition(final double scaleFactor) {
        return centerPosition;
    }

    @Override
    public Dimension2D getSize(final double scaleFactor) {
        final int margin = 2; // mm
        final double widthMm = fieldTemplate.getBounds2D().getWidth();
        final double heigthMm = fieldTemplate.getBounds2D().getHeight();

        return new DP2DDimension2D(widthMm + margin, heigthMm + margin);
    }

    @Override
    public int getZIndex() {
        return -1;
    }

    @Override
    public void paintObject(final Graphics2D g, final Rectangle2D tileBounds, final Rectangle2D objectBounds, final double zoomFactor,
            final double scaleFactor) {
        // draw the dies.
        for (int y = 0; y < nrDiesY; y++) {
            for (int x = 0; x < nrDiesX; x++) {
                final double dieX = centerPosition.getX() - (fieldWidth / 2) + (x * dieWidth) + (dieWidth / 2);
                final double dieY = centerPosition.getY() - (fieldHeight / 2) + (y * dieHeight) + (dieHeight / 2);
                final Point2D diePosition = new Point2D.Double(dieX, dieY);
                drawDie(diePosition, g, zoomFactor, objectBounds);
            }
        }

        // draw the field border.
        drawFieldBorder(g, zoomFactor, objectBounds);
    }

    /**
     * Draw a die at the given die position.
     * 
     * @param diePosition
     *            The position to draw a die.
     * @param g
     *            The {@link Graphics2D}.
     * @param zoomFactor
     *            The zoom factor.
     * @param objectBounds
     *            The object bounds.
     */
    private void drawDie(final Point2D diePosition, final Graphics2D g, final double zoomFactor, final Rectangle2D objectBounds) {
        final java.awt.Point centerPx = DP2DCoordinateUtils.convertASMLToScreenCoordinates(diePosition, objectBounds, zoomFactor);

        final double widthPx = DP2DCoordinateUtils.mmToPxDbl(dieWidth - 0.20, zoomFactor);
        final double heightPx = DP2DCoordinateUtils.mmToPxDbl(dieHeight - 0.20, zoomFactor);

        final Area die = (Area) dieTemplate.clone();
        die.transform(AffineTransform.getScaleInstance(widthPx / dieWidth, heightPx / dieHeight));
        die.transform(AffineTransform.getTranslateInstance(centerPx.getX() - (widthPx / 2), centerPx.getY() - (heightPx / 2)));

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setComposite(AlphaComposite.SrcOver.derive(0.7f));
        g.setColor(DIE_EDGE_COLOR);
        g.draw(die);
        g.setComposite(AlphaComposite.Src);
    }

    /**
     * Draw the field border.
     * 
     * @param g
     *            The {@link Graphics2D}.
     * @param zoomFactor
     *            The zoom factor.
     * @param objectBounds
     *            The object bounds.
     */
    private void drawFieldBorder(final Graphics2D g, final double zoomFactor, final Rectangle2D objectBounds) {
        final java.awt.Point centerPx = DP2DCoordinateUtils.convertASMLToScreenCoordinates(centerPosition, objectBounds, zoomFactor);

        final double widthPx = DP2DCoordinateUtils.mmToPxDbl(fieldWidth, zoomFactor);
        final double heightPx = DP2DCoordinateUtils.mmToPxDbl(fieldHeight, zoomFactor);

        final Area field = (Area) fieldTemplate.clone();
        field.transform(AffineTransform.getScaleInstance(widthPx / fieldWidth, heightPx / fieldHeight));
        field.transform(AffineTransform.getTranslateInstance(centerPx.getX() - (widthPx / 2), centerPx.getY() - (heightPx / 2)));

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setComposite(AlphaComposite.SrcOver.derive(0.7f));
        g.setColor(EDGE_COLOR);
        g.draw(field);
        g.setComposite(AlphaComposite.Src);
    }

    @Override
    public void paintObjectAsThumbnail(final Graphics2D g, final Rectangle2D objectBounds, final double zoomFactor, final double scaleFactor) {
        drawFieldBorder(g, zoomFactor, objectBounds);
    }

    @Override
    public boolean isAnchorObject() {
        return true;
    }
}
