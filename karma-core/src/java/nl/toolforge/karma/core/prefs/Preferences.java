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
 * <p/>
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
	 *
	 * @deprecated todo : should be removed; moved to {@link nl.toolforge.karma.core.LocalEnvironment}
	 */
	public static final String BOOTSTRAP_CONFIGURATION_FILE_PROPERTY = "bootstrap.configuration";

	/**
	 * Property for the configuration directory where Karma should locate configuration. Note that
	 * {@link #BOOTSTRAP_CONFIGURATION_FILE_PROPERTY} is merely a startup thing. If that property is not set, this
	 * property will be read from as a system property and when present, <code>karma.properties</code> will be read
	 * from the directory indicated by this property.
	 *
	 * @deprecated todo : should be removed; moved to {@link nl.toolforge.karma.core.LocalEnvironment}
	 */
//	public static final String CONFIGURATION_DIRECTORY = "configuration.directory";

	// The following properties are present in the bootstrap configuration file (generally karma.properties).
	//

	/**
	 * The property that contains the development home directory for Karma; where projects are stored on the local
	 * harddisk.
	 */
//	public static final String DEVELOPMENT_HOME_DIRECTORY_PROPERTY = "development.home";

	//
	//
//	private static final boolean RUNTIME_MODE = System.getProperty("runtime.mode", "UNKNOWN").equals("COMMAND_LINE");

	/**
	 * Determines testmodes and disables file access
	 */
	private static final boolean TESTMODE = (System.getProperty("TESTMODE") == null ? false : System.getProperty("TESTMODE").equals("true"));

	/**
	 * The property that contains the configuration directory for Karma.
	 */
	//public static final String CONFIGURATION_DIRECTORY = "karma.configuration.directory";

//	public static final String LOCALE_PROPERTY = "locale";

	/**
	 * <p>The property that represents the directory on a user's local system where Karma administration files
	 * can be found. Karma will assume that this directory contains two subdirectories: <code>manifests</code>
	 * (containing the manifest xml files), and <code>repositories</code> (containing version control repository
	 * configuration files).
	 * <p/>
	 * <p>The <code>update-manifests<code>-command assumes a number of other properties (the
	 * <code>karma.manifest-store.vc</code> namespace, to be able to update these directories from a version controlled
	 * repository.
	 */
//	public static final String MANIFEST_STORE_DIRECTORY_PROPERTY = "manifest-store.directory";
//
//	public static final String MANIFEST_STORE_HOSTNAME_PROPERTY = "manifest-store.vc.hostname";
//	public static final String MANIFEST_STORE_PORT_PROPERTY = "manifest-store.vc.port";
//	public static final String MANIFEST_STORE_REPOSITORY_PROPERTY = "manifest-store.vc.repository";
//	public static final String MANIFEST_STORE_USERNAME_PROPERTY = "manifest-store.vc.username";
//	public static final String MANIFEST_STORE_PASSWORD_PROPERTY = "manifest-store.vc.password";

	/**
	 * The property that identifies the manifest that was last used by Karma.
	 */
//	public static final String MANIFEST_HISTORY_PROPERTY = "manifest.history";
//
//	public static final String LOCATION_STORE_DIRECTORY_PROPERTY = "location-store.directory";

//	private static List requiredProperties = new ArrayList();

	// Compile a list of all required properties, which can easily be referenced by clients to present all
	// required properties for the user environment.
	//
//	static {
//		requiredProperties.add(MANIFEST_STORE_DIRECTORY_PROPERTY);
//		requiredProperties.add(MANIFEST_STORE_HOSTNAME_PROPERTY);
//		requiredProperties.add(MANIFEST_STORE_PORT_PROPERTY);
//		requiredProperties.add(MANIFEST_STORE_REPOSITORY_PROPERTY);
//		requiredProperties.add(MANIFEST_STORE_USERNAME_PROPERTY);
//		requiredProperties.add(MANIFEST_STORE_PASSWORD_PROPERTY);
//	}

	// Private variable that holds the user's development home directory, the root
	// directory where the user has stored his/her Karma managed projects.
	//
