package nl.toolforge.karma.core.test;

import junit.framework.TestCase;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.prefs.Preferences;

/**
 * This testclass is highly recommended when writing JUnit testclasses for Karma. It initializes some basic stuff
 * that affects classes like {@link Preferences} etc.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class BaseTest extends TestCase {

	public void setUp() {

		// Fake some parameters that would have been passed to the JVM
		//

		// The following is required to allow the Preferences class to use the test-classpath
		//
		System.setProperty("TESTMODE", "true");
		System.setProperty("locale", "en");

		// Overrides karma.properties for Junit testing.
		//
		System.setProperty(LocalEnvironment.BOOTSTRAP_CONFIGURATION, "test/test-karma.properties");

		// Initialize the LocationFactory
		//
		try {
			LocationFactory locationFactory = LocationFactory.getInstance();
			locationFactory.load(getClass().getClassLoader().getResourceAsStream("test/locations.xml"),
					getClass().getClassLoader().getResourceAsStream("test/location-authentication.xml"));

		} catch (KarmaException e) {
			throw new KarmaRuntimeException("BaseTest setup error", e);
		}
	}
}
