package nl.toolforge.karma.core;

import junit.framework.TestCase;
import nl.toolforge.core.util.file.MyFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @version $Id$
 */
public final class TestLocalEnvironment extends TestCase {

  private static File workingContext;
  private static Properties p;
  private static LocalEnvironment localEnvironment;

  static {
    try {
      workingContext = MyFileUtils.createTempDirectory();
//			f2 = MyFileUtils.createTempDirectory();
//			f3 = MyFileUtils.createTempDirectory();

      p = new Properties();
      p.put(LocalEnvironment.WORKING_CONTEXT_DIRECTORY, workingContext.getPath());
//			p.put(LocalEnvironment.MANIFEST_STORE_DIRECTORY, f2.getPath());
//			p.put(LocalEnvironment.LOCATION_STORE_DIRECTORY, f3.getPath());
      p.put(LocalEnvironment.MANIFEST_STORE_HOST, "one");
      p.put(LocalEnvironment.MANIFEST_STORE_REPOSITORY, "two");
      p.put(LocalEnvironment.MANIFEST_STORE_PROTOCOL, "three");
      p.put(LocalEnvironment.LOCATION_STORE_HOST, "one");
      p.put(LocalEnvironment.LOCATION_STORE_REPOSITORY, "two");
      p.put(LocalEnvironment.LOCATION_STORE_PROTOCOL, "three");

      try {
        localEnvironment = LocalEnvironment.getInstance(p);
      } catch(KarmaException k) {
        fail(k.getMessage());
      }

    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  public void testGetWorkingContext() {
    try {
      assertEquals(workingContext, localEnvironment.getWorkingContext());

    } catch (KarmaException e) {
      fail(e.getMessage());
    }
  }

  public void testGetDevelopmentHome() {
    try {
      assertEquals(new File(workingContext, "projects"), localEnvironment.getDevelopmentHome());
    } catch (KarmaException e) {
      fail(e.getMessage());
    }

  }

  public void testGetManifestStore() {
    try {
      assertEquals(new File(workingContext, "manifests"), localEnvironment.getManifestStore());

    } catch (KarmaException e) {
      fail(e.getMessage());
    }

  }

  public void testGetLocationStore() {
    try {
      assertEquals(new File(workingContext, "locations"), localEnvironment.getLocationStore());

    } catch (KarmaException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Leave as last ...
   */

  public void testZZZZZZZZZZ() {
    boolean b = workingContext.delete();
  }
}
