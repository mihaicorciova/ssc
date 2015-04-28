package com.asml.wfa.metrotools.tooltotoolmatching.gui.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.apache.commons.math.util.FastMath;

import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.Dimension2DImpl;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.DP2DCoordinateUtils;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.standardObjects.BaseDataPlot2DObject;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.toggle.DP2DHideFlyersToggle;
import com.asml.wfa.common.guicomponents.widgets.plots.dataplot2d.v2.toggle.DataPlot2DToggle;

public class VectorDataPoint extends BaseDataPlot2DObject {

    private static final DataPlot2DToggle HIDE_INVALIDS_TOGGLE = new DP2DHideFlyersToggle();

    private static final Color FLYER_COLOR = Color.BLACK;

    private static final double MULTIPLICATION_FACTOR = 10.0; // 1nm = 3mm on the plot -> Change also VectorScaleLegend
    private static final int ARROWSIZE = 2; // px
    private static final int ARROWSIZETHUMB = 1; // px
    private static final float LINEWIDTH = 0.75f;
    private static final float LINEWIDTHTHUMB = 0.5f;
    private static final int DATAPOINTLAYER = 2;
    private static final int DATAPOINTINVALIDLAYER = DATAPOINTLAYER + 1;

    private final Point2D position;
    private final double xval;
    private final double yval;
    private final boolean valid;

    /**
     * Constructor.
     * 
     * @param xpos
     *            the position of the measurement on the wafer in ASML coordinates
     * @param ypos
     *            the position of the measurement on the wafer in ASML coordinates
     * @param xval
     *            the x-component of the measurement in nm
     * @param yval
     *            the y-component of the measurement in nm
     * @param valid
     */
    public VectorDataPoint(final double xpos, final double ypos, final double xval, final double yval, final boolean valid) {
        this.position = new Point2D.Double(xpos, ypos);
        this.valid = valid;

        if (!Double.isNaN(xval) && !Double.isNaN(yval)) {
            this.xval = xval;
            this.yval = yval;
        } else {
            this.xval = 0;
            this.yval = 0;
        }
    }

    @Override
    public Double getScalarValue() {
        return FastMath.sqrt(this.xval * this.xval + this.yval * this.yval);
    }

    @Override
    public boolean hasScalarValue() {
        return this.valid;
    }

    @Override
    public Point2D getPosition(final double scaleFactor) {

        final double widthmm = xval * scaleFactor * MULTIPLICATION_FACTOR;
        final double heightmm = yval * scaleFactor * MULTIPLICATION_FACTOR;

        return new Point2D.Double(position.getX() + widthmm / 2.0, position.getY() + heightmm / 2.0);
    }

    @Override
    public Dimension2D getSize(final double scaleFactor) {
        final double widthmm = Math.abs(xval * scaleFactor * MULTIPLICATION_FACTOR);
        final double heightmm = Math.abs(yval * scaleFactor * MULTIPLICATION_FACTOR);

        final double angle = FastMath.atan2(heightmm, widthmm);

        final double offsetHeight = FastMath.cos(angle) * ARROWSIZE;
        final double offsetWidth = FastMath.sin(angle) * ARROWSIZE;

        return new Dimension2DImpl(widthmm + offsetWidth, heightmm + offsetHeight);
    }

    @Override
    public int getZIndex() {
        if (valid) {
            return DATAPOINTLAYER;
        } else {
            return DATAPOINTINVALIDLAYER;
        }
    }

    @Override
    public void paintObject(final Graphics2D g, final Rectangle2D tileBounds, final Rectangle2D objectBounds, final double zoomFactor,
            final double scaleFactor) {
        doPaint(g, objectBounds, scaleFactor, zoomFactor, LINEWIDTH, createArrowHeadShape(ARROWSIZE));
    }

    @Override
    public void paintObjectAsThumbnail(final Graphics2D g, final Rectangle2D objectBounds, final double zoomFactor, final double scaleFactor) {
        doPaint(g, objectBounds, scaleFactor, zoomFactor, LINEWIDTHTHUMB, createArrowHeadShape(ARROWSIZETHUMB));
    }

    private void doPaint(final Graphics2D g, final Rectangle2D objectBounds, final double scaleFactor, final double zoomFactor,
            final float lineWidth, Shape arrowHead) {
        if (valid || !isToggleEnabled(HIDE_INVALIDS_TOGGLE)) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final Point2D positionMm = getPosition(scaleFactor);

            final double startXmm = positionMm.getX() - xval * scaleFactor * MULTIPLICATION_FACTOR / 2.0;
            final double startYmm = positionMm.getY() - yval * scaleFactor * MULTIPLICATION_FACTOR / 2.0;
            final double endXmm = positionMm.getX() + xval * scaleFactor * MULTIPLICATION_FACTOR / 2.0;
            final double endYmm = positionMm.getY() + yval * scaleFactor * MULTIPLICATION_FACTOR / 2.0;

            final Point startPx =
                    DP2DCoordinateUtils.convertASMLToScreenCoordinates(new Point2D.Double(startXmm, startYmm), objectBounds, zoomFactor);
            final Point endPx = DP2DCoordinateUtils.convertASMLToScreenCoordinates(new Point2D.Double(endXmm, endYmm), objectBounds, zoomFactor);

            final double angle = FastMath.atan2(endPx.y - startPx.y, endPx.x - startPx.x);

            // take some margin underneath the arrowhead, so that you don't see the line end protruding from underneath the arrow tip.
            final int endX = (int) (endPx.x - ARROWSIZE / 2 * FastMath.cos(angle));
            final int endY = (int) (endPx.y - ARROWSIZE / 2 * FastMath.sin(angle));

            if (valid) {
                g.setColor(getValueManager().getColorForValue(getScalarValue()));
            } else {
                g.setColor(FLYER_COLOR);
            }

            g.setStroke(new BasicStroke(lineWidth));
            g.drawLine(startPx.x, startPx.y, endX, endY);

            final AffineTransform rotate = AffineTransform.getRotateInstance(angle);
            arrowHead = rotate.createTransformedShape(arrowHead);

            final double awidth = arrowHead.getBounds().getWidth();
            final double aheight = arrowHead.getBounds().getHeight();

            final double tx;
            final double ty;

            tx = endPx.x - awidth / 2.0 * FastMath.cos(angle);
            ty = endPx.y - aheight / 2.0 * FastMath.sin(angle);

            final AffineTransform translate = AffineTransform.getTranslateInstance(tx, ty);
            arrowHead = translate.createTransformedShape(arrowHead);

            g.fill(arrowHead);
        }
    }

    private Shape createArrowHeadShape(final int size) {
        return new RegularPolygon(0, 0, size, 3);
    }

    @Override
    public boolean isAnchorObject() {
        return false;
    }
}
