package nl.toolforge.karma.core.boot;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationLoader;
import nl.toolforge.karma.core.manifest.ManifestCollector;
import nl.toolforge.karma.core.manifest.ManifestLoader;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.cvs.CVSRepository;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  public final static String WORKING_CONTEXT = "karma.working-context";

  private final static String DEFAULT_CONVERSION_PATTERN = "%d{HH:mm:ss} [%5p] - %m%n";

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
  public static final String PROJECT_BASE_DIRECTORY = System.getProperty("user.home") + File.separator + "karma";

  // Stuff in the workingcontext.properties

  public static final String MANIFEST_STORE_HOST = "manifest-store.cvs.host";
  public static final String MANIFEST_STORE_PORT = "manifest-store.cvs.port";
  public static final String MANIFEST_STORE_REPOSITORY = "manifest-store.cvs.repository";
  public static final String MANIFEST_STORE_MODULE = "manifest-store.cvs.module"; // Including offset
//  public static final String MANIFEST_STORE_OFFSET = "manifest-store.cvs.offset";
  public static final String MANIFEST_STORE_PROTOCOL = "manifest-store.cvs.protocol";
  public static final String MANIFEST_STORE_USERNAME = "manifest-store.cvs.username";

  public static final String LOCATION_STORE_HOST = "location-store.cvs.host";
  public static final String LOCATION_STORE_PORT = "location-store.cvs.port";
  public static final String LOCATION_STORE_REPOSITORY = "location-store.cvs.repository";
  public static final String LOCATION_STORE_MODULE = "location-store.cvs.module";
  public static final String LOCATION_STORE_PROTOCOL = "location-store.cvs.protocol";
  public static final String LOCATION_STORE_USERNAME = "location-store.cvs.username";

  /**
   * The property that identifies the local directory where jar dependencies can be found. Dependencies are
   * resolved Maven style, but to support environments where Maven is not available, the directory is configurable.
   * The default Maven repository is used when this property is not set in <code>karma.properties</code>.
   */

  // Stuff in the karma.properties

  public static final String LOCAL_REPOSITORY = "jar.repository";

  private static File localRepositoryBaseDir = null;
  private static File configurationBaseDir = null;

  private String workingContext = null;

  private File projectBaseDir = null;

