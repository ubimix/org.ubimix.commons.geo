package org.webreformatter.commons.geo;

import java.io.File;

import junit.framework.Assert;

import org.webreformatter.commons.geo.TilesLoader.DownloadListener;

public class TileDownloadSandbox {

    public static void main(String[] args) {
        GeoPoint first = new GeoPoint(48.86709, 2.33535);
        GeoPoint second = new GeoPoint(48.86256, 2.34067);

        double distance = first.getDistance(second);
        double bearing = first.getBearing(second);
        GeoPoint calculatedPoint = first.getPoint(bearing, distance);
        println("-----------------------------------------------");
        println("Calculating point by bearing + distance: ");
        println(" - First point        : " + first);
        println(" - Distance           : " + distance + "km");
        println(" - Bearing            : " + bearing);
        println(" - Real second point  : " + second);
        println(" - Calculated point   : " + calculatedPoint);

        int minZoom = 13;
        int maxZoom = 17;
        int screenWidth = 500;
        int screenHeight = 500;
        println("");
        println("-----------------------------------------------");
        println("Tiles download:");
        File dir = new File("./tmp");
        DownloadListener listener = new TilesLoader.DownloadListener(dir);
        TilesLoader loader = new TilesLoader(
            first,
            second,
            screenWidth,
            screenHeight);
        loader.load(minZoom, maxZoom, listener);
    }

    protected static void println(String msg) {
        System.out.println(msg);
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

    public void testTiler(
        GeoPoint first,
        GeoPoint second,
        int zoomLevel,
        double scale) {
        final ImageTiler tiler = new ImageTiler(first, second, scale);
        Assert.assertEquals(0, tiler.getTopLeft().getX());
        Assert.assertEquals(0, tiler.getTopLeft().getY());
        Assert.assertTrue(tiler.getTopRight().getX() > 0);
        Assert.assertEquals(0, tiler.getTopRight().getY());
        Assert.assertEquals(0, tiler.getBottomLeft().getX());
        Assert.assertTrue(tiler.getBottomLeft().getY() > 0);
        Assert.assertTrue(tiler.getBottomRight().getX() > 0);
        Assert.assertTrue(tiler.getBottomRight().getY() > 0);

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

        TilesLoader loader = new TilesLoader(first, second);
        loader.load(zoomLevel, zoomLevel, new TilesLoader.LoadListener() {
            @Override
            public void onTile(TileInfo tile) {
                GeoPoint bottomLeft = tile.getTopLeftCoordinates();
                GeoPoint topRight = tile.getBottomRightCoordinates();
                ImagePoint imageBottomLeft = tiler.getImagePosition(bottomLeft);
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
}
