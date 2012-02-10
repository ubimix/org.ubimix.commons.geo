package org.webreformatter.commons.geo;

import java.io.File;

import org.webreformatter.commons.geo.TilesLoader.DownloadListener;

public class TileDownloadSandbox {

    public static void main(String[] args) {
        GeoPoint first = new GeoPoint(2.3081588745117188, 48.8872947821604);
        // GeoPoint first = new GeoPoint(2.372704, 48.887972);
        GeoPoint second = new GeoPoint(2.3727035522460938, 48.83670138083755);

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
        int maxZoom = 14;
        println("");
        println("-----------------------------------------------");
        println("Tiles download:");
        File dir = new File("./tmp");
        DownloadListener listener = new TilesLoader.DownloadListener(dir);
        TilesLoader loader = new TilesLoader();
        loader.load(first, second, minZoom, maxZoom, listener);
    }

    protected static void println(String msg) {
        System.out.println(msg);
    }
}
