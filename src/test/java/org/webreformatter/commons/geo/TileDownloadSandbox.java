package org.webreformatter.commons.geo;

import java.io.File;

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
        int maxZoom = 18;
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
            minZoom,
            maxZoom,
            screenWidth,
            screenHeight);
        loader.load(listener);
    }

    protected static void println(String msg) {
        System.out.println(msg);
    }

}
