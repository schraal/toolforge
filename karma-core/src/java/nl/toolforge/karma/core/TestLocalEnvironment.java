package nl.toolforge.karma.core;

import junit.framework.TestCase;
import nl.toolforge.karma.core.test.BaseTest;

import java.util.Locale;

public class TestLocalEnvironment extends BaseTest {

	public void testConstructor() {
    LocalEnvironment l = new LocalEnvironment();
		assertNotNull(l);
	}

	public void testGetConfigurationDirectory() {

	}

	public void testGetLocale() {

		// Should have been set by BaseTest ...
		//
		assertEquals(Locale.ENGLISH, LocalEnvironment.getLocale());
	}

	public void testManifestHistory() {

		LocalEnvironment l = new LocalEnvironment();

		String oldManifest = "karma-1-2-3";
	}

}
