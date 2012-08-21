/**
 * 
 */
package org.webreformatter.commons.geo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

/**
 * @author kotelnikov
 */
public class ImageTilesGenerator {

    public interface ITileImageListener {

        void onTile(TileInfo tile, BufferedImage tileImage);

    }

    public static BufferedImage readImage(InputStream input) throws IOException {
        try {
            ImageInputStream imageInput = ImageIO.createImageInputStream(input);
            BufferedImage image = ImageIO.read(imageInput);
            return image;
        } finally {
            input.close();
        }
    }

    public static void writeImage(
        BufferedImage image,
        OutputStream output,
        String format) throws IOException {
        try {
            ImageIO.write(image, format, output);
        } finally {
            output.close();
        }
    }

    private BufferedImage fImage;

    private int fImageZoomLevel;

    private ImagePoint fPinPoint;

    private GeoPoint fPinPointGeo;

    private ImagePoint fScreenSize;

    private int fTileSize = 256;

    private Image fTileTemplate;

    public ImageTilesGenerator(
        BufferedImage image,
        GeoPoint pinPointGeo,
        ImagePoint pinPoint,
        int imageZoomLevel) {
        this(image, pinPointGeo, pinPoint, imageZoomLevel, null);
    }

    /**
     * 
     */
    public ImageTilesGenerator(
        BufferedImage image,
        GeoPoint pinPointGeo,
        ImagePoint pinPoint,
        int imageZoomLevel,
        ImagePoint screenSize) {
        fImage = image;
        fPinPoint = pinPoint;
        fPinPointGeo = pinPointGeo;
        fScreenSize = screenSize;
        fImageZoomLevel = imageZoomLevel;
    }

    public void generateTiles(
        int minZoomLevel,
        int maxZoomLevel,
        final ITileImageListener listener) {
        final int imageWidth = fImage.getWidth();
        final int imageHeight = fImage.getHeight();
        ImagePoint imageSize = new ImagePoint(imageHeight, imageWidth);
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
                    BufferedImage tileImage = newEmptyTile();
                    int sourceX = (int) position.getX();
                    int sourceY = (int) position.getY();
                    int targetX = 0;
                    int targetY = 0;
                    if (sourceX < 0) {
                        targetX = -sourceX / scale;
                        sourceX = 0;
                    }
                    if (sourceY < 0) {
                        targetY = -sourceY / scale;
                        sourceY = 0;
                    }
                    int sourceWidth = Math.min(
                        imageWidth - sourceX,
                        Math.min(sourceTileSize, imageWidth - sourceX));
                    int sourceHeight = Math.min(
                        imageHeight - sourceY,
                        Math.min(sourceTileSize, imageHeight - sourceY));
                    int targetWidth = Math.min(targetTileSize, sourceWidth
                        / scale);
                    int targetHeight = Math.min(targetTileSize, sourceHeight
                        / scale);

                    if (targetWidth > 0 && targetHeight > 0) {
                        Graphics2D g = tileImage.createGraphics();
                        try {
                            g.setComposite(AlphaComposite.SrcOver);
                            ImageObserver observer = null;
                            g.drawImage(
                                fImage,
                                targetX,
                                targetY,
                                targetX + targetWidth,
                                targetY + targetHeight,
                                sourceX,
                                sourceY,
                                sourceX + sourceWidth,
                                sourceY + sourceHeight,
                                observer);
                        } finally {
                            g.dispose();
                        }
                    }
                    listener.onTile(tile, tileImage);
                }
            });
        }
    }

    public Color getBackgroundColor() {
        return Color.WHITE;
    }

    private Image getTileTemplate() {
        if (fTileTemplate == null) {
            BufferedImage tileImage = new BufferedImage(
                fTileSize,
                fTileSize,
                BufferedImage.TYPE_INT_RGB);
            final ImageFilter filter = new RGBImageFilter() {
                @Override
                public final int filterRGB(
                    final int x,
                    final int y,
                    final int rgb) {
                    return 0x00FFFFFF & rgb;
                }
            };
            final ImageProducer ip = new FilteredImageSource(
                tileImage.getSource(),
                filter);
            fTileTemplate = Toolkit.getDefaultToolkit().createImage(ip);
        }
        return fTileTemplate;
    }

    private BufferedImage newEmptyTile() {
        Image img = getTileTemplate();
        BufferedImage tile = new BufferedImage(
            img.getWidth(null),
            img.getHeight(null),
            BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = tile.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        return tile;
    }

}
