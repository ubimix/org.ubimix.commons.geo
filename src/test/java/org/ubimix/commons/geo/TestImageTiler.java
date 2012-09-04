package org.ubimix.commons.geo;

import junit.framework.TestCase;

public class TestImageTiler extends TestCase {

    protected static void println(String msg) {
        System.out.println(msg);
    }

    public void testImageTiler() {
        int zoomLevel = 13;
        ImagePoint size = new ImagePoint(1024, 1024);
        GeoPoint pinPointGeo = new GeoPoint(48.86709, 2.33535);
        ImagePoint pinPoint = new ImagePoint(523, 645);
        ImagePoint screenSize = new ImagePoint(400, 700);

        final ImageTiler tiler = new ImageTiler(
            pinPointGeo,
            pinPoint,
            zoomLevel);
        ImagePoint testPoint = tiler.getImagePosition(pinPointGeo);
        assertEquals(pinPoint, testPoint);
        GeoPoint testPointGeo = tiler.getGeoPosition(pinPoint);
        assertEquals(
            pinPointGeo.getLongitude(),
            testPointGeo.getLongitude(),
            1e-6);
        assertEquals(
            pinPointGeo.getLatitude(),
            testPointGeo.getLatitude(),
            1e-6);

        TilesLoader loader = tiler.getTilesLoader(size);
        loader.load(new TilesLoader.LoadListener() {
            @Override
            public void onTile(TileInfo tile) {
                ImagePoint p = tiler.getTilePosition(tile);
                System.out.println(p);
            }
        });

        // // Get the coordinates of the first tile
        // ImagePoint startPoint = new ImagePoint(0, 0);
        // GeoPoint startGeoPoint = tiler.getGeoPosition(startPoint);
        // GeoPoint lastGeoPoint = tiler.getGeoPosition(size);
        //
        // assertTrue(startGeoPoint.getLongitude() <
        // pinPointGeo.getLongitude());
        // assertTrue(startGeoPoint.getLatitude() > pinPointGeo.getLatitude());
        //
        // TileInfo firstTile = new TileInfo(startGeoPoint, zoomLevel);
        // TileInfo lastTile = new TileInfo(lastGeoPoint, zoomLevel);
        //
        // System.out.println(firstTile + " - " + lastTile);
        //
        // // Get the corner of the first tile
        // GeoPoint topLeftGeo = firstTile.getTopLeftCoordinates();
        // GeoPoint bottomRightGeo = lastTile.getBottomRightCoordinates();
        //
        // double geoBearing = pinPointGeo.getBearing(topLeftGeo);
        //
        // ImagePoint topLeft = tiler.getImagePosition(topLeftGeo);
        // assertTrue(topLeft.getX() <= pinPoint.getX());
        // assertTrue(topLeft.getY() <= pinPoint.getY());
        //
        // ImagePoint bottomRight = tiler.getImagePosition(bottomRightGeo);
        // assertTrue(bottomRight.getX() >= size.getX());
        // assertTrue(bottomRight.getY() >= size.getY());
        //
        // TilesLoader loader = new TilesLoader(
        // topLeftGeo,
        // bottomRightGeo,
        // (int) screenSize.getX(),
        // (int) screenSize.getY());
        //
        // loader.load(zoomLevel, zoomLevel, new TilesLoader.LoadListener() {
        //
        // private int fColumn;
        //
        // private int fRow;
        //
        // @Override
        // public void beginTileColumn(int x, int zoom) {
        // fRow = 0;
        // super.beginTileColumn(x, zoom);
        // }
        //
        // @Override
        // public void onTile(TileInfo tile) {
        // GeoPoint bottomLeft = tile.getTopLeftCoordinates();
        // GeoPoint topRight = tile.getBottomRightCoordinates();
        // ImagePoint imageBottomLeft = tiler.getImagePosition(bottomLeft);
        // ImagePoint imageTopRight = tiler.getImagePosition(topRight);
        // System.out.println("["
        // + imageBottomLeft
        // + " - "
        // + imageTopRight
        // + "] Size: "
        // + imageBottomLeft.getDistanceXY(imageTopRight));
        // }
        // });
        //
        // System.out.println("Zone: " + topLeft + " - " + bottomRight);
        //
        // double imageBearing = pinPoint.getBearing(topLeft);
        //
        // System.out.println(topLeft + " - " + pinPoint + " => " +
        // imageBearing);
        // System.out.println(topLeftGeo
        // + " - "
        // + pinPointGeo
        // + " => "
        // + geoBearing);
        //
        // for (long x = 0; x < size.getX(); x += 512) {
        // for (long y = 0; y < size.getY(); y += 512) {
        // ImagePoint corner = new ImagePoint(x, y);
        // GeoPoint geo = tiler.getGeoPosition(corner);
        // // tiler.getGeoPosition(geo);
        // }
        // }

        // GeoPoint topRight = tiler.getGeoPosition(tiler.getTopRight());
        // TilesLoader loader = new TilesLoader(bottomLeft, topRight);
        // loader.load(zoomLevel, zoomLevel, new TilesLoader.LoadListener() {
        // @Override
        // public void onTile(TileInfo tile) {
        // GeoPoint bottomLeft = tile.getTopLeftCoordinates();
        // GeoPoint topRight = tile.getBottomRightCoordinates();
        // ImagePoint imageBottomLeft = tiler.getImagePosition(bottomLeft);
        // ImagePoint imageTopRight = tiler.getImagePosition(topRight);
        // System.out.println("["
        // + imageBottomLeft
        // + " - "
        // + imageTopRight
        // + "] Size: "
        // + imageBottomLeft.getDistanceXY(imageTopRight));
        // }
        // });
    }

