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
package nl.toolforge.karma.core.location;

import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;

/**
 * @author D.A. Smedes
 * @version $Id:
 */
public class TestLocationLoader extends BaseTest {

  public void testLoad() {

    LocationLoader loader = null;

    try {
      loader = getWorkingContext().getLocationLoader();
      loader.load();

    } catch (LocationException e) {
      fail(e.getMessage());
    }

    try {
      assertNotNull(loader.get("test-id-1"));
      assertNotNull(loader.get("test-id-4"));

      assertEquals(loader.getLocations().keySet().size(), 6);

      assertEquals(((CVSLocationImpl) loader.get("test-id-1")).getUsername(), "asmedes");

    } catch (LocationException e) {
      fail(e.getMessage());
    }

    try {

      loader.get("bla");

      fail("This id doesn't exist.");

    } catch (LocationException e) {
      assertTrue(true);
    }
  }

}