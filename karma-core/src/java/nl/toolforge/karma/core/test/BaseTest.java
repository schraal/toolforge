package nl.toolforge.karma.core.test;

import junit.framework.TestCase;
import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationLoader;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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

  private File workingContext = null;
//  private File f2 = null;
//  private File f3 = null;

  private File tmp = null;

  public void setUp() {

    // Fake some parameters that would have been passed to the JVM
    //

    // The following is required to allow the Preferences class to use the test-classpath
    //
    System.setProperty("TESTMODE", "true"); //??
    System.setProperty("locale", "en");

    try {
      workingContext = MyFileUtils.createTempDirectory();
//      f2 = MyFileUtils.createTempDirectory();
//      f3 = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      e.printStackTrace();
    }

    p = new Properties();
    p.put(LocalEnvironment.WORKING_CONTEXT_DIRECTORY, workingContext.getPath());
//    p.put(LocalEnvironment.MANIFEST_STORE_DIRECTORY, f2.getPath());
//    p.put(LocalEnvironment.LOCATION_STORE_DIRECTORY, f3.getPath());

    try {
      tmp = MyFileUtils.createTempDirectory();

      writeFile("test-locations.xml");
      writeFile("test-locations-2.xml");
      writeFile("authenticators.xml");

    } catch (IOException e) {
      fail(e.getMessage());
    }


    // Initialize the LocationFactory
    //
    try {
      LocationLoader loader = LocationLoader.getInstance();
      loader.load(tmp);

    } catch (LocationException e) {
      fail("INITIALIZATION ERROR : " + e.getErrorMessage());
    }
  }

  public void tearDown() {
    try {
      org.apache.commons.io.FileUtils.deleteDirectory(workingContext);
//      org.apache.commons.io.FileUtils.deleteDirectory(f2);
//      org.apache.commons.io.FileUtils.deleteDirectory(f3);
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      FileUtils.deleteDirectory(tmp);
    } catch (IOException e) {
      fail(e.getMessage());
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

  private synchronized void writeFile(String fileRef) throws IOException {

    BufferedReader in =
        new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileRef)));
    BufferedWriter out =
        new BufferedWriter(new FileWriter(new File(tmp, fileRef)));

    String str;
    while ((str = in.readLine()) != null) {
      out.write(str);
    }
    out.close();
    in.close();
  }
}
