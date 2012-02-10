/**
 * 
 */
package org.webreformatter.commons.geo;

/**
 * @author kotelnikov
 */
public class TileInfo {

    private final int fX;

    private final int fY;

    private final int fZoom;

    public TileInfo(double longitude, double latitude, int zoom) {
        this(GeoUtils.getTileXByLongitude(longitude, zoom), GeoUtils
            .getTileYByLatitude(latitude, zoom), zoom);
    }

    public TileInfo(GeoPoint point, int zoom) {
        this(point.getLongitude(), point.getLatitude(), zoom);
    }

    /**
     * 
     */
    public TileInfo(int x, int y, int zoom) {
        fX = x;
        fY = y;
        fZoom = zoom;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TileInfo)) {
            return false;
        }
        TileInfo o = (TileInfo) obj;
        return fX == o.fX && fY == o.fY && fZoom == o.fZoom;
    }

    public GeoPoint getBottomLeftCoordinates() {
        return new GeoPoint(getLongitude(), getLatitude());
    }

    private double getDistance(int deltaX, int deltaY, int zoom) {
        double lon1 = GeoUtils.getTileLongitudeByX(fX, zoom);
        double lat1 = GeoUtils.getTileLatitudeByY(fY, zoom);
        double lon2 = GeoUtils.getTileLongitudeByX(fX + deltaX, zoom);
        double lat2 = GeoUtils.getTileLatitudeByY(fY + deltaY, zoom);
        return GeoUtils.getDistance(lon1, lat1, lon2, lat2);
    }

    /**
     * @return the width of this tile (in kilometers)
     */
    public double getHeight() {
        double distance = getDistance(0, 1, fZoom);
        return distance;
    }

    public double getLatitude() {
        return GeoUtils.getTileLatitudeByY(fY, fZoom);
    }

    public double getLongitude() {
        return GeoUtils.getTileLongitudeByX(fX, fZoom);
    }

    public TileInfo getNewZoom(int zoom) {
        return new TileInfo(getLongitude(), getLatitude(), zoom);
    }

    public TileInfo getNextTile(int deltaX, int deltaY) {
        return getNextTile(deltaX, deltaY, fZoom);
    }

    public TileInfo getNextTile(int deltaX, int deltaY, int zoom) {
        double longitude = GeoUtils.getTileLongitudeByX(fX + deltaX, zoom);
        double latitude = GeoUtils.getTileLatitudeByY(fY + deltaY, zoom);
        return new TileInfo(longitude, latitude, zoom);
    }

    /**
     * @return the next horizontal tile
     */
    public TileInfo getNextX() {
        return getNextTile(1, 0, fZoom);
    }

    /**
     * @return the next vertical tile
     */
    public TileInfo getNextY() {
        return getNextTile(0, 1, fZoom);
    }

    /**
     * @return the previous horizontal tile
     */
    public TileInfo getPrevX() {
        return getNextTile(-1, 0, fZoom);
    }

    /**
     * @return the previous vertical tile
     */
    public TileInfo getPrevY() {
        return getNextTile(0, -1, fZoom);
    }

    /**
     * @param zoom the zoom level
     * @return the path to the tile containing this point
     */
    public String getTilePath() {
        return GeoUtils.getTilePath(getLongitude(), getLatitude(), fZoom);
    }

    public GeoPoint getTopRightCoordinates() {
        double lon = GeoUtils.getTileLongitudeByX(fX + 1, fZoom);
        double lat = GeoUtils.getTileLatitudeByY(fY - 1, fZoom);
        return new GeoPoint(lon, lat);
    }

    /**
     * @return the width of this tile (in kilometers)
     */
    public double getWidth() {
        double distance = getDistance(1, 0, fZoom);
        return distance;
    }

    /**
     * @return the X position of this tile
     */
    public int getX() {
        return fX;
    }

    /**
     * @return the Y position of this tile
     */
    public int getY() {
        return fY;
    }

    /**
     * Returns the zoom level of this tile
     * 
     * @return the zoom level of this tile
     */
    public int getZoom() {
        return fZoom;
    }

    @Override
    public int hashCode() {
        return fX ^ fY ^ fZoom;
    }

    /**
     * Verifies if the specified point is on this tile.
     * 
     * @param longitude the longitude of the point to check
     * @param latitude the latitude of the point to check
     * @return <code>true</code> if the specified geographic point is in this
     *         tile
     */
    public boolean in(double longitude, double latitude) {
        double longMin = GeoUtils.getTileLongitudeByX(fX, fZoom);
        double longMax = GeoUtils.getTileLongitudeByX(fX + 1, fZoom);
        if (longitude < longMin || longitude > longMax) {
            return false;
        }
        double latMin = GeoUtils.getTileLatitudeByY(fY + 1, fZoom);
        double latMax = GeoUtils.getTileLatitudeByY(fY, fZoom);
        if (latitude < latMin || latitude > latMax) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return fX + ":" + fY + ":" + fZoom;
    }

}
