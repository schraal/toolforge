package nl.toolforge.karma.core.prefs;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.ManifestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.*;

/**
 * <p>This class is used to store configuration settings. If Karma is used from the command line, then settings will be
 * stored in a file, otherwise the preferences will not be persisted, to enable multiple longer running instances to run
 * in parallel, for instance from a GUI.
 *
 * <p>This class is the reference class for the user's local environment. It shows the full configuration that must be
 * present for Karma to run properly as well as provides for methods to setup a default environment on a user's
 * development machine. Note that <emp>default</emp> settings as implemented by this class can <b>always</b> be
 * overridden by providing the preferred settings in the <code>karma.properties</karma> file.
 *
 * <p>A user's environment consists of the following :</p>
 *
 * <ul>
 *   <li/>A project home directory, where Karma enabled projects are stored. All project source code is checked out
 *        using this directory as the root. The environment variable identified by {@link #DEVELOPMENT_HOME_DIRECTORY_PROPERTY}
 *        is used to assign a directory.
 *   <li/>A configuration directory, where Karma gets its configuration. The configuration (property file) is loaded
 *        by the Karma runtime as a <code>bootstrap.configuration</code> system property.
 * </ul>
 *
 * <p>This class is a <b>BOOTSTRAP</b> class, not dependent on any other Karma classes.
 *
 * <p>Configuration that is changed when using Karma, is written in a <code>preferences</code> file in the directory
 * indicated by {@link #CONFIGURATION_DIRECTORY_PROPERTY}.
 *
 * @author W.M.Oosterom
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class Preferences {

	private static Log logger = LogFactory.getLog(Preferences.class);

	/**
	 * This property is used to determine the path to the bootstrap properties file (<code>karma.properties</code>). The
	 * convention is that a filename <b>NOT</b> starting with a <code>/</code> means that it will be loaded from the
	 * classpath, otherwise the value for the property will be considered an absolute filename.
	 */
	public static final String BOOTSTRAP_CONFIGURATION_FILE_PROPERTY = "bootstrap.configuration";

	/**
	 * Property for the configuration directory where Karma should locate configuration. Note that
	 * {@link #BOOTSTRAP_CONFIGURATION_FILE_PROPERTY} is merely a startup thing. If that property is not set, this
	 * property will be read from as a system property and when present, <code>karma.properties</code> will be read
	 * from the directory indicated by this property.
	 */
	public static final String CONFIGURATION_DIRECTORY_PROPERTY = "configuration.directory";

	// The following properties are present in the bootstrap configuration file (generally karma.properties).
	//

	/**
	 * The property that contains the development home directory for Karma; where projects are stored on the local
	 * harddisk.
	 */
	public static final String DEVELOPMENT_HOME_DIRECTORY_PROPERTY = "development.home";

	//
	//
	private static final boolean RUNTIME_MODE = System.getProperty("runtime.mode", "UNKNOWN").equals("COMMAND_LINE");

	/** Determines testmodes and disables file access */
	private static final boolean TESTMODE = (System.getProperty("TESTMODE") == null ? false : System.getProperty("TESTMODE").equals("true"));

	/** The property that contains the configuration directory for Karma. */
	//public static final String CONFIGURATION_DIRECTORY_PROPERTY = "karma.configuration.directory";

	public static final String LOCALE_PROPERTY = "locale";

	/**
	 * <p>The property that represents the directory on a user's local system where Karma administration files
	 * can be found. Karma will assume that this directory contains two subdirectories: <code>manifests</code>
	 * (containing the manifest xml files), and <code>repositories</code> (containing version control repository
	 * configuration files).
	 *
	 * <p>The <code>update-manifests<code>-command assumes a number of other properties (the
	 * <code>karma.manifest-store.vc</code> namespace, to be able to update these directories from a version controlled
	 * repository.
	 */
	public static final String MANIFEST_STORE_DIRECTORY_PROPERTY = "manifest-store.directory";

	public static final String MANIFEST_STORE_HOSTNAME_PROPERTY = "manifest-store.vc.hostname";
	public static final String MANIFEST_STORE_PORT_PROPERTY = "manifest-store.vc.port";
	public static final String MANIFEST_STORE_REPOSITORY_PROPERTY = "manifest-store.vc.repository";
	public static final String MANIFEST_STORE_USERNAME_PROPERTY = "manifest-store.vc.username";
	public static final String MANIFEST_STORE_PASSWORD_PROPERTY = "manifest-store.vc.password";

	/** The property that identifies the manifest that was last used by Karma. */
	public static final  String MANIFEST_HISTORY_PROPERTY = "manifest.history";

	public static final String LOCATION_STORE_DIRECTORY_PROPERTY = "location-store.directory";

	private static List requiredProperties = new ArrayList();

	// Compile a list of all required properties, which can easily be referenced by clients to present all
	// required properties for the user environment.
	//
	static {
		requiredProperties.add(MANIFEST_STORE_DIRECTORY_PROPERTY);
		requiredProperties.add(MANIFEST_STORE_HOSTNAME_PROPERTY);
		requiredProperties.add(MANIFEST_STORE_PORT_PROPERTY);
		requiredProperties.add(MANIFEST_STORE_REPOSITORY_PROPERTY);
		requiredProperties.add(MANIFEST_STORE_USERNAME_PROPERTY);
		requiredProperties.add(MANIFEST_STORE_PASSWORD_PROPERTY);
	}

	// Private variable that holds the user's development home directory, the root
	// directory where the user has stored his/her Karma managed projects.
	//
	private static String developmentHome = null;

	// See 'developmentHome'; when that parameter is null, the defaultDevelopmentHome
	// will be returned. This paramater is initialized in a static block, based on the
	// user's operation system.
	//
	private static String defaultDevelopmentHome = System.getProperty("user.home").concat(File.separator).concat("karma-projects");

	// The configuration directory where karma can locate its configuration files
	//
	private static String karmaConfigurationDirectory = null;

	// The default configuration directory where karma can locate its configuration files if
	// no configuration directory is specified as a command-line property.
	//
	private static String defaultKarmaConfigurationDirectory = System.getProperty("user.home").concat(File.separator).concat(".karma");

	//private OsFamily operatingSystemFamily = null;
