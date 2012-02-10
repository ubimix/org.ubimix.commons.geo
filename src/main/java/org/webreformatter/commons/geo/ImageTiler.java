/**
 * 
 */
package org.webreformatter.commons.geo;

/**
 * This is an utility class used to translate image width and heights in image
 * coordinates.
 * 
 * @author kotelnikov
 */
public class ImageTiler {

    private GeoPoint fBottomLeftGeo;

    private double fImageScale;

    private ImagePoint fImageSize;

    public ImageTiler(GeoPoint bottomLeft, GeoPoint topRight, double imageScale) {
        fImageScale = imageScale;
        GeoPoint distance = bottomLeft.getDistanceXY(topRight);
        long width = Math.abs(Math.round(distance.getX() / fImageScale));
        long height = Math.abs(Math.round(distance.getY() / fImageScale));
        fImageSize = new ImagePoint(width, height);
        fBottomLeftGeo = bottomLeft;
    }

    public ImageTiler(GeoPoint bottomLeft, ImagePoint size, double imageScale) {
        fImageSize = size;
        fBottomLeftGeo = bottomLeft;
        fImageScale = imageScale;
    }

    /**
     * @param imageWidth the width of the image
     * @param imageHeight the height of the image
     * @param imageScale meters in one image pixel
     */
    public ImageTiler(
        GeoPoint bottomLeft,
        int imageWidth,
        int imageHeight,
        double imageScale) {
        this(bottomLeft, new ImagePoint(imageWidth, imageHeight), imageScale);
    }

    public ImagePoint getBottomLeft() {
        return new ImagePoint(0, 0);
    }

    public GeoPoint getBottomLeftPos() {
        return fBottomLeftGeo;
    }

    public TileInfo getBottomLeftTile(int zoomLevel) {
        return new TileInfo(getBottomLeftPos(), zoomLevel);
    }

    public ImagePoint getBottomRight() {
        return new ImagePoint(fImageSize.getX(), 0);
    }

    public GeoPoint getBottomRightPos() {
        ImagePoint point = getBottomRight();
        return getGeoPosition(point);
    }

    public GeoPoint getGeoPosition(ImagePoint point) {
        ImagePoint bottomLeft = getBottomLeft();
        double distance = bottomLeft.getDistance(point);
        distance *= fImageScale;
        double bearing = bottomLeft.getBearing(point);
        GeoPoint geoPosition = fBottomLeftGeo.getPoint(bearing, distance);
        return geoPosition;
    }

    public long getImageHeight() {
        return fImageSize.getY();
    }

    /**
     * Translates the specified geographical position to a point on the image.
     * 
     * @param currentPoint the current geographical position which should be
     *        translated to the point on the image
     * @return the position of the geographical point
     */
    public ImagePoint getImagePosition(GeoPoint currentPoint) {
        double bearing = fBottomLeftGeo.getBearing(currentPoint);
        double distance = fBottomLeftGeo.getDistance(currentPoint);
        distance /= fImageScale;
        ImagePoint imageBottomLeft = getBottomLeft();
        ImagePoint result = imageBottomLeft.getPoint(bearing, distance);
        return result;
    }

    public double getImageScale() {
        return fImageScale;
    }

    public ImagePoint getImageSize() {
        return fImageSize;
    }

    public long getImageWidth() {
        return fImageSize.getX();
    }

    public ImagePoint getTopLeft() {
        return new ImagePoint(0, fImageSize.getY());
    }

    public GeoPoint getTopLeftPos() {
        ImagePoint point = getTopLeft();
        GeoPoint geoPos = getGeoPosition(point);
        return geoPos;
    }

    public ImagePoint getTopRight() {
        return fImageSize;
    }

    public GeoPoint getTopRightPos() {
        ImagePoint point = getTopRight();
        GeoPoint geoPos = getGeoPosition(point);
        return geoPos;
    }

    public TileInfo getTopRightTile(int zoomLevel) {
        return new TileInfo(getTopRightPos(), zoomLevel);
    }

}
