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
package nl.toolforge.karma.core;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * @version $Id$
 */
public class TestVersion extends TestCase {

  public void testConstuctorWithValidVersions() {

    Version v = null;

    try {
      v = new Version("0-1");
      v = new Version("00-1");
      v = new Version("00-11");

      v = new Version("1-0");
      v = new Version("0-1");

      assertTrue(true);

    } catch (PatternSyntaxException i) {
      fail(i.getMessage());
    }
  }

  public void testConstuctorWithInvalidVersions() {

    Version v = null;

    try {
      v = new Version("");
      v = new Version(" ");
      v = new Version(" 0");
      v = new Version(" 0-");

      v = new Version("0");
      v = new Version("0-");
      v = new Version("0-1-");
      v = new Version("0-1-1-");
      v = new Version("0-1-1-1");

      v = new Version("0--1");
      v = new Version("0--1-0");

      v = new Version("0-1a");
      v = new Version("A-1");
      v = new Version("1-A");

      fail("Should have failed; invalid pattern.");

    } catch (IllegalArgumentException i) {
      assertTrue(true);
    }
  }

  public void testConstuctorWithInts() {

    Version v = new Version(new int[]{0, 1, 3});
    assertEquals("0-1-3", v.getVersionNumber());
  }

  public void testVersionParsing() {

    Version v = new Version("1-00");
    assertEquals(new Version("1-0"), v);

    v = new Version("1-01");
    assertEquals(new Version("1-1"), v);
    assertEquals(v.getVersionNumber(), "1-1");

    v = new Version("01-01");
    assertNotSame(new Version("01-10").getVersionNumber(), "01-10");
  }


  public void testCompare() {

    Version v1 = new Version("0-2");
    Version v2 = new Version("1-1");
    Version v3 = new Version("0-3");

    List s = new ArrayList();
    s.add(v1);
    s.add(v2);
    s.add(v3);

    Collections.sort(s);

    assertEquals(v1, (Version) s.get(0));
    assertEquals(v2, (Version) s.get(2));
    assertEquals(v3, (Version) s.get(1));
  }

  public void testBoundaries1() {

    Version v1 = new Version("0-2");
    Version v2 = new Version("0-3");

    assertTrue(v1.isLowerThan(v2));
    assertTrue(v2.isHigherThan(v1));
  }

  public void testBoundaries2() {

    Version v1 = new Version("0-2");
    Version v2 = new Version("1-3");

    assertTrue(v1.isLowerThan(v2));
    assertTrue(v2.isHigherThan(v1));
  }

  public void testBoundaries3() {

    Version v1 = new Version("0-11");
    Version v2 = new Version("0-2");

    assertTrue(v2.isLowerThan(v1));
    assertTrue(v1.isHigherThan(v2));
  }

  public void testBoundaries4() {

    Version v1 = new Version("1-0");
    Version v2 = new Version("0-1");

    assertTrue(v2.isLowerThan(v1));
    assertTrue(v1.isHigherThan(v2));
  }

  public void testPatch() {

    Version v = null;

    try {
      v = new Patch("0-0-1");
      v = new Patch("00-1-9");
//      v = new Patch("0000-1111-9999");

      assertTrue(true);

    } catch (PatternSyntaxException i) {
      fail(i.getMessage());
    }
  }

  public void testCreatePatch() {

    Version v1 = new Version("0-2");
    assertEquals(v1.createPatch(1), new Patch("0-2-1"));
  }

  public void testIncrease() {
    Version v1 = new Version("0-1");
    Version v2 = new Version("0-99");
    
    v1.increase();
    v2.increase();
    
    assertEquals("0-2",   v1.getVersionNumber());
    assertEquals("0-100", v2.getVersionNumber());
    
    Version p1 = new Patch("0-1-1");
    Version p2 = new Patch("0-1-99");
    
    p1.increase();
    p2.increase();
    
    assertEquals("0-1-2",   p1.getVersionNumber());
    assertEquals("0-1-100", p2.getVersionNumber());
  }
  
  public void testIncreaseMajor() {
    Version v1 = new Version("0-1");
    Version v2 = new Patch("0-1-1");
    
    v1.increaseMajor();
    
    assertEquals("1-0", v1.getVersionNumber());
    
    v1.increaseMajor();
    
    assertEquals("2-0", v1.getVersionNumber());
    
    try {
      v2.increaseMajor();
      
      fail("Increasing the major version of a Patch should have thrown an exception.");
    } catch (Exception e) {
      assertTrue(e instanceof IllegalStateException);
    }
  }
}
