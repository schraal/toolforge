package nl.toolforge.karma.core.boot;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

import nl.toolforge.core.util.file.MyFileUtils;
import org.apache.commons.io.FileUtils;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestWorkingContextConfiguration extends TestCase {

  public void testConstructor() {
    assertNotNull(new WorkingContextConfiguration(new WorkingContext("arjen")));
  }

  public void testLoad() {

    File tmp = null;
    try {
      tmp = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      fail(e.getMessage());
    }

    WorkingContext ctx = new WorkingContext("test", tmp);
    WorkingContextConfiguration config = new WorkingContextConfiguration(ctx);

    try {

      MyFileUtils.writeFile(
          WorkingContext.getConfigurationBaseDir(),
          new File("test/authenticators.xml"),
          this.getClass().getClassLoader());

      MyFileUtils.writeFile(
          ctx.getWorkingContextConfigurationBaseDir(),
          new File("test/test-working-context.xml"),
          "working-context.xml",
          this.getClass().getClassLoader());
      ctx.init();

    } catch (IOException e) {
      fail(e.getMessage());
    }

    boolean b = config.load();
    assertTrue(b);

    assertEquals(config.getProperty(WorkingContext.PROJECT_BASE_DIRECTORY_PROPERTY), "/tmp/");
    assertEquals(config.getProperty(WorkingContext.PROJECT_LOCAL_REPOSITORY_PROPERTY), "/home/asmedes/.repository");

    assertNotNull(config.getLocationStore());
    assertNotNull(config.getManifestStore());

    try {
      FileUtils.deleteDirectory(tmp);
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
