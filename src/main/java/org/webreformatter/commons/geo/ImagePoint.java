package org.webreformatter.commons.geo;

/**
 * Instances of this type represent image points.
 * 
 * @author kotelnikov
 */
public class ImagePoint {

    public static ImagePoint getDistanceXY(ImagePoint first, ImagePoint second) {
        long deltaY = Math.abs(first.getY() - second.getY());
        long deltaX = Math.abs(first.getX() - second.getX());
        return new ImagePoint(deltaY, deltaX);
    }

    public static double getImageBearing(long deltaY, long deltaX) {
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
     * @param y
     * @param x
     */
    public ImagePoint(long y, long x) {
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
        double bearing = getImageBearing(fY - point.fY, point.fX - fX);
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
        double h = Math.cos(bearing) * -distance;
        double w = Math.sin(bearing) * distance;
        long y = fY + Math.round(h);
        long x = fX + Math.round(w);
        ImagePoint result = new ImagePoint(y, x);
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
        int hashY = (int) (fY ^ (fY >>> 32));
        int hashX = (int) (fX ^ (fX >>> 32));
        return hashX ^ hashY;
    }

    @Override
    public String toString() {
        return fY + ":" + fX;
    }
}