package nl.toolforge.karma.core.test;

import junit.framework.TestCase;
import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.location.LocationException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * This testclass is highly recommended when writing JUnit testclasses for Karma. It initializes some basic stuff. Just
 * check this implementation to see how it may help.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class BaseTest extends TestCase {

  private Properties p = null;

  private File f1 = null;
  private File f2 = null;
  private File f3 = null;

  public void setUp() {

    // Fake some parameters that would have been passed to the JVM
    //

    // The following is required to allow the Preferences class to use the test-classpath
    //
    System.setProperty("TESTMODE", "true");
    System.setProperty("locale", "en");

    try {
      f1 = MyFileUtils.createTempDirectory();
      f2 = MyFileUtils.createTempDirectory();
      f3 = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      e.printStackTrace();
    }

    p = new Properties();
    p.put(LocalEnvironment.DEVELOPMENT_STORE_DIRECTORY, f1.getPath());
    p.put(LocalEnvironment.MANIFEST_STORE_DIRECTORY, f2.getPath());
    p.put(LocalEnvironment.LOCATION_STORE_DIRECTORY, f3.getPath());

    // Initialize the LocationFactory
    //
    try {
      LocationFactory locationFactory = LocationFactory.getInstance();
      locationFactory.load(getClassLoader().getResourceAsStream("test-locations.xml"),
          getClass().getClassLoader().getResourceAsStream("test-location-authentication.xml"));

    } catch (LocationException e) {
      fail("INITIALIZATION ERROR : " + e.getErrorMessage());
    }
  }

  public void tearDown() {
    try {
      org.apache.commons.io.FileUtils.deleteDirectory(f1);
      org.apache.commons.io.FileUtils.deleteDirectory(f2);
      org.apache.commons.io.FileUtils.deleteDirectory(f3);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public final Properties getProperties() {
    return p;
  }

  /**
   * Helper method to retrieve the this class' classloader.
   */
  public ClassLoader getClassLoader() {
    return this.getClass().getClassLoader();
  }
}
