package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.Version;

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

      s = new SourceModule("a", l, new DevelopmentLine("B"));
      assertNotNull(s);
      assertFalse(s.hasVersion());
      assertTrue(s.hasDevelopmentLine());
      assertEquals(new DevelopmentLine("B"), s.getDevelopmentLine());

      s = new SourceModule("a", l, new Version("0-1"));
      assertNotNull(s);
      assertTrue(s.hasVersion());
      assertFalse(s.hasDevelopmentLine());
      assertEquals(new Version("0-1"), s.getVersion());
      assertEquals("0-1", s.getVersionAsString());

       s = new SourceModule("a", l, new Version("0-1"), new DevelopmentLine("B"));
      assertNotNull(s);
      assertTrue(s.hasVersion());
      assertTrue(s.hasDevelopmentLine());
      assertEquals(new Version("0-1"), s.getVersion());
      assertEquals(new DevelopmentLine("B"), s.getDevelopmentLine());
      assertEquals("0-1", s.getVersionAsString());

    } catch (LocationException e) {

    }


  }
}
