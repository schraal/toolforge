package nl.toolforge.karma.core.boot;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationLoader;
import nl.toolforge.karma.core.manifest.ManifestCollector;
import nl.toolforge.karma.core.manifest.ManifestLoader;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A <code>WorkingContext</code> is used by Karma to determine the environment in which the user wants to use Karma. A
 * working context is represented on your local harddisk by a directory in which a developers' project work will be
 * stored. The <code>WorkingContext</code> class is the bridge from Karma domain objects (<code>Manifest</code> and
 * <code>Module</code> to name the most important ones) to a developer's harddisk.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class WorkingContext {

    private  static final String DEFAULT_CONVERSION_PATTERN = "%d{HH:mm:ss} [%5p] - %m%n";

  static {

    // Configure the logging system.
    //
    try {

      if (WorkingContext.class.getClassLoader().getResource("log4j.xml") != null) {

        Logger.getLogger(WorkingContext.class).info("'Log4j.xml' used to for logging configuration.");

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

        Logger.getLogger(WorkingContext.class).info(
            "Default logging configuration enabled; override by placing 'log4j.xml' and 'log4j.dtd' on your classpath.");
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new KarmaRuntimeException("*** PANIC *** Log4j system could not be initialized.");
    }
  }


  /**
   * Base directory
   */
  public static final String BASE_DIRECTORY_PROPERTY = System.getProperty("user.home") + File.separator + "karma";

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

  /**
   * The property that identifies the local directory where jar dependencies can be found. Dependencies are
   * resolved Maven style, but to support environments where Maven is not available, the directory is configurable.
   * The default Maven repository is used when this property is not set in <code>karma.properties</code>.
   */
  // todo needs rethinking
  public static final String JAR_REPOSITORY = "jar.repository";

  private String workingContext = null;
  private File defaultConfigurationDirectory = null;
  private Properties configuration = null;
  private List invalidConfiguration = null;

  private ManifestCollector manifestCollector = null;
  private ManifestLoader manifestLoader = null;
  private LocationLoader locationLoader = null;

  /**
   * <p>Constructs a working context, relative to the default locations. The default location for Karma configuration
   * is <code>$USER_HOME/.karma</code>. Each working context, created with this constructor will have a directory
   * with <code>name</code> as its directory name. Configuration for the working context is then located in that
   * subdirectory in the <code>workingcontext.properties</code> file. The default location for the project files for the
   * context is <code>$USER_HOME/karma/&lt;name&gt;</code>.
   *
   * <p>If the working context with <code>name</code> does not yet exist, it will be created.
   *
   * <p>Properties located in the directory are loaded as configuration.
   *
   * @param workingContext An existing working context, or <code>null</code>, in which case Karma will create the
   *   <code>default</code> context.
   */
  public WorkingContext(String workingContext) {
    this(workingContext, new File(System.getProperty("user.home"), ".karma"), null);
  }

  /**
   * Constructs a working context, using <code>defaultConfigurationDirectory</code> as the base directory.
   * <code>configuration</code> is used to configure the working context. The <code>configuration</code> object is
   * assumed to contain the default configuration properties for a working context. If not, a
   * <code>KarmaRuntimeException</code> is thrown. A call to {@link #initialize} can be used to check if the
   * configuration is complete.
   *
   * @param defaultConfigurationDirectory
   * @param workingContext
   * @param configuration
   */
  public WorkingContext(String workingContext, File defaultConfigurationDirectory, Properties configuration) {

    if (workingContext == null || "".equals(workingContext)) {
      workingContext = "default";
    }
    this.workingContext = workingContext;

    if (defaultConfigurationDirectory == null) {
      throw new IllegalArgumentException("Default configuration directory cannot be null.");
    }
    this.defaultConfigurationDirectory = defaultConfigurationDirectory;

    this.configuration = (configuration == null ? new Properties() : configuration);

    initialize();
  }

  /**
   * Initializes this <code>WorkingContext</code>. The initialization process checks the configuration for the
   * working context and reports any missing configuration items which can be obtained via the
   * {@link #getInvalidConfiguration}-method.
   */
  private void initialize() {

    // Create $HOME/.karma or the manual location.
    //
    if (getConfigurationDirectory().exists()) {
      if (!getConfigurationDirectory().isDirectory()) {
        throw new KarmaRuntimeException(getConfigurationDirectory().getPath() + " is not a directory.");
      }
    } else {
      if (!getConfigurationDirectory().mkdirs()) {
        throw new KarmaRuntimeException(
            "Could not create default configuration directory " + getConfigurationDirectory().getPath() + " for Karma.");
      }
    }
    if (getDefaultConfigurationFile().exists()) {
      // todo Inlezen 'karma.properties'; currently not supported.
      //
    } else {
      // todo report about missing default configuration
      //
    }

    // Check the working context
    //
    File dir = new File(getConfigurationDirectory(), workingContext);
    if (dir.exists()) {
      if (!dir.isDirectory()) {
        throw new KarmaRuntimeException(dir.getPath() + " is not a directory.");
      }
    } else {
      dir.mkdir();
    }

    if (getWorkingContextConfigurationFile().exists()) {
      try {
        configuration.load(new FileInputStream(getWorkingContextConfigurationFile()));
      } catch (IOException e) {
        throw new KarmaRuntimeException(e.getMessage());
      }
    }
    // Existing or not existing, check it ...
    //
    checkConfiguration();
  }

  private void checkConfiguration() {

    invalidConfiguration = new ArrayList();

    if ((String) configuration.get(MANIFEST_STORE_HOST) == null) {
      invalidConfiguration.add(new ConfigurationItem(MANIFEST_STORE_HOST, "Manifest store host", "127.0.0.1"));
    }
    if ((String) configuration.get(MANIFEST_STORE_PORT) == null) {
      invalidConfiguration.add(new ConfigurationItem(MANIFEST_STORE_PORT, "Manifest store port", "2401"));
    }
    if ((String) configuration.get(MANIFEST_STORE_PROTOCOL) == null) {
      invalidConfiguration.add(new ConfigurationItem(MANIFEST_STORE_PROTOCOL, "Manifest store protocol", "local"));
    }
    if ((String) configuration.get(MANIFEST_STORE_REPOSITORY) == null) {
      invalidConfiguration.add(new ConfigurationItem(MANIFEST_STORE_REPOSITORY, "Manifest store repository", "/cvs"));
    }
    if ((String) configuration.get(MANIFEST_STORE_USERNAME) == null) {
      invalidConfiguration.add(new ConfigurationItem(MANIFEST_STORE_USERNAME, "Manifest store username", null));
    }
    if ((String) configuration.get(LOCATION_STORE_HOST) == null) {
      invalidConfiguration.add(new ConfigurationItem(LOCATION_STORE_HOST, "Location store host", "127.0.0.1"));
    }
    if ((String) configuration.get(LOCATION_STORE_PORT) == null) {
      invalidConfiguration.add(new ConfigurationItem(LOCATION_STORE_PORT, "Location store port", "2401"));
    }
    if ((String) configuration.get(LOCATION_STORE_PROTOCOL) == null) {
      invalidConfiguration.add(new ConfigurationItem(LOCATION_STORE_PROTOCOL, "Location store protocol", "local"));
    }
    if ((String) configuration.get(LOCATION_STORE_REPOSITORY) == null) {
      invalidConfiguration.add(new ConfigurationItem(LOCATION_STORE_REPOSITORY, "Location store repository", "/cvs"));
    }
    if ((String) configuration.get(LOCATION_STORE_USERNAME) == null) {
      invalidConfiguration.add(new ConfigurationItem(LOCATION_STORE_USERNAME, "Location store username", null));
    }
  }

  public String getName() {
    return workingContext;
  }

  public File getConfigurationDirectory() {
    return this.defaultConfigurationDirectory;
  }

  private File getDefaultConfigurationFile() {
    return new File(getConfigurationDirectory(), "karma.properties");
  }

  private File getWorkingContextConfigurationFile() {
    return new File(getWorkingContextDirectory(), "workingcontext.properties");
  }

  /**
   * Returns a <code>List</code> of <code>ConfigurationItem</code>s each one identifying a property that has not been
   * set by the user for the working context. Together with a call to {@link #getConfiguration} clients can process
   * all missing properties.
   */
  public List getInvalidConfiguration() {
    checkConfiguration();
    return invalidConfiguration;
  }

  /**
   * Get the configuration for this working context.
   */
  public Properties getConfiguration() {
    return configuration;
  }

  public File getWorkingContextDirectory() {
    return new File(getConfigurationDirectory(), workingContext);
  }

  public void storeConfiguration() throws IOException {
    getConfiguration().store(new FileOutputStream(getWorkingContextConfigurationFile()), "");
  }

  public class ConfigurationItem {

    private String property = null;
    private String label = null;
    private String defaultValue = null;

    public ConfigurationItem(String property, String label, String defaultValue) {
      this.property = property;
      this.label = label;
      this.defaultValue = defaultValue;
    }

    public String getProperty() {
      return property;
    }

    public String getLabel() {
      return label;
    }

    public String getDefaultValue() {
      return defaultValue;
    }
  }

  /**
   * Gets a reference to the location where manifests can be retrieved. Supports only CVS for now.
   */
  public Location getManifestStoreLocation() throws LocationException {

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
  public Location getLocationStoreLocation() throws LocationException {

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
   * Retrieve a valid reference to the manifest store directory. The available manifests
   * are stored here.
   *
   * @return A valid reference to the manifest store directory.
   */
  public  File getManifestStore() {

    File m = new File(getWorkingContextDirectory(), "manifests");
    m.mkdir();

    return m;
  }

  /**
   * Retrieve a valid reference to the location store directory. The available locations
   * are stored here.
   *
   * @return A valid reference to the location store directory.
   */
  public File getLocationStore() {

    File l = new File(getWorkingContextDirectory(), "locations");
    l.mkdir();

    return l;
  }

  /**
   * Retrieve a valid reference to the developer home directory. This is the directory where
   * the manifest instances are checked out from the version control system.
   *
   * @return A valid reference to the development home directory.
   */
  public File getDevelopmentHome() {

    File p = new File(getWorkingContextDirectory(), "projects");
    p.mkdir();

    return p;
  }

  /**
   * Returns the <code>File</code> location for the repository where <code>jar</code>-dependencies can be found. The
   * system property <code>JAR_REPOSITORY</code> is used to determine the location. If not set, the default jar
   * repository ($HOME/.karma/repository) is returned (and created if it doesn't exist).
   *
   * @return See method description.
   */
  public File getLocalRepository() {

    File localRepository = null;

    if (configuration.get(JAR_REPOSITORY) == null) {
      localRepository = new File(System.getProperty("user.home"), ".karma" + File.separator + "repository");
    } else {
      localRepository = new File((String)configuration.get(JAR_REPOSITORY));
    }
    localRepository.mkdirs();

    return localRepository;
  }

  /**
   * Determines the last used manifest for this working context.
   */
  public String getContextManifestPreference() {

    // Preference property identifying the manifest that was used in the last Karma session.
    //
    final String LAST_USED_MANIFEST_PREFERENCE = "karma.manifest.last";

    return getName() + "." + LAST_USED_MANIFEST_PREFERENCE;
  }

  /**
   * Returns a reference to the <code>LocationLoader</code> for the working context.
   * @return A location loader.
   */
  public LocationLoader getLocationLoader() throws LocationException {
    if (locationLoader == null) {
      locationLoader = new LocationLoader(this);
      locationLoader.load();
    }
    return locationLoader;
  }

  /**
   * Returns a reference to the <code>ManifestCollector</code> for the working context.
   * @return A manifest loader.
   */
  public ManifestCollector getManifestCollector() {
    if (manifestCollector == null) {
      manifestCollector = new ManifestCollector(this);
    }
    return manifestCollector;
  }

  /**
   * Returns a reference to the <code>ManifestLoader</code> for the working context.
   * @return A manifest loader.
   */
  public ManifestLoader getManifestLoader() {
    if (manifestLoader == null) {
      manifestLoader = new ManifestLoader(this);
    }
    return manifestLoader;
  }
}