    public void testImageTilerCoordinates() {
        int zoomLevel = 15;
        GeoPoint pinPointGeo = new GeoPoint(48.86709, 2.33535);
        ImagePoint pinPoint = new ImagePoint(523, 645);

        final ImageTiler tiler = new ImageTiler(
            pinPointGeo,
            pinPoint,
            zoomLevel);
        ImagePoint first = new ImagePoint(0, 0);
        ImagePoint second = new ImagePoint(789, 938);

        GeoPoint firstGeo = tiler.getGeoPosition(first);
        GeoPoint secondGeo = tiler.getGeoPosition(second);
        assertTrue(firstGeo.getLatitude() > secondGeo.getLatitude());
        assertTrue(firstGeo.getLongitude() < secondGeo.getLongitude());

        ImagePoint test = tiler.getImagePosition(firstGeo);
        assertEquals(first, test);
        test = tiler.getImagePosition(secondGeo);
        assertEquals(second, test);

        test = tiler.getImagePosition(new GeoPoint(
            firstGeo.getLatitude(),
            secondGeo.getLongitude()));
        assertEquals(new ImagePoint(first.getY(), second.getX()), test);
        test = tiler.getImagePosition(new GeoPoint(
            secondGeo.getLatitude(),
            firstGeo.getLongitude()));
        assertEquals(new ImagePoint(second.getY(), first.getX()), test);

        GeoPoint testGeo = tiler.getGeoPosition(new ImagePoint(
            first.getY(),
            second.getX()));
        assertEquals(firstGeo.getLatitude(), testGeo.getLatitude(), 1e-4);
        assertEquals(secondGeo.getLongitude(), testGeo.getLongitude(), 1e-4);
        testGeo = tiler.getGeoPosition(new ImagePoint(second.getY(), first
            .getX()));
        assertEquals(secondGeo.getLatitude(), testGeo.getLatitude(), 1e-4);
        assertEquals(firstGeo.getLongitude(), testGeo.getLongitude(), 1e-4);
    }

    public void testImageTilesPositions() {
        testImageTilesPositions(645, 523);
        testImageTilesPositions(-645, -523);
        testImageTilesPositions(11, 13);
        testImageTilesPositions(-11, -13);
        testImageTilesPositions(0, 0);
    }

    private void testImageTilesPositions(int y, int x) {
        int zoomLevel = 13;
        GeoPoint pinPointGeo = new GeoPoint(0, 0);
        ImagePoint pinPoint = new ImagePoint(y, x);
        ImageTiler tiler = new ImageTiler(pinPointGeo, pinPoint, zoomLevel);
        TileInfo firstTile = tiler.getFirstTile();
        ImagePoint firstTilePos = tiler.getTilePosition(firstTile);
        long tileSize = tiler.getTileSize();
        assertEquals(
            ((pinPoint.getY() % tileSize) - tileSize) % tileSize,
            firstTilePos.getY());
        assertEquals(
            (pinPoint.getX() % tileSize - tileSize) % tileSize,
            firstTilePos.getX());
    }

    public void testTileCoordinates() {
        int zoomLevel = 18;
        GeoPoint pinPointGeo = new GeoPoint(48.86709, 2.33535);

        TileInfo tile = new TileInfo(pinPointGeo, zoomLevel);
        GeoPoint topLeftGeo = tile.getTopLeftCoordinates();
        GeoPoint bottomRightGeo = tile.getBottomRightCoordinates();
        assertTrue(topLeftGeo.getLongitude() < bottomRightGeo.getLongitude());
        assertTrue(topLeftGeo.getLatitude() > bottomRightGeo.getLatitude());

        double bearing = topLeftGeo.getBearing(bottomRightGeo);
        ImagePoint topLeft = new ImagePoint(0, 0);
        ImagePoint bottomRight = new ImagePoint(512, 512);
        double imageBearing = topLeft.getBearing(bottomRight);
        assertEquals(bearing, imageBearing, 1e-4);

        double distance = topLeft.getDistance(bottomRight);
        ImagePoint test = topLeft.getPoint(imageBearing, distance);
        assertEquals(bottomRight.getX(), test.getX());
        assertEquals(bottomRight.getY(), test.getY());

    }
}
