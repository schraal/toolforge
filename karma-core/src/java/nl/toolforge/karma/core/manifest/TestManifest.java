package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.location.Location;

import java.io.File;
import java.util.Date;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestManifest extends BaseTest {

  public void testManifest() {

    try {
      Manifest m = new Manifest("test-manifest-1");
      m.load(getClassLoader());

      assertTrue("test-manifest-1".equals(m.getName()));
      assertTrue("1-0".equals(m.getVersion()));

      //assertNotNull(m.getDescription()); //todo something wrong in the digester rules.xml
      assertEquals(5, m.size());

      m = new Manifest("test-manifest-1");
      m.load(getClassLoader());
      assertEquals(5, m.size());

    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  public void testModuleCache() {

    try {
      Manifest m = new Manifest("test-manifest-1");
      m.load(getClassLoader());

      assertEquals(5, m.getAllModules().size());
      assertEquals(5, m.getAllModules().size());

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  public void testAddAndGetModule() {

    Manifest m = new Manifest("a");
    try {
      m.addModule(null);
      fail("Should have failed. No null allowed.");
    } catch (Exception r) {
      assertTrue(true);
    }

    try {
      m.addModule(new ModuleDescriptor("a", "src", "local-test"));

      Location l = LocationFactory.getInstance().get("local-test");
      Module module = new SourceModule("a", l);

      assertEquals(m.getModule("a"), module);

    } catch (Exception r) {
      assertTrue(true);
    }


  }

}
