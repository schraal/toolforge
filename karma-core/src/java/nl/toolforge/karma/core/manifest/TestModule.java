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

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationLoader;
import nl.toolforge.karma.core.test.BaseTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestModule extends BaseTest {

  private LocationLoader locationFactory = null;

  public void setUp() {
    super.setUp();
    try {
      locationFactory = LocationLoader.getInstance();
    } catch (LocationException e) {
     fail(e.getMessage());
    }
  }

  public void testTypes() {

    Module.SourceType type = new Module.SourceType("src");
    assertEquals(type.getSourceType(), "src");

    Module.DeploymentType depl = new Module.DeploymentType("webapp-bla");
    assertEquals(new Module.DeploymentType("webapp-bla"), Module.WEBAPP);
    assertEquals(depl.getPrefix(), "webapp");

    depl = new Module.DeploymentType("eapp-bla");
    assertEquals(new Module.DeploymentType("eapp-bla"), Module.EAPP);
    assertEquals(depl.getPrefix(), "eapp");

    depl = new Module.DeploymentType("appserver-bla");
    assertEquals(new Module.DeploymentType("appserver-bla"), Module.APPSERVER);
    assertEquals(depl.getPrefix(), "appserver");

    depl = new Module.DeploymentType("config-appserver-bla");
    assertEquals(new Module.DeploymentType("config-appserver-bla"), Module.CONFIG_APPSERVER);
    assertEquals(depl.getPrefix(), "config-appserver");

    depl = new Module.DeploymentType("bla");
    assertEquals(new Module.DeploymentType("bla"), Module.JAR);
    assertEquals(depl.getPrefix(), "");
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
      l = locationFactory.get("test-id-2");
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
