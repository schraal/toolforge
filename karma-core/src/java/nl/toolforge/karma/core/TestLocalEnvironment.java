package nl.toolforge.karma.core;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import junit.framework.TestCase;
import nl.toolforge.core.util.file.MyFileUtils;

public class TestLocalEnvironment extends TestCase {

  private File f1;
  private File f2;
  private File f3;
  private Properties p;
  private LocalEnvironment localEnvironment;

  public void setUp() {
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

  public void tearDown() {
    try {
      FileUtils.deleteDirectory(f1);
      FileUtils.deleteDirectory(f2);
      FileUtils.deleteDirectory(f3);
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
  }

	public void testAll() {
    try {
      localEnvironment = LocalEnvironment.getInstance(p);
      assertNotNull(localEnvironment);
      assertEquals(f1, localEnvironment.getDevelopmentHome());
      assertEquals(f2, localEnvironment.getManifestStore());
      assertEquals(f3, localEnvironment.getLocationStore());
    } catch (KarmaRuntimeException kre) {
      fail(kre.getMessage());
    } catch (KarmaException e) {
      fail(e.getMessage());
    }
	}


}
