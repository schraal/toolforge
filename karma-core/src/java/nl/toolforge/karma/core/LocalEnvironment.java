package nl.toolforge.karma.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import nl.toolforge.karma.core.location.LocationException;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class maintains a users' local environment. The local environment is required to properly run Karma tools. It
 * contains references to directories and handles history settings.
 *
 * The environment consists of two parts:
 *
 * 1/ bootstrap configuration which is stored in the $USER_HOME/.karma/karma.properties
 * 2/ dynamic configuration which is stored using java.util.prefs
 *
 * @author D.A. Smedes
 *
 * @version $Id:
 */
public class LocalEnvironment {

  private static Log logger = LogFactory.getLog(LocalEnvironment.class);

  /** Determines testmodes and disables file access */
//  private static final boolean TESTMODE = (System.getProperty("TESTMODE") == null ? false : System.getProperty("TESTMODE").equals("true"));

  /**
   * This property is used to determine the path to the bootstrap properties file (<code>karma.properties</code>). The
   * convention is that a filename <b>NOT</b> starting with a <code>/</code> means that it will be loaded from the
   * classpath, otherwise the value for the property will be considered an absolute filename.
   */
  public static final String BOOTSTRAP_CONFIGURATION_DIRECTORY = "bootstrap.configuration";



  /** Property that identifies the user's development home directory. */
  public static final String DEVELOPMENT_HOME_DIRECTORY = "development.store";
  /** Property that identifies the directory where manifest files are stored. */
  public static final String MANIFEST_STORE_DIRECTORY = "manifest.store";
  /** Property that identifies the directory where location files are stored. */
  public static final String LOCATION_STORE_DIRECTORY = "location.store";


  private Properties configuration = new Properties();


  private static LocalEnvironment localEnvironment;

  public static LocalEnvironment getInstance() {
    return getInstance(null);
  }

  public static LocalEnvironment getInstance(Properties properties) {
    if (localEnvironment == null) {
      localEnvironment = new LocalEnvironment(properties);
    }
    return localEnvironment;
  }

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
  private void createDefaultProperties(File configFile) throws IOException {
    String karmaBase = System.getProperty("user.home")+File.separator+"karma"+File.separator;
    configuration.put(DEVELOPMENT_HOME_DIRECTORY, karmaBase+"projects");
    configuration.put(MANIFEST_STORE_DIRECTORY, karmaBase+"manifests");
    configuration.put(LOCATION_STORE_DIRECTORY, karmaBase+"locations");
    configuration.store(new FileOutputStream(configFile), "Generated karma defaults");
  }




  /**
   *
   *
   *
   * @return A valid reference to the manifest store directory.
   * @throws ManifestException When the manifest store directory does not exist.
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
   *
   * @return A valid reference to the location store directory.
   * @throws LocationException When then location store directory does not exist.
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
