package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.test.BaseTest;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestModuleFactory extends BaseTest {

  public void testGetInstance() {
    ModuleFactory f = ModuleFactory.getInstance();
    assertNotNull(f);
  }

  public void testCreate1() {

    ModuleFactory f = ModuleFactory.getInstance();

    ModuleDescriptor d1 = new ModuleDescriptor("module-1", "src", "test-id-1");
    d1.setVersion("0-1");

    ModuleDescriptor d2 = new ModuleDescriptor("module-1", "src", "test-id-1");

    ModuleDescriptor d3 = new ModuleDescriptor("module-1", "src", "test-id-1");
    d3.setVersion("0-1");

    ModuleDescriptor d4 = new ModuleDescriptor("module-1", "maven", "test-id-1");
    Module m = null;

    try {
      m = f.create(d1);
      assertTrue(m instanceof SourceModule);
      assertFalse(m instanceof MavenModule);
      m = f.create(d2);
      assertTrue(m instanceof SourceModule);
      assertFalse(m instanceof MavenModule);
      m = f.create(d3);
      assertTrue(m instanceof SourceModule);
      assertFalse(m instanceof MavenModule);
      m = f.create(d4);
      assertTrue(m instanceof MavenModule);
    } catch (LocationException e) {
      fail(e.getMessage());
    }
  }

}
