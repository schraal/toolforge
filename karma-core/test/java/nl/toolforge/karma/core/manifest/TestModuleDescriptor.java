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

import junit.framework.TestCase;
import nl.toolforge.karma.core.module.ModuleDigester;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestModuleDescriptor extends TestCase {

  public void testSettersAndGetters() {

    ModuleDigester m = new ModuleDigester("a", "o");
//    ModuleDigester m = new ModuleDigester("a", "src", "o");
    m.setVersion("0-1");

    assertEquals("0-1", m.getVersion());
    assertEquals("o", m.getLocation());
    assertEquals("a", m.getName());
//    assertEquals(ModuleDigester.SOURCE_MODULE, m.getType());
  }

  public void testEquals() {

    ModuleDigester m1 = new ModuleDigester("a", "o");
    ModuleDigester m2 = new ModuleDigester("a", "o");
    ModuleDigester m3 = new ModuleDigester("b", "o");
    ModuleDigester m4 = new ModuleDigester("a", "b");
//    ModuleDigester m1 = new ModuleDigester("a", "src", "o");
//    ModuleDigester m2 = new ModuleDigester("a", "src", "o");
//    ModuleDigester m3 = new ModuleDigester("b", "src", "o");
//    ModuleDigester m4 = new ModuleDigester("a", "src", "b");

    assertTrue(m1.equals(m2));
    assertFalse(m1.equals(m3));
    assertFalse(m2.equals(m3));
    assertFalse(m1.equals(m4));
    assertFalse(m2.equals(m4));
  }

}
