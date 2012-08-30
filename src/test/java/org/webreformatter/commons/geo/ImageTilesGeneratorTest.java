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
        new Main("url=" + name) {
            @Override
            protected InputStream openImageStream(String imageUrl)
                throws java.io.IOException {
                return getClass().getResourceAsStream("/" + imageUrl);
            }
        }.run();
    }
}
