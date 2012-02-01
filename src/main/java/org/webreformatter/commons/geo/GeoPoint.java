package org.webreformatter.commons.geo;

/**
 * @author kotelnikov
 */
public class GeoPoint {

    private final double fLatitude;

    private final double fLongitude;

    private final double R = 6371; // km

    public GeoPoint(double longitude, double latitude) {
        fLongitude = GeoUtils.checkLongitude(longitude);
        fLatitude = GeoUtils.checkLatitutde(latitude);
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
        return fLongitude == o.fLongitude && fLatitude == o.fLatitude;
    }

    // http://www.movable-type.co.uk/scripts/latlong.html
    public double getBearing(GeoPoint point) {
        double lat1 = getLatitudeRad();
        double lat2 = point.getLatitudeRad();
        double dLon = point.getLongitudeRad() - getLongitudeRad();
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1)
            * Math.sin(lat2)
            - Math.sin(lat1)
            * Math.cos(lat2)
            * Math.cos(dLon);
        double brng = Math.atan2(y, x);
        return brng;
    }

    // http://www.movable-type.co.uk/scripts/latlong.html
    public double getDistance(GeoPoint point) {
        double lat1 = getLatitudeRad();
        double lon1 = getLongitudeRad();
        double lat2 = point.getLatitudeRad();
        double lon2 = point.getLongitudeRad();
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.sin(dLat / 2)
            * Math.sin(dLat / 2)
            + Math.sin(dLon / 2)
            * Math.sin(dLon / 2)
            * Math.cos(lat1)
            * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    public double getLatitude() {
        return fLatitude;
    }

    public double getLatitudeRad() {
        return Math.toRadians(fLatitude);
    }

    public double getLongitude() {
        return fLongitude;
    }

    public double getLongitudeRad() {
        return Math.toRadians(fLongitude);
    }

    // http://www.movable-type.co.uk/scripts/latlong.html
    public GeoPoint getPoint(double bearing, double distance) {
        double lat1 = getLatitudeRad();
        double lon1 = getLongitudeRad();
        double lat2 = Math.asin(Math.sin(lat1)
            * Math.cos(distance / R)
            + Math.cos(lat1)
            * Math.sin(distance / R)
            * Math.cos(bearing));
        double lon2 = lon1
            + Math.atan2(
                Math.sin(bearing) * Math.sin(distance / R) * Math.cos(lat1),
                Math.cos(distance / R) - Math.sin(lat1) * Math.sin(lat2));
        lon2 = Math.toDegrees(lon2);
        lat2 = Math.toDegrees(lat2);
        return new GeoPoint(lon2, lat2);
    }

    @Override
    public int hashCode() {
        long bitsLong = Double.doubleToLongBits(fLongitude);
        int hashLong = (int) (bitsLong ^ (bitsLong >>> 32));
        long bitsLat = Double.doubleToLongBits(fLatitude);
        int hashLat = (int) (bitsLat ^ (bitsLat >>> 32));
        return hashLong ^ hashLat;
    }

    @Override
    public String toString() {
        return fLongitude + ":" + fLatitude;
    }
}