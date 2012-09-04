/**
 * 
 */
package org.ubimix.commons.geo;

/**
 * This is an utility class used to transform an image in a set of geographic
 * tiles.
 * 
 * @author kotelnikov
 */
public class ImageTiler {

    /**
     * Default tile size.
     */
    public static int DEFAULT_TILE_SIZE = 256;

    /**
     * The tile containing the top left corner of the image.
     */
    private TileInfo fFirstTile;

    /**
     * Image position of the tile containing the top left corner of the image.
     * This position could have negative X and/or Y coordinates in the case when
     * tiles are not aligned to image corners.
     */
    private ImagePoint fFirstTilePosition;

    /**
     * The "scale" of the image - how much meters are in one pixel. This is a
     * calculated value.
     */
    private double fImageScale = 0;

    /**
     * "Pin point" on the image corresponding to a specific geographic
     * coordinate.
     */
    private final ImagePoint fPinPoint;

    /**
     * Geographic coordinates of a "pin point" on the image.
     */
    private final GeoPoint fPinPointGeo;

    /**
     * The size (in pixels) of an individual tile.
     */
    private final int fTileSize;

    /**
     * Zoom level of the image. Possible values are 1..18.
     */
    private final int fZoomLevel;

    /**
     * This constructor is used to define a pin point on the image with the
     * corresponding geographic position. This constructor defines the default
     * size of image tiles - 256x256.
     * 
     * @param pinPointGeo the geographic position of the pin point on the image
     * @param pinPoint position of the pin point on the image
     * @param zoomLevel the zoom level
     */
    public ImageTiler(GeoPoint pinPointGeo, ImagePoint pinPoint, int zoomLevel) {
        this(DEFAULT_TILE_SIZE, pinPointGeo, pinPoint, zoomLevel);
    }

    /**
     * This constructor is used to define tiles aligned to the corner of the
     * image.
     * 
     * @param zoomLevel the zoom level
     */
    public ImageTiler(int zoomLevel) {
        this(new GeoPoint(0, 0), new ImagePoint(0, 0), zoomLevel);
    }

    /**
     * This constructor is used to define a pin point on the image, the
     * corresponding geographic position, zoom level of the image and the tile
     * of individual tiles.
     * 
     * @param tileSize size of tiles
     * @param pinPointGeo geographic position of the pin point on the image
     * @param pinPoint position of the pin point on the image
     * @param zoomLevel image zoom level
     */
    public ImageTiler(
        int tileSize,
        GeoPoint pinPointGeo,
        ImagePoint pinPoint,
        int zoomLevel) {
        fPinPoint = pinPoint;
        fPinPointGeo = pinPointGeo;
        fZoomLevel = zoomLevel;
        fTileSize = tileSize;
    }

    /**
     * Returns the tile containing the start point of the image.
     * 
     * @return the tile containing the start point of the image
     */
    public TileInfo getFirstTile() {
        if (fFirstTile == null) {
            ImagePoint startPoint = new ImagePoint(0, 0);
            fFirstTile = getTile(startPoint);
        }
        return fFirstTile;
    }

    /**
     * Returns position of the first tile on the image. Note that the returned
     * position could have negative X and/or Y coordinates in the case when
     * tiles are not aligned to the image corners.
     * 
     * @return position of the first tile on the image
     */
    public ImagePoint getFirstTilePosition() {
        if (fFirstTilePosition == null) {
            TileInfo tile = getFirstTile();
            GeoPoint topLeftGeo = tile.getTopLeftCoordinates();
            fFirstTilePosition = getImagePosition(topLeftGeo);
        }
        return fFirstTilePosition;
    }

    /**
     * Returns a geographic position of the specified point on the image
     * 
     * @param point the point on the image
     * @return the geographic position corresponding to the specified point on
     *         the image
     */
    public GeoPoint getGeoPosition(ImagePoint point) {
        double distance = fPinPoint.getDistance(point);
        double d = Math.sqrt(Math.pow(fTileSize, 2) + Math.pow(fTileSize, 2));
        distance = (distance * getImageScale()) / d;
        double bearing = fPinPoint.getBearing(point);
        GeoPoint geoPosition = fPinPointGeo.getPoint(bearing, distance);
        return geoPosition;
    }

