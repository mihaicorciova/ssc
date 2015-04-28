package com.asml.wfa.metrotools.tooltotoolmatching.gui.widgets;

import java.awt.Polygon;

public class RegularPolygon extends Polygon {

    private static final long serialVersionUID = 1926669978874750590L;

    public RegularPolygon(final int x, final int y, final int r, final int vertexCount) {
        this(x, y, r, vertexCount, 0);
    }

    public RegularPolygon(final int x, final int y, final int r, final int vertexCount, final double startAngle) {
        super(getXCoordinates(x, y, r, vertexCount, startAngle), getYCoordinates(x, y, r, vertexCount, startAngle), vertexCount);
    }

    protected static int[] getXCoordinates(final int x, final int y, final int r, final int vertexCount, final double startAngle) {
        final int res[] = new int[vertexCount];
        final double addAngle = 2 * Math.PI / vertexCount;
        double angle = startAngle;
        for (int i = 0; i < vertexCount; i++) {
            res[i] = (int) Math.round(r * Math.cos(angle)) + x;
            angle += addAngle;
        }
        return res;
    }

    protected static int[] getYCoordinates(final int x, final int y, final int r, final int vertexCount, final double startAngle) {
        final int res[] = new int[vertexCount];
        final double addAngle = 2 * Math.PI / vertexCount;
        double angle = startAngle;
        for (int i = 0; i < vertexCount; i++) {
            res[i] = (int) Math.round(r * Math.sin(angle)) + y;
            angle += addAngle;
        }
        return res;
    }
}
