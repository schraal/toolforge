package nl.toolforge.karma.core.manifest;

import junit.framework.TestCase;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestModuleDescriptor extends TestCase {

  public void testSettersAndGetters() {

    ModuleDescriptor m = new ModuleDescriptor("a", "src", "o");
    m.setDevelopmentLine("s");
    m.setVersion("0-1");
//    m.setLocation("a");
//    m.setName("b");
//    m.setType("o");

    assertEquals("s", m.getDevelopmentLine());
    assertEquals("0-1", m.getVersion());
    assertEquals("o", m.getLocation());
    assertEquals("a", m.getName());
    assertEquals(ModuleDescriptor.SOURCE_MODULE, m.getType());
  }

  public void testEquals() {

    ModuleDescriptor m1 = new ModuleDescriptor("a", "src", "o");
    ModuleDescriptor m2 = new ModuleDescriptor("a", "src", "o");
    ModuleDescriptor m3 = new ModuleDescriptor("b", "src", "o");
    ModuleDescriptor m4 = new ModuleDescriptor("a", "src", "b");

    assertTrue(m1.equals(m2));
    assertFalse(m1.equals(m3));
    assertFalse(m2.equals(m3));
    assertFalse(m1.equals(m4));
    assertFalse(m2.equals(m4));
  }

}
