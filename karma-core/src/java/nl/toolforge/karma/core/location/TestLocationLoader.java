package nl.toolforge.karma.core.location;

import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;

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