package nl.toolforge.karma.core.test;

import junit.framework.TestCase;
import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.location.LocationFactory;
//import nl.toolforge.karma.core.prefs.Preferences;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * This testclass is highly recommended when writing JUnit testclasses for Karma. It initializes some basic stuff
 * that affects classes like {@link java.util.prefs.Preferences} etc.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class BaseTest extends TestCase {

	private Properties p = null;

	public void setUp() {

		// Fake some parameters that would have been passed to the JVM
		//

		// The following is required to allow the Preferences class to use the test-classpath
		//
		System.setProperty("TESTMODE", "true");
		System.setProperty("locale", "en");

		// Overrides karma.properties for Junit testing.
		//
//		System.setProperty(LocalEnvironment.BOOTSTRAP_CONFIGURATION_DIRECTORY, "test/test-karma.properties");
//todo remove

		File f1 = null;
		File f2 = null;
		File f3 = null;

		try {
			f1 = MyFileUtils.createTempDirectory();
			f2 = MyFileUtils.createTempDirectory();
			f3 = MyFileUtils.createTempDirectory();
		} catch (IOException e) {
			e.printStackTrace();
		}

		p = new Properties();
		p.put(LocalEnvironment.DEVELOPMENT_HOME_DIRECTORY, f1.getPath());
		p.put(LocalEnvironment.MANIFEST_STORE_DIRECTORY, f2.getPath());
		p.put(LocalEnvironment.LOCATION_STORE_DIRECTORY, f3.getPath());

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

	public final Properties getProperties() {
		return p;
	}

}
