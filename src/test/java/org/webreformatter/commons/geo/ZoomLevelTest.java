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

    }

}
