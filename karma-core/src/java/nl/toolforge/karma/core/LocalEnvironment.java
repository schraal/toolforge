package nl.toolforge.karma.core;

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * <p>This class maintains a users' local environment. The local environment is required to properly run Karma tools. It
 * contains references to directories, which are necessary to bootstrap Karma.
 *
 * <p>Bootstrapping Karma also means that the log-system (Log4j, obviously) is initialized. This class initializes a
 * default logging system, which can be overridden by placing a <code>log4j.xml</code> and <code>log4j.dtd</code> on
 * your runtime classpath when running a Karma client application. The default logging configuration can be tweaked a
 * little by providing a <code>loglevel</code>
 *
 * <p>When <code>LocalEnvironment</code> is instantiated two environment properties are , specify <code>karma.home</code> as en environment property to your JVM. If it doesn't exist,
 * Karma will resolve the default <code>user.home</code> property. To override the default log-level
 * (<code>DEBUG</code>) for the logging system, set <code>loglevel</code> environment property (this only works for the
 * default logging system; if you supply log4j configuration files, this setting will be ignored).
 *
 * <p><code>LocalEnvironment</code> can be instanciated in two ways:
 *
 * <ol>
 * <li/>Pass a <code>Properties</code> Object to the <code>getInstance()</code> method. The properties are read from
 *      this Object.
 * <li/>Don't pass anything to the <code>getInstance()</code> method. The properties are read from file. The properties
 *      are assumed to be located in <code>$USER_HOME/.karma/karma.properties</code>.
 * </ol>
 *
 * <p>This class won't be instantiated when the required properties can't be found, or reference non-existing,
 * non-createable directories.
 *
 * @author D.A. Smedes
 * @author W.H. Schraal
 * @version $Id$
 */
public final class LocalEnvironment {

  private  static final String DEFAULT_CONVERSION_PATTERN = "%d{HH:mm:ss} [%5p] - %m%n";