//	private static String developmentHome = null;

	// See 'developmentHome'; when that parameter is null, the defaultDevelopmentHome
	// will be returned. This paramater is initialized in a static block, based on the
	// user's operation system.
	//
//	private static String defaultDevelopmentHome = System.getProperty("user.home").concat(File.separator).concat("karma-projects");

	// The configuration directory where karma can locate its configuration files
	//
//	private static String karmaConfigurationDirectory = System.getProperty("");

	// The default configuration directory where karma can locate its configuration files if
	// no configuration directory is specified as a command-line property.
	//
//	private static String defaultKarmaConfigurationDirectory = System.getProperty("user.home").concat(File.separator).concat(".karma");

	//private OsFamily operatingSystemFamily = null;
//	private static String operatingSystem = null;

	private static Preferences instance = null;

	/**
	 * <p>The constructor will check the user's environment and create whatever needs to be created as per
	 * the documentation on http://toolforge.sourceforge.net/karma/core.
	 * <p/>
	 * <p>The following actions are performed:</p>
	 * <p/>
	 * <ul>
	 * <li/><code>${user.home}.karma</code> will be created as the user's configuration directory when
	 * no system property <code>karma.configuration.directory</code> can be found.
	 * </ul>
	 *
	 * @return A <code>Preferences<code> instance, ready to rock 'n roll !
	 */
	public synchronized static Preferences getInstance(String configurationDirectory) {

		if (instance == null) {
			instance = new Preferences(configurationDirectory);
//			instance.initUserEnvironment();
		}
		return instance;
	}

	private Properties values = new Properties();
//	private Properties bootstrapConfiguration = new Properties();

	// Private constructor to prevent direct instantiation
	//
	private Preferences(String configurationDirectory) {

//		// Bootstrapping configuration
//		//
//		try {
//
//			String bootstrapConfigurationFile = System.getProperty(BOOTSTRAP_CONFIGURATION);
//			bootstrapConfigurationFile = (bootstrapConfigurationFile == null ? null : bootstrapConfigurationFile.trim());
//
//			if (bootstrapConfigurationFile == null) {
//				throw new KarmaRuntimeException("Bootstrap PANIC. Property 'bootstrap.configuration' should be provided as system property.");
//			}
//
//			InputStream f = null;
//			if (new File(bootstrapConfigurationFile).isAbsolute()) {
//				// Load from disk
//				//
//				f = new FileInputStream(new File(bootstrapConfigurationFile));
//			} else {
//				// Load from classpath
//				//
//				f = getClass().getClassLoader().getResourceAsStream(bootstrapConfigurationFile);
//			}
//
//			bootstrapConfiguration.load(f);
//
//		} catch (Exception e) {
//			throw new KarmaRuntimeException("Bootstrap PANIC. Karma could not be started, bootstrap configuration could not be found.");
//		}

		// We first try to load the preferences that might be there on disk
		//
		load(configurationDirectory);

//		// Next we traverse the bootstrap properties in the karma.properties file, which could override
//		// the preferences that are loaded before. The preferences might be outdated.
//		//
//		try {
//
//			for (Enumeration e = bootstrapConfiguration.propertyNames(); e.hasMoreElements();) {
//				String prop = (String) e.nextElement();
//				values.put(prop, bootstrapConfiguration.getProperty(prop));
//			}
//
//		} catch (Exception e) {
//			logger.error("Could not load startup configuration, exiting...");
//			throw new KarmaRuntimeException("Could not load startup configuration, exiting...", e);
//		}

		// Store preferences to a file.
		//
		flush(configurationDirectory);
	}

	/**
	 * Adds all properties in <code>properties</code> to this <code>Preferences</code> instance. Existing properties will
	 * be overwritten.
	 *
	 * @param properties A <code>Properties</code> object.
	 */
	public Properties put(Properties properties) {

		try {

			for (Enumeration e = properties.propertyNames(); e.hasMoreElements();) {
				String prop = (String) e.nextElement();
				values.put(prop, properties.getProperty(prop));
			}

		} catch (Exception e) {
			throw new KarmaRuntimeException("Could not add properties to preferences.", e);
		}

		return values;
	}

	/**
	 * Retrieves a property value by <code>key</code>. If the value for <code>key</code> is not found, the
	 * <code>defaultValue</code> will be returned.
	 *
	 * @param key The key for the property.
	 * @param defaultValue
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
		} catch (NumberFormatException e) {
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

	public final void flush(String configurationDirectory) {

		if (!TESTMODE) {
//			if (RUNTIME_MODE) {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(new File(configurationDirectory, "preferences"));
				values.store(out, "Karma Preferences");

				logger.info("Karma preferences written to " + configurationDirectory);

			} catch (IOException e) {
				logger.error("Could not write preferences to " + configurationDirectory);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// ignore
					}
				}
//				}
			}
		}
	}

	/**
	 * Loads preference data from a file called <code>preferences</code>. If the file cannot be found, fine, we ignore
	 * that and print a message to the log-file. This is handy for JUnit tests. The code remains the same ...
	 *
	 * @param configDir
	 */
	public final Preferences load(String configDir) {

		if (!TESTMODE) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(new File(configDir, "preferences"));
				values.load(in);
			} catch (IOException e) {
				logger.info("No preferences could be found. Ignoring ...");
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						logger.info("No preferences could be found. Ignoring ...");
					}
				}
			}
		}
		return this;
	}

	/**
	 * Returns a <code>List</code> of all required configuration for a user environment.
	 *
	 * @return A <code>List</code> with all required properties for a user environment.
	 */
