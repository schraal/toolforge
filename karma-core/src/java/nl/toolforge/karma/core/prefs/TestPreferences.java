package nl.toolforge.karma.core.prefs;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.test.BaseTest;

import java.io.File;

/**
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class TestPreferences extends BaseTest {

	public void testGetDevelopmentHome() {

    Preferences prefs = Preferences.getInstance();
		try {
			assertEquals(prefs.getDevelopmentHome(), new File(prefs.get("development.home")));
		} catch (KarmaException e) {
			fail(e.getMessage());
		}
	}

	public void testGetConfigurationDirectory() {

		Preferences prefs = Preferences.getInstance();
		assertEquals(prefs.getConfigurationDirectoryAsString(), "/tmp");
	}

}