    /**
     * Translates the specified geographical position to a point on the image.
     * 
     * @param currentPoint the current geographical position which should be
     *        translated to the point on the image
     * @return the position of the geographical point
     */
    public ImagePoint getImagePosition(GeoPoint currentPoint) {
        double bearing = fPinPointGeo.getBearing(currentPoint);
        double distance = fPinPointGeo.getDistance(currentPoint);
        double d = Math.sqrt(Math.pow(fTileSize, 2) + Math.pow(fTileSize, 2));
        distance = (distance * d) / getImageScale();
        ImagePoint result = fPinPoint.getPoint(bearing, distance);
        return result;
    }

    /**
     * Returns the scale of the image - how much meters are in one image pixel.
     * 
     * @return the scale of the image - how much meters are in one image pixel
     */
    private double getImageScale() {
        if (fImageScale == 0) {
            TileInfo tile = new TileInfo(fPinPointGeo, fZoomLevel);
            GeoPoint bottomRight = tile.getBottomRightCoordinates();
            GeoPoint topLeft = tile.getTopLeftCoordinates();
            fImageScale = bottomRight.getDistance(topLeft);
        }
        return fImageScale;
    }

    /**
     * This method translates a position on the image to the tile.
     * 
     * @param point a point on the image
     * @return the tile containing this point
     */
    public TileInfo getTile(ImagePoint point) {
        GeoPoint startGeoPoint = getGeoPosition(point);
        TileInfo firstTile = new TileInfo(startGeoPoint, fZoomLevel);
        return firstTile;
    }

    /**
     * Returns position of the specified tile on the image. Note that the
     * returned position could have negative X and/or Y coordinates in the case
     * when tiles are not aligned to the image corners.
     * 
     * @param tile the image tile
     * @return the position of the beginning of the tile
     */
    public ImagePoint getTilePosition(TileInfo tile) {
        TileInfo firstTile = getFirstTile();
        ImagePoint firstTilePosition = getFirstTilePosition();
        long y = firstTilePosition.getY()
            + (tile.getY() - firstTile.getY())
            * fTileSize;
        long x = firstTilePosition.getX()
            + (tile.getX() - firstTile.getX())
            * fTileSize;
        ImagePoint result = new ImagePoint(y, x);
        return result;
    }

    /**
     * Returns the size of tiles in pixels.
     * 
     * @return the size of tiles
     */
    public long getTileSize() {
        return fTileSize;
    }

    /**
     * Returns an {@link TilesLoader} object iterating over all tiles covering
     * an image of the specified size.
     * 
     * @param imageSize the size of the image
     * @return an {@link TilesLoader} object iterating over all tiles covering
     *         an image of the specified size
     */
    public TilesLoader getTilesLoader(ImagePoint imageSize) {
        return getTilesLoader(imageSize, null);
    }

    /**
     * Returns an {@link TilesLoader} object iterating over all tiles covering
     * an image of the specified size.
     * 
     * @param imageSize the size of the image
     * @param screenSize the size of the screen
     * @return an {@link TilesLoader} object iterating over all tiles covering
     *         an image of the specified size
     */
    public TilesLoader getTilesLoader(
        ImagePoint imageSize,
        ImagePoint screenSize) {
        TileInfo firstTile = getFirstTile();
        TileInfo lastTile = getTile(imageSize);
        GeoPoint topLeftGeo = firstTile.getTopLeftCoordinates();
        GeoPoint bottomRightGeo = lastTile.getTopLeftCoordinates();
        TilesLoader loader = screenSize != null ? new TilesLoader(
            topLeftGeo,
            bottomRightGeo,
            fZoomLevel,
            fZoomLevel,
            (int) screenSize.getX(),
            (int) screenSize.getY()) : new TilesLoader(
            topLeftGeo,
            bottomRightGeo,
            fZoomLevel,
            fZoomLevel);
        return loader;
    }

}
