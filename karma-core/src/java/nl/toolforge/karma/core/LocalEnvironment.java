package nl.toolforge.karma.core;

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * <p>This class maintains a users' local environment. The local environment is required to properly run Karma tools. It
 * contains references to directories, which are necessary to bootstrap Karma.
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

  private static final Log logger = LogFactory.getLog(LocalEnvironment.class);

  /** Property that identifies the user's development home directory. */
  public static final String DEVELOPMENT_STORE_DIRECTORY = "development-store.local";

  /** Property that identifies the directory where manifest files are stored. */
  public static final String MANIFEST_STORE_DIRECTORY = "manifest-store.local.checkout-directory";

  /** Property that identifies the directory where location files are stored. */
  public static final String LOCATION_STORE_DIRECTORY = "location-store.local.checkout-directory";

  /** Preference property identifying the manifest that was used in the last Karma session. */
  public static final String LAST_USED_MANIFEST_PREFERENCE = "karma.manifest.last";

  public static final String MANIFEST_STORE_HOST = "manifest-store.cvs.host";
  public static final String MANIFEST_STORE_PORT = "manifest-store.cvs.port";
  public static final String MANIFEST_STORE_REPOSITORY = "manifest-store.cvs.repository";
  public static final String MANIFEST_STORE_PROTOCOL = "manifest-store.cvs.protocol";
  public static final String MANIFEST_STORE_USERNAME = "manifest-store.cvs.username";
  public static final String MANIFEST_STORE_PASSWORD = "manifest-store.cvs.password";

  public static final String LOCATION_STORE_HOST = "location-store.cvs.host";
  public static final String LOCATION_STORE_PORT = "location-store.cvs.port";
  public static final String LOCATION_STORE_REPOSITORY = "location-store.cvs.repository";
  public static final String LOCATION_STORE_PROTOCOL = "location-store.cvs.protocol";
  public static final String LOCATION_STORE_USERNAME = "location-store.cvs.username";
  public static final String LOCATION_STORE_PASSWORD = "location-store.cvs.password";

  private static final String PLACEHOLDER = "<...>";

  /** Property Object that stores the key-value pairs of the defined properties. */
  private Properties configuration = new Properties();

  /**
   * The one-and-only instance of this class.
   */
  private static LocalEnvironment localEnvironment;

  /**
   * Return the one-and-only instance of this class. It will be initialized from file
   * when it does not yet exist.
   */
  public static LocalEnvironment getInstance() throws KarmaException {
    return getInstance(null);
  }

  /**
   * Return the one-and-only instance of this class. It will be initialized using the given
   * Properties object when it does not yet exist.
   *
   * @param properties Properties object used to initialize the instance.
   * @throws KarmaException When {@link KarmaException.MISSING_CONFIGURATION} is detected, it will result in a Karma
   *   startup failure.
   */
  public static LocalEnvironment getInstance(Properties properties) throws KarmaException {
    if (localEnvironment == null) {
      localEnvironment = new LocalEnvironment(properties);
    } else {
      logger.warn("This singleton class has already been initialized. Use getInstance() instead.");
    }
    return localEnvironment;
  }

  /**
   * Private constructor. This class is only initialized via the getInstance() methods.
   *
   * @param properties The properties object that is used to initialize. When this parameter is null,
   *   the properties are read from the <code>karma.properties</code> configuration file.
   */
  private LocalEnvironment(Properties properties) throws KarmaException {

    try {

      if (properties == null) {
        //read the properties from file
        File configFile = new File(getConfigurationDirectory(), "karma.properties");
        if (getConfigurationDirectory().exists()) {
          if (configFile.exists()) {
            //inlezen
            configuration.load(new FileInputStream(configFile));
          } else {
            //file aanmaken
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
      String store;
      store = (String) configuration.get(DEVELOPMENT_STORE_DIRECTORY);
      if (store == null || store.equals("") || store.equals(PLACEHOLDER)) {
        logger.error("Development store is missing; property " + DEVELOPMENT_STORE_DIRECTORY + " has invalid value.");
        throw new KarmaException(KarmaException.MISSING_CONFIGURATION, new Object[]{DEVELOPMENT_STORE_DIRECTORY});
      } else {
        new File(store).mkdirs();
      }
      store = (String) configuration.get(MANIFEST_STORE_DIRECTORY);
      if (store == null || store.equals("") || store.equals(PLACEHOLDER)) {
        logger.error("Manifest store is missing; property " + MANIFEST_STORE_DIRECTORY + " has invalid value.");
        throw new KarmaException(KarmaException.MISSING_CONFIGURATION, new Object[]{MANIFEST_STORE_DIRECTORY});
      } else {
        new File(store).mkdirs();
      }
      store = (String) configuration.get(LOCATION_STORE_DIRECTORY);
      if (store == null || store.equals("") || store.equals(PLACEHOLDER)) {
        logger.error("Location store is missing; property " + LOCATION_STORE_DIRECTORY + " has invalid value.");
        throw new KarmaException(KarmaException.MISSING_CONFIGURATION, new Object[]{LOCATION_STORE_DIRECTORY});
      } else {
        new File(store).mkdirs();
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
      configItems[5] = (String) configuration.get(MANIFEST_STORE_PASSWORD);
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
      if (configItems[5] == null || "".equals(configItems[5]) || configItems[5].equals(PLACEHOLDER)) {
        logger.warn(
            "Configuration of manifest-store possibly incomplete, " + MANIFEST_STORE_PASSWORD +
            " is missing or has invalid value.");
      }

      configItems[3] = (String) configuration.get(LOCATION_STORE_PORT);
      configItems[4] = (String) configuration.get(LOCATION_STORE_USERNAME);
      configItems[5] = (String) configuration.get(LOCATION_STORE_PASSWORD);
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
      if (configItems[5] == null || "".equals(configItems[5]) || configItems[5].equals(PLACEHOLDER)) {
        logger.warn(
            "Configuration of location-store possibly incomplete, " + LOCATION_STORE_PASSWORD +
            " is missing or has invalid value.");
      }

    } catch (IOException e) {
      throw new KarmaRuntimeException("The bootstrap configuration file could not be loaded.", e);
    }
  }

  /**
   * <p>Gets a <code>File</code> reference to the configuration directory, currently defined as :
   * <code>$HOME/.karma</code>.
   *
   * @return The configuration directory for Karma.
   */
  public File getConfigurationDirectory() {
    return new File(System.getProperty("user.home"), ".karma");
  }

  /**
   * Create the karma.properties file with default values:
   *
   * <p/>
   * <ul>
   * <li/><code>development-store.local                    = $USER_HOME/karma/projects</code>
   * <li/><code>manifest-store.local.checkout-directory    = $USER_HOME/karma/manifests</code>
   * <li/><code>location-store.local.checkout-directory    = $USER_HOME/karma/locations</code>
   * <li/><code>manifest-store.cvs.host                    = &lt;&gt;</code>
   * </ul>
   *
   * @param configFile The config file to write the default properties to.
   * @throws IOException When the config file could not be created.
   */
  private void createDefaultProperties(File configFile) throws IOException {

    String karmaBase = System.getProperty("user.home") + File.separator + "karma" + File.separator;

    configuration.put(DEVELOPMENT_STORE_DIRECTORY, karmaBase + "projects");
    configuration.put(MANIFEST_STORE_DIRECTORY, karmaBase + "manifests");
    configuration.put(LOCATION_STORE_DIRECTORY, karmaBase + "locations");

    configuration.put(MANIFEST_STORE_HOST, PLACEHOLDER);
    configuration.put(MANIFEST_STORE_PORT, PLACEHOLDER);
    configuration.put(MANIFEST_STORE_REPOSITORY, PLACEHOLDER);
    configuration.put(MANIFEST_STORE_PROTOCOL, PLACEHOLDER);
    configuration.put(MANIFEST_STORE_USERNAME, PLACEHOLDER);
    configuration.put(MANIFEST_STORE_PASSWORD, PLACEHOLDER);

    configuration.put(LOCATION_STORE_HOST, PLACEHOLDER);
    configuration.put(LOCATION_STORE_PORT, PLACEHOLDER);
    configuration.put(LOCATION_STORE_REPOSITORY, PLACEHOLDER);
    configuration.put(LOCATION_STORE_PROTOCOL, PLACEHOLDER);
    configuration.put(LOCATION_STORE_USERNAME, PLACEHOLDER);
    configuration.put(LOCATION_STORE_PASSWORD, PLACEHOLDER);

    logger.info("Karma configuration created in " + configFile.getPath());

    String header = "Automatically generated Karma configuration properties";

    configuration.store(new FileOutputStream(configFile), header);
  }


  /**
   * Retrieve a valid reference to the manifest store directory. The available manifests
   * are stored here.
   *
   * @return A valid reference to the manifest store directory.
   * @throws KarmaException When the manifest store directory does not exist.
   */
  public File getManifestStore() throws KarmaException {
    try {
      File home = null;

      String manifestStore = (String) configuration.get(MANIFEST_STORE_DIRECTORY);
      logger.debug("Manifest store directory: " + manifestStore);

      home = new File(manifestStore);
      if (!home.exists()) {
        throw new KarmaException(KarmaException.MANIFEST_STORE_NOT_FOUND);
      }

      return home;
    } catch (NullPointerException n) {
      throw new KarmaException(KarmaException.MANIFEST_STORE_NOT_FOUND);
    }
  }

  /**
   * Gets a reference to the location where manifests can be retrieved. Supports only CVS for now.
   */
  public Location getManifestStoreLocation() throws LocationException {

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
      try {
        location.setPassword(configuration.getProperty(MANIFEST_STORE_PASSWORD));
      } catch (Exception e) {
        throw new LocationException(LocationException.INVALID_MANIFEST_STORE_LOCATION, new Object[]{"'"+MANIFEST_STORE_PASSWORD+"'"});
      }
    }
    return location;
  }

  /**
   * Gets a reference to the location where <code>location.xml</code> can be retrieved. Supports only CVS for now.
   */
  public Location getLocationStoreLocation() throws LocationException {

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
      try {
        location.setPassword(configuration.getProperty(LOCATION_STORE_PASSWORD));
      } catch (Exception e) {
        throw new LocationException(LocationException.INVALID_LOCATION_STORE_LOCATION, new Object[]{"'"+LOCATION_STORE_PASSWORD+"'"});
      }
    }
    return location;
  }

  /**
   * Retrieve a valid reference to the location store directory. The available locations
   * are stored here.
   *
   * @return A valid reference to the location store directory.
   * @throws KarmaException When then location store directory does not exist.
   */
  public File getLocationStore() throws KarmaException {
    try {
      File home = null;

      String locationStore = (String) configuration.get(LOCATION_STORE_DIRECTORY);
      logger.debug("Location store directory: " + locationStore);

      home = new File(locationStore);
      if (!home.exists()) {
        throw new KarmaException(KarmaException.LOCATION_STORE_NOT_FOUND);
      }

      return home;
    } catch (NullPointerException n) {
      throw new KarmaException(KarmaException.LOCATION_STORE_NOT_FOUND);
    }
  }

  /**
   * Retrieve a valid reference to the developer home directory. This is the directory where
   * the manifest instances are checked out from the version control system.
   *
   * @return A valid reference to the development home directory.
   * @throws KarmaException When the development home does not exists.
   */
  public File getDevelopmentHome() throws KarmaException {

    try {
      File home = null;

      String developmentHome = (String) configuration.get(DEVELOPMENT_STORE_DIRECTORY);
      logger.debug("development home directory: " + developmentHome);
      home = new File(developmentHome);
      if (!home.exists()) {
        throw new KarmaException(KarmaException.DEVELOPMENT_HOME_NOT_FOUND);
      }
      return home;
    } catch (NullPointerException n) {
      throw new KarmaException(KarmaException.DEVELOPMENT_HOME_NOT_FOUND);
    }
  }
}
