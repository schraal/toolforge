/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.boot;

import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationLoader;
import nl.toolforge.karma.core.manifest.ManifestCollector;
import nl.toolforge.karma.core.manifest.ManifestLoader;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.IOException;

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

  public final static ErrorCode CANNOT_REMOVE_ACTIVE_WORKING_CONTEXT = new ErrorCode("WCO-00001");

  public final static String WORKING_CONTEXT_PREFERENCE = "karma.working-context";

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

        // By default, disable commons.digester messages.
        //
        Logger dig = Logger.getLogger("org.apache.commons.digester");
        dig.isAttached(fileAppender);
        dig.setLevel(Level.OFF);

        Logger.getLogger(WorkingContext.class).info(
            "Default logging configuration enabled; override by placing 'log4j.xml' and 'log4j.dtd' on your classpath.");
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new KarmaRuntimeException("*** PANIC *** Log4j system could not be initialized.");
    }
  }

  /**
   * String indicating the configuration directory for working contexts and the general location for user specific
   * Karma configuration. Its default value is <code>$HOME/.karma</code>; the value cannot be changed.
   */
  public static final String CONFIGURATION_BASE_DIRECTORY = System.getProperty("user.home") + File.separator + ".karma";

  /**
   * Property indicating the base directory for development projects. All manifests will be checked out under this
   * directory, and the manifest store and location store are checked out at this location as well.
   */
  public static final String PROJECT_BASE_DIRECTORY_PROPERTY = "project.basedir";

  /**
   * Property indicating the root of a repository directory `Maven style`. For those who don't know Maven, check out
   * the <a href="http://maven.apache.org">Maven</a> website. This directory does not have to be the default Maven
   * repository directory. It can be any directory on a users' harddisk, as long as binary dependencies can be resolved
   * `Maven style`, because that is what Karma does as well.
   */
  public static final String PROJECT_LOCAL_REPOSITORY_PROPERTY = "project.local.repository";

  public static final String MANIFEST_STORE_MODULE = "manifest-store.module";
  public static final String LOCATION_STORE_MODULE = "location-store.module";

  /**
   * The property that identifies the local directory where jar dependencies can be found. Dependencies are
   * resolved Maven style, but to support environments where Maven is not available, the directory is configurable.
   * The default Maven repository is used when this property is not set in <code>karma.properties</code>.
   */

  // Stuff in the karma.properties

  public static final String LOCAL_REPOSITORY = "jar.repository";

  /** The default working context. */
  public static final String DEFAULT = "default";

  private static File localRepository = null;
  private static File configurationBaseDir = null;

  private String workingContext = null;

//  private File projectBaseDir = null;

