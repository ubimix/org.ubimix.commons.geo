/**
 * 
 */
package org.webreformatter.commons.geo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kotelnikov
 */
public abstract class AbstractImageTilesGenerator {

    private Map<Integer, ImageTiler> fImageTilers = new HashMap<Integer, ImageTiler>();

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

    protected void clear() {
        fImageTilers.clear();
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
            int max = maxZoomLevel;
            if (fImageZoomLevel > 0 && fImageZoomLevel < max) {
                max = fImageZoomLevel;
            }
            for (int zoom = max; zoom >= 0; zoom--) {
                final int scale = 1 << (max - zoom);
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
        for (int zoomLevel = minZoomLevel; zoomLevel <= maxZoomLevel; zoomLevel++) {
            final ImageTiler tiler = getImageTiler(zoomLevel);
            final double scale = getImageBlockScale(zoomLevel);
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
                        targetX = (long) (-sourceX / scale);
                        sourceX = 0;
                    }
                    if (sourceY < 0) {
                        targetY = (long) (-sourceY / scale);
                        sourceY = 0;
                    }
                    long sourceWidth = (int) Math.min(
                        imageSize.getX() - sourceX,
                        Math.min(sourceTileSize, imageSize.getX() - sourceX));
                    long sourceHeight = (int) Math.min(
                        imageSize.getY() - sourceY,
                        Math.min(sourceTileSize, imageSize.getY() - sourceY));
                    long targetWidth = (long) Math.min(
                        targetTileSize,
                        sourceWidth / scale);
                    long targetHeight = (long) Math.min(
                        targetTileSize,
                        sourceHeight / scale);

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

    /**
     * @param zoomLevel
     * @return
     */
    protected double getImageBlockScale(int zoomLevel) {
        double result = 1 << Math.abs(fImageZoomLevel - zoomLevel);
        if (fImageZoomLevel < zoomLevel) {
            result = 1 / result;
        }
        return result;
    }

    public ImageTiler getImageTiler(int zoomLevel) {
        ImageTiler tiler = fImageTilers.get(zoomLevel);
        if (tiler == null) {
            double scale = getImageBlockScale(zoomLevel);
            int sourceTileSize = (int) (fTileSize * scale);
            tiler = new ImageTiler(
                sourceTileSize,
                fPinPointGeo,
                fPinPoint,
                zoomLevel);
            fImageTilers.put(zoomLevel, tiler);
        }
        return tiler;
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
        clear();
    }

    public void setPinPoint(ImagePoint pinPoint) {
        fPinPoint = pinPoint != null ? pinPoint : new ImagePoint(0, 0);
        clear();
    }

    public void setPinPointGeo(GeoPoint pinPointGeo) {
        fPinPointGeo = pinPointGeo != null ? pinPointGeo : new GeoPoint(0, 0);
        clear();
    }

    public void setScreenSize(ImagePoint screenSize) {
        fScreenSize = screenSize;
        clear();
    }

    public void setTileSize(int tileSize) {
        fTileSize = tileSize;
        clear();
    }
}
