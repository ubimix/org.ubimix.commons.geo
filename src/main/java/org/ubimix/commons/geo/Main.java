/**
 * 
 */
package org.ubimix.commons.geo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ubimix.commons.geo.ImageTilesGenerator.FileTileWriter;
import org.ubimix.commons.geo.ImageTilesGenerator.TileFormat;

/**
 * @author kotelnikov
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: ");
            System.out.println(">app param1=value1 param2=value2 ...");
            System.out.println("Parameters: ");
            System.out
                .println(" - url               - URL of the image to split to tiles");
            System.out
                .println(" - dir (optional)    - the root directory for all generated tiles; default: './tmp'");
            System.out
                .println(" - image (optional)  - the name of the image directory");
            System.exit(-1);
        }
        new Main(args).run();
    }

    private String fImageUrl;

    private File fOutputDir;

    private Map<String, String> fParameters = new LinkedHashMap<String, String>();

    /**
     * @throws Exception
     */
    public Main(String... args) throws IOException {
        initParams(args);
        String dir = getParameter("dir", "./tmp");
        fOutputDir = new File(dir);
        String str = getParameter("url", null);
        if (str == null) {
            throw new IllegalArgumentException("Image URL is not defined.");
        }
        fImageUrl = str;
    }

    protected TileFormat getFormat() {
        TileFormat format = null;
        String str = getParameter("format", null);
        if (str == null) {
            int idx = fImageUrl.lastIndexOf(".");
            if (idx >= 0) {
                str = fImageUrl.substring(idx + 1);
                format = TileFormat.fromString(str, null);
            }
        }
        if (format == null) {
            format = TileFormat.fromString(str, TileFormat.JPG);
        }
        return format;
    }

    protected String getImageName(String name) {
        int idx = name.lastIndexOf('/');
        if (idx > 0) {
            name = name.substring(idx + 1);
        }
        idx = name.indexOf('?');
        if (idx >= 0) {
            name = name.substring(0, idx);
        }
        idx = name.indexOf('#');
        if (idx >= 0) {
            name = name.substring(0, idx);
        }
        return name;
    }

    private String getParameter(String key, String defaultValue) {
        String value = fParameters.get(key);
        if (value == null) {
            value = System.getProperty(key);
        }
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    protected void initParams(String... args) {
        for (int i = 0; i < args.length;) {
            String arg = args[i++];
            int idx = arg.indexOf('=');
            String key = arg;
            String value = null;
            if (idx > 0) {
                key = arg.substring(0, idx);
                value = arg.substring(idx + 1);
            }
            fParameters.put(key, value);
        }
    }

    protected InputStream openImageStream(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        InputStream input = url.openStream();
        return input;
    }

    public void println(Object obj) {
        System.out.println(obj);
    }

    public void run() throws IOException {
        String dirName = getParameter("image", getImageName(fImageUrl + ""));
        int idx = dirName.lastIndexOf(".");
        if (idx > 0) {
            dirName = dirName.substring(0, idx);
        }

        File outputDir = new File(fOutputDir, dirName);
        println("Loading " + fImageUrl + "...");
        InputStream input = openImageStream(fImageUrl);
        BufferedImage image = ImageTilesGenerator.readImage(input);
        println("Image was successfully loaded.");

        GeoPoint pinPointGeo = new GeoPoint(0, 0);
        ImagePoint pinPoint = new ImagePoint(0, 0);
        ImagePoint screenSize = new ImagePoint(200, 300);

        int maxZoom = 18;
        int imageZoomLevel = maxZoom - 1;
        ImageTilesGenerator generator = new ImageTilesGenerator(image);
        generator.setPinPoint(pinPoint);
        generator.setPinPointGeo(pinPointGeo);
        generator.setImageZoomLevel(imageZoomLevel);
        generator.setScreenSize(screenSize);
        TileFormat format = getFormat();
        generator.setTileFormat(format);
        generator.setBackgroundColor(Color.WHITE);

        println("Splitting the image to tiles.");
        println("Output directory: " + outputDir);

        final File rootDir = outputDir;
        final int[] minZoom = { maxZoom };
        generator.generateTiles(maxZoom, new FileTileWriter(rootDir) {
            @Override
            public void onTile(
                TileInfo tile,
                BufferedImage tileImage,
                TileFormat tileFormat) {
                if (minZoom[0] > tile.getZoom()) {
                    minZoom[0] = tile.getZoom();
                }
                println("Tile: " + tile.getTilePath(tileFormat.toString()));
                super.onTile(tile, tileImage, tileFormat);
            }
        });
        ImageTiler tiler = generator.getImageTiler(imageZoomLevel);
        String json = TilesPrintUtil.toJson(
            image,
            tiler,
            maxZoom,
            minZoom[0],
            "id",
            "\"" + dirName + "\"",
            "title",
            "\"" + dirName + "\"",
            "url",
            "\"" + fImageUrl + "\"",
            "width",
            image.getWidth(),
            "height",
            image.getHeight(),
            "format",
            "\"" + format + "\"");
        println(json);
        println("Done.");
    }

}