  static {

    // Configure the logging system.
    //
    try {

      if (LocalEnvironment.class.getClassLoader().getResource("log4j.xml") != null) {

        Logger.getLogger(LocalEnvironment.class).info("'Log4j.xml' used to for logging configuration.");

      } else {

        // Initialize default logging.

        Logger root = Logger.getRootLogger();

        String karmaHome = System.getProperty("karma.home");
        karmaHome = (karmaHome == null ? System.getProperty("user.home") : karmaHome);

        // Create the log directory
        //
        new File(karmaHome, "logs").mkdirs();

        File defaultLogFile = new File(karmaHome, "logs/karma-default.log");

        PatternLayout patternLayout = new PatternLayout(DEFAULT_CONVERSION_PATTERN);
        FileAppender fileAppender = new FileAppender(patternLayout, defaultLogFile.getPath());
        fileAppender.setName("Default Karma logging appender.");

        // The default Appender for a Logger. We don't want it.
        //
        root.removeAppender(root.getAppender("console"));

        root.addAppender(fileAppender);

        String logLevel = null;
        logLevel = (System.getProperty("loglevel") == null ? "DEBUG" : System.getProperty("loglevel")); // Pass 1
        logLevel = (logLevel.toUpperCase().matches("ALL|DEBUG|ERROR|FATAL|INFO|OFF|WARN") ? logLevel : "DEBUG");  // Pass 2

        root.setLevel(Level.toLevel(logLevel));

        Logger.getLogger(LocalEnvironment.class).info(
            "Default logging configuration enabled; override by placing 'log4j.xml' and 'log4j.dtd' on your classpath.");
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new KarmaRuntimeException("*** PANIC *** Log4j system could not be initialized.");
    }
  }

  private static final Log logger = LogFactory.getLog(LocalEnvironment.class);

  /**
   * The working context for Karma. Right now, a working context is a directory on a users' local harddisk.
   */
  public static final String WORKING_CONTEXT_DIRECTORY = "working-context";

  /**
   * The property that identifies the local directory where jar dependencies can be found. Dependencies are
   * resolved Maven style, but to support environments where Maven is not available, the directory is configurable.
   * The default Maven repository is used when this property is not set in <code>karma.properties</code>.
   */
  public static final String JAR_REPOSITORY = "jar.repository";

  public static final String MANIFEST_STORE_HOST = "manifest-store.cvs.host";
  public static final String MANIFEST_STORE_PORT = "manifest-store.cvs.port";
  public static final String MANIFEST_STORE_REPOSITORY = "manifest-store.cvs.repository";
  public static final String MANIFEST_STORE_PROTOCOL = "manifest-store.cvs.protocol";
  public static final String MANIFEST_STORE_USERNAME = "manifest-store.cvs.username";

  public static final String LOCATION_STORE_HOST = "location-store.cvs.host";
  public static final String LOCATION_STORE_PORT = "location-store.cvs.port";
  public static final String LOCATION_STORE_REPOSITORY = "location-store.cvs.repository";
  public static final String LOCATION_STORE_PROTOCOL = "location-store.cvs.protocol";
  public static final String LOCATION_STORE_USERNAME = "location-store.cvs.username";

  private static final String PLACEHOLDER = "<...>";

  /** Property Object that stores the key-value pairs of the defined properties. */
  private static Properties configuration = null;

  /**
   * The one-and-only instance of this class.
   */
//  private static LocalEnvironment localEnvironment;

  /**
   * Initialize the <code>LocalEnvironment</code> with <code>karma.propeties</code>, located in
   * <code>$HOME/.karma</code>.
   *
   * @throws KarmaException
   */
  public static void initialize() throws KarmaException {
    initialize(null);
  }

  /**
   * Initialize the <code>LocalEnvironment</code> with <code>properties</code>.
   *
   * @param properties Properties object used to initialize the instance.
   * @throws KarmaException When {@link KarmaException.MISSING_CONFIGURATION} is detected, it will result in a Karma
   *   startup failure.
   */
  public static void initialize(Properties properties) throws KarmaException {
    new LocalEnvironment(properties);
  }

  /**
   * Private constructor. This class is only initialized via the getInstance() methods.
   *
   * @param properties The properties object that is used to initialize. When this parameter is null,
   *   the properties are read from the <code>karma.properties</code> configuration file.
   */
  private LocalEnvironment(Properties properties) throws KarmaException {

    configuration = new Properties();

    try {

      if (properties == null) {
        // Read the properties from file
        //
        File configFile = new File(getConfigurationDirectory(), "karma.properties");
        if (getConfigurationDirectory().exists()) {
          if (configFile.exists()) {
            // Inlezen
            configuration.load(new FileInputStream(configFile));
          } else {
            // File aanmaken
            createDefaultProperties(configFile);
          }
        } else {
          //dir aanmaken
          getConfigurationDirectory().mkdir();
          //file aanmaken
          createDefaultProperties(configFile);
        }
      } else {
        configuration = properties;
      }

      // Create the directories, if they don't yet exist.
      //
      String workingContext = (String) configuration.get(WORKING_CONTEXT_DIRECTORY);
      if (workingContext == null || workingContext.equals("") || workingContext.equals(PLACEHOLDER)) {
        logger.error("Working context is missing; property " + WORKING_CONTEXT_DIRECTORY + " has invalid value.");
        throw new KarmaException(KarmaException.MISSING_CONFIGURATION, new Object[]{WORKING_CONTEXT_DIRECTORY});
      } else {
        if (new File(workingContext).mkdirs()) {
          logger.info("New working context created at : " + workingContext);
        }
      }

      String jarRepository = (String) configuration.get(JAR_REPOSITORY);
      if (jarRepository == null || jarRepository.equals("") || jarRepository.equals(PLACEHOLDER)) {
        logger.warn("Jar repository location is missing; property " + JAR_REPOSITORY);
      }

      // Check other 'essential' configuration.
      //
      String[] configItems = new String [6];
      configItems[0] = (String) configuration.get(MANIFEST_STORE_HOST);
      configItems[1] = (String) configuration.get(MANIFEST_STORE_REPOSITORY);
      configItems[2] = (String) configuration.get(MANIFEST_STORE_PROTOCOL);

      if (configItems[0] == null || "".equals(configItems[0]) || configItems[0].equals(PLACEHOLDER)) {
        logger.error(
            "Configuration of manifest-store is invalid; property " + MANIFEST_STORE_HOST +
            " is missing or has invalid value.");
        throw new KarmaException(KarmaException.MISSING_CONFIGURATION, new Object[]{"'"+MANIFEST_STORE_HOST+"'"});
      }
      if (configItems[1] == null || "".equals(configItems[1]) || configItems[1].equals(PLACEHOLDER)) {
        logger.error(
            "Configuration of manifest-store is invalid; property " + MANIFEST_STORE_REPOSITORY +
            " is missing or has invalid value.");
        throw new KarmaException(KarmaException.MISSING_CONFIGURATION, new Object[]{"'"+MANIFEST_STORE_REPOSITORY+"'"});
      }
      if (configItems[2] == null || "".equals(configItems[2]) || configItems[2].equals(PLACEHOLDER)) {
        logger.error(
            "Configuration of manifest-store is invalid; property " + MANIFEST_STORE_PROTOCOL +
            " is missing or has invalid value.");
        throw new KarmaException(KarmaException.MISSING_CONFIGURATION, new Object[]{"'"+MANIFEST_STORE_PROTOCOL+"'"});
      }

      configItems[0] = (String) configuration.get(LOCATION_STORE_HOST);
      configItems[1] = (String) configuration.get(LOCATION_STORE_REPOSITORY);
      configItems[2] = (String) configuration.get(LOCATION_STORE_PROTOCOL);

      if (configItems[0] == null || "".equals(configItems[0]) || configItems[0].equals(PLACEHOLDER)) {
        logger.error(
            "Configuration of location-store is invalid; property " + LOCATION_STORE_HOST +
            " is missing or has invalid value.");
        throw new KarmaException(KarmaException.MISSING_CONFIGURATION, new Object[]{"'"+LOCATION_STORE_HOST+"'"});
      }
      if (configItems[1] == null || "".equals(configItems[1]) || configItems[1].equals(PLACEHOLDER)) {
        logger.error(
            "Configuration of location-store is invalid; property " + LOCATION_STORE_REPOSITORY +
            " is missing or has invalid value.");
        throw new KarmaException(KarmaException.MISSING_CONFIGURATION, new Object[]{"'"+LOCATION_STORE_REPOSITORY+"'"});
      }
      if (configItems[2] == null || "".equals(configItems[2]) || configItems[2].equals(PLACEHOLDER)) {
        logger.error(
            "Configuration of location-store is invalid; property " + LOCATION_STORE_PROTOCOL +
            " is missing or has invalid value.");
        throw new KarmaException(KarmaException.MISSING_CONFIGURATION, new Object[]{"'"+LOCATION_STORE_PROTOCOL+"'"});
      }

      // Check other 'non-essential' configuration.
      //
      configItems[3] = (String) configuration.get(MANIFEST_STORE_PORT);
      configItems[4] = (String) configuration.get(MANIFEST_STORE_USERNAME);
      if (configItems[3] == null || "".equals(configItems[3]) || configItems[3].equals(PLACEHOLDER)) {
        logger.warn(
            "Configuration of manifest-store possibly incomplete, " + MANIFEST_STORE_PORT +
            " is missing or has invalid value.");
      }
      if (configItems[4] == null || "".equals(configItems[4]) || configItems[4].equals(PLACEHOLDER)) {
        logger.warn(
            "Configuration of manifest-store possibly incomplete, " + MANIFEST_STORE_USERNAME +
            " is missing or has invalid value.");
      }

      configItems[3] = (String) configuration.get(LOCATION_STORE_PORT);
      configItems[4] = (String) configuration.get(LOCATION_STORE_USERNAME);
      if (configItems[3] == null || "".equals(configItems[3]) || configItems[3].equals(PLACEHOLDER)) {
        logger.warn(
            "Configuration of location-store possibly incomplete, " + LOCATION_STORE_PORT +
            " is missing or has invalid value.");
      }
      if (configItems[4] == null || "".equals(configItems[4]) || configItems[4].equals(PLACEHOLDER)) {
        logger.warn(
            "Configuration of location-store possibly incomplete, " + LOCATION_STORE_USERNAME +
            " is missing or has invalid value.");
      }

    } catch (IOException e) {
      throw new KarmaRuntimeException("The bootstrap configuration file could not be loaded.", e);
    }

    logConfiguration();
  }

  private void logConfiguration() {

    logger.info("Working context home directory : " + (String) configuration.get(WORKING_CONTEXT_DIRECTORY));
    logger.info("Jar repository : " + (String) configuration.get(JAR_REPOSITORY));

    logger.debug("Manifest store host : " + (String) configuration.get(MANIFEST_STORE_HOST));
    logger.debug("Manifest store port : " + (String) configuration.get(MANIFEST_STORE_PORT));
    logger.debug("Manifest store repository : " + (String) configuration.get(MANIFEST_STORE_REPOSITORY));
    logger.debug("Manifest store protocol : " + (String) configuration.get(MANIFEST_STORE_PROTOCOL));
    logger.debug("Manifest store username : " + (String) configuration.get(MANIFEST_STORE_USERNAME));

    logger.debug("Location store host : " + (String) configuration.get(LOCATION_STORE_HOST));
    logger.debug("Location store port : " + (String) configuration.get(LOCATION_STORE_PORT));
    logger.debug("Location store repository : " + (String) configuration.get(LOCATION_STORE_REPOSITORY));
    logger.debug("Location store protocol : " + (String) configuration.get(LOCATION_STORE_PROTOCOL));
    logger.debug("Location store username : " + (String) configuration.get(LOCATION_STORE_USERNAME));
  }

  /**
   * <p>Gets a <code>File</code> reference to the configuration directory, currently defined as :
   * <code>$HOME/.karma</code>.
   *
   * @return The configuration directory for Karma.
   */
  public static File getConfigurationDirectory() {
    return new File(System.getProperty("user.home"), ".karma");
  }

  /**
   * Create the <code>karma.properties</code> configuration file.
   *
   * @param configFile The config file to write the default properties to.
   * @throws IOException When the config file could not be created.
   */
  private void createDefaultProperties(File configFile) throws IOException {

    String karmaBase = System.getProperty("user.home") + File.separator + "karma" + File.separator;

    configuration.put(WORKING_CONTEXT_DIRECTORY, karmaBase);
    configuration.put(
        JAR_REPOSITORY,
        System.getProperty("user.home") + File.separator + ".maven" + File.separator + "repository"
    );

    configuration.put(MANIFEST_STORE_HOST, PLACEHOLDER);
    configuration.put(MANIFEST_STORE_PORT, PLACEHOLDER);
    configuration.put(MANIFEST_STORE_REPOSITORY, PLACEHOLDER);
    configuration.put(MANIFEST_STORE_PROTOCOL, PLACEHOLDER);
    configuration.put(MANIFEST_STORE_USERNAME, PLACEHOLDER);

    configuration.put(LOCATION_STORE_HOST, PLACEHOLDER);
    configuration.put(LOCATION_STORE_PORT, PLACEHOLDER);
    configuration.put(LOCATION_STORE_REPOSITORY, PLACEHOLDER);
    configuration.put(LOCATION_STORE_PROTOCOL, PLACEHOLDER);
    configuration.put(LOCATION_STORE_USERNAME, PLACEHOLDER);

    logger.info("Karma configuration created in " + configFile.getPath());

    String header = "Automatically generated Karma configuration properties";

    configuration.store(new FileOutputStream(configFile), header);
  }

  public static File getWorkingContext() {

    if (configuration == null) {
      throw new KarmaRuntimeException("Local environment has not been initialized. Call initialize().");
    }

    String workingContext = (String) configuration.get(WORKING_CONTEXT_DIRECTORY);

    File home = new File(workingContext);

    if (!home.exists()) {
      throw new KarmaRuntimeException("No working context. (Not defined in karma.properties ?)");
    }

    return home;
  }

  public static String getWorkingContextAsString() {

    if (getWorkingContext() == null) {
      throw new KarmaRuntimeException("No working context defined. Local environment may not have been initialized properly.");
    }
    return getWorkingContext().getAbsolutePath();
  }

  /**
   * Gets the property at which the last used manifest is stored in <code>.java</code>. This property is
   * context-sensitive.
   *
   * @return
   */
  public static String getContextManifestPreference() {

    // Preference property identifying the manifest that was used in the last Karma session.
    //
    final String LAST_USED_MANIFEST_PREFERENCE = "karma.manifest.last";

    return getWorkingContextAsString() + "." + LAST_USED_MANIFEST_PREFERENCE;
  }

  /**
   * Retrieve a valid reference to the manifest store directory. The available manifests
   * are stored here.
   *
   * @return A valid reference to the manifest store directory.
   */
  public static File getManifestStore() {
    return new File(getWorkingContext(), "manifests");
  }

  /**
   * Gets a reference to the location where manifests can be retrieved. Supports only CVS for now.
   */
  public static Location getManifestStoreLocation() throws LocationException {

    if (configuration == null) {
      throw new KarmaRuntimeException("Local environment has not been initialized. Call initialize().");
    }

    CVSLocationImpl location = new CVSLocationImpl("manifest-store");
    try {
      location.setHost(configuration.getProperty(MANIFEST_STORE_HOST));
    } catch (Exception e) {
      throw new LocationException(LocationException.INVALID_MANIFEST_STORE_LOCATION, new Object[]{"'"+MANIFEST_STORE_HOST+"'"});
    }
    try {
      location.setRepository(configuration.getProperty(MANIFEST_STORE_REPOSITORY));
    } catch (Exception e) {
      throw new LocationException(LocationException.INVALID_MANIFEST_STORE_LOCATION, new Object[]{"'"+MANIFEST_STORE_REPOSITORY+"'"});
    }
    try {
      location.setProtocol(configuration.getProperty(MANIFEST_STORE_PROTOCOL));
    } catch (Exception e) {
      throw new LocationException(LocationException.INVALID_MANIFEST_STORE_LOCATION, new Object[]{"'"+MANIFEST_STORE_PROTOCOL+"'"});
    }
    if (!location.getProtocol().equals(CVSLocationImpl.LOCAL)) {
      try {
        location.setPort(new Integer(configuration.getProperty(MANIFEST_STORE_PORT)).intValue());
      } catch (Exception e) {
        throw new LocationException(LocationException.INVALID_MANIFEST_STORE_LOCATION, new Object[]{"'"+MANIFEST_STORE_PORT+"'"});
      }
      try {
        location.setUsername(configuration.getProperty(MANIFEST_STORE_USERNAME));
      } catch (Exception e) {
        throw new LocationException(LocationException.INVALID_MANIFEST_STORE_LOCATION, new Object[]{"'"+MANIFEST_STORE_USERNAME+"'"});
      }
    }
    return location;
  }

  /**
   * Gets a reference to the location where <code>location.xml</code> can be retrieved. Supports only CVS for now.
   */
  public static Location getLocationStoreLocation() throws LocationException {

    if (configuration == null) {
      throw new KarmaRuntimeException("Local environment has not been initialized. Call initialize().");
    }

    CVSLocationImpl location = new CVSLocationImpl("location-store");
    try {
      location.setHost(configuration.getProperty(LOCATION_STORE_HOST));
    } catch (Exception e) {
      throw new LocationException(LocationException.INVALID_LOCATION_STORE_LOCATION, new Object[]{"'"+LOCATION_STORE_HOST+"'"});
    }
    try {
      location.setRepository(configuration.getProperty(LOCATION_STORE_REPOSITORY));
    } catch (Exception e) {
      throw new LocationException(LocationException.INVALID_LOCATION_STORE_LOCATION, new Object[]{"'"+LOCATION_STORE_REPOSITORY+"'"});
    }
    try {
      location.setProtocol(configuration.getProperty(LOCATION_STORE_PROTOCOL));
    } catch (Exception e) {
      throw new LocationException(LocationException.INVALID_LOCATION_STORE_LOCATION, new Object[]{"'"+LOCATION_STORE_PROTOCOL+"'"});
    }
    if (!location.getProtocol().equals(CVSLocationImpl.LOCAL)) {

      try {
        location.setPort(new Integer(configuration.getProperty(LOCATION_STORE_PORT)).intValue());
      } catch (Exception e) {
        throw new LocationException(LocationException.INVALID_LOCATION_STORE_LOCATION, new Object[]{"'"+LOCATION_STORE_PORT+"'"});
      }
      try {
        location.setUsername(configuration.getProperty(LOCATION_STORE_USERNAME));
      } catch (Exception e) {
        throw new LocationException(LocationException.INVALID_LOCATION_STORE_LOCATION, new Object[]{"'"+LOCATION_STORE_USERNAME+"'"});
      }
    }
    return location;
  }

  /**
   * Retrieve a valid reference to the location store directory. The available locations
   * are stored here.
   *
   * @return A valid reference to the location store directory.
   */
  public static File getLocationStore() {
    return new File(getWorkingContext(), "locations");
  }

  /**
   * Retrieve a valid reference to the developer home directory. This is the directory where
   * the manifest instances are checked out from the version control system.
   *
   * @return A valid reference to the development home directory.
   */
  public static File getDevelopmentHome() {
    return new File(getWorkingContext(), "projects");
  }

  /**
   * Returns the <code>File</code> location for the repository where <code>jar</code>-dependencies can be found. The
   * system property <code>JAR_REPOSITORY</code> is used to determine the location. If not set, the default jar
   * repository ($HOME/.karma/repository) is returned (and created if it doesn't exist).
   *
   * @return See method description.
   */
  public static File getLocalRepository() {

    File defaultJarRepository = new File(System.getProperty("user.home"), ".karma" + File.separator + "repository");
    File configuredJarRepository = new File((String)configuration.get(JAR_REPOSITORY));

    if (configuration.get(JAR_REPOSITORY) == null) {
      defaultJarRepository.mkdirs();
    } else {
      configuredJarRepository.mkdirs();
    }

    return ((String)configuration.get(JAR_REPOSITORY) == null ? defaultJarRepository : configuredJarRepository);
  }
}
