package org.webreformatter.commons.geo;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        //$JUnit-BEGIN$
        suite.addTestSuite(TestImageTiler.class);
        suite.addTestSuite(TilesLoaderTest.class);
        suite.addTestSuite(ZoomLevelTest.class);
        //$JUnit-END$
        return suite;
    }

}
