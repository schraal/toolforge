/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.manifest;

import com.mockobjects.dynamic.Mock;
import junit.framework.TestCase;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.util.SourceModuleLayoutTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tests the {@link Module} interface and the {@link SourceModule} implementation.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestModule extends TestCase {

  private Location location = null;

  public void setUp() {

    Mock mock = new Mock(Location.class);
    location = (Location) mock.proxy();
  }

  public void testTypes() {

    Module.Type type = new Module.Type();
    type.setType("JAVA-SOURCE-MODULE");
    assertEquals(type.getType(), "JAVA-SOURCE-MODULE");
  }

  public void testConstructor() {

    SourceModule s = null;

    s = new SourceModule("a", location);
    assertNotNull(s);
    assertEquals("a", s.getName());
    assertNull(s.getVersion());
    assertEquals("N/A", s.getVersionAsString());

    s = new SourceModule("a", location, new Version("0-1"));
    assertNotNull(s);
    assertTrue(s.hasVersion());
    assertFalse(s.hasPatchLine());
    assertEquals(new Version("0-1"), s.getVersion());
    assertEquals("0-1", s.getVersionAsString());
  }

  public void testGetLocation() {
    SourceModule s1 = new SourceModule("A", location);
    assertEquals(location, s1.getLocation());
  }


  public void testGetLayoutTemplate() {
    SourceModule s1 = new SourceModule("A", location);
    assertTrue(s1.getLayoutTemplate() instanceof SourceModuleLayoutTemplate);
  }

  public void testComparator() {

    SourceModule s1 = new SourceModule("A", location);
    SourceModule s2 = new SourceModule("B", location);
    SourceModule s3 = new SourceModule("C", location);
    SourceModule s4 = new SourceModule("D", location);

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
