package org.webreformatter.commons.geo;

/**
 * @author kotelnikov
 */
public class GeoPoint {

    public static GeoPoint getDistanceXY(GeoPoint first, GeoPoint second) {
        double minLon = Math.min(first.getLongitude(), second.getLongitude());
        double maxLon = Math.max(first.getLongitude(), second.getLongitude());
        double minLat = Math.min(first.getLatitude(), second.getLatitude());
        double maxLat = Math.max(first.getLatitude(), second.getLatitude());
        double width = GeoUtils.getDistance(minLon, minLat, maxLon, minLat);
        double height = GeoUtils.getDistance(minLon, minLat, minLon, maxLat);
        return new GeoPoint(width, height);
    }

    public static GeoPoint max(GeoPoint a, GeoPoint b) {
        return new GeoPoint(
            Math.max(a.getLongitude(), b.getLongitude()),
            Math.max(a.getLatitude(), b.getLatitude()));
    }

    public static GeoPoint min(GeoPoint a, GeoPoint b) {
        return new GeoPoint(
            Math.min(a.getLongitude(), b.getLongitude()),
            Math.min(a.getLatitude(), b.getLatitude()));
    }

    public static GeoPoint newPoint(String lon, String lat) {
        GeoPoint result = null;
        try {
            double a = Double.parseDouble(lon);
            double b = Double.parseDouble(lat);
            result = new GeoPoint(a, b);
        } catch (Throwable t) {
            // Just ignore it
        }
        return result;
    }

    private final double fX;

    private final double fY;

    /**
     * @param longitude - longitude / "X" coordinate
     * @param latitude - latitude / "Y" coordinate
     */
    public GeoPoint(double longitude, double latitude) {
        fX = longitude;
        fY = latitude;
    }

    public GeoPoint checkGeoCoordinates() {
        double lon = GeoUtils.checkLongitude(fX);
        double lat = GeoUtils.checkLatitutde(fY);
        return new GeoPoint(lon, lat);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GeoPoint)) {
            return false;
        }
        GeoPoint o = (GeoPoint) obj;
        return fX == o.fX && fY == o.fY;
    }

    // http://www.movable-type.co.uk/scripts/latlong.html
    public double getBearing(GeoPoint point) {
        return GeoUtils.getBearing(
            getLongitude(),
            getLatitude(),
            point.getLongitude(),
            point.getLatitude());
    }

    // http://www.movable-type.co.uk/scripts/latlong.html
    public double getDistance(GeoPoint point) {
        return GeoUtils.getDistance(
            getLongitude(),
            getLatitude(),
            point.getLongitude(),
            point.getLatitude());
    }

    public GeoPoint getDistanceXY(GeoPoint point) {
        double deltaLon = GeoUtils.getDistance(fX, fY, point.fX, fY);
        double deltaLat = GeoUtils.getDistance(fX, fY, fX, point.fY);
        return new GeoPoint(deltaLon, deltaLat);
    }

    public double getLatitude() {
        return fY;
    }

    public double getLatitudeRad() {
        return Math.toRadians(fY);
    }

    public double getLongitude() {
        return fX;
    }

    public double getLongitudeRad() {
        return Math.toRadians(fX);
    }

    // http://www.movable-type.co.uk/scripts/latlong.html
    public GeoPoint getPoint(double bearing, double distance) {
        double[] coords = GeoUtils.getPoint(
            getLongitude(),
            getLatitude(),
            bearing,
            distance);
        return new GeoPoint(coords[0], coords[1]);
    }

    public double getX() {
        return fX;
    }

    public double getY() {
        return fY;
    }

    @Override
    public int hashCode() {
        long bitsLong = Double.doubleToLongBits(fX);
        int hashLong = (int) (bitsLong ^ (bitsLong >>> 32));
        long bitsLat = Double.doubleToLongBits(fY);
        int hashLat = (int) (bitsLat ^ (bitsLat >>> 32));
        return hashLong ^ hashLat;
    }

    public GeoPoint setLatitudeFrom(GeoPoint point) {
        return new GeoPoint(fX, point.fY);
    }

    public GeoPoint setLongitudeFrom(GeoPoint point) {
        return new GeoPoint(point.fX, fY);
    }

    @Override
    public String toString() {
        return fX + ":" + fY;
    }
}