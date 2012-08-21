/**
 * 
 */
package org.webreformatter.commons.geo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kotelnikov
 */
public class TilesLoader {

    /**
     * @author kotelnikov
     */
    public static class DownloadListener extends LoadListener {
        private final static Logger log = Logger
            .getLogger(DownloadListener.class.getName());

        private File fBaseDir;

        private String fBaseUrl;

        public DownloadListener() {
            this(new File("./"));
        }

        public DownloadListener(File baseDir) {
            this("http://tile.openstreetmap.org/", baseDir);
        }

        public DownloadListener(String baseUrl, File baseDir) {
            fBaseUrl = baseUrl;
            fBaseDir = baseDir;
        }

        @Override
        public void begin(
            TileInfo minTile,
            TileInfo maxTile,
            GeoPoint min,
            GeoPoint max) {
            ImagePoint tileNumbers = TileInfo.getTileNumber(minTile, maxTile);
            String msg = String.format(
                "Download %d (%d x %d) tiles for zoom level %d ...",
                tileNumbers.getX() * tileNumbers.getY(),
                tileNumbers.getY(),
                tileNumbers.getX(),
                minTile.getZoom());
            println(msg);

            tileNumbers = TileInfo.getTileNumber(min, max, minTile.getZoom());
            msg = String.format(
                "Real area contains %d (%d x %d) tiles.",
                tileNumbers.getX() * tileNumbers.getY(),
                tileNumbers.getY(),
                tileNumbers.getX());
            println(msg);
        }

        protected void copy(String url, File file) {
            try {
                file.getParentFile().mkdirs();
                URL u = new URL(url);
                InputStream input = u.openStream();
                try {
                    OutputStream out = new FileOutputStream(file);
                    try {
                        byte[] buf = new byte[1024 * 10];
                        int len;
                        while ((len = input.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                    } finally {
                        out.close();
                    }
                } finally {
                    input.close();
                }
            } catch (Throwable t) {
                handleError("Can not download a resource. URL: " + url, t);
            }

        }

        protected File getFile(String path) {
            return new File(fBaseDir, path);
        }

        protected String getUrl(String path) {
            return fBaseUrl + path;
        }

        private void handleError(String msg, Throwable e) {
            log.log(Level.WARNING, msg, e);
        }

        @Override
        public void onTile(TileInfo tile) {
            String path = tile.getTilePath();
            String url = getUrl(path);
            File file = getFile(path);
            print("Copy '" + url + "' to '" + file + "'... ");
            copy(url, file);
            println("OK");
        }

        protected void print(String msg) {
            System.out.print(msg);
        }

        protected void println(String msg) {
            print(msg + "\n");
        }
    }

    /**
     * @author kotelnikov
     */
    public interface ILoadListener {

        void begin(
            TileInfo minTile,
            TileInfo maxTile,
            GeoPoint min,
            GeoPoint max);

        void end(TileInfo minTile, TileInfo maxTile, GeoPoint min, GeoPoint max);

        void onTile(TileInfo tile);
    }

    /**
     * @author kotelnikov
     */
    public static class LoadListener implements ILoadListener {

        public void begin(
            TileInfo minTile,
            TileInfo maxTile,
            GeoPoint min,
            GeoPoint max) {
        }

        public void end(
            TileInfo minTile,
            TileInfo maxTile,
            GeoPoint min,
            GeoPoint max) {
        }

        public void onTile(TileInfo tile) {
        }

    }

    private static int TILE_SIZE_IN_PIXELS = 256;

    private GeoPoint fFirst;

    private int fScreenAreaHeight;

    private int fScreenAreaWidth;

    private GeoPoint fSecond;

    private int fZoomMax;

    private int fZoomMin;

    /**
     * @param first
     * @param second
     */
    public TilesLoader(GeoPoint first, GeoPoint second, int minZoom, int maxZoom) {
        this(first, second, minZoom, maxZoom, -1, -1);
    }

    /**
     * @param first
     * @param second
     * @param screenAreaWidth
     * @param screenAreaHeight
     */
    public TilesLoader(
        GeoPoint first,
        GeoPoint second,
        int minZoom,
        int maxZoom,
        int screenAreaWidth,
        int screenAreaHeight) {
        fZoomMin = minZoom;
        fZoomMax = maxZoom;
        fFirst = first;
        fSecond = second;
        fScreenAreaWidth = screenAreaWidth;
        fScreenAreaHeight = screenAreaHeight;
    }

    private int getTilesNumber(int w) {
        int num = (TILE_SIZE_IN_PIXELS - 1 + w) / TILE_SIZE_IN_PIXELS;
        return num;
    }

    public void load(ILoadListener listener) {
        GeoPoint first = fFirst;
        GeoPoint second = fSecond;
        GeoPoint min = GeoPoint.min(first, second);
        GeoPoint max = GeoPoint.max(first, second);
        first = new GeoPoint(max.getLatitude(), min.getLongitude());
        second = new GeoPoint(min.getLatitude(), max.getLongitude());
        for (int zoom = Math.min(fZoomMin, fZoomMax); zoom <= Math.max(
            fZoomMin,
            fZoomMax); zoom++) {
            TileInfo firstTile = new TileInfo(first, zoom);
            TileInfo secondTile = new TileInfo(second, zoom);

            int yMin = firstTile.getY();
            int yMax = secondTile.getY();
            int xMin = firstTile.getX();
            int xMax = secondTile.getX();
            int deltaX = Math.max(fScreenAreaWidth
                - (xMax - xMin)
                * TILE_SIZE_IN_PIXELS, 0);
            int deltaY = Math.max(fScreenAreaHeight
                - (yMax - yMin)
                * TILE_SIZE_IN_PIXELS, 0);
            if (deltaX > 0 || deltaY > 0) {
                int dX = getTilesNumber((deltaX + 1) / 2);
                int dY = getTilesNumber((deltaY + 1) / 2);
                firstTile = firstTile.getNextTile(-dY, -dX);
                secondTile = secondTile.getNextTile(dY, dX);
                yMin = firstTile.getY();
                yMax = secondTile.getY();
                xMin = firstTile.getX();
                xMax = secondTile.getX();
            }

            listener.begin(firstTile, secondTile, first, second);
            for (int x = xMin; x <= xMax; x++) {
                for (int y = yMin; y <= yMax; y++) {
                    TileInfo tile = new TileInfo(y, x, zoom);
                    listener.onTile(tile);
                }
            }
            listener.end(firstTile, secondTile, first, second);
        }
    }

    @Deprecated
    public void load(int minZoom, int maxZoom, ILoadListener listener) {
        fZoomMin = minZoom;
        fZoomMax = maxZoom;
        load(listener);
    }

    @Deprecated
    public void load(
        ZoomLevel minZoom,
        ZoomLevel maxZoom,
        ILoadListener listener) {
        fZoomMax = minZoom.getLevel();
        fZoomMax = maxZoom.getLevel();
        load(listener);
    }

}
