/**
 * 
 */
package org.webreformatter.commons.geo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String testPath = GeoUtils.getTilePath(latitude, longitude, zoom);
        assertEquals(zoom + "/" + tileX + "/" + tileY + ".png", testPath);
    }

    public void testGeoPoint() {
        double longitude = 11.01296;
        double latitude = 49.60055;
        GeoPoint point = new GeoPoint(latitude, longitude);
        assertEquals(longitude, point.getLongitude());
        assertEquals(latitude, point.getLatitude());
    }

    public void testImagePoint() {
        GeoPoint first;
        GeoPoint second;

        first = new GeoPoint(0, 0);
        second = new GeoPoint(0, 0);
        testImagePoints(first, second);
        testImagePoints(second, first);

        first = new GeoPoint(2, -2);
        second = new GeoPoint(0, 0);
        testImagePoints(first, second);
        testImagePoints(second, first);

        first = new GeoPoint(-2, -2);
        second = new GeoPoint(2, 2);
        testImagePoints(first, second);
        testImagePoints(second, first);

        first = new GeoPoint(48.863854, 2.335904);
        second = new GeoPoint(48.866155, 2.339723);
        testImagePoints(first, second);
        testImagePoints(second, first);

        first = new GeoPoint(49.5611, 10.951);
        second = new GeoPoint(49.6282, 11.0574);
        testImagePoints(first, second);
        testImagePoints(second, first);
    }

    public void testImagePoints(GeoPoint first, GeoPoint second) {
        // Calculating the bearing and the distance between points
        // based on their geographical coordinates.
        double geoBearing = first.getBearing(second);
        double geoDistance = first.getDistance(second);

        ImagePoint imageFirst = new ImagePoint(0, 0);
        ImagePoint imageSecond = imageFirst.getPoint(geoBearing, geoDistance);

        // Calculating the bearing and distance between points using the image
        // points.
        double imgBearing = imageFirst.getBearing(imageSecond);
        double imgDistance = imageFirst.getDistance(imageSecond);

        // Bearing calculated by images should be quite close to the
        // value calculated directly from geo points.
        assertEquals(geoBearing, imgBearing, 0.002);

        // Distance calculated using geo points should be similar with
        // the distance defined by the image.
        assertEquals(geoDistance, imgDistance, 1); // One meter

        // Distance by longitude and by latitude (in meters)
        // should be the same as the size of the image
        // because the image scale is one meter in one pixel.
        double deltaLon = first.getDistance(first.setLongitudeFrom(second));
        double deltaLat = first.getDistance(first.setLatitudeFrom(second));
        double errorLon = deltaLon / 100;
        double errorLat = deltaLat / 100;
        assertEquals(deltaLon, Math.abs(imageSecond.getX()), errorLon);
        assertEquals(deltaLat, Math.abs(imageSecond.getY()), errorLat);

        // Find the original image position using the bearing and distance
        // values found from geographical positions.
        double invertedBearing = second.getBearing(first);
        ImagePoint imageTest = imageSecond.getPoint(
            invertedBearing,
            geoDistance);
        assertEquals(imageFirst.getX(), imageTest.getX(), errorLon);
        assertEquals(imageFirst.getY(), imageTest.getY(), errorLat);
    }

    public void testImageTiler() {
        GeoPoint first;
        GeoPoint second;
        double scale = 1; // 1 meters in one pixel

        first = new GeoPoint(-2, -2);
        second = new GeoPoint(2, 2);
        testTiler(first, second, 12, scale);

        first = new GeoPoint(48.863854, 2.335904);
        second = new GeoPoint(48.866155, 2.339723);
        testTiler(first, second, 18, scale);

    }

    public void testTileInfo() {

        double longitude = 11.01296;
        double latitude = 49.60055;
        short zoom = 13;
        int tileX = 4346;
        int tileY = 2792;

        TileInfo tile = new TileInfo(latitude, longitude, zoom);

        testTileInfo(tile, tileX, tileY, zoom);
        assertEquals(tile, new TileInfo(tileY, tileX, zoom));
        assertTrue(tile.in(latitude, longitude));

        {
            TileInfo nextTile = tile.getNextTile(1, 1);
            GeoPoint bottomLeft = nextTile.getTopLeftCoordinates();
            assertTrue(bottomLeft.getLongitude() > longitude);
            assertTrue(bottomLeft.getLatitude() < latitude);
        }

        {
            TileInfo nextTile = tile.getNextTile(1, 1);
            GeoPoint bottomLeft = nextTile.getTopLeftCoordinates();
            double bottomLeftLon = bottomLeft.getLongitude();
            double bottomLeftLat = bottomLeft.getLatitude();
            assertTrue(bottomLeftLon > longitude);
            assertTrue(bottomLeftLat < latitude);

            GeoPoint topRight = tile.getBottomRightCoordinates();
            assertEquals(
                bottomLeft.getLatitude(),
                topRight.getLatitude(),
                0.001);
            assertEquals(
                bottomLeft.getLongitude(),
                topRight.getLongitude(),
                0.001);
        }
        GeoPoint point = new GeoPoint(48.863854, 2.335904);
        TileInfo top = new TileInfo(point, zoom);
        TileInfo tile1 = top.getNextTile(3, 3);
        TileInfo tile2 = top.getNextTile(4, 3);
        System.out.println(tile1 + " - " + tile2);

        int beginLine = 3;
        int endLine = 4;
        int beginCol = 3;
        int endCol = 3;
        Set<TileInfo> set = new HashSet<TileInfo>();
        for (int l = beginLine; l <= endLine; l++) {
            for (int c = beginCol; c <= endCol; c++) {
                tile = top.getNextTile(l, c);
                assertFalse(set.contains(tile));
                set.add(tile);
            }
        }
    }

    public void testTileInfo(TileInfo tile, int tileX, int tileY, short zoom) {
        assertEquals(tileX, tile.getX());
        assertEquals(tileY, tile.getY());
        assertEquals(zoom, tile.getZoom());
        assertEquals(
            zoom + "/" + tileX + "/" + tileY + ".png",
            tile.getTilePath());
    }

    public void testTileInfo1() {
        GeoPoint point = new GeoPoint(48.86709, 2.33535);
        for (int zoom = 1; zoom <= 18; zoom++) {
            TileInfo tile = new TileInfo(point, zoom);
            assertTrue(tile.contains(point));

            GeoPoint topLeft = tile.getTopLeftCoordinates();
            GeoPoint bottomRight = tile.getBottomRightCoordinates();
            assertTrue(topLeft.getLongitude() <= point.getLongitude());
            assertTrue(bottomRight.getLongitude() >= point.getLongitude());

            assertTrue(bottomRight.getLatitude() < topLeft.getLatitude());
            assertTrue(topLeft.getLatitude() >= point.getLatitude());
            assertTrue(bottomRight.getLatitude() <= point.getLatitude());

        }
    }

    public void testTiler(
        GeoPoint first,
        GeoPoint second,
        int zoomLevel,
        double scale) {
        final ImageTiler tiler = new ImageTiler(first, second, scale);
        assertEquals(0, tiler.getTopLeft().getX());
        assertEquals(0, tiler.getTopLeft().getY());
        assertTrue(tiler.getTopRight().getX() > 0);
        assertEquals(0, tiler.getTopRight().getY());
        assertEquals(0, tiler.getBottomLeft().getX());
        assertTrue(tiler.getBottomLeft().getY() > 0);
        assertTrue(tiler.getBottomRight().getX() > 0);
        assertTrue(tiler.getBottomRight().getY() > 0);

        ImagePoint tileNumbers = TileInfo.getTileNumber(
            first,
            second,
            zoomLevel);
        System.out.println("======================================");

        String msg = String.format(
            "Download %d (%d x %d) tiles for zoom level %d ...",
            tileNumbers.getX() * tileNumbers.getY(),
            tileNumbers.getX(),
            tileNumbers.getY(),
            zoomLevel);
        System.out.println(msg);

        System.out.println("Image params: "
            + tiler.getImageWidth()
            + " x "
            + tiler.getImageHeight());

        TileInfo bottomLeftTile = tiler.getBottomLeftTile(zoomLevel);
        TileInfo topRightTile = tiler.getTopRightTile(zoomLevel);

        System.out.println("============ Geo Boxes ==============");
        System.out.println("Inbounding box: ["
            + topRightTile.getTopLeftCoordinates()
            + "] - ["
            + bottomLeftTile.getBottomRightCoordinates()
            + "]");
        System.out.println("Outbounding box: ["
            + topRightTile.getBottomRightCoordinates()
            + "] - ["
            + bottomLeftTile.getTopLeftCoordinates()
            + "]");
        System.out.println("============ Image Boxes ==============");
        System.out
            .println("Inbounding box: ["
                + tiler.getImagePosition(bottomLeftTile
                    .getBottomRightCoordinates())
                + "] - ["
                + tiler.getImagePosition(topRightTile.getTopLeftCoordinates())
                + "]");
        System.out.println("Outbounding box: ["
            + tiler.getImagePosition(bottomLeftTile.getTopLeftCoordinates())
            + "] - ["
            + tiler.getImagePosition(topRightTile.getBottomRightCoordinates())
            + "]");
        System.out.println("============ Tiles ==============");

        TilesLoader loader = new TilesLoader();
        loader.load(
            first,
            second,
            zoomLevel,
            zoomLevel,
            new TilesLoader.LoadListener() {
                @Override
                public void onTile(TileInfo tile) {
                    GeoPoint bottomLeft = tile.getTopLeftCoordinates();
                    GeoPoint topRight = tile.getBottomRightCoordinates();
                    ImagePoint imageBottomLeft = tiler
                        .getImagePosition(bottomLeft);
                    ImagePoint imageTopRight = tiler.getImagePosition(topRight);
                    System.out.println("["
                        + imageBottomLeft
                        + " - "
                        + imageTopRight
                        + "] Size: "
                        + imageBottomLeft.getDistanceXY(imageTopRight));
                }
            });
    }

    public void testTilesLoader() {
        TilesLoader loader = new TilesLoader();
        GeoPoint first = new GeoPoint(49.5611, 10.951);
        GeoPoint second = new GeoPoint(49.6282, 11.0574);
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
        loader.load(first, second, 13, 14, new TilesLoader.LoadListener() {
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
