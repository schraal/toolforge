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
    try {
      assertEquals(LocationType.CVS, LocationType.getTypeInstance("cvs"));
      assertEquals(LocationType.DIRECTORY, LocationType.getTypeInstance("directory"));
      assertEquals(LocationType.SUBVERSION, LocationType.getTypeInstance("svn"));
    } catch (LocationException e) {
      fail(e.getMessage());
    }
  }
}
