/**
 * 
 */
package org.webreformatter.commons.geo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import junit.framework.TestCase;

import org.webreformatter.commons.geo.AbstractImageTilesGenerator.TilesStat;
import org.webreformatter.commons.geo.ImageTilesGenerator.FileTileWriter;
import org.webreformatter.commons.geo.ImageTilesGenerator.TileFormat;

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

        InputStream input = getClass().getResourceAsStream("/" + name);
        BufferedImage image = ImageTilesGenerator.readImage(input);

        GeoPoint pinPointGeo = new GeoPoint(48.86709, 2.33535);
        ImagePoint pinPoint = new ImagePoint(10, 10);
        ImagePoint screenSize = new ImagePoint(400, 500);

        int imageZoomLevel = 18;
        int maxZoom = imageZoomLevel;
        ImageTilesGenerator generator = new ImageTilesGenerator(image);
        generator.setPinPoint(pinPoint);
        generator.setPinPointGeo(pinPointGeo);
        generator.setImageZoomLevel(imageZoomLevel);
        generator.setScreenSize(screenSize);
        // generator.setTileFormat(TileFormat.PNG);
        generator.setBackgroundColor(Color.WHITE);

        final File rootDir = outputDir;
        final int[] minZoom = { maxZoom };
        TilesStat stat = generator.generateTiles(maxZoom, new FileTileWriter(
            rootDir) {
            @Override
            public void onTile(
                TileInfo tile,
                BufferedImage tileImage,
                TileFormat tileFormat) {
                if (minZoom[0] > tile.getZoom()) {
                    minZoom[0] = tile.getZoom();
                }
                System.out.println("Tile: "
                    + tile.getTilePath(tileFormat.toString()));
                super.onTile(tile, tileImage, tileFormat);
            }
        });
        System.out.println(stat);
    }

}