//  private File defaultConfigurationDirectory = null;
  private Properties configuration = null;
  private Map invalidConfiguration = null;

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
    this(
        workingContext,
        new File(System.getProperty("user.home"), ".karma"),  // Default configuration base directory
        new File(System.getProperty("user.home"), "karma"),
        null);
  }

  /**
   * Constructs a working context, using <code>defaultConfigurationDirectory</code> as the base directory.
   * <code>configuration</code> is used to configure the working context. The <code>configuration</code> object is
   * assumed to contain the default configuration properties for a working context. If not, a
   * <code>KarmaRuntimeException</code> is thrown. A call to {@link #initialize} can be used to check if the
   * configuration is complete.
   *
   * @param workingContext A name for the working context.
   * @param configBaseDir  The directory where configuration is stored. This directory will be used to create
   *                       configuration on a per-context basis. Each context will have its own subdirectory.
   * @param projectBaseDir The directory where projects are stored. This directory will be used to create
   *                       projectdirectories on a per-context basis. Each context will have its own subdirectory.
   * @param configuration  Alternative to reading configuration from the working context configuration directory.
   */
  public WorkingContext(String workingContext, File configBaseDir, File projectBaseDir, Properties configuration) {

    if (workingContext == null || "".equals(workingContext)) {
      workingContext = "default";
    }
    this.workingContext = workingContext;

    if (configBaseDir == null) {
      throw new IllegalArgumentException("Configuration base directory cannot be null.");
    }
    configurationBaseDir = configBaseDir;

    if (projectBaseDir == null) {
      throw new IllegalArgumentException("Project base directory cannot be null.");
    }
    this.projectBaseDir = projectBaseDir;

    this.configuration = (configuration == null ? new Properties() : configuration);

    // The repository where Karma can locate jar files (dependencies).
    //
    if (getConfiguration().getProperty(LOCAL_REPOSITORY) != null) {
      localRepositoryBaseDir = new File(getConfiguration().getProperty(LOCAL_REPOSITORY));
    }

    initialize();
  }

  /**
   * Initializes this <code>WorkingContext</code>. The initialization process checks the configuration for the
   * working context and reports any missing configuration items which can be obtained via the
   * {@link #getInvalidConfiguration}-method.
   */
  private void initialize() {

    if (getWorkingContextConfigDir().exists()) {
      try {
        configuration.load(new FileInputStream(getConfigurationFile()));
      } catch (IOException e) {
        // too bad .. we'll ask the user.
      }
    }
    // Existing or not existing, check it ...
    //
    checkConfiguration();
  }

  private void checkConfiguration() {

    invalidConfiguration = new HashMap();

    checkManifestStoreConfiguration();
    checkLocationStoreConfiguration();
  }

  private void checkManifestStoreConfiguration() {

    List invalids = new ArrayList();

    String protocol = (String) configuration.getProperty(MANIFEST_STORE_PROTOCOL);

    if (protocol == null) {
      invalids.add(
          new ConfigurationItem(MANIFEST_STORE_PROTOCOL, "What is your CVS server protocol ? (pserver|local)", "local")
      );
    } else {
      if (CVSRepository.PSERVER.equals(protocol)) {
        if ((String) configuration.getProperty(MANIFEST_STORE_HOST) == null) {
          invalids.add(
              new ConfigurationItem(MANIFEST_STORE_HOST, "At which host is your CVS server located ?", "127.0.0.1")
          );
        }
        if ((String) configuration.getProperty(MANIFEST_STORE_PORT) == null) {
          invalids.add(
              new ConfigurationItem(MANIFEST_STORE_PORT, "What is your CVS server port ?", "2401")
          );
        }
      }
      if ((String) configuration.getProperty(MANIFEST_STORE_REPOSITORY) == null) {
        invalids.add(
            new ConfigurationItem(MANIFEST_STORE_REPOSITORY, "Which repository is used for the manifest store ?", "/home/cvs")
        );
      }
      if ((String) configuration.getProperty(MANIFEST_STORE_MODULE) == null) {
        invalids.add(
            new ConfigurationItem(MANIFEST_STORE_MODULE, "Which module is used for the manifests ?", "manifests")
        );
      }
      if ((String) configuration.getProperty(MANIFEST_STORE_USERNAME) == null) {
        invalids.add(new ConfigurationItem(MANIFEST_STORE_USERNAME, "What is your login username ?", null));
      }
    }
    invalidConfiguration.put("MANIFEST-STORE", invalids);
  }

  private void checkLocationStoreConfiguration() {

    List invalids = new ArrayList();

    String protocol = (String) configuration.getProperty(LOCATION_STORE_PROTOCOL);

    if (protocol == null) {
      invalids.add(
          new ConfigurationItem(LOCATION_STORE_PROTOCOL, "What is your CVS server protocol ? (pserver|local)", "local")
      );
    } else {
      if (CVSRepository.PSERVER.equals(protocol)) {
        if ((String) configuration.getProperty(LOCATION_STORE_HOST) == null) {
          invalids.add(
              new ConfigurationItem(LOCATION_STORE_HOST, "At which host is your CVS server located ?", "127.0.0.1")
          );
        }
        if ((String) configuration.getProperty(LOCATION_STORE_PORT) == null) {
          invalids.add(
              new ConfigurationItem(LOCATION_STORE_PORT, "What is your CVS server port ?", "2401")
          );
        }
      }
      if ((String) configuration.getProperty(LOCATION_STORE_REPOSITORY) == null) {
        invalids.add(
            new ConfigurationItem(LOCATION_STORE_REPOSITORY, "Which repository is used for the 'locations' module ?", "/home/cvs")
        );
      }
      if ((String) configuration.getProperty(LOCATION_STORE_MODULE) == null) {
        invalids.add(
            new ConfigurationItem(LOCATION_STORE_MODULE, "What module is used for your locations ?", "locations")
        );
      }
      if ((String) configuration.getProperty(LOCATION_STORE_USERNAME) == null) {
        invalids.add(new ConfigurationItem(LOCATION_STORE_USERNAME, "What is your login username ?", null));
      }
    }
    invalidConfiguration.put("LOCATION-STORE", invalids);
  }

  public String getName() {
    return workingContext;
  }

  /**
   * Returns a <code>List</code> of <code>ConfigurationItem</code>s each one identifying a property that has not been
   * set by the user for the working context. Together with a call to {@link #getConfiguration} clients can process
   * all missing properties.
   */
  public Map getInvalidConfiguration() {
    checkConfiguration();
    return invalidConfiguration;
  }

  /**
   * Get the configuration for this working context.
   */
  public Properties getConfiguration() {
    return configuration;
  }

  /**
   * Stores the current configuration in <code>workingcontext.properties</code>, which is located in
   * {@link #getWorkingContextConfigDir()}.
   *
   * @throws IOException When the configuration could not be stored.
   */
  public void storeConfiguration() throws IOException {

    getConfiguration().store(new FileOutputStream(getConfigurationFile()), "");

    // We know we are dealing with VersionControlSystem instances, so we can cast,
    //
    storeAuthentication("manifest-store", configuration.getProperty(MANIFEST_STORE_USERNAME));
    storeAuthentication("location-store", configuration.getProperty(LOCATION_STORE_USERNAME));
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
   * Gets a reference to the location where manifests can be retrieved. Supports only CVS for now (a
   * <code>CVSLocationImpl</code> is returned).
   */
  public Location getManifestStoreLocation() throws LocationException {

    if (configuration == null) {
      throw new KarmaRuntimeException("Local environment has not been initialized. Call initialize().");
    }

    CVSRepository location = new CVSRepository("manifest-store");

    try {
      location.setRepository(configuration.getProperty(MANIFEST_STORE_REPOSITORY));

      String module = configuration.getProperty(MANIFEST_STORE_MODULE);

      if (module.lastIndexOf("/") > 0) {
        location.setOffset(module.substring(0, module.lastIndexOf("/")));
      } else if (module.lastIndexOf("/") > 0) {
        location.setOffset(module.substring(0, module.lastIndexOf("\\")));
      } else {
        location.setOffset(null);
      }

    } catch (Exception e) {
      throw new LocationException(LocationException.INVALID_MANIFEST_STORE_LOCATION, new Object[]{"'"+MANIFEST_STORE_REPOSITORY+"'"});
    }
    try {
      location.setProtocol(configuration.getProperty(MANIFEST_STORE_PROTOCOL));
    } catch (Exception e) {
      throw new LocationException(LocationException.INVALID_MANIFEST_STORE_LOCATION, new Object[]{"'"+MANIFEST_STORE_PROTOCOL+"'"});
    }
    if (!location.getProtocol().equals(CVSRepository.LOCAL)) {
      try {
        location.setHost(configuration.getProperty(MANIFEST_STORE_HOST));
      } catch (Exception e) {
        throw new LocationException(LocationException.INVALID_MANIFEST_STORE_LOCATION, new Object[]{"'"+MANIFEST_STORE_HOST+"'"});
      }
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
   * Gets a reference to the location where <code>location.xml</code> can be retrieved. Supports only CVS for now (a
   * <code>CVSRepository</code> is returned).
   */
  public Location getLocationStoreLocation() throws LocationException {

    if (configuration == null) {
      throw new KarmaRuntimeException("Local environment has not been initialized. Call initialize().");
    }

    CVSRepository location = new CVSRepository("location-store");
    try {
      location.setRepository(configuration.getProperty(LOCATION_STORE_REPOSITORY));

      String module = configuration.getProperty(LOCATION_STORE_MODULE);

      if (module.lastIndexOf("/") > 0) {
        location.setOffset(module.substring(0, module.lastIndexOf("/")));
      } else if (module.lastIndexOf("/") > 0) {
        location.setOffset(module.substring(0, module.lastIndexOf("\\")));
      } else {
        location.setOffset(null);
      }

    } catch (Exception e) {
      throw new LocationException(LocationException.INVALID_LOCATION_STORE_LOCATION, new Object[]{"'"+LOCATION_STORE_REPOSITORY+"'"});
    }
    try {
      location.setProtocol(configuration.getProperty(LOCATION_STORE_PROTOCOL));
    } catch (Exception e) {
      throw new LocationException(LocationException.INVALID_LOCATION_STORE_LOCATION, new Object[]{"'"+LOCATION_STORE_PROTOCOL+"'"});
    }
    if (!location.getProtocol().equals(CVSRepository.LOCAL)) {
      try {
        location.setHost(configuration.getProperty(LOCATION_STORE_HOST));
      } catch (Exception e) {
        throw new LocationException(LocationException.INVALID_LOCATION_STORE_LOCATION, new Object[]{"'"+LOCATION_STORE_HOST+"'"});
      }
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
   *
   * @param id
   * @param userName
   */
  private void storeAuthentication(String id, String userName) {

    Authenticator authenticator = new Authenticator();
    authenticator.setId(id);
    authenticator.setUsername(userName);

    authenticator.addAuthenticator(authenticator);
  }


  /**
   * Returns a <code>File</code> reference to the default base directory for Karma configuration files. When the
   * directory does not exist, it is created.
   *
   * @return A <code>File</code> reference to the default base directory for Karma configuration files.
   */
  public static File getConfigurationBaseDir() {
    // Create something like $USER_HOME/.karma/
    //
    if (!configurationBaseDir.exists()) {
      configurationBaseDir.mkdir();
    }
    return configurationBaseDir;
  }


  /**
   * Returns a <code>File</code> reference to the configuration directory for the current working context. When the
   * directory does not exist, it is created.
   *
   * @return a <code>File</code> reference to the configuration directory for the current working context.
   */
  public File getWorkingContextConfigDir() {

    File file = new File(getConfigurationBaseDir(), workingContext);
    if (!file.exists()) {
      file.mkdir();
    }
    return file;
  }

  /**
   * Returns a <code>File</code> reference to <code>workingcontext.properties</code> for the current working context.
   *
   * @return A <code>File</code> reference to <code>workingcontext.properties</code> for the current working context.
   */
  public File getConfigurationFile() {
    return new File(getWorkingContextConfigDir(), "workingcontext.properties");
  }

  /**
   * Returns a <code>File</code> reference to the project base directory. In this directory, each working context has
   * its own directory. When the directory does not exist, it is created.
   *
   * @return a <code>File</code> reference to the project base directory.
   */
  public File getProjectBaseDirectory() {

    // Create something like $USER_HOME/karma/
    //
    if (!projectBaseDir.exists()) {
      projectBaseDir.mkdir();
    }

    return projectBaseDir;
  }

  /**
   * Returns a <code>File</code> reference to the project base directory. In this directory, all project work is
   * stored by Karma, withing the current working context. When the directory does not exist, it is created.
   *
   * @return a <code>File</code> reference to the project base directory.
   */
  public File getWorkingContextProjectDir() {

    // Create something like $USER_HOME/karma/<working-context>
    //
    File file = new File(getProjectBaseDirectory(), getName());
    if (!file.exists()) {
      file.mkdir();
    }

    return file;
  }

  /**
   * Returns a <code>File</code> reference to the administration directory for the working context. When the directory
   * does not exist, it will be created. This directory is a direct subdirectory for
   * {@link #getWorkingContextProjectDir}.
   *
   * @return A reference to the administration directory.
   */
  public File getAdminDir() {

    File m = new File(getWorkingContextProjectDir(), ".admin");
    if (!m.exists()) {
      m.mkdir();
    }

    return m;
  }


  /**
   * Gets the module name from the value of the <code>MANIFEST_STORE_MODULE</code> property by grabbing the bit after
   * the last <code>"/"</code>.
   */
  public String getManifestStoreModule() {

    String module = configuration.getProperty(MANIFEST_STORE_MODULE);

    if (module.lastIndexOf("/") > 0) {
      return module.substring(0, module.lastIndexOf("/"));
    } else if (module.lastIndexOf("/") > 0) {
      return module.substring(0, module.lastIndexOf("\\"));
    } else {
      return module;
    }
  }

  /**
   * Gets the module name from the value of the <code>LOCATION_STORE_MODULE</code> property by grabbing the bit after
   * the last <code>"/"</code>.
   */
  public String getLocationStoreModule() {

    String module = configuration.getProperty(LOCATION_STORE_MODULE);

    if (module.lastIndexOf("/") > 0) {
      return module.substring(0, module.lastIndexOf("/"));
    } else if (module.lastIndexOf("/") > 0) {
      return module.substring(0, module.lastIndexOf("\\"));
    } else {
      return module;
    }
  }


  /**
   * Returns a <code>File</code> reference to the location store directory for the working context. When the directory
   * does not exist, it will be created. This directory is a direct subdirectory for
   * {@link #getWorkingContextProjectDir}.
   *
   * @return A reference to the location store directory.
   */
  public File getManifestStore() {

    // Hardcoded ...

//    new File(getAdminDir(), "manifest-store").mkdir();

    File l = new File(new File(getAdminDir(), "manifest-store"), configuration.getProperty(MANIFEST_STORE_MODULE)); // including offset
    if (!l.exists()) {
      l.mkdir();
    }

    return l;
  }

  /**
   * Returns a <code>File</code> reference to the location store directory for the working context. When the directory
   * does not exist, it will be created. This directory is a direct subdirectory for
   * {@link #getWorkingContextProjectDir}.
   *
   * @return A reference to the location store directory.
   */
  public File getLocationStore() {

    // Hardcoded ...

//    new File(getAdminDir(), "location-store").mkdir();

    File l = new File(new File(getAdminDir(), "location-store"), configuration.getProperty(LOCATION_STORE_MODULE)); // including offset
    if (!l.exists()) {
      l.mkdir();
    }

    return l;
  }

  /**
   * Returns a <code>File</code> reference to the development home directory for the working context. When the directory
   * does not exist, it will be created. This directory is a direct subdirectory for
   * {@link #getWorkingContextProjectDir} and is used by Karma to store a local copy of the manifest and its modules.
   *
   * @return A reference to the developmet home directory.
   */
  public File getDevelopmentHome() {

    File p = new File(getWorkingContextProjectDir(), "projects");
    if (!p.exists()) {
      p.mkdir();
    }

    return p;
  }

  /**
   * Returns the <code>File</code> location for the repository where <code>jar</code>-dependencies can be found. The
   * system property {@link LOCAL_REPOSITORY} is used to determine the location. If not set, the default jar
   * repository ($HOME/.karma/.repository) is returned. If the directory does not exist, a <code>.repository</code> is
   * created relative to {@link #getConfigurationBaseDir}.
   *
   * @return See method description.
   */
  public static File getLocalRepository() {

    if (localRepositoryBaseDir == null) {
      localRepositoryBaseDir = new File(getConfigurationBaseDir(), ".repository");
    }
    localRepositoryBaseDir.mkdir();

    return localRepositoryBaseDir;
  }

  public static File getKarmaHome() {

    if (System.getProperty("karma.home") == null) {
      throw new KarmaRuntimeException("KARMA_HOME (karma.home) environment variable has not been set.");
    }

    return new File(System.getProperty("karma.home"));
  }

  /**
   * <p>Determines the last used manifest for this working context. This fact is maintained in the <code>.java</code> file
   * on a users' harddisk, as per the specification for <code>java.util.prefs</code>, included in the JDK since
   * <code>1.4</code>.
   *
   * <p>A <code>String</code> made up of the working context name and <code>karma.manifest.last</code>.
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
