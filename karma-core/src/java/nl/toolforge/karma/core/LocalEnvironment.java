package nl.toolforge.karma.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import nl.toolforge.karma.core.prefs.Preferences;
import nl.toolforge.karma.core.prefs.UnavailableValueException;
import nl.toolforge.karma.core.location.LocationException;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Locale;

/**
 * This class maintains a users' local environment. The local environment is required to properly run Karma tools. It
 * contains references to directories and handles history settings.
 *
 * @author D.A. Smedes
 *
 * @version $Id:
 */
public class LocalEnvironment {

	private static Log logger = LogFactory.getLog(LocalEnvironment.class);

	private static Preferences preferences = null;

	/** Determines testmodes and disables file access */
	private static final boolean TESTMODE = (System.getProperty("TESTMODE") == null ? false : System.getProperty("TESTMODE").equals("true"));

	/**
	 * This property is used to determine the path to the bootstrap properties file (<code>karma.properties</code>). The
	 * convention is that a filename <b>NOT</b> starting with a <code>/</code> means that it will be loaded from the
	 * classpath, otherwise the value for the property will be considered an absolute filename.
	 */
	public static final String BOOTSTRAP_CONFIGURATION = "bootstrap.configuration";

	/**
	 * Property for the configuration directory where Karma should locate its configuration settings. Note that
	 * {@link #BOOTSTRAP_CONFIGURATION} is merely a startup thing. If that property is not set, this
	 * property will be read as a system property and when present, <code>karma.properties</code> will be read
	 * from the directory indicated by this property.
	 */
	public static final String CONFIGURATION_DIRECTORY = "configuration.directory";

	//
	// Other available properties
	//

	/**
	 * Property that identifies the current Karma locale. This property can also be passed to the JVM as
	 * <code>-Dlocale=&lt;locale&gt;</code>*/
	public static final String LOCALE = "locale";
	/** Property that identifies the user's development home directory. */
	public static final String DEVELOPMENT_HOME_DIRECTORY = "development.home";
	/** Property that identifies the directory where manifest files are stored. */
	public static final String MANIFEST_STORE_DIRECTORY = "manifest.store";
	/** Property that identifies the directory where location files are stored. */
	public static final String LOCATION_STORE_DIRECTORY = "location.store";
	/** Property that identifies the last used manifest. */
	public static final String MANIFEST_HISTORY = "manifest.history";

	private Properties configuration = new Properties();

	/**
	 * <p>Creates and configures the users' local environment. The constructor will read configuration from a configuration
	 * identified by the system property {@link #BOOTSTRAP_CONFIGURATION}. If this property does not exist,
	 * <code>karma.properties</code> will be read from the classpath as a default. The configuration the local environment
	 * can then be extended by including the preferences stored on disk by calling {@link #includePreferences()}. The
	 * existing properties are not overridden; new properties will be appended.
	 */
	public LocalEnvironment() {

		// Bootstrapping configuration
		//
		try {

			String bootstrapConfigurationFile = System.getProperty(BOOTSTRAP_CONFIGURATION);
			bootstrapConfigurationFile = (bootstrapConfigurationFile == null ? null : bootstrapConfigurationFile.trim());

			if (bootstrapConfigurationFile == null) {
				throw new KarmaRuntimeException("Bootstrap PANIC. Property 'bootstrap.configuration' should be provided as system property.");
			}

			InputStream f = null;
			if (new File(bootstrapConfigurationFile).isAbsolute()) {
				// Load from disk
				//
				f = new FileInputStream(new File(bootstrapConfigurationFile));
			} else {
				// Load from classpath
				//
				f = getClass().getClassLoader().getResourceAsStream(bootstrapConfigurationFile);
			}

			configuration.load(f);

		} catch (Exception e) {
			throw new KarmaRuntimeException("Bootstrap PANIC. Karma could not be started, bootstrap configuration could not be found.");
		}
	}

	// TODO uitzoeken ...
	//
	public final synchronized void includePreferences() {

		preferences = Preferences.getInstance(CONFIGURATION_DIRECTORY);

		// Add the user's configuration to the currently available preferences.
		//
		configuration = preferences.put(configuration);
	}

