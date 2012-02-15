/**
 * 
 */
package org.webreformatter.commons.geo;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class ZoomLevelTest extends TestCase {

    /**
     * @param name
     */
    public ZoomLevelTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        for (ZoomLevel level : ZoomLevel.values()) {
            String name = level.toString().toLowerCase();
            ZoomLevel test = ZoomLevel.toZoomLevel(name);
            assertSame(level, test);

            name = name.toUpperCase();
            test = ZoomLevel.toZoomLevel(name);
            assertSame(level, test);

            int levelValue = level.getLevel();
            test = ZoomLevel.toZoomLevel(levelValue);
            assertSame(level, test);

            test = ZoomLevel.toZoomLevel(levelValue + "");
            assertSame(level, test);
        }

        ZoomLevel test = ZoomLevel.toZoomLevel(123);
        assertSame(ZoomLevel.BUILDING, test);

        test = ZoomLevel.toZoomLevel(ZoomLevel.BUILDING.getLevel() - 1);
        assertSame(ZoomLevel.STREET, test);

        test = ZoomLevel.toZoomLevel(ZoomLevel.STREET.getLevel() - 1);
        assertSame(ZoomLevel.CITY, test);

        test = ZoomLevel.toZoomLevel(ZoomLevel.CITY.getLevel() - 1);
        assertSame(ZoomLevel.AREA, test);

        test = ZoomLevel.toZoomLevel(ZoomLevel.AREA.getLevel() - 1);
        assertSame(ZoomLevel.WIDEAREA, test);

        test = ZoomLevel.toZoomLevel(ZoomLevel.WIDEAREA.getLevel() - 1);
        assertSame(ZoomLevel.WORLD, test);

        test = ZoomLevel.toZoomLevel(ZoomLevel.WORLD.getLevel() - 1);
        assertSame(ZoomLevel.WORLD, test);
    }

}
