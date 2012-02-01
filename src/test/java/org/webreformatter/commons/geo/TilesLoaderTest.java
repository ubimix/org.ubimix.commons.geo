/**
 * 
 */
package org.webreformatter.commons.geo;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class TilesLoaderTest extends TestCase {

    /**
     * @param name
     */
    public TilesLoaderTest(String name) {
        super(name);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void test() throws Exception {
        double longitude = 11.01296;
        double latitude = 49.60055;
        short zoom = 13;
        int tileX = 4346;
        int tileY = 2792;

        assertEquals(tileX, GeoUtils.getTileXByLongitude(longitude, zoom));
        assertEquals(tileY, GeoUtils.getTileYByLatitude(latitude, zoom));
        String testPath = GeoUtils.getTilePath(longitude, latitude, zoom);
        assertEquals(zoom + "/" + tileX + "/" + tileY + ".png", testPath);
    }

    public void testGeoPoint() {
        double longitude = 11.01296;
        double latitude = 49.60055;
        GeoPoint point = new GeoPoint(longitude, latitude);
        assertEquals(longitude, point.getLongitude());
        assertEquals(latitude, point.getLatitude());
    }

    public void testTileInfo() {
        double longitude = 11.01296;
        double latitude = 49.60055;
        short zoom = 13;
        int tileX = 4346;
        int tileY = 2792;

        TileInfo tile = new TileInfo(longitude, latitude, zoom);
        testTileInfo(tile, tileX, tileY, zoom);
        assertEquals(tile, new TileInfo(tileX, tileY, zoom));
        assertTrue(tile.in(longitude, latitude));
    }

    public void testTileInfo(TileInfo tile, int tileX, int tileY, short zoom) {
        assertEquals(tileX, tile.getX());
        assertEquals(tileY, tile.getY());
        assertEquals(zoom, tile.getZoom());
        assertEquals(
            zoom + "/" + tileX + "/" + tileY + ".png",
            tile.getTilePath());
    }

    public void testTilesLoader() {
        TilesLoader loader = new TilesLoader();
        GeoPoint min = new GeoPoint(10.951, 49.5611);
        GeoPoint max = new GeoPoint(11.0574, 49.6282);
        String[] tiles = {
            "13/4345/2791.png",
            "13/4345/2792.png",
            "13/4345/2793.png",
            "13/4346/2791.png",
            "13/4346/2792.png",
            "13/4346/2793.png",
            "13/4347/2791.png",
            "13/4347/2792.png",
            "13/4347/2793.png",
            "14/8690/5582.png",
            "14/8690/5583.png",
            "14/8690/5584.png",
            "14/8690/5585.png",
            "14/8690/5586.png",
            "14/8690/5587.png",
            "14/8691/5582.png",
            "14/8691/5583.png",
            "14/8691/5584.png",
            "14/8691/5585.png",
            "14/8691/5586.png",
            "14/8691/5587.png",
            "14/8692/5582.png",
            "14/8692/5583.png",
            "14/8692/5584.png",
            "14/8692/5585.png",
            "14/8692/5586.png",
            "14/8692/5587.png",
            "14/8693/5582.png",
            "14/8693/5583.png",
            "14/8693/5584.png",
            "14/8693/5585.png",
            "14/8693/5586.png",
            "14/8693/5587.png",
            "14/8694/5582.png",
            "14/8694/5583.png",
            "14/8694/5584.png",
            "14/8694/5585.png",
            "14/8694/5586.png",
            "14/8694/5587.png",
            "14/8695/5582.png",
            "14/8695/5583.png",
            "14/8695/5584.png",
            "14/8695/5585.png",
            "14/8695/5586.png",
            "14/8695/5587.png" };
        final List<String> list = new ArrayList<String>();
        loader.load(min, max, 13, 14, new TilesLoader.LoadListener() {
            @Override
            public void onTile(TileInfo tile) {
                list.add(tile.getTilePath());
            }
        });
        assertEquals(tiles.length, list.size());
        for (int i = 0; i < tiles.length; i++) {
            String control = tiles[i];
            String test = list.get(i);
            assertEquals(control, test);
        }

    }
}
