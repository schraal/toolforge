package nl.toolforge.karma.core.test;

import junit.framework.TestCase;
import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.KarmaException;
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

      new File(workingContext, "manifests").mkdirs();
      new File(workingContext, "locations").mkdirs();
      new File(workingContext, "projects").mkdirs();

    } catch (IOException e) {
      e.printStackTrace();
    }

    p = new Properties();
    p.put(LocalEnvironment.WORKING_CONTEXT_DIRECTORY, workingContext.getPath());

    p.put(LocalEnvironment.MANIFEST_STORE_HOST, "localhost");
    p.put(LocalEnvironment.MANIFEST_STORE_PORT, "2401");
    p.put(LocalEnvironment.MANIFEST_STORE_PROTOCOL, "local");
    p.put(LocalEnvironment.MANIFEST_STORE_REPOSITORY, "/tmp/test-CVSROOT");
    p.put(LocalEnvironment.MANIFEST_STORE_USERNAME, "asmedes");

    p.put(LocalEnvironment.LOCATION_STORE_HOST, "localhost");
    p.put(LocalEnvironment.LOCATION_STORE_PORT, "2401");
    p.put(LocalEnvironment.LOCATION_STORE_PROTOCOL, "local");
    p.put(LocalEnvironment.LOCATION_STORE_REPOSITORY, "/tmp/test-CVSROOT");
    p.put(LocalEnvironment.LOCATION_STORE_USERNAME, "asmedes");

    try {

      // Initializes the LocalEnvironment so we can work with it ...
      //
      LocalEnvironment.initialize(p);
    } catch (KarmaException e) {
      fail(e.getMessage());
    }

    try {
      writeFile(LocalEnvironment.getLocationStore(), "test-locations.xml");
      writeFile(LocalEnvironment.getLocationStore(), "test-locations-2.xml");
      writeFile(LocalEnvironment.getLocationStore(), "authenticators.xml");
      writeFile(LocalEnvironment.getManifestStore(), "test-manifest-1.xml");
      writeFile(LocalEnvironment.getManifestStore(), "included-test-manifest-1.xml");
    } catch (IOException e) {
      fail(e.getMessage());
    }


    // Initialize the LocationFactory
    //
    try {
      LocationLoader loader = LocationLoader.getInstance();
      loader.load(LocalEnvironment.getLocationStore());

    } catch (LocationException e) {
      fail("INITIALIZATION ERROR : " + e.getErrorMessage());
    }
  }

  public void tearDown() {
    try {
      org.apache.commons.io.FileUtils.deleteDirectory(workingContext);
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

  private synchronized void writeFile(File dir, String fileRef) throws IOException {

    BufferedReader in =
        new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(fileRef)));
    BufferedWriter out =
        new BufferedWriter(new FileWriter(new File(dir, fileRef)));

    String str;
    while ((str = in.readLine()) != null) {
      out.write(str);
    }
    out.close();
    in.close();
  }
}
