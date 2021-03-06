package nl.toolforge.karma.core.boot;

import com.mockobjects.dynamic.Mock;
import junit.framework.TestCase;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestStore extends TestCase {

  private WorkingContext ctx = new WorkingContext("blaat");

  private Location location = null;

  public void setUp() {
    Mock mock = new Mock(Location.class);
    location = (Location) mock.proxy();
  }

  public void testGetModule() {

    ManifestStore store = new ManifestStore(ctx);
    try {
      store.getModule();

      fail("Module name has not been set.");

    } catch (KarmaRuntimeException k) {}
  }

}
