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
package nl.toolforge.karma.core.vc;

import junit.framework.TestCase;
import nl.toolforge.karma.core.Version;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestDevelopmentLine extends TestCase {

  public void testConstructorWithInvalidValues() {

    DevelopmentLine l = null;

    try {

      l = new DevelopmentLine(null);
      l = new DevelopmentLine("");
      l = new DevelopmentLine("*");
      l = new DevelopmentLine("!");
      l = new DevelopmentLine("~");
      l = new DevelopmentLine(" ");
      l = new DevelopmentLine(" A");

      l = new DevelopmentLine("0A");
      l = new DevelopmentLine("aaa_0-0");

      l = new DevelopmentLine(" :=+';\"<,>.");

      l = new DevelopmentLine("Aaaa_bb-ccc");

      fail("Should have failed.");

    } catch (IllegalArgumentException i) {
      assertTrue(true);
    }
  }

  public void testConstructorWithValidValues() {

    DevelopmentLine l = null;

    try {

      l = new DevelopmentLine("Aaaa");
      l = new DevelopmentLine("DEV");
      l = new DevelopmentLine("Aaaa-bb");
      l = new DevelopmentLine("Aaaa-bb-ccc");
      l = new DevelopmentLine("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

      l = new DevelopmentLine("Aaaa-0");

      assertTrue(true);

    } catch (IllegalArgumentException i) {
      fail(i.getMessage());
    }
  }

  public void testEquals() {

    DevelopmentLine l1 = new DevelopmentLine("Aaaa");
    DevelopmentLine l2 = new DevelopmentLine("Aaaa");
    DevelopmentLine l3 = new DevelopmentLine("AAAA");

    assertEquals(l1, l2);
    assertFalse(l1.equals(l3));
  }

  public void testPatchLine() {

    Version version = new Version("0-9");
    PatchLine l1 = new PatchLine(version);
    assertTrue("PATCHLINE_0-9".equals(l1.getName()));
  }

  public void testBla() {

    assertTrue("PATCHLINE_0-9".matches("PATCHLINE_\\d{1,4}-\\d{1,4}"));
  }

}
