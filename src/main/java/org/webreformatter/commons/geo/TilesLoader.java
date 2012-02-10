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
        public void begin(GeoPoint min, GeoPoint max, int zoom) {
            ImagePoint tileNumbers = TileInfo.getTileNumber(min, max, zoom);
            String msg = String.format(
                "Download %d (%d x %d) tiles for zoom level %d ...",
                tileNumbers.getX() * tileNumbers.getY(),
                tileNumbers.getX(),
                tileNumbers.getY(),
                zoom);
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

        void begin(GeoPoint min, GeoPoint max, int zoom);

        void end(GeoPoint min, GeoPoint max, int zoom);

        void onTile(TileInfo tile);
    }

    /**
     * @author kotelnikov
     */
    public static class LoadListener implements ILoadListener {

        public void begin(GeoPoint min, GeoPoint max, int zoom) {
        }

        public void end(GeoPoint min, GeoPoint max, int zoom) {
        }

        public void onTile(TileInfo tile) {
        }

    }

    /**
     * 
     */
    public TilesLoader() {
    }

    public void load(
        GeoPoint min,
        GeoPoint max,
        int minZoom,
        int maxZoom,
        ILoadListener listener) {
        GeoPoint a = min;
        GeoPoint b = max;
        min = GeoPoint.min(a, b);
        max = GeoPoint.max(a, b);
        for (int zoom = minZoom; zoom <= maxZoom; zoom++) {
            TileInfo minTile = new TileInfo(min, zoom);
            TileInfo maxTile = new TileInfo(max, zoom);
            listener.begin(min, max, zoom);
            int xMin = minTile.getX();
            int xMax = maxTile.getX();
            int yMin = maxTile.getY();
            int yMax = minTile.getY();
            for (int x = xMin; x <= xMax; x++) {
                for (int y = yMin; y <= yMax; y++) {
                    TileInfo tile = new TileInfo(x, y, zoom);
                    listener.onTile(tile);
                }
            }
            listener.end(min, max, zoom);
        }
    }

}
