package nl.toolforge.karma.core.prefs;

import junit.framework.TestCase;

import java.io.File;

import nl.toolforge.karma.core.prefs.Preferences;
import nl.toolforge.karma.core.test.BaseTest;

/**
 *
 * @author D.A. Smedes
 */
public class TestPreferences extends BaseTest {

	public void testDummy() {
		assertTrue(true);
	}

//	public void testGetManifestStore() {
//
//        // Fake 'java -Dkarma.home.directory=/home/asmedes/dev/projects
//        //
//        System.setProperty("karma.development.home", "/tmp");
//
//		try {
//			Preferences p = Preferences.getInstance(false);
//			assertEquals(p.getManifestStore().getPath(), "/tmp");
//		} catch (Exception e) {
//			fail(e.getMessage());
//		}
//    }
}
