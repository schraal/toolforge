package nl.toolforge.karma.core.module;

import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationLoader;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.test.BaseTest;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestModuleDescriptor extends BaseTest {

  private LocationLoader locationFactory = null;

  public void setUp() {
    super.setUp();
    try {
      locationFactory = getWorkingContext().getLocationLoader();
    } catch (LocationException e) {
      fail(e.getMessage());
    }
  }

  public void testCreateFile() {

    File tmp = null;
    try {
      tmp = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      fail();
    }

    ModuleDescriptor d = null;
    try {
      SourceModule s = new SourceModule("A", locationFactory.get("test-id-1"));
      d = new ModuleDescriptor(s);

      d.createFile(tmp);

    } catch (LocationException e) {
      fail();
    } catch (IOException e) {
      fail();
    }

    try {
      FileUtils.deleteDirectory(tmp);
    } catch (IOException e) {
      fail();
    }

    if (tmp != null && tmp.exists()) {
      tmp.delete();
    }
  }
}