//	private static String operatingSystem = null;

	private static Preferences instance = null;

	/**
	 * <p>The constructor will check the user's environment and create whatever needs to be created as per
	 * the documentation on http://toolforge.sourceforge.net/karma/core.
	 *
	 * <p>The following actions are performed:</p>
	 *
	 * <ul>
	 *   <li/><code>${user.home}.karma</code> will be created as the user's configuration directory when
	 *        no system property <code>karma.configuration.directory</code> can be found.
	 * </ul>
	 *
	 *
	 * @return A <code>Preferences<code> instance, ready to rock 'n roll !
	 *
	 */
	public synchronized static Preferences getInstance() {

		if (instance == null) {
			instance = new Preferences();
			instance.initUserEnvironment();
		}
		return instance;
	}

	private Properties values = new Properties();
	private Properties bootstrapConfiguration = new Properties();

	// Private constructor to prevent direct instantiation
	//
	private Preferences() {

		// Bootstrapping configuration
		//
		try {

			String bootstrapConfigurationFile = System.getProperty(BOOTSTRAP_CONFIGURATION_FILE_PROPERTY);
			bootstrapConfigurationFile = (bootstrapConfigurationFile == null ? null : bootstrapConfigurationFile.trim());

			if (bootstrapConfigurationFile == null) {
				throw new KarmaRuntimeException(
					"Bootstrap PANIC. Property 'bootstrap.configuration' should be provided as system property.");
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

			bootstrapConfiguration.load(f);

		} catch (Exception e) {
			throw new KarmaRuntimeException(
				"Bootstrap PANIC. Karma could not be started, bootstrap configuration could not be found.");
		}

		// We first try to load the preferences that might be there on disk
		//
		load(getConfigurationDirectoryAsString());

		// Next we traverse the bootstrap properties in the karma.properties file, which could override
		// the preferences that are loaded before. The preferences might be outdated.
		//
		try {

			for (Enumeration e = bootstrapConfiguration.propertyNames(); e.hasMoreElements();) {
				String prop = (String) e.nextElement();
				put(prop, bootstrapConfiguration.getProperty(prop));
			}

		} catch (Exception e) {
			logger.error("Could not load startup configuration, exiting...");
			throw new KarmaRuntimeException("Could not load startup configuration, exiting...", e);
		}

		// Store preferences to a file.
		//
		flush();
	}

	private void initUserEnvironment() {

		// Try to obtain the 'karma.configuration.directory property, which was optionally
		// passed as a Java command line option.
		//
		karmaConfigurationDirectory = System.getProperty(CONFIGURATION_DIRECTORY_PROPERTY);

		if ((karmaConfigurationDirectory == null) || (karmaConfigurationDirectory.length() == 0)) {
			karmaConfigurationDirectory = defaultKarmaConfigurationDirectory;
		}

		developmentHome = values.getProperty(DEVELOPMENT_HOME_DIRECTORY_PROPERTY);

		if ((developmentHome == null) || (developmentHome.length() == 0)) {

			// The property wasn't passed with the 'java' command during startup, so we're
			// going to use the defaultDevelopmentHome property.
			developmentHome = defaultDevelopmentHome;

			// If the home directory does not yet exist, it will be created.
			//
			if (new File(developmentHome).mkdir()) {
				logger.info("Development home directory " + developmentHome + " has been created.");
			} else {
				logger.info("Development home directory " + developmentHome + " already exists.");
			}
		}

		// Get the set of properties from 'karma.properties', as they represent the user's
		// choices for directories etc.
		//
	}


	/**
	 * Retrieves a property value by <code>key</code>. If the value for <code>key</code> is not found, the
	 * <code>defaultValue</code> will be returned.
	 *
	 * @param key
	 * @param defaultValue
	 *
	 * @return The value for <code>key</code>, or <code>defaultValue</code> if no value was found for key
	 *         <code>key</code>.
	 */
	public String get(String key, String defaultValue) {
		String returnValue = defaultValue;

		if ((returnValue = values.getProperty(key)) == null) {
			returnValue = defaultValue;
		}

		return returnValue;
	}

	public String get(String key) {
		String returnValue = values.getProperty(key);

		if (returnValue == null) {
			throw new UnavailableValueException("No value found in preferences for key \"" + key + "\"");
		} else {
			return returnValue;
		}
	}


	public int getInt(String key, int defaultValue) {
		try {
			return Integer.parseInt(get(key, Integer.toString(defaultValue)));
		}
		catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public int getInt(String key) {
		return Integer.parseInt(get(key));
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return Boolean.getBoolean(get(key, (new Boolean(defaultValue)).toString()));
	}

	public void put(String key, String value) {
		values.setProperty(key, value);
	}

	public void putInt(String key, int value) {
		put(key, Integer.toString(value));
	}

	public void putBoolean(String key, boolean value) {
		put(key, (new Boolean(value)).toString());
	}

	public void flush() {

		if (!TESTMODE) {
			if (RUNTIME_MODE) {
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(new File(getConfigurationDirectoryAsString(), "preferences"));
					values.store(out, "Karma Preferences");

					logger.info("Karma preferences written to " + getConfigurationDirectoryAsString());

				} catch (IOException e) {
					logger.error("Could not write preferences to " + getConfigurationDirectoryAsString());
				}
				finally {
					if (out != null) {
						try {
							out.close();
						}
						catch (IOException e) {
							// ignore
						}
					}
				}
			}
		}
	}

	/**
	 * Loads preference data from a file called <code>preferences</code>. If the file cannot be found, fine, we ignore
	 * that and print a message to the log-file. This is handy for JUnit tests. The code remains the same ...
	 *
	 * @param configDir
	 */
	private void load(String configDir) {

		if (!TESTMODE) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(new File(configDir, "preferences"));
				values.load(in);
			}
			catch (IOException e) {
				logger.info("No preferences could be found. Ignoring ...");
			}
			finally {
				if (in != null) {
					try {
						in.close();
					}
					catch (IOException e) {
						logger.info("No preferences could be found. Ignoring ...");
					}
				}
			}
		}
	}

	/**
	 * Returns a <code>List</code> of all required configuration for a user environment.
	 *
	 * @return A <code>List</code> with all required properties for a user environment.
	 */
	public final List getRequiredConfiguration() {
		return requiredProperties;
	}

	/**
	 * Shows the current configuration. Karma will try and resolve all missing configuration items and present them
	 * in a <code>HashMap</code>.
	 *
	 * @return A <code>Map</code> with the current status of configuration for a user environment.
	 */
	public final Map getCurrentConfiguration() throws KarmaRuntimeException {

		try {
			throw new KarmaRuntimeException("Not implemented lazy bastard you are !");
		} catch (Exception e) {
			throw new KarmaRuntimeException("Missing configuration. Check documentation.");
		}
	}

	public final void createHome() {
	}

	/**
	 * Retrieves the user's development home directory, where all projects are stored.
	 *
	 * @return The user's karma home directory.
	 * @throws KarmaException When the development home directory cannot be referenced to by a <code>File</code>.
	 */
	public final File getDevelopmentHome() throws KarmaException {

		File home = null;

		try {
			if (developmentHome == null) {
				home = new File(defaultDevelopmentHome);
			}  else {
				home = new File(developmentHome);
			}
		} catch (NullPointerException n) {
			throw new KarmaException(KarmaException.NO_DEVELOPMENT_HOME);
		}

		return home;
	}

	/**
	 * Returns the user's karma home directory as a String.
	 *
	 * @return
	 */
	public final String getKarmaHomeDirectoryAsString() {

		if (developmentHome == null) {
			return defaultDevelopmentHome;
		}  else {
			return developmentHome;
		}
	}

	/**
	 * Retrieves the user's configuration directory, where all projects are stored. This method uses the
	 * <code>configuration.directory</code> property from <code>karma.properties</code>.
	 *
	 * @return The user's karma configuration directory.
	 * @throws NullPointerException When the
	 */
	public final File getConfigurationDirectory() throws KarmaException {

		File home = null;

		try {
			if (karmaConfigurationDirectory == null) {
				home = new File(defaultKarmaConfigurationDirectory);
			}  else {
				home = new File(karmaConfigurationDirectory);
			}
		} catch (NullPointerException n) {
			throw new KarmaException(KarmaException.NO_CONFIGURATION_DIRECTORY);
		}

		return home;
	}

	/**
	 * Returns the user's karma configuration directory as a String.
	 *
	 * @return The full pathname of Karma's configuration directory as a string.
	 */
	public final String getConfigurationDirectoryAsString() {

		if (karmaConfigurationDirectory == null) {
			return defaultKarmaConfigurationDirectory;
		}  else {
			return karmaConfigurationDirectory;
		}
	}
	/**
	 * Gets the operating system (family)name (WINDOWS, UNIX) for the environment the user works on..
	 *
	 * @return The operating system (family)name that the user works on.
	 */
