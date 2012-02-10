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

    private double fImageScale;

    private ImagePoint fImageSize;

    private GeoPoint fTopLeftGeo;

    public ImageTiler(GeoPoint topLeft, GeoPoint bottomRight, double imageScale) {
        GeoPoint min = GeoPoint.min(topLeft, bottomRight);
        GeoPoint max = GeoPoint.max(topLeft, bottomRight);
        fImageScale = imageScale;
        GeoPoint distance = min.getDistanceXY(max);
        double w = distance.getX() / fImageScale;
        long width = Math.abs(Math.round(w));

        double h = distance.getY() / fImageScale;
        long height = Math.abs(Math.round(h));
        fImageSize = new ImagePoint(width, height);
        fTopLeftGeo = min;
    }

    public ImageTiler(GeoPoint bottomLeft, ImagePoint size, double imageScale) {
        fImageSize = size;
        fTopLeftGeo = bottomLeft;
        fImageScale = imageScale;
    }

    /**
     * @param imageWidth the width of the image
     * @param imageHeight the height of the image
     * @param imageScale meters in one image pixel
     */
    public ImageTiler(
        GeoPoint topLeft,
        int imageWidth,
        int imageHeight,
        double imageScale) {
        this(topLeft, new ImagePoint(imageWidth, imageHeight), imageScale);
    }

    public ImagePoint getBottomLeft() {
        return new ImagePoint(0, fImageSize.getY());
    }

    public GeoPoint getBottomLeftPos() {
        ImagePoint point = getBottomLeft();
        GeoPoint geoPos = getGeoPosition(point);
        return geoPos;
    }

    public TileInfo getBottomLeftTile(int zoomLevel) {
        return new TileInfo(getBottomLeftPos(), zoomLevel);
    }

    public ImagePoint getBottomRight() {
        return fImageSize;
    }

    public GeoPoint getBottomRightPos() {
        ImagePoint point = getBottomRight();
        return getGeoPosition(point);
    }

    public TileInfo getBottomRightTile(int zoomLevel) {
        return new TileInfo(getBottomRightPos(), zoomLevel);
    }

    public GeoPoint getGeoPosition(ImagePoint point) {
        ImagePoint topLeft = getTopLeft();
        double factor = 1;
        if (point.getX() == topLeft.getX()) {
            if (point.getY() > topLeft.getY()) {
                factor = -1;
            }
        }
        double distance = topLeft.getDistance(point);
        distance *= fImageScale;
        distance *= factor;
        double bearing = topLeft.getBearing(point);
        GeoPoint geoPosition = fTopLeftGeo.getPoint(bearing, distance);
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
        double bearing = fTopLeftGeo.getBearing(currentPoint);
        double distance = fTopLeftGeo.getDistance(currentPoint);
        distance /= fImageScale;
        ImagePoint imageBottomLeft = getTopLeft();
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
        return new ImagePoint(0, 0);
    }

    public GeoPoint getTopLeftPos() {
        ImagePoint point = getTopLeft();
        return getGeoPosition(point);
    }

    public TileInfo getTopLeftTile(int zoomLevel) {
        return new TileInfo(getTopLeftPos(), zoomLevel);
    }

    public ImagePoint getTopRight() {
        return new ImagePoint(fImageSize.getX(), 0);
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
