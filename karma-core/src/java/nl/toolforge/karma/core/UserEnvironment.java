package nl.toolforge.karma.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This class is the reference class for the user's local environment. It shows the full configuration that must be
 * present for Karma to run properly as well as provides for methods to setup a default environment on a user's
 * development machine. Note that <emp>default</emp> settings as implemented by this class can <b>always</b> be
 * overridden by providing the preferred settings in the <code>karma.properties</karma> file.
 *
 * <p>A user's environment consists of the following :</p>
 *
 * <ul>
 *   <li/>A home directory, where Karma enabled projects are stored. ALl project source code is checked out using this
 *        directory as the root. The environment variable <code>KARMA_HOME</code> or <code>karma.home</code> can be
 *        used to assign a directory. Using '<code>java -Dkarma.home</code> overrides the value of
 *        <code>KARMA_HOME</code>. This way, multiple threads of Karma can be run.
 *   <li/>A configuration directory, where Karma gets its configuration.  This configuration directory is presented to
 *        the Karma runtime as a <code>karma.configuration.directory</code> system property.
 *   <li/>A <code>karma.properties</code>, contained in the configuration directory. This property file has a number of
 *        default properties:
 *        <table>
 *          <tr>
 *            <td><code>karma.development.home</code></td>
 *            <td>Path to the directory where Karma will checkout manifests.</td>
 *          </tr>
 *        </table>
 * </ul>
 *
 * @author D.A. Smedes
 */
public final class UserEnvironment {

    //private static Log logger = LogFactory.getLog(UserEnvironment.class);

    /** The property that contains the configuration directory for Karma. */
    public static final String CONFIGURATION_DIRECTORY_PROPERTY = "karma.configuration.directory";

    /** The property that contains the development home directory for Karma; where projects are stored on the local
     * harddisk.
     */
    public static final String HOME_DIRECTORY_PROPERTY = "karma.development.home";

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
    public static final String MANIFEST_STORE_DIRECTORY_PROPERTY = "karma.manifest-store.directory";

    public static final String MANIFEST_STORE_HOSTNAME_PROPERTY = "karma.manifest-store.vc.hostname";
    public static final String MANIFEST_STORE_PORT_PROPERTY = "karma.manifest-store.vc.port";
    public static final String MANIFEST_STORE_REPOSITORY_PROPERTY = "karma.manifest-store.vc.repository";
    public static final String MANIFEST_STORE_USERNAME_PROPERTY = "karma.manifest-store.vc.username";
    public static final String MANIFEST_STORE_PASSWORD_PROPERTY = "karma.manifest-store.vc.password";

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

    // Private variable that holds the user's Karma home directory, the root
    // directory where the user has stored his/her Karma managed projects.
    //
    private static String karmaHomeDirectory = null;

    // See 'karmaHomeDirectory'; when that parameter is null, the defaultKarmaHomeDirectory
    // will be returned. This paramater is initialized in a static block, based on the
    // user's operation system.
    //
    private static String defaultKarmaHomeDirectory = System.getProperty("user.home").concat(File.separator).concat("karma-projects");

    // The configuration directory where karma can locate its configuration files
    //
    private static String karmaConfigurationDirectory = null;

    // The default configuration directory where karma can locate its configuration files if
    // no configuration directory is specified as a command-line property.
    //
    private static String defaultKarmaConfigurationDirectory = System.getProperty("user.home").concat(File.separator).concat(".karma");

    //private OsFamily operatingSystemFamily = null;
    private static String operatingSystem = null;

