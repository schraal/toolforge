package nl.toolforge.karma.core.boot;

import java.io.File;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class Karma {

  /**
   * String indicating the configuration directory for working contexts and the general location for user specific
   * Karma configuration. Its default value is <code>$HOME/.karma</code>; the value cannot be changed.
   */
  public static final String CONFIGURATION_BASE_DIRECTORY = System.getProperty("user.home") + File.separator + ".karma";

  private static File configurationBaseDir = null;

  private Karma() {
    // Cannot be instantiated. Static stuff.
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
    configurationBaseDir = new File(CONFIGURATION_BASE_DIRECTORY);
    configurationBaseDir.mkdirs();

    return configurationBaseDir;
  }

  /**
   * Checks if <code>workingContext</code> already has a configuration file with the default name
   * (<code>working-context.xml</code>). If not, this method returns <code>null</code>. Otherwise, this method returns
   * the <code>File</code> representation of the configuration file.
   *
   * @return The <code>File</code> representation of the configuration file, or <code>null</code>.
   */
  public static File getConfigurationFile(WorkingContext workingContext) {
    return new File(workingContext.getWorkingContextConfigurationBaseDir(), "working-context.xml");
  }

}
