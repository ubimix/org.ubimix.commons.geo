package org.webreformatter.commons.geo;

import java.awt.image.BufferedImage;

public class TilesPrintUtil {

    public static String toJson(
        BufferedImage image,
        ImageTiler tiler,
        int maxZoom,
        int minZoom,
        Object... args) {
        ImagePoint pinPoint = new ImagePoint(0, 0);
        GeoPoint topLeftGeo = tiler.getGeoPosition(pinPoint);
        ImagePoint imageSize = new ImagePoint(
            image.getHeight(),
            image.getWidth());
        GeoPoint bottomRightGeo = tiler.getGeoPosition(imageSize);
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        toJsonParams(
            buf,
            "minZoom",
            minZoom,
            "maxZoom",
            maxZoom,
            "area",
            "["
                + toJsonArray(
                    null,
                    topLeftGeo.getLatitude(),
                    topLeftGeo.getLongitude())
                + ","
                + toJsonArray(
                    null,
                    bottomRightGeo.getLatitude(),
                    bottomRightGeo.getLongitude()) + "]");
        if (args.length > 0) {
            buf.append(",");
            toJsonParams(buf, args);
        }
        buf.append("\n}");
        return buf.toString();
    }

    public static String toJsonArray(StringBuilder buf, Object... objs) {
        if (buf == null) {
            buf = new StringBuilder();
        }
        buf.append("[");
        int i = 0;
        for (Object obj : objs) {
            if (i > 0) {
                buf.append(",");
            }
            buf.append(obj);
            i++;
        }
        buf.append("]");
        return buf.toString();
    }

    public static String toJsonObj(StringBuilder buf, Object... objs) {
        if (buf == null) {
            buf = new StringBuilder();
        }
        buf.append("{");
        toJsonParams(buf, objs);
        buf.append("\n}");
        return buf.toString();
    }

    protected static String toJsonParams(StringBuilder buf, Object... objs) {
        if (buf == null) {
            buf = new StringBuilder();
        }
        for (int i = 0; i < objs.length;) {
            if (i > 0) {
                buf.append(",");
            }
            String key = objs[i++] + "";
            String value = i < objs.length ? objs[i++] + "" : null;
            buf.append("\n  \"");
            buf.append(key);
            buf.append("\":");
            buf.append(value);
        }
        return buf.toString();
    }
}
