/**
 * 
 */
package org.webreformatter.commons.geo;

/**
 * <pre>
 * http://wiki.openstreetmap.org/wiki/Zoom_levels:
 *  Level   Degree  Area                              m / pixel  ~Scale
 *  0       360     whole world                         156.412  1:500 Mio
 *  1       180                                         78.206   1:250 Mio
 *  2       90                                          39.103   1:150 Mio
 *  3       45                                          19.551   1:70 Mio
 *  4       22,5                                        9.776    1:35 Mio
 *  5       11,2                                        4.888    1:15 Mio
 *  6       5,625                                       2.444    1:10 Mio
 *  7       2,813                                       1.222    1:4 Mio
 *  8       1,406                                       610,984  1:2 Mio
 *  9       0,703   wide area                           305,492  1:1 Mio
 *  10      0,352                                       152,746  1:500.000
 *  11      0,176   area                                76,373   1:250.000
 *  12      0,088                                       38,187   1:150.000
 *  13      0,044   village or town                     19,093   1:70.000
 *  14      0,022   largest editable area on the applet 9,547    1:35.000
 *  15      0,011                                       4,773    1:15.000
 *  16      0,005   small road                          2,387    1:8.000
 *  17      0,003                                       1,193    1:4.000
 *  18      0,001                                       0,596    1:2.000
 * </pre>
 * 
 * @author kotelnikov
 */
public enum ZoomLevel {

    AREA(11), BUILDING(17), CITY(13), STREET(16), WIDEAREA(9), WORLD(0);

    public static ZoomLevel DEFAULT = STREET;

    public static ZoomLevel[] LEVELS = {
        WORLD,
        WIDEAREA,
        AREA,
        CITY,
        STREET,
        BUILDING };

    public static ZoomLevel max(ZoomLevel first, ZoomLevel second) {
        return first.getLevel() > second.getLevel() ? first : second;
    }

    public static ZoomLevel min(ZoomLevel first, ZoomLevel second) {
        return first.getLevel() < second.getLevel() ? first : second;
    }

    public static ZoomLevel toZoomLevel(int value) {
        return toZoomLevel(value, DEFAULT);
    }

    public static ZoomLevel toZoomLevel(int value, ZoomLevel defaultLevel) {
        ZoomLevel result = null;
        ZoomLevel prev = WORLD;
        for (ZoomLevel level : LEVELS) {
            if (value >= prev.getLevel() && value <= level.getLevel()) {
                result = level;
                break;
            }
            prev = level;
        }
        if (result == null) {
            result = defaultLevel;
        }
        return result;
    }

    public static ZoomLevel toZoomLevel(String name) {
        return toZoomLevel(name, DEFAULT);
    }

    public static ZoomLevel toZoomLevel(String name, ZoomLevel defaultLevel) {
        ZoomLevel result = null;
        if (name != null) {
            name = name.trim();
            name = name.toLowerCase();
            for (ZoomLevel level : ZoomLevel.values()) {
                String levelName = level.toString();
                if (name.equals(levelName)) {
                    result = level;
                    break;
                }
            }
            if (result == null) {
                int value = Integer.parseInt(name);
                result = toZoomLevel(value, defaultLevel);
            }
        }
        if (result == null) {
            result = defaultLevel;
        }
        return result;
    }

    private int fLevel;

    private ZoomLevel(int level) {
        fLevel = level;
    }

    public int getLevel() {
        return fLevel;
    }

    @Override
    public String toString() {
        String name = super.toString();
        return name.toLowerCase();
    }

}