	private static UserEnvironment instance = null;

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
     * @param create Determines if resources should be created when non-existing (<code>true</code>). This
     *               is usefull for testing purposes (test clients would call this constructor with
     *               <code>false</code>.
     *
     * @throws KarmaRuntimeException When creation of the Karma configuration directory failed (creation is
     *                               done when the user has not provided a configuration directory property
     *                               when starting a Karma user interface).
     */
    public static UserEnvironment getInstance(boolean create) {

		if (instance == null) {
			instance = new UserEnvironment();

			instance.init(create);
		}
		return instance;

	}

	private void init(boolean create) {

        // Determine the Operation System the user works on
        //
        //operatingSystemFamily = Os.getFamily(System.getProperty("os.name"));
        operatingSystem = System.getProperty("os.name");

        // Try to obtain the 'karma.configuration.directory property, which was optionally
        // passed as a Java command line option.
        //
        karmaConfigurationDirectory = System.getProperty(CONFIGURATION_DIRECTORY_PROPERTY);

        if ((karmaConfigurationDirectory == null) || (karmaConfigurationDirectory.length() == 0)) {

            // The property wasn't passed with the 'java' command during startup, so we're
            // going to use the defaultKarmaConfigurationDirectory property.
            karmaConfigurationDirectory = defaultKarmaConfigurationDirectory;

            // If this configuration directory does not yet exist, it will be created, but only
            // if the 'create' parameter is 'true'
            //
            if (create == true) {

                try {
                    new File(karmaConfigurationDirectory).createNewFile();
                } catch (IOException i) {
                    throw new KarmaRuntimeException("Creation of Karma configuration directory failed.");
                }
            }
        }

        karmaHomeDirectory = System.getProperty(HOME_DIRECTORY_PROPERTY);

        if ((karmaHomeDirectory == null) || (karmaHomeDirectory.length() == 0)) {

            // The property wasn't passed with the 'java' command during startup, so we're
            // going to use the defaultKarmaHomeDirectory property.
            karmaHomeDirectory = defaultKarmaHomeDirectory;

            // If the home directory does not yet exist, it will be created, but only
            // if the 'create' parameter is 'true'
            //
            if (create == true) {

                try {
                    new File(karmaHomeDirectory).createNewFile();
                } catch (IOException i) {
                    throw new KarmaRuntimeException("Creation of Karma home directory failed.");
                }
            }
        }

        // Get the set of properties from 'karma.properties', as they represent the user's
        // choices for directories etc.
        //
    }

    /**
     * Returns a <code>List</code> of all required configuration for a user environment.
     *
     * @return A <code>List</code> with all required properties for a user environment.
     */
    public static List getRequiredConfiguration() {
        return requiredProperties;
    }

    /**
     * Shows the current configuration. Karma will try and resolve all missing configuration items and present them
     * in a <code>HashMap</code>.
     *
     * @return A <code>Map</code> with the current status of configuration for a user environment.
     */
    public static Map getCurrentConfiguration() throws KarmaRuntimeException {

        try {
            throw new KarmaRuntimeException("Not implemented yet. Lazy bastard you are Arjen.");
        } catch (Exception e) {
            throw new KarmaRuntimeException("Current configuration could not be resolved. This is serious!");
        }
    }

    public static void createHome() {
    }

    /**
     * Retrieves the user's home directory, where all projects are stored.
     *
     * @return The user's karma home directory.
     * @throws NullPointerException When the
     */
    public static File getKarmaHomeDirectory() {

        File home = null;

        try {
            if (karmaHomeDirectory == null) {
                home = new File(defaultKarmaHomeDirectory);
            }  else {
                home = new File(karmaHomeDirectory);
            }
        } catch (NullPointerException n) {
            //logger.debug("Karma home directory is null. Without this directory, nothing can work");
            throw n;
        }

        return home;
    }

    /**
     * Returns the user's karma home directory as a String.
     *
     * @return
     */
    public static String getKarmaHomeDirectoryAsString() {

        if (karmaHomeDirectory == null) {
            return defaultKarmaHomeDirectory;
        }  else {
            return karmaHomeDirectory;
        }
    }

        /**
     * Retrieves the user's configuration directory, where all projects are stored.
     *
     * @return The user's karma configuration directory.
     * @throws NullPointerException When the
     */
    public static File getConfigurationDirectory() {

        File home = null;

        try {
            if (karmaConfigurationDirectory == null) {
                home = new File(defaultKarmaConfigurationDirectory);
            }  else {
                home = new File(karmaConfigurationDirectory);
            }
        } catch (NullPointerException n) {
            //logger.debug("Karma configuration directory is null. Without this directory, nothing can work");
            throw n;
        }

        return home;
    }

    /**
     * Returns the user's karma configuration directory as a String.
     *
     * @return The full pathname of Karma's configuration directory as a string.
     */
    public static String getKarmaConfigurationDirectoryAsString() {

        if (karmaConfigurationDirectory == null) {
            return defaultKarmaConfigurationDirectory;
        }  else {
            return karmaConfigurationDirectory;
        }
    }
    /**
     * Gets the operating system (family)name (WINDOWS, UNIX) that the current user works on.
     *
     * @return The operating system (family)name that the user works on.
     */
    public static String getOperationSystem() {
        return operatingSystem;
    }
}