//	public final String getOperationSystem() {
//		return operatingSystem;
//	}

	public final File getManifestStore() throws ManifestException {

		File home = null;

		try {
			logger.debug("Manifest store directory: " + get(MANIFEST_STORE_DIRECTORY_PROPERTY));

			home = new File(get(MANIFEST_STORE_DIRECTORY_PROPERTY));
		} catch (NullPointerException n) {
			throw new ManifestException(ManifestException.NO_MANIFEST_STORE_DIRECTORY);
		} catch (UnavailableValueException u) {
			throw new ManifestException(ManifestException.NO_MANIFEST_STORE_DIRECTORY);
		}

		return home;
	}


	/**
	 * Constructs a <code>Locale</code> fromt the <code>locale</code>-property value from <code>karma.properties</code>.
	 *
	 * @return A <code>Locale</code> objects. Returns the default locale <code>Locale.ENGLISH</code> when the property
	 *         is not found.
	 */
	public final Locale getLocale() {

		try {
			Locale locale = new Locale(get(LOCALE_PROPERTY));
			logger.info("Current locale : " + locale);
			return locale;
		} catch (UnavailableValueException u) {
			logger.info("Property 'locale' has not been set. Default to ENGLISH.");
			return Locale.ENGLISH;
		}
	}

	public final void setManifestHistory(String manifestName) {
		put(MANIFEST_HISTORY_PROPERTY, manifestName);
//		flush();
	}
}


