package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.Version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestModule extends BaseTest {

  private LocationFactory locationFactory = null;

  public void setUp() {
    super.setUp();
    locationFactory = LocationFactory.getInstance();
  }

  public void testConstructor() {

    try {
      Location l = locationFactory.get("local-test");

      SourceModule s = null;

      s = new SourceModule("a", l);
      assertNotNull(s);
      assertEquals("a", s.getName());
      assertNull(s.getVersion());
      assertEquals("N/A", s.getVersionAsString());

      s = new SourceModule("a", l, new Version("0-1"));
      assertNotNull(s);
      assertTrue(s.hasVersion());
      assertFalse(s.hasPatchLine());
      assertEquals(new Version("0-1"), s.getVersion());
      assertEquals("0-1", s.getVersionAsString());

    } catch (LocationException e) {

    }
  }

  public void testComparator() {

    Location l = null;
    try {
      l = locationFactory.get("local-test");
    } catch (LocationException e) {
      fail(e.getMessage() + "; test initialization failed most probably.");
    }

    SourceModule s1 = new SourceModule("A", l);
    SourceModule s2 = new SourceModule("B", l);
    SourceModule s3 = new SourceModule("C", l);
    SourceModule s4 = new SourceModule("D", l);

    List c = new ArrayList();
    c.add(s2);
    c.add(s1);
    c.add(s4);
    c.add(s3);

    assertEquals("B", ((Module) c.get(0)).getName());
    assertEquals("A", ((Module) c.get(1)).getName());
    assertEquals("D", ((Module) c.get(2)).getName());
    assertEquals("C", ((Module) c.get(3)).getName());

    Collections.sort(c, new ModuleComparator());

    assertEquals("A", ((Module) c.get(0)).getName());
    assertEquals("B", ((Module) c.get(1)).getName());
    assertEquals("C", ((Module) c.get(2)).getName());
    assertEquals("D", ((Module) c.get(3)).getName());
  }

}
