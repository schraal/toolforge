package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationLoader;
import nl.toolforge.karma.core.test.BaseTest;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestManifest extends BaseTest {

  public void testManifest() {

    try {
      DevelopmentManifest m = new DevelopmentManifest("test-manifest-1");
      m.load();

      assertTrue("test-manifest-1".equals(m.getName()));
      assertTrue("1-0".equals(m.getVersion()));
      assertEquals(3, m.size());

      //assertNotNull(m.getDescription()); //todo something wrong in the digester rules.xml

      m = new DevelopmentManifest("test-manifest-1");
      m.load();
      assertEquals(3, m.size());

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  public void testModuleCache() {

    try {
      DevelopmentManifest m = new DevelopmentManifest("test-manifest-1");
      assertEquals(0, m.getAllModules().size());

      m.load();

      assertEquals(3, m.getAllModules().size());
      assertEquals(3, m.getAllModules().size());

      m.load();

      assertEquals(3, m.getAllModules().size());

      m = new DevelopmentManifest("included-test-manifest-1");
      m.load();

      assertEquals(1, m.getAllModules().size());

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  public void testAddAndGetModule() {

    DevelopmentManifest m = new DevelopmentManifest("a");
    try {
      m.addModule(null);
      fail("Should have failed. No null allowed.");
    } catch (Exception r) {
      assertTrue(true);
    }

    try {
      m.addModule(new ModuleDescriptor("a", "src", "local-test"));

      Location l = LocationLoader.getInstance().get("local-test");
      Module module = new SourceModule("a", l);

      assertEquals(m.getModule("a"), module);

    } catch (Exception r) {
      assertTrue(true);
    }
  }

}
