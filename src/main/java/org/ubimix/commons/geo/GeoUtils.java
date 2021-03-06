/**
 * 
 */
package org.ubimix.commons.geo;

/**
 * This class contains utility methods used to transform geographic coordinates
 * to map tiles coordinates and paths.
 * 
 * @see http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
 * @author kotelnikov
 */
public class GeoUtils {

    /**
     * Radius of the Earth in meters
     */
    private static final double R = 6371 * 1000; // in meters

    /**
     * Checks that the specified latitude is in the range
     * [-85.0511287798:85.0511287798]. If the specified value is out of range
     * then this method returns the nearest possible value.
     * 
     * @param latitude the latitude to check
     * @return a valid latitude
     */
    public static double checkLatitutde(double latitude) {
        if (latitude < -85.0511287798) {
            latitude = -85.0511287798;
        }
        if (latitude > 85.0511287798) {
            latitude = 85.0511287798;
        }
        return latitude;
    }

    /**
     * Checks that the specified longitude is in the range [-180:180]. If the
     * specified value is out of range then this method returns the nearest
     * possible value.
     * 
     * @param longitude the longitude to check
     * @return a valid longitude
     */
    public static double checkLongitude(double longitude) {
        if (longitude < -180.0) {
            longitude = -180.0;
        }
        if (longitude > 179.9999999) {
            longitude = 179.9999999;
        }
        return longitude;
    }

    /**
     * Calculates bearing between two points defined by their geo coordinates.
     * 
     * @param lat1 latitude of the first point
     * @param lon1 longitude of the first point
     * @param lat2 latitude of the second point
     * @param lon2 longitude of the second point
     * @return bearing between two points
     */
    // http://www.movable-type.co.uk/scripts/latlong.html
    public static double getBearing(
        double lat1,
        double lon1,
        double lat2,
        double lon2) {
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double dLon = Math.toRadians(lon2 - lon1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1)
            * Math.sin(lat2)
            - Math.sin(lat1)
            * Math.cos(lat2)
            * Math.cos(dLon);
        double brng = Math.atan2(y, x);
        return brng;
    }

    /**
     * Calculates geo distance (in meters) between two points defined by their
     * geographical coordinates.
     * 
     * @param lat1 latitude of the first point
     * @param lon1 longitude of the first point
     * @param lat2 latitude of the second point
     * @param lon2 longitude of the second point
     * @return distance in meters between these points
     */
    // http://www.movable-type.co.uk/scripts/latlong.html
    public static double getDistance(
        double lat1,
        double lon1,
        double lat2,
        double lon2) {
        lon1 = Math.toRadians(lon1);
        lat1 = Math.toRadians(lat1);
        lon2 = Math.toRadians(lon2);
        lat2 = Math.toRadians(lat2);
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

    /**
     * Calculates coordinates of the point B defined by coordinates of the point
     * A, bearing and distance from A and B.
     * 
     * @param lat1 the latitude of the first point (point A)
     * @param lon1 the longitude of the first point (point A)
     * @param bearing bearing from A to B
     * @param distance the distance from A to B
     * @return the coordinates of the point B (longitude / latitude)
     */
    // http://www.movable-type.co.uk/scripts/latlong.html
    public static double[] getPoint(
        double lat1,
        double lon1,
        double bearing,
        double distance) {
        lon1 = Math.toRadians(lon1);
        lat1 = Math.toRadians(lat1);
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
        return new double[] { lon2, lat2 };
    }

    /**
     * Returns the latitude of the tile containing a geo point with the
     * specified latitude. Use the {@link #zoom(double, short)} method to
     * transform the returned value to the tile number (Y coordinate).
     * 
     * @param pointLatitude the latitude of the point contained in the tile
     * @return the latitude of the tile containing the specified point
     */
    // http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#lon.2Flat_to_tile_numbers
    public static double getTileLatitude(double pointLatitude) {
        // (1 - (log(tan(lat_rad) + sec(lat_rad)) / π)) / 2
        // Where:
        // * lat_rad = ((lat_deg * π) / 360)
        // * sec == 1/cos
        double latitudeRad = Math.toRadians(pointLatitude);
        double result = (1 - Math.log(Math.tan(latitudeRad)
            + (1 / Math.cos(latitudeRad)))
            / Math.PI) / 2;
        return result;
    }

    /**
     * Returns the latitude of the tile by the Y coordinate of this tile.
     * 
     * @param tileY the Y coordinate of the tile
     * @param zoom the zoom level of the tile
     * @return the latitude of the tile by the Y coordinate of this tile
     */
    public static double getTileLatitudeByY(int tileY, int zoom) {
        double n = Math.PI - (2.0 * Math.PI * tileY) / Math.pow(2.0, zoom);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    /**
     * Returns the longitude of the tile containing a point with the specified
     * longitude. Use the {@link #zoom(double, short)} method to transform the
     * returned value to the tile number (X coordinate).
     * 
     * @param pointLongitude the longitude of the point contained in the tile
     * @return the longitude of the tile containing the specified point
     */
    // http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#lon.2Flat_to_tile_numbers
    public static double getTileLongitude(double pointLongitude) {
        // ((lon_deg + 180) / 360)
        double value = (pointLongitude + 180.0) / 360.0;
        return value;
    }

    /**
     * Returns the longitude of the tile by the X coordinate of the tile.
     * 
     * @param tileX the X coordinate of the tile
     * @param zoom the zoom level of the tile
     * @return the longitude of the tile by the X coordinate of the tile
     */
    public static double getTileLongitudeByX(int tileX, int zoom) {
        return tileX / Math.pow(2.0, zoom) * 360.0 - 180.0;
    }

    /**
     * Returns the path to the tile containing the specified geographic point.
     * 
     * @param latitude the latitude of the point
     * @param longitude the longitude of the point
     * @param zoom the zoom level of the tile
     * @return a path of the tile containing the specified geo point
     */
    public static String getTilePath(double latitude, double longitude, int zoom) {
        int x = getTileXByLongitude(longitude, zoom);
        int y = getTileYByLatitude(latitude, zoom);
        return getTilePath(y, x, zoom);
    }

    public static String getTilePath(long y, long x, int zoom) {
        return getTilePath(y, x, zoom, "png");
    }

    public static String getTilePath(long y, long x, int zoom, String ext) {
        return zoom + "/" + x + "/" + y + "." + ext;
    }

    /**
     * Returns the X coordinate of the tile containing a point with the
     * specified longitude.
     * 
     * @param longitude longitude of a point contained in the tile
     * @param zoom the zoom level of the tile
     * @return the tile X coordinate by longitude of a point contained in this
     *         tile
     */
    public static int getTileXByLongitude(double longitude, int zoom) {
        double value = getTileLongitude(longitude);
        return zoom(value, zoom);
    }

    /**
     * Returns the Y coordinate of the tile containing a point with the
     * specified latitude.
     * 
     * @param latitude latitude of a point contained in the tile
     * @param zoom the zoom level of the tile
     * @return the tile Y coordinate by latitude of a point contained in this
     *         tile
     */
    public static int getTileYByLatitude(double latitude, int zoom) {
        double value = getTileLatitude(latitude);
        return zoom(value, zoom);
    }

    /**
     * Transforms the geographic coordinates (longitude or latitude) to the
     * corresponding tile number (X or Y).
     * 
     * @param value the geographic coordinate (longitude or latitude)
     * @param zoom the zoom level of the tile
     * @return the tile number
     */
    public static int zoom(double value, int zoom) {
        return (int) (value * Math.pow(2, zoom));
    }

}
