/**
 * 
 */
package org.webreformatter.commons.geo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

/**
 * @author kotelnikov
 */
public class ImageTilesGenerator extends AbstractImageTilesGenerator {

    public static class FileTileWriter implements ITileImageListener {

        private final static Logger log = Logger.getLogger(FileTileWriter.class
            .getName());

        private File fRootDir;

        public FileTileWriter(File rootDir) {
            fRootDir = rootDir;
        }

        protected void handleError(String msg, Throwable t) {
            log.log(Level.WARNING, msg, t);
        }

        @Override
        public void onTile(
            TileInfo tile,
            BufferedImage tileImage,
            TileFormat tileFormat) {
            String type = tileFormat.toString();
            String path = tile.getTilePath(type);
            File tileFile = new File(fRootDir, path);
            tileFile.getParentFile().mkdirs();
            try {
                FileOutputStream out = new FileOutputStream(tileFile);
                try {
                    ImageIO.write(tileImage, type, out);
                } finally {
                    out.close();
                }
            } catch (Throwable t) {
                handleError("Can not write tite '"
                    + tile
                    + "' in the file '"
                    + tileFile
                    + "'.", t);
            }
        }
    }

    public interface ITileImageListener {

        void onTile(
            TileInfo tile,
            BufferedImage tileImage,
            TileFormat tileFormat);

    }

    public static enum TileFormat {

        JPG("jpg"), PNG("png");

        private String fExt;

        private TileFormat(String ext) {
            fExt = ext;
        }

        @Override
        public String toString() {
            return fExt;
        }
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

    private Color fBackgroundColor;

    private BufferedImage fImage;

    private ITileImageListener fListener;

    private TileFormat fTileFormat = TileFormat.JPG;

    private Image fTileTemplate;

    public ImageTilesGenerator(BufferedImage image) {
        fImage = image;
    }

    @Override
    protected void copyTile(
        TileInfo tile,
        int sourceTileSize,
        int targetTileSize,
        ImagePoint sourceLeftTop,
        ImagePoint sourceBottomRight,
        ImagePoint targetLeftTop,
        ImagePoint targetBottomRight) {
        BufferedImage tileImage = newEmptyTile();
        Graphics2D g = tileImage.createGraphics();
        try {
            if (targetLeftTop.getX() != targetBottomRight.getX()
                && targetLeftTop.getY() != targetBottomRight.getY()) {
                g.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(
                    RenderingHints.KEY_ALPHA_INTERPOLATION,
                    RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                g.setComposite(AlphaComposite.SrcOver);
                ImageObserver observer = null;
                g.drawImage(
                    fImage,
                    (int) targetLeftTop.getX(),
                    (int) targetLeftTop.getY(),
                    (int) targetBottomRight.getX(),
                    (int) targetBottomRight.getY(),
                    (int) sourceLeftTop.getX(),
                    (int) sourceLeftTop.getY(),
                    (int) sourceBottomRight.getX(),
                    (int) sourceBottomRight.getY(),
                    observer);
            }
        } finally {
            g.dispose();
        }
        fListener.onTile(tile, tileImage, fTileFormat);
    }

    public TilesStat generateTiles(
        int minZoomLevel,
        int maxZoomLevel,
        final ITileImageListener listener) {
        fListener = listener;
        try {
            return generateTiles(minZoomLevel, maxZoomLevel, new ImagePoint(
                fImage.getHeight(),
                fImage.getWidth()));
        } finally {
            fListener = null;
        }
    }

    public TilesStat generateTiles(
        int maxZoomLevel,
        final ITileImageListener listener) {
        fListener = listener;
        try {
            return generateTiles(
                maxZoomLevel,
                new ImagePoint(fImage.getHeight(), fImage.getWidth()));
        } finally {
            fListener = null;
        }
    }

    public TilesStat generateTiles(final ITileImageListener listener) {
        fListener = listener;
        try {
            return generateTiles(new ImagePoint(
                fImage.getHeight(),
                fImage.getWidth()));
        } finally {
            fListener = null;
        }
    }

    public Color getBackgroundColor() {
        return fBackgroundColor;
    }

    public TileFormat getTileFormat() {
        return fTileFormat;
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
        int imageType = BufferedImage.TYPE_INT_ARGB;
        if (fTileFormat == TileFormat.JPG) {
            imageType = BufferedImage.TYPE_INT_RGB;
        }
        BufferedImage tile = new BufferedImage(fTileSize, fTileSize, imageType);
        Color canvasColor = getBackgroundColor();
        Graphics2D g = tile.createGraphics();
        try {
            if (canvasColor != null) {
                g.setColor(canvasColor);
                g.fillRect(0, 0, fTileSize, fTileSize);
            } else if (fTileFormat == TileFormat.PNG) {
                Image template = getTileTemplate();
                final Graphics2D g2 = tile.createGraphics();
                g2.drawImage(template, 0, 0, null);
            }
        } finally {
            g.dispose();
        }
        return tile;
    }

    public void setBackgroundColor(Color backgroundColor) {
        fBackgroundColor = backgroundColor;
    }

    public void setTileFormat(TileFormat tileFormat) {
        fTileFormat = tileFormat != null ? tileFormat : TileFormat.JPG;
    }

}
