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
