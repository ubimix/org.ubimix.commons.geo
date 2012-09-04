package org.ubimix.commons.geo;

/**
 * A geographic point containing longitude/latitude values.
 * 
 * @author kotelnikov
 */
public class GeoPoint {

    /**
     * Calculates and returns geographic distance by X and Y coordinates.
     * 
     * @param first the first geo point
     * @param second the second geo point
     * @return distance by X and Y coordinates between the specified geographic
     *         points
     */
    public static GeoPoint getDistanceXY(GeoPoint first, GeoPoint second) {
        double minLon = Math.min(first.getLongitude(), second.getLongitude());
        double maxLon = Math.max(first.getLongitude(), second.getLongitude());
        double minLat = Math.min(first.getLatitude(), second.getLatitude());
        double maxLat = Math.max(first.getLatitude(), second.getLatitude());
        double width = GeoUtils.getDistance(minLat, minLon, minLat, maxLon);
        double height = GeoUtils.getDistance(minLat, minLon, maxLat, minLon);
        return new GeoPoint(height, width);
    }

    public static GeoPoint max(GeoPoint a, GeoPoint b) {
        return new GeoPoint(
            Math.max(a.getLatitude(), b.getLatitude()),
            Math.max(a.getLongitude(), b.getLongitude()));
    }

    public static GeoPoint min(GeoPoint a, GeoPoint b) {
        return new GeoPoint(
            Math.min(a.getLatitude(), b.getLatitude()),
            Math.min(a.getLongitude(), b.getLongitude()));
    }

    public static GeoPoint newPoint(String lat, String lon) {
        GeoPoint result = null;
        try {
            double a = Double.parseDouble(lat);
            double b = Double.parseDouble(lon);
            result = new GeoPoint(a, b);
        } catch (Throwable t) {
            // Just ignore it
        }
        return result;
    }

    private final double fX;

    private final double fY;

    /**
     * @param latitude - latitude / "Y" coordinate
     * @param longitude - longitude / "X" coordinate
     */
    public GeoPoint(double latitude, double longitude) {
        fX = longitude;
        fY = latitude;
    }

    public GeoPoint checkGeoCoordinates() {
        double lat = GeoUtils.checkLatitutde(fY);
        double lon = GeoUtils.checkLongitude(fX);
        return new GeoPoint(lat, lon);
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
            getLatitude(),
            getLongitude(),
            point.getLatitude(),
            point.getLongitude());
    }

    // http://www.movable-type.co.uk/scripts/latlong.html
    public double getDistance(GeoPoint point) {
        return GeoUtils.getDistance(
            getLatitude(),
            getLongitude(),
            point.getLatitude(),
            point.getLongitude());
    }

    public GeoPoint getDistanceXY(GeoPoint point) {
        double deltaLon = GeoUtils.getDistance(fY, fX, fY, point.fX);
        double deltaLat = GeoUtils.getDistance(fY, fX, point.fY, fX);
        return new GeoPoint(deltaLat, deltaLon);
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
            getLatitude(),
            getLongitude(),
            bearing,
            distance);
        return new GeoPoint(coords[1], coords[0]);
    }

    public double getX() {
        return fX;
    }

    public double getY() {
        return fY;
    }

    @Override
    public int hashCode() {
        long bitsLat = Double.doubleToLongBits(fY);
        int hashLat = (int) (bitsLat ^ (bitsLat >>> 32));
        long bitsLong = Double.doubleToLongBits(fX);
        int hashLong = (int) (bitsLong ^ (bitsLong >>> 32));
        return hashLat ^ hashLong;
    }

    public GeoPoint setLatitudeFrom(GeoPoint point) {
        return new GeoPoint(point.fY, fX);
    }

    public GeoPoint setLongitudeFrom(GeoPoint point) {
        return new GeoPoint(fY, point.fX);
    }

    @Override
    public String toString() {
        return fY + ";" + fX;
    }
}