package nl.toolforge.karma.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.toolforge.karma.core.location.LocationException;

/**
 * This class maintains a users' local environment. The local environment is required to properly run Karma tools. It
 * contains references to directories, which are necessary to bootstrap Karma.
 *
 * LocalEnvironment can be instanciated in two ways:
 *
 * <ol>
 *   <li/>Pass a Properties Object to the getInstance() method. The properties are read from this Object.
 *   <li/>Don't pass anything to the getInstance() method. The properties are read from file. The properties are
 *        assumed to be located in $USER_HOME/.karma/karma.properties.
 * </ol>
 *
 * This class won't be instanciated when the necessary properties can't be found, or reference non-existing,
 * non-createable directories.
 *
 * @author D.A. Smedes
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public final class LocalEnvironment {

  private static final Log logger = LogFactory.getLog(LocalEnvironment.class);


  /** Property that identifies the user's development home directory. */
  public static final String DEVELOPMENT_HOME_DIRECTORY = "development.store";

  /** Property that identifies the directory where manifest files are stored. */
  public static final String MANIFEST_STORE_DIRECTORY = "manifest.store";

  /** Property that identifies the directory where location files are stored. */
  public static final String LOCATION_STORE_DIRECTORY = "location.store";


  /** Property Object that stores the key-value pairs of the defined properties. */
  private Properties configuration = new Properties();


  /** The one-and-only instance of this class. */
  private static LocalEnvironment localEnvironment;

  /**
   * Return the one-and-only instance of this class. It will be initialized from file
   * when it does not yet exist.
   *
   * @return The one-and-only instance of this class.
   */
  public static final LocalEnvironment getInstance() {
    return getInstance(null);
  }

  /**
   * Return the one-and-only instance of this class. It will be initialized using the given
   * Properties object when it does not yet exist.
   *
   * @param properties  Properties object used to initialize the instance.
   * @return The one-and-only instance of the class.
   */
  public static final LocalEnvironment getInstance(Properties properties) {
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
   * @param properties  The properties object that is used to initialize. When this parameter is null,
   *                    the properties are read from file.
   */
  private LocalEnvironment(Properties properties) {
    try {

      if (properties == null) {
        //read the properties from file
        File configDir = new File(System.getProperty("user.home"), ".karma");
        File configFile = new File(configDir, "karma.properties");
        if (configDir.exists()) {
          if (configFile.exists()) {
            //inlezen
            configuration.load(new FileInputStream(configFile));
          } else {
            //file aanmaken
            createDefaultProperties(configFile);
          }
        } else {
          //dir aanmaken
          configDir.mkdir();
          //file aanmaken
          createDefaultProperties(configFile);
        }
      } else {
        configuration = properties;
      }

      //create the directories, if necessary
      try {
        getDevelopmentHome().mkdir();
        getManifestStore().mkdir();
        getLocationStore().mkdir();
      } catch (KarmaException ke) {
        throw new KarmaRuntimeException("Could not initialise the environment", ke);
      }

    } catch (IOException e) {
      throw new KarmaRuntimeException("The bootstrap configuration file could not be loaded.", e);
    }
  }


  /**
   * Create the karma.properties file with default values:
   *
   * <ul>
   *   <li/>DEVELOPMENT_HOME_DIRECTORY = $USER_HOME/karma/projects
   *   <li/>MANIFEST_STORE_DIRECTORY   = $USER_HOME/karma/manifests
   *   <li/>LOCATION_STORE_DIRECTORY   = $USER_HOME/karma/locations
   * </ul>
   *
   * @param configFile  The config file to write.
   * @throws IOException  When the config file could not be created.
   */
  private final void createDefaultProperties(File configFile) throws IOException {
    String karmaBase = System.getProperty("user.home")+File.separator+"karma"+File.separator;
    configuration.put(DEVELOPMENT_HOME_DIRECTORY, karmaBase+"projects");
    configuration.put(MANIFEST_STORE_DIRECTORY, karmaBase+"manifests");
    configuration.put(LOCATION_STORE_DIRECTORY, karmaBase+"locations");
    configuration.store(new FileOutputStream(configFile), "Generated karma defaults");
  }




  /**
   * Retrieve a valid reference to the manifest store directory. The available manifests
   * are stored here.
   *
   * @return A valid reference to the manifest store directory.
   * @throws ManifestException  When the manifest store directory does not exist.
   */
  public final File getManifestStore() throws ManifestException {
    try {
      File home = null;

      String manifestStore = (String) configuration.get(MANIFEST_STORE_DIRECTORY);
      logger.debug("Manifest store directory: " + manifestStore);

      home = new File(manifestStore);
      if (!home.exists()) {
        throw new ManifestException(ManifestException.NO_MANIFEST_STORE_DIRECTORY);
      }

      return home;
    } catch (NullPointerException n) {
      throw new ManifestException(ManifestException.NO_MANIFEST_STORE_DIRECTORY);
    }
  }

  /**
   * Retrieve a valid reference to the location store directory. The available locations
   * are stored here.
   *
   * @return A valid reference to the location store directory.
   * @throws LocationException  When then location store directory does not exist.
   */
  public final File getLocationStore() throws LocationException {
    try {
      File home = null;

      String locationStore = (String) configuration.get(LOCATION_STORE_DIRECTORY);
      logger.debug("Location store directory: " + locationStore);

      home = new File(locationStore);
      if (!home.exists()) {
        throw new LocationException(LocationException.NO_LOCATION_STORE_DIRECTORY);
      }

      return home;
    } catch (NullPointerException n) {
      throw new LocationException(LocationException.NO_LOCATION_STORE_DIRECTORY);
    }
  }

  /**
   * Retrieve a valid reference to the developer home directory. This is the directory where
   * the manifest instances are checked out from the version control system.
   *
   * @return A valid reference to the development home directory.
   * @throws KarmaException  When the development home does not exists.
   */
  public final File getDevelopmentHome() throws KarmaException {
    try {
      File home = null;

      String developmentHome = (String) configuration.get(DEVELOPMENT_HOME_DIRECTORY);
      logger.debug("development home directory: " + developmentHome);

      home = new File(developmentHome);
      if (!home.exists()) {
        throw new KarmaException(KarmaException.NO_DEVELOPMENT_HOME);
      }

      return home;
    } catch (NullPointerException n) {
      throw new KarmaException(KarmaException.NO_DEVELOPMENT_HOME);
    }
  }


}
