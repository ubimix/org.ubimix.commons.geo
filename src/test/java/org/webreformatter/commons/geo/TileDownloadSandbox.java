package org.webreformatter.commons.geo;

import java.io.File;

import org.webreformatter.commons.geo.TilesLoader.DownloadListener;

public class TileDownloadSandbox {

    public static void main(String[] args) {
        GeoPoint min = new GeoPoint(2.346783, 48.832973);
        GeoPoint max = new GeoPoint(2.422228, 48.86031);
        int minZoom = 13;
        int maxZoom = 14;
        File dir = new File("./tmp");
        DownloadListener listener = new TilesLoader.DownloadListener(dir);
        TilesLoader loader = new TilesLoader();
        loader.load(min, max, minZoom, maxZoom, listener);
    }
}