	public final void flushPreferences() {
		preferences.flush(getConfigurationDirectory());
	}

//
//	/**
//	 * <p>Initializes the user environment. The following steps are performed:
//	 *
//	 * <ul>
//	 *   <li/>The users' development home is created if the directory does not yet exist.
//	 * </ul>
//	 *
//	 * <p><b>NOTE: it is advised not to run </b>
//	 */
//	public final void initUserEnvironment() {
//
//		// Try to obtain the 'karma.configuration.directory property, which was optionally
//		// passed as a Java command line option.
//		//
//		karmaConfigurationDirectory = System.getProperty(CONFIGURATION_DIRECTORY);
//
//		if ((karmaConfigurationDirectory == null) || (karmaConfigurationDirectory.length() == 0)) {
//			karmaConfigurationDirectory = defaultKarmaConfigurationDirectory;
//		}
//
//		developmentHome = configuration.getProperty(DEVELOPMENT_HOME_DIRECTORY_PROPERTY);
//
//		if ((developmentHome == null) || (developmentHome.length() == 0)) {
//
//			// The property wasn't passed with the 'java' command during startup, so we're
//			// going to use the defaultDevelopmentHome property.
//			developmentHome = defaultDevelopmentHome;
//
//			// If the home directory does not yet exist, it will be created.
//			//
//			if (!TESTMODE) {
//				if (new File(developmentHome).mkdir()) {
//					logger.info("Development home directory " + developmentHome + " has been created.");
//				} else {
//					logger.info("Development home directory " + developmentHome + " already exists.");
//				}
//			}
//		}
//
//		// Get the set of properties from 'karma.properties', as they represent the user's
//		// choices for directories etc.
//		//
//	}

	/**
	 * Retrieves the current configuration directory.
	 *
	 * @return
	 */
	public String getConfigurationDirectory() {

		String configurationDirectory = System.getProperty(CONFIGURATION_DIRECTORY);

		if (configurationDirectory == null) {
			throw new UnavailableValueException("Configuration directory should be passed as a parameter to the JVM.");
		}

		return configurationDirectory;
	}



	/**
	 * Constructs a <code>Locale</code> from the <code>locale</code>-property value from <code>karma.properties</code> or
	 * the <code>locale</code> system property..
	 *
	 * @return A <code>Locale</code> object. Returns the default locale <code>Locale.ENGLISH</code> when the property
	 *         is not found.
	 */
	public static final Locale getLocale() {

		if (System.getProperty(LOCALE) != null) {
			Locale locale = new Locale(System.getProperty(LOCALE));
			logger.info("Current locale : " + locale);
			return locale;
		}
		logger.info("Current locale : " + Locale.ENGLISH);
		return Locale.ENGLISH;
	}

	public final File getManifestStore() throws ManifestException {

		File home = null;

		try {
			String manifestStore = (String) configuration.get(MANIFEST_STORE_DIRECTORY);
			logger.debug("Manifest store directory: " + manifestStore);

			home = new File(manifestStore);
		} catch (NullPointerException n) {
			throw new ManifestException(ManifestException.NO_MANIFEST_STORE_DIRECTORY);
		} catch (UnavailableValueException u) {
			throw new ManifestException(ManifestException.NO_MANIFEST_STORE_DIRECTORY);
		}

		return home;
	}

	public final File getLocationStore() throws LocationException {

		File home = null;

		try {
			String locationStore = (String) configuration.get(LOCATION_STORE_DIRECTORY);
			logger.debug("Location store directory: " + locationStore);

			home = new File(locationStore);
		} catch (NullPointerException n) {
			throw new LocationException(LocationException.NO_LOCATION_STORE_DIRECTORY);
		} catch (UnavailableValueException u) {
			throw new LocationException(LocationException.NO_LOCATION_STORE_DIRECTORY);
		}

		return home;
	}


	/**
	 *
	 * @return
	 */
	public final String getDevelopmentHome() {

		String developmentHome = configuration.getProperty(DEVELOPMENT_HOME_DIRECTORY);

		if ((developmentHome == null) || (developmentHome.length() == 0)) {

			// The property wasn't passed with the 'java' command during startup, so we're
			// going to use the defaultDevelopmentHome property.
			developmentHome = System.getProperty("user.home").concat(File.separator).concat("karma-projects");

			// If the home directory does not yet exist, it will be created.
			//
			if (!TESTMODE) {
				if (new File(developmentHome).mkdir()) {
					logger.info("Development home directory " + developmentHome + " has been created.");
				} else {
					logger.info("Development home directory " + developmentHome + " already exists.");
				}
			}
		}

		return developmentHome;
	}

	/**
	 * Sets the value of {@link #MANIFEST_HISTORY}. This value is later used to restore the last used manifest.
	 *
	 * @param manifestName The name of a manifest.
	 */

	public final void setManifestHistory(String manifestName) {
		preferences.put(MANIFEST_HISTORY, manifestName);
//		flush();
	}

	public final String getManifestHistory() {
		return preferences.get(MANIFEST_HISTORY);
	}

}
