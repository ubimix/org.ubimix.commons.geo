/**
 * 
 */
package org.webreformatter.commons.geo;

/**
 * @author kotelnikov
 */
public abstract class AbstractImageTilesGenerator {

    private int fImageZoomLevel;

    private ImagePoint fPinPoint = new ImagePoint(0, 0);

    private GeoPoint fPinPointGeo = new GeoPoint(0, 0);

    private ImagePoint fScreenSize;

    protected int fTileSize = 256;

    /**
     * 
     */
    public AbstractImageTilesGenerator() {
    }

    protected abstract void copyTile(
        TileInfo tile,
        int sourceTileSize,
        int targetTileSize,
        ImagePoint sourceLeftTop,
        ImagePoint sourceBottomRight,
        ImagePoint targetLeftTop,
        ImagePoint targetBottomRight);

    protected void generateTiles(ImagePoint imageSize) {
        generateTiles(getImageZoomLevel(), imageSize);
    }

    protected void generateTiles(int maxZoomLevel, ImagePoint imageSize) {
        int minZoomLevel = maxZoomLevel;
        ImagePoint screenSize = getScreenSize();
        if (screenSize != null) {
            for (int zoom = maxZoomLevel; zoom >= 0; zoom--) {
                final int scale = 1 << (maxZoomLevel - zoom);
                long newImageWidth = imageSize.getX() / scale;
                long newImageHeight = imageSize.getY() / scale;
                if (newImageWidth <= screenSize.getX()
                    || newImageHeight <= screenSize.getY()) {
                    break;
                }
                minZoomLevel = zoom;
            }
        }
        generateTiles(minZoomLevel, maxZoomLevel, imageSize);
    }

    protected void generateTiles(
        int minZoomLevel,
        int maxZoomLevel,
        final ImagePoint imageSize) {
        int maxLevel = Math.max(
            Math.max(fImageZoomLevel, minZoomLevel),
            maxZoomLevel);
        for (int zoomLevel = minZoomLevel; zoomLevel <= maxZoomLevel; zoomLevel++) {
            final int scale = 1 << (maxLevel - zoomLevel);
            int sourceTileSize = fTileSize * scale;
            final ImageTiler tiler = new ImageTiler(
                sourceTileSize,
                fPinPointGeo,
                fPinPoint,
                zoomLevel);
            TilesLoader loader = tiler.getTilesLoader(imageSize, fScreenSize);
            loader.load(new TilesLoader.LoadListener() {
                @Override
                public void onTile(TileInfo tile) {
                    ImagePoint position = tiler.getTilePosition(tile);
                    int targetTileSize = fTileSize;
                    int sourceTileSize = (int) tiler.getTileSize();
                    long sourceX = (int) position.getX();
                    long sourceY = (int) position.getY();
                    long targetX = 0;
                    long targetY = 0;
                    if (sourceX < 0) {
                        targetX = -sourceX / scale;
                        sourceX = 0;
                    }
                    if (sourceY < 0) {
                        targetY = -sourceY / scale;
                        sourceY = 0;
                    }
                    long sourceWidth = (int) Math.min(
                        imageSize.getX() - sourceX,
                        Math.min(sourceTileSize, imageSize.getX() - sourceX));
                    long sourceHeight = (int) Math.min(
                        imageSize.getY() - sourceY,
                        Math.min(sourceTileSize, imageSize.getY() - sourceY));
                    long targetWidth = Math.min(targetTileSize, sourceWidth
                        / scale);
                    long targetHeight = Math.min(targetTileSize, sourceHeight
                        / scale);

                    ImagePoint targetLeftTop = new ImagePoint(targetY, targetX);
                    ImagePoint targetBottomRight = new ImagePoint(targetY
                        + targetHeight, targetX + targetWidth);
                    ImagePoint sourceLeftTop = new ImagePoint(sourceY, sourceX);
                    ImagePoint sourceBottomRight = new ImagePoint(sourceY
                        + sourceHeight, sourceX + sourceWidth);
                    copyTile(
                        tile,
                        sourceTileSize,
                        targetTileSize,
                        sourceLeftTop,
                        sourceBottomRight,
                        targetLeftTop,
                        targetBottomRight);
                }
            });
        }
    }

    public int getImageZoomLevel() {
        return fImageZoomLevel;
    }

    public ImagePoint getPinPoint() {
        return fPinPoint;
    }

    public GeoPoint getPinPointGeo() {
        return fPinPointGeo;
    }

    public ImagePoint getScreenSize() {
        return fScreenSize;
    }

    public int getTileSize() {
        return fTileSize;
    }

    public void setImageZoomLevel(int imageZoomLevel) {
        fImageZoomLevel = imageZoomLevel;
    }

    public void setPinPoint(ImagePoint pinPoint) {
        fPinPoint = pinPoint;
    }

    public void setPinPointGeo(GeoPoint pinPointGeo) {
        fPinPointGeo = pinPointGeo;
    }

    public void setScreenSize(ImagePoint screenSize) {
        fScreenSize = screenSize;
    }

    public void setTileSize(int tileSize) {
        fTileSize = tileSize;
    }
}
