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
import org.apache.commons.io.FileUtils;

/**
 * @author D.A. Smedes
 * @version $Id:
 */
public class TestLocationLoader extends TestCase {

  private File tmp = null;

  public void setUp() {

    try {
      tmp = MyFileUtils.createTempDirectory();

      writeFile("test-locations.xml");
      writeFile("test-locations-2.xml");
      writeFile("authenticators.xml");

    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  public void tearDown() {

    try {
      FileUtils.deleteDirectory(tmp);
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  public void testLoad() {

    LocationLoader loader = null;

    try {
      loader = LocationLoader.getInstance();

      loader.load(tmp);

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

  /**
   * Reads fileRef from the classPath and writes it to a temp directory.
   * @param fileRef
   * @throws IOException
   */
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