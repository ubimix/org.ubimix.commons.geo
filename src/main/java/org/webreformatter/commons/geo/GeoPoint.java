package org.webreformatter.commons.geo;

/**
 * @author kotelnikov
 */
public class GeoPoint {

    private final double fLatitude;

    private final double fLongitude;

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

    public double getLatitude() {
        return fLatitude;
    }

    public double getLongitude() {
        return fLongitude;
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