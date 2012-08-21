/**
 * 
 */
package org.webreformatter.commons.geo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.webreformatter.commons.geo.ImageTilesGenerator.ITileImageListener;

/**
 * @author kotelnikov
 */
public class ImageTilesGeneratorTest extends TestCase {

    /**
     * @param name
     */
    public ImageTilesGeneratorTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        String name;
        name = "flower.jpg";

        String dirName = name;
        int idx = dirName.lastIndexOf(".");
        if (idx > 0) {
            dirName = dirName.substring(0, idx);
        }
        File outputDir = new File("./tmp", dirName);
        // outputDir = new File("../../../GeoTest/abc");

        InputStream input = getClass().getResourceAsStream("/" + name);
        BufferedImage image = ImageTilesGenerator.readImage(input);

        GeoPoint pinPointGeo = new GeoPoint(48.86709, 2.33535);
        ImagePoint pinPoint = new ImagePoint(10, 10);
        ImagePoint screenSize = new ImagePoint(400, 500);

        int imageZoomLevel = 18;
        int minZoom = 1;
        int maxZoom = imageZoomLevel;
        ImageTilesGenerator generator = new ImageTilesGenerator(
            image,
            pinPointGeo,
            pinPoint,
            imageZoomLevel,
            screenSize);

        final File rootDir = outputDir;
        generator.generateTiles(minZoom, maxZoom, new ITileImageListener() {
            @Override
            public void onTile(TileInfo tile, BufferedImage tileImage) {
                String type = "png";
                // type = "jpg";
                String path = tile.getTilePath(type);
                File tileFile = new File(rootDir, path);
                System.out.println("Writing tile: " + tileFile);
                tileFile.getParentFile().mkdirs();
                try {
                    FileOutputStream out = new FileOutputStream(tileFile);
                    ImageTilesGenerator.writeImage(tileImage, out, type);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

}
