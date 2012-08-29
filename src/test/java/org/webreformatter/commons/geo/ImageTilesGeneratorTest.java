/**
 * 
 */
package org.webreformatter.commons.geo;

import java.io.InputStream;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class ImageTilesGeneratorTest extends TestCase {

    /**
     * @param name
     */
    public ImageTilesGeneratorTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        String name = "flower.jpg";
        name = "1950s (end of) - Moscow, panorama.jpg";
        name = "PlanDeParis-Delagrive-1740.jpg";
        name = "PlanDeParis-Turgot-1739.jpg";
        name = "hdr_mountains_lake-wallpaper-2560x1440.jpg";
        name = "Moscow-1901.jpg";
        name = "bugatti-16c-galibier-4.jpg";
        new Main("url=" + name) {
            @Override
            protected InputStream openImageStream(String imageUrl)
                throws java.io.IOException {
                return getClass().getResourceAsStream("/" + imageUrl);
            }
        }.run();
    }
}
