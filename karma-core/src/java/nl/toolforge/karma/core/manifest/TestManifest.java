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

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationLoader;
import nl.toolforge.karma.core.test.BaseTest;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestManifest extends BaseTest {

  public void testManifest() {

    try {
      DevelopmentManifest m = new DevelopmentManifest("test-manifest-1");

      m.load();

      assertTrue( "test-manifest-1".equals(m.getName()));
			assertTrue( Pattern.matches("[0-9][0-9]?[0-9]?-[0-9][0-9]?[0-9]?", m.getVersion()));
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
