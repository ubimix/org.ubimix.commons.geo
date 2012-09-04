/**
 * 
 */
package org.ubimix.commons.geo;

/**
 * @author kotelnikov
 */
public class TileInfo {

    public static ImagePoint getTileNumber(
        GeoPoint first,
        GeoPoint second,
        int zoom) {
        TileInfo minTile = new TileInfo(first, zoom);
        TileInfo maxTile = new TileInfo(second, zoom);
        return getTileNumber(minTile, maxTile);
    }

    public static ImagePoint getTileNumber(TileInfo minTile, TileInfo maxTile) {
        int xMin = Math.min(minTile.getX(), maxTile.getX());
        int xMax = Math.max(minTile.getX(), maxTile.getX());
        int yMin = Math.min(minTile.getY(), maxTile.getY());
        int yMax = Math.max(minTile.getY(), maxTile.getY());
        int numberXTiles = xMax - xMin + 1;
        int numberYTiles = yMax - yMin + 1;
        return new ImagePoint(numberYTiles, numberXTiles);
    }

    private final int fX;

    private final int fY;

    private final int fZoom;

    public TileInfo(double latitude, double longitude, int zoom) {
        this(GeoUtils.getTileYByLatitude(latitude, zoom), GeoUtils
            .getTileXByLongitude(longitude, zoom), zoom);
    }

    public TileInfo(GeoPoint point, int zoom) {
        this(point.getLatitude(), point.getLongitude(), zoom);
    }

    /**
     * 
     */
    public TileInfo(int y, int x, int zoom) {
        fY = y;
        fX = x;
        fZoom = zoom;
    }

    public boolean contains(GeoPoint point) {
        return in(point.getLatitude(), point.getLongitude());
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
        return fY == o.fY && fX == o.fX && fZoom == o.fZoom;
    }

    public GeoPoint getBottomRightCoordinates() {
        double lat = GeoUtils.getTileLatitudeByY(incY(1), fZoom);
        double lon = GeoUtils.getTileLongitudeByX(incX(1), fZoom);
        return new GeoPoint(lat, lon).checkGeoCoordinates();
    }

    private double getDistance(int deltaY, int deltaX, int zoom) {
        double lat1 = GeoUtils.getTileLatitudeByY(fY, zoom);
        double lon1 = GeoUtils.getTileLongitudeByX(fX, zoom);
        double lat2 = GeoUtils.getTileLatitudeByY(incY(deltaY), zoom);
        double lon2 = GeoUtils.getTileLongitudeByX(incX(deltaX), zoom);
        return GeoUtils.getDistance(lat1, lon1, lat2, lon2);
    }

    /**
     * @return the width of this tile (in kilometers)
     */
    public double getHeight() {
        double distance = getDistance(1, 0, fZoom);
        return distance;
    }

    public double getLatitude() {
        return GeoUtils.getTileLatitudeByY(fY, fZoom);
    }

    public double getLongitude() {
        return GeoUtils.getTileLongitudeByX(fX, fZoom);
    }

    public TileInfo getNewZoom(int zoom) {
        return new TileInfo(getLatitude(), getLongitude(), zoom);
    }

    public TileInfo getNextTile(int deltaY, int deltaX) {
        return getNextTile(deltaY, deltaX, fZoom);
    }

    public TileInfo getNextTile(int deltaY, int deltaX, int zoom) {
        return new TileInfo(incY(deltaY), incX(deltaX), zoom);
    }

    /**
     * @param zoom the zoom level
     * @return the path to the tile containing this point
     */
    public String getTilePath() {
        return GeoUtils.getTilePath(fY, fX, fZoom);
    }

    /**
     * @param zoom the zoom level
     * @return the path to the tile containing this point
     */
    public String getTilePath(String fileExt) {
        return GeoUtils.getTilePath(fY, fX, fZoom, fileExt);
    }

    public GeoPoint getTopLeftCoordinates() {
        double lat = GeoUtils.getTileLatitudeByY(incY(0), fZoom);
        double lon = GeoUtils.getTileLongitudeByX(incX(0), fZoom);
        return new GeoPoint(lat, lon).checkGeoCoordinates();
    }

    /**
     * @return the width of this tile (in kilometers)
     */
    public double getWidth() {
        double distance = getDistance(0, 1, fZoom);
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
     * @param latitude the latitude of the point to check
     * @param longitude the longitude of the point to check
     * @return <code>true</code> if the specified geographic point is in this
     *         tile
     */
    public boolean in(double latitude, double longitude) {
        double longMin = GeoUtils.getTileLongitudeByX(incX(0), fZoom);
        double longMax = GeoUtils.getTileLongitudeByX(incX(1), fZoom);
        double latMin = GeoUtils.getTileLatitudeByY(incY(1), fZoom);
        double latMax = GeoUtils.getTileLatitudeByY(incY(0), fZoom);
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
        return fY + deltaY;
    }

    @Override
    public String toString() {
        return fY + ";" + fX + ";" + fZoom;
    }

}
