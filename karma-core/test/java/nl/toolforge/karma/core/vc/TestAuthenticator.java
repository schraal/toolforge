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

import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationLoader;
import nl.toolforge.karma.core.test.BaseTest;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestAuthenticator extends BaseTest {

  public void testAuthenticatorKey() {

    AuthenticatorKey key1 = new AuthenticatorKey("default", "location-store");
    AuthenticatorKey key2 = new AuthenticatorKey("default", "location-store");

    assertTrue(key1.equals(key2));

    AuthenticatorKey key3 = new AuthenticatorKey("default", "manifest-store");

    assertFalse(key1.equals(key3));

    AuthenticatorKey key4 = new AuthenticatorKey("local", "location-store");

    assertFalse(key1.equals(key4));
  }

  public void testAuthenticate() {

    LocationLoader loader = null;

    try {
      loader = getWorkingContext().getLocationLoader();
      loader.load();

      VersionControlSystem cvs = (VersionControlSystem) loader.get("test-id-1");

      Authenticator a = Authenticators.getAuthenticator(new AuthenticatorKey(getWorkingContext().getName(), cvs.getId()));
      assertEquals(a.getUsername(), "asmedes");

    } catch (LocationException e) {
      fail(e.getMessage());
    } catch (AuthenticationException e) {
      fail(e.getMessage());
    }
  }


}
