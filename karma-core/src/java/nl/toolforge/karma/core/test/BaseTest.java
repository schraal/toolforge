package nl.toolforge.karma.core.test;

import junit.framework.TestCase;
import nl.toolforge.karma.core.prefs.Preferences;

/**
 * This testclass is highly recommended when writing JUnit testclasses for Karma. It initializes some basic stuff
 * that affects classes like {@link Preferences} etc.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class BaseTest extends TestCase {

	public void setUp() {

		// Fake some parameters that would have been passed to the JVM
		//

		// The following is required to allow the Preferences class to use the test-classpath
		//
		System.setProperty("TESTMODE", "true");
	}
}
