package nl.toolforge.karma.core;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;
import nl.toolforge.core.util.file.MyFileUtils;

public final class TestLocalEnvironment extends TestCase {

  private static File f1;
  private static File f2;
  private static File f3;
  private static Properties p;
  private static LocalEnvironment localEnvironment;

  static {
    try {
      f1 = MyFileUtils.createTempDirectory();
      f2 = MyFileUtils.createTempDirectory();
      f3 = MyFileUtils.createTempDirectory();

      p = new Properties();
      p.put(LocalEnvironment.DEVELOPMENT_HOME_DIRECTORY, f1.getPath());
      p.put(LocalEnvironment.MANIFEST_STORE_DIRECTORY  , f2.getPath());
      p.put(LocalEnvironment.LOCATION_STORE_DIRECTORY  , f3.getPath());

    } catch (IOException ioe) {
      fail();
    }
  }


  public static final void testConstructor() {
    try {
      localEnvironment = LocalEnvironment.getInstance(p);
      assertNotNull(localEnvironment);
    } catch (KarmaRuntimeException kre) {
      fail(kre.getMessage());
    }
  }

  public static final void testGetDevelopmentHome() {
    try {
      assertEquals(f1, localEnvironment.getDevelopmentHome());

      f1.delete();
      try {
        localEnvironment.getDevelopmentHome();
        fail("The development home should not have been there");
      } catch (KarmaException ke) {
        assertTrue(true);
      }
    } catch (KarmaException e) {
      fail();
    }

  }

  public static final void testGetManifestStore() {
    try {
      assertEquals(f2, localEnvironment.getManifestStore());

      f2.delete();
      try {
        localEnvironment.getManifestStore();
        fail("The manifest store should not have been there");
      } catch (KarmaException ke) {
        assertTrue(true);
      }
    } catch (KarmaException e) {
      fail();
    }

  }

  public static final void testGetLocationStore() {
    try {
      assertEquals(f3, localEnvironment.getLocationStore());

      f3.delete();
      try {
        localEnvironment.getLocationStore();
        fail("The location store should not have been there");
      } catch (KarmaException ke) {
        assertTrue(true);
      }
    } catch (KarmaException e) {
      fail();
    }

  }


}
