package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationLoader;
import nl.toolforge.karma.core.test.BaseTest;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestAuthenticator extends BaseTest {

  public void testAuthenticate() {

    LocationLoader loader = null;

    try {
      loader = getWorkingContext().getLocationLoader();
      loader.load();

      VersionControlSystem cvs = (VersionControlSystem) loader.get("test-id-1");

      Authenticator a = Authenticators.getAuthenticator(cvs);
      assertEquals(a.getUsername(), "asmedes");

    } catch (LocationException e) {
      fail(e.getMessage());
    } catch (AuthenticationException e) {
      fail(e.getMessage());
    }
  }


}
