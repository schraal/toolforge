package nl.toolforge.karma.core.location;

import junit.framework.TestCase;
import nl.toolforge.karma.core.KarmaException;

/**
 * @author D.A. Smedes
 * @version $Id:
 */
public class TestLocationFactory extends TestCase {

	public void testLoad() {

		LocationFactory factory = LocationFactory.getInstance();

		try {
			factory.load(getClass().getClassLoader().getResourceAsStream("test-locations.xml"),
				getClass().getClassLoader().getResourceAsStream("test-location-authentication.xml"));

			assertNotNull(factory.get("local-test"));
			assertNotNull(factory.get("subversion-test"));
			assertEquals(factory.getLocations().keySet().size(), 3);

		} catch (LocationException e) {
			fail(e.getMessage());
		}
	}

}