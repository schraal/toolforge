/*
* Copyright (c) 2004 Your Corporation. All Rights Reserved.
*/
package nl.toolforge.karma.core.location;

import junit.framework.TestCase;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestLocationType extends TestCase {

  public void testGetTypeInstance() {
    assertEquals(LocationType.CVS, LocationType.getTypeInstance("cvs"));
    assertEquals(LocationType.DIRECTORY, LocationType.getTypeInstance("directory"));
    assertEquals(LocationType.SUBVERSION, LocationType.getTypeInstance("svn"));

    try {
      assertEquals(LocationType.SUBVERSION, LocationType.getTypeInstance("blaat"));
      fail("Should have failed because `blaat` is invalid.");
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }
  }
}
