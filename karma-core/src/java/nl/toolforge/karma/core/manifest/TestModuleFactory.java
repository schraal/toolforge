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

import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.test.BaseTest;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestModuleFactory extends BaseTest {

  public void testGetInstance() {
    ModuleFactory f = new ModuleFactory(getWorkingContext());
    assertNotNull(f);
  }

  public void testCreate1() {

    ModuleFactory f = new ModuleFactory(getWorkingContext());

    ModuleDigester d1 = new ModuleDigester("module-1", "test-id-1");
//    ModuleDigester d1 = new ModuleDigester("module-1", "src", "test-id-1");
    d1.setVersion("0-1");

    ModuleDigester d2 = new ModuleDigester("module-1", "test-id-1");
//    ModuleDigester d2 = new ModuleDigester("module-1", "src", "test-id-1");

    ModuleDigester d3 = new ModuleDigester("module-1", "test-id-1");
//    ModuleDigester d3 = new ModuleDigester("module-1", "src", "test-id-1");
    d3.setVersion("0-1");

//    ModuleDigester d4 = new ModuleDigester("module-1", "test-id-1");
//    ModuleDigester d4 = new ModuleDigester("module-1", "maven", "test-id-1");
    Module m = null;

    try {
      m = f.create(d1);
      assertTrue(m instanceof SourceModule);
//      assertFalse(m instanceof MavenModule);
      m = f.create(d2);
      assertTrue(m instanceof SourceModule);
//      assertFalse(m instanceof MavenModule);
      m = f.create(d3);
      assertTrue(m instanceof SourceModule);
//      assertFalse(m instanceof MavenModule);
//      m = f.create(d4);
//      assertTrue(m instanceof MavenModule);
    } catch (LocationException e) {
      fail(e.getMessage());
    }
  }

}
