package nl.toolforge.karma.core.location;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.karma.core.test.BaseTest;
import org.apache.commons.io.FileUtils;

/**
 * @author D.A. Smedes
 * @version $Id:
 */
public class TestLocationLoader extends BaseTest {

  public void testLoad() {

    LocationLoader loader = null;

    try {
      loader = LocationLoader.getInstance();

      loader.load();

    } catch (LocationException e) {
      fail(e.getMessage());
    }

    try {
      assertNotNull(loader.get("test-id-1"));
      assertNotNull(loader.get("test-id-4"));

      assertEquals(loader.getLocations().keySet().size(), 6);

      assertEquals(((CVSLocationImpl) loader.get("test-id-1")).getUsername(), "asmedes");

    } catch (LocationException e) {
      fail(e.getMessage());
    }

    try {

      loader.get("bla");

      fail("This id doesn't exist.");

    } catch (LocationException e) {
      assertTrue(true);
    }
  }

}