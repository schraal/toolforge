package nl.toolforge.karma.core.test;

import junit.framework.TestCase;
import nl.toolforge.karma.core.prefs.Preferences;

/**
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class BaseTest extends TestCase {

	public void setUp() {
		// Fake some parameters that would have been passed to the JVM
		//
		System.setProperty(Preferences.CONFIGURATION_DIRECTORY_PROPERTY, "/home/asmedes/.karma");
		System.setProperty("MODE", "COMMAND_LINE_MODE");
	}
}