//	public final List getRequiredConfiguration() {
//		return requiredProperties;
//	}

	/**
	 * Shows the current configuration. Karma will try and resolve all missing configuration items and present them
	 * in a <code>HashMap</code>.
	 *
	 * @return A <code>Map</code> with the current status of configuration for a user environment.
	 */
//	public final Map getCurrentConfiguration() throws KarmaRuntimeException {
//
//		try {
//			throw new KarmaRuntimeException("Not implemented lazy bastard you are !");
//		} catch (Exception e) {
//			throw new KarmaRuntimeException("Missing configuration. Check documentation.");
//		}
//	}
//
//	public final void createHome() {
//	}

	/**
	 * Retrieves the user's development home directory, where all projects are stored.
	 *
	 * @return The user's karma home directory.
	 * @throws KarmaException When the development home directory cannot be referenced to by a <code>File</code>.
	 */
//	public final File getDevelopmentHome() throws KarmaException {
//
//		File home = null;
//
//		try {
//			if (developmentHome == null) {
//				home = new File(defaultDevelopmentHome);
//			} else {
//				home = new File(developmentHome);
//			}
//		} catch (NullPointerException n) {
//			throw new KarmaException(KarmaException.NO_DEVELOPMENT_HOME);
//		}
//
//		return home;
//	}

	/**
	 * Returns the user's karma home directory as a String.
	 *
	 * @return
	 */
//	public final String getKarmaHomeDirectoryAsString() {
//
//		if (developmentHome == null) {
//			return defaultDevelopmentHome;
//		} else {
//			return developmentHome;
//		}
//	}

	/**
	 * Retrieves the user's configuration directory, where all projects are stored. This method uses the
	 * <code>configuration.directory</code> property from <code>karma.properties</code>.
	 *
	 * @return The user's karma configuration directory.
	 * @throws NullPointerException When the
	 */
//	public final File getConfigurationDirectory() throws KarmaException {
//
//		File home = null;
//
//		try {
//			if (karmaConfigurationDirectory == null) {
//				home = new File(defaultKarmaConfigurationDirectory);
//			} else {
//				home = new File(karmaConfigurationDirectory);
//			}
//		} catch (NullPointerException n) {
//			throw new KarmaException(KarmaException.NO_CONFIGURATION_DIRECTORY);
//		}
//
//		return home;
//	}

	/**
	 * Returns the user's karma configuration directory as a String.
	 *
	 * @return The full pathname of Karma's configuration directory as a string.
	 */
//	public final String getConfigurationDirectoryAsString() {
//
//		if (karmaConfigurationDirectory == null) {
//			return defaultKarmaConfigurationDirectory;
//		} else {
//			return karmaConfigurationDirectory;
//		}
//	}

}


