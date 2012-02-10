package org.webreformatter.commons.geo;

/**
 * @author kotelnikov
 */
public class ImagePoint {

    public static ImagePoint getDistanceXY(ImagePoint first, ImagePoint second) {
        long deltaX = Math.abs(first.getX() - second.getX());
        long deltaY = Math.abs(first.getY() - second.getY());
        return new ImagePoint(deltaX, deltaY);
    }

    public static double getImageBearing(long deltaX, long deltaY) {
        // double bearing = Math.atan(deltaX / deltaY);
        double bearing = Math.atan2(deltaX, deltaY);
        return bearing;
    }

    public static long getImageDistance(double distance, double scale) {
        double value = distance / scale;
        long result = Math.round(value);
        return result;
    }

    public static double getImageScale(int imageLength, double geoDistance) {
        return geoDistance / imageLength;
    }

    private final long fX;

    private final long fY;

    /**
     * @param x
     * @param y
     */
    public ImagePoint(long x, long y) {
        fX = x;
        fY = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ImagePoint)) {
            return false;
        }
        ImagePoint o = (ImagePoint) obj;
        return fX == o.fX && fY == o.fY;
    }

    public double getBearing(ImagePoint point) {
        double bearing = getImageBearing(point.fX - fX, point.fY - fY);
        return bearing;
    }

    public double getDistance(ImagePoint point) {
        double distance = Math.sqrt(Math.pow(point.fX - fX, 2)
            + Math.pow(point.fY - fY, 2));
        return Math.abs(distance);
    }

    public ImagePoint getDistanceXY(ImagePoint point) {
        return getDistanceXY(this, point);
    }

    public ImagePoint getPoint(double bearing, double distance) {
        double h = Math.cos(bearing) * distance;
        double w = Math.sin(bearing) * distance;
        long x = fX + Math.round(w);
        long y = fY + Math.round(h);
        ImagePoint result = new ImagePoint(x, y);
        return result;
    }

    public long getX() {
        return fX;
    }

    public long getY() {
        return fY;
    }

    @Override
    public int hashCode() {
        int hashX = (int) (fX ^ (fX >>> 32));
        int hashY = (int) (fY ^ (fY >>> 32));
        return hashX ^ hashY;
    }

    @Override
    public String toString() {
        return fX + ":" + fY;
    }
}