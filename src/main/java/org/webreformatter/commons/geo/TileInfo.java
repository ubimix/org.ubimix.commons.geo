/**
 * 
 */
package org.webreformatter.commons.geo;

/**
 * @author kotelnikov
 */
public class TileInfo {

    public static ImagePoint getTileNumber(
        GeoPoint first,
        GeoPoint second,
        int zoom) {
        GeoPoint min = GeoPoint.min(first, second);
        GeoPoint max = GeoPoint.max(first, second);
        TileInfo minTile = new TileInfo(min, zoom);
        TileInfo maxTile = new TileInfo(max, zoom);
        int xMin = minTile.getX();
        int xMax = maxTile.getX();
        int yMin = maxTile.getY();
        int yMax = minTile.getY();
        int numberXTiles = xMax - xMin + 1;
        int numberYTiles = yMax - yMin + 1;
        return new ImagePoint(numberXTiles, numberYTiles);
    }

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

    public GeoPoint getBottomRightCoordinates() {
        double lon = GeoUtils.getTileLongitudeByX(incX(1), fZoom);
        double lat = GeoUtils.getTileLatitudeByY(incY(1), fZoom);
        return new GeoPoint(lon, lat).checkGeoCoordinates();
    }

    private double getDistance(int deltaX, int deltaY, int zoom) {
        double lon1 = GeoUtils.getTileLongitudeByX(fX, zoom);
        double lat1 = GeoUtils.getTileLatitudeByY(fY, zoom);
        double lon2 = GeoUtils.getTileLongitudeByX(incX(deltaX), zoom);
        double lat2 = GeoUtils.getTileLatitudeByY(incY(deltaY), zoom);
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
        double longitude = GeoUtils.getTileLongitudeByX(incX(deltaX), zoom);
        double latitude = GeoUtils.getTileLatitudeByY(incY(deltaY), zoom);
        return new TileInfo(longitude, latitude, zoom);
    }

    /**
     * @param zoom the zoom level
     * @return the path to the tile containing this point
     */
    public String getTilePath() {
        return GeoUtils.getTilePath(getLongitude(), getLatitude(), fZoom);
    }

    public GeoPoint getTopLeftCoordinates() {
        double lon = GeoUtils.getTileLongitudeByX(incX(0), fZoom);
        double lat = GeoUtils.getTileLatitudeByY(incY(0), fZoom);
        return new GeoPoint(lon, lat).checkGeoCoordinates();
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
        double longMin = GeoUtils.getTileLongitudeByX(incX(0), fZoom);
        double longMax = GeoUtils.getTileLongitudeByX(incX(1), fZoom);
        double latMin = GeoUtils.getTileLatitudeByY(incY(0), fZoom);
        double latMax = GeoUtils.getTileLatitudeByY(incY(-1), fZoom);
        boolean lonIn = in(longMin, longMax, longitude);
        boolean latIn = in(latMin, latMax, latitude);
        return lonIn & latIn;
    }

    private <T extends Comparable<T>> boolean in(T first, T second, T point) {
        int a = point.compareTo(first);
        int b = point.compareTo(second);
        return a * b <= 0;
    }

    private int incX(int deltaX) {
        return fX + deltaX;
    }

    private int incY(int deltaY) {
        return fY - deltaY;
    }

    @Override
    public String toString() {
        return fX + ":" + fY + ":" + fZoom;
    }

}
