package nl.toolforge.karma.core.location;

import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.karma.core.KarmaException;
import junit.framework.TestCase;

/**
 *
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class TestLocationFactory extends TestCase {

  public void testLoad() {

		LocationFactory factory =  LocationFactory.getInstance();

		try {
 			factory.load(
        getClass().getClassLoader().getResourceAsStream("locations.xml"),
        getClass().getClassLoader().getResourceAsStream("location-authentication.xml")
      );

			assertNotNull(factory.get("local-test"));
			assertNotNull(factory.get("subversion-test"));
      assertEquals(factory.getLocations().keySet().size(), 3);

		} catch (KarmaException e) {
			fail(e.getMessage());
		}
	}

}