//  private File defaultConfigurationDirectory = null;
//  private Properties configuration = null;
//  private Map invalidConfiguration = null;

  private ManifestCollector manifestCollector = null;
  private ManifestLoader manifestLoader = null;
  private LocationLoader locationLoader = null;

  private WorkingContextConfiguration configuration = null;


  /**
   * Constructs a <code>WorkingContext</code> in the default configuration base directory. The {@link #init()}-method
   * should be called to configure this working context.
   *
   * @param workingContext A working context name. If <code>workingContext</code> doesn't match the <code>\w+</code>
   *                       pattern, {@link DEFAULT} is assumed.
   */
  public WorkingContext(String workingContext) {
    this(workingContext, new File(CONFIGURATION_BASE_DIRECTORY));
  }

  /**
   * Constructs a <code>WorkingContext</code> with <code>configBaseDir</code> as the configuration base directory. The
   * {@link #init()}-method should be called to configure this working context.
   *
   * @param workingContext A working context name. If <code>workingContext</code> doesn't match the <code>\w+</code>
   *                       pattern, {@link DEFAULT} is assumed.
   * @param configBaseDir  The configuration base directory. If the directory does not exist, it will be created.
   */
  public WorkingContext(String workingContext, File configBaseDir) {

    if (!workingContext.matches("[\\w\\-]+")) {
      workingContext = DEFAULT;
    }
    this.workingContext = workingContext;

    if (configBaseDir == null) {
      throw new IllegalArgumentException("Configuration base directory cannot be null.");
    }
    configurationBaseDir = configBaseDir;
  }

  /**
   * Tries loading the <code>working-context.xml</code> for the working context and performs some basic checking upon
   * the availability of certain properties. If all is well, returns a <code>true</code>. This method returns
   * <code>false</code> if :
   *
   * <ul>
   *   <li>The file does not exist for the working context.</li>
   *   <li>Property <code>PROJECT_BASE_DIRECTORY_PROPERTY</code> is not available.</li>
   * </ul>
   *
   * @return <code>true</code> if the working context is ready ro rock 'n roll.
   *
   * @throws KarmaRuntimeException When the <code>working-context.xml</code>-file cannot be loaded correctly due to
   *                               XML errors.
   */
  public boolean init() {

    configuration = new WorkingContextConfiguration(this);
    boolean b = configuration.load();

    String p = configuration.getProperty(PROJECT_LOCAL_REPOSITORY_PROPERTY);
    if (p == null || "".equals(p)) {
      localRepository = new File(System.getProperty("user.home"), ".maven/repository");
    } else {
      localRepository = new File(p);
    }

    // Basic check on the availability of certain crucial properties.
    //
    p = configuration.getProperty(PROJECT_BASE_DIRECTORY_PROPERTY);
    if (p == null) {
      return false;
    }

    return b;
  }

  /**
   * Returns the name of this working context.
   *
   * @return The name of this working context.
   */
  public String getName() {
    return workingContext;
  }

  /**
   * Get the configuration for this working context.
   */
  public WorkingContextConfiguration getConfiguration() {

    if (configuration == null) {
      throw new KarmaRuntimeException("Configuration for working context `" + this + "` is null. `init()` has not been called.");
    }

    return configuration;
  }

  /**
   * Removes a working contexts' configuration directory.
   */
  public synchronized void remove() throws IOException {
    FileUtils.deleteDirectory(getWorkingContextConfigurationBaseDir());
  }


  public Location getLocationStoreLocation() {
    return getConfiguration().getLocationStore();
  }

  public Location getManifestStoreLocation() {
    return getConfiguration().getManifestStore();
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
   * directory does not exist, it is created. This method will return the directory <code>File</code> reference to
   * <code>$HOME/.karma/working-contexts/&lt;working-context-name&gt;</code>.
   *
   * @return a <code>File</code> reference to the configuration directory for the current working context.
   */
  public File getWorkingContextConfigurationBaseDir() {

    // Create the base configuration directory the base directory where working contexts are stored.
    //
    File workingContextsDir = new File(getConfigurationBaseDir(), "working-contexts");
    workingContextsDir.mkdir();

    File file = new File(workingContextsDir, workingContext);
    if (!file.exists()) {
      file.mkdir();
    }
    return file;
  }

  /**
   * Returns a <code>File</code> reference to the project base directory, which can be configured by the
   * <code>projects.basedir</code> property in the <code>working-context.xml</code> file.
   *
   * @return a <code>File</code> reference to the project base directory.
   */
  public File getProjectBaseDirectory() {

    String projectBaseDirProperty = getConfiguration().getProperty(PROJECT_BASE_DIRECTORY_PROPERTY);

    if (projectBaseDirProperty == null) {
      throw new KarmaRuntimeException("Property `project.basedir` is not configured for working context `" + this + "`.");
    }

    File projectBaseDir = new File(projectBaseDirProperty);
    if (!projectBaseDir.exists()) {
      projectBaseDir.mkdir();
    }
    return projectBaseDir;
  }

//  /**
//   * Returns a <code>File</code> reference to the project root directory, which . In this directory, all project work is
//   * stored by Karma, withing the current working context. When the directory does not exist, it is created.
//   *
//   * @return a <code>File</code> reference to the project base directory.
//   */
////  public File getWorkingContextProjectDir() {
//  public File getProjectRootDirectory() {
//
//    // Create something like $USER_HOME/karma/<working-context>
//    //
//    File file = new File(getProjectBaseDirectory(), getName());
//    if (!file.exists()) {
//      file.mkdir();
//    }
//
//    return file;
//  }

//
//  /**
//   * Returns a <code>File</code> reference to the checkout directtory for modules
//   * @return
//   */
//  public File getModuleRootDirectory() {
//
//    // Create something like $USER_HOME/karma/<working-context>
//    //
//    File file = new File(getProjectBaseDirectory(), getName());
//    if (!file.exists()) {
//      file.mkdir();
//    }
//
//    return file;
//  }

  /**
   * Returns a <code>File</code> reference to the administration directory for the working context. In the
   * administration directory, the manifest store and the location store are located for the current working context.
   *
   * @return A reference to the administration directory for the current working context.
   */
  public File getAdminDir() {

    File m = new File(getProjectBaseDirectory(), ".admin");
    if (!m.exists()) {
      m.mkdir();
    }

    return m;
  }


  /**
   * Gets the module name from the value of the <code>MANIFEST_STORE_MODULE</code> property by grabbing the bit after
   * the last <code>"/"</code>.
   *
   * @return The module name for the manifest store or <code>null</code> if the property is not set.
   */
  public String getManifestStoreModule() {

    String module = getConfiguration().getProperty(MANIFEST_STORE_MODULE);

    if (module == null) {
      return null;
    }
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
   *
   * @return The module name for the location store or <code>null</code> if the property is not set.
   */
  public String getLocationStoreModule() {

    String module = getConfiguration().getProperty(LOCATION_STORE_MODULE);

    if (module == null) {
      return null;
    }
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
   * {@link #getAdminDir()}.
   *
   * @return A reference to the location store directory.
   */
  public File getManifestStore() {

    File l = new File(new File(getAdminDir(), "manifest-store"), getConfiguration().getProperty(MANIFEST_STORE_MODULE)); // including offset
    if (!l.exists()) {
      l.mkdirs();
    }

    return l;
  }

  /**
   * Returns a <code>File</code> reference to the location store directory for the working context. When the directory
   * does not exist, it will be created. This directory is a direct subdirectory for
   * {@link #getAdminDir()}.
   *
   * @return A reference to the location store directory.
   */
  public File getLocationStore() {

    File l = new File(new File(getAdminDir(), "location-store"), getConfiguration().getProperty(LOCATION_STORE_MODULE)); // including offset
    if (!l.exists()) {
      l.mkdirs();
    }

    return l;
  }

//  /**
//   * Returns a <code>File</code> reference to the development home directory for the working context. When the directory
//   * does not exist, it will be created. This directory is a direct subdirectory for
//   * {@link #getModuleRootDirectory()} and is used by Karma to store a local copy of the manifest and its modules.
//   *
//   * @return A reference to the developmet home directory.
//   * @deprecated Use
//   */
//  public File getDevelopmentHome() {
//
//    File p = new File(getProjectRootDirectory(), "projects");
//    if (!p.exists()) {
//      p.mkdir();
//    }
//
//    return p;
//  }

  /**
   * See {@link PROJECT_LOCAL_REPOSITORY_PROPERTY}. When the property is not set, the default repository is
   * assumed to be in {@link #getConfigurationBaseDir}/<code>.repository</code>.
   *
   * @return See method description.
   *
   * @see PROJECT_LOCAL_REPOSITORY_PROPERTY
   */
  public static File getLocalRepository() {

    return localRepository;

//    File l = new File(System.getProperty());
//
//    try {
//      if (localRepository == null) {
//        l = new File(getConfigurationBaseDir(), ".repository");
//      } else {
//        l = new File(localRepository);
//        if (!l.exists()) {
//          l.mkdir();
//        }
//      }
//    } catch (NullPointerException n) {
//    }
//
//    return l;

//    String localRepository = configuration.getProperty(PROJECT_LOCAL_REPOSITORY_PROPERTY);
//
//    File l = null;
//
//    try {
//      if (localRepository == null) {
//        l = new File(getConfigurationBaseDir(), ".repository");
//      } else {
//        l = new File(localRepository);
//        if (!l.exists()) {
//          l.mkdir();
//        }
//      }
//    } catch (NullPointerException n) {
//    }
//
//    return l;

  }

//
//    if (localRepositoryBaseDir == null) {
//      localRepositoryBaseDir = new File(getConfigurationBaseDir(), ".repository");
//    }
//    localRepositoryBaseDir.mkdir();
//
//    return localRepositoryBaseDir;
//}

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
    return getName() + "." + "karma.manifest.last";
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

  /**
   * Returns the working contexts' name.
   *
   * @return The working contexts' name.
   */
  public String toString() {
    return getName();
  }
}
