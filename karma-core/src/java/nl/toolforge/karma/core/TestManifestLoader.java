package nl.toolforge.karma.core;

import junit.framework.TestCase;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.karma.core.prefs.Preferences;
import nl.toolforge.karma.core.cmd.CommandLoader;

import java.util.List;

/**
 *
 * @author D.A. Smedes
 */
public class TestManifestLoader extends BaseTest {

	public void testLoad1() {

		ManifestLoader ml = ManifestLoader.getInstance();

		try {
			Manifest m = ml.load("manifest-example.xml");

			assertEquals("There should be one source module in the manifest", 1, m.countSourceModules());

		} catch (KarmaException k) {
			k.printStackTrace();
			fail(k.getMessage());
		}
	}
}
