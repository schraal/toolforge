package nl.toolforge.karma.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/**
 * Class that checks if the user has a proper Maven enabled environment. This is required when
 * <code>&lt;mavenmodule&gt;</code>s are used. This class is called
 *
 * @author D.A. Smedes
 */
public final class MavenEnvironment {

  /** The MAVEN_HOME environment variable that must be set. This should be passed to the JVM upon startup. */
  public static final String ENV_MAVEN_HOME = "maven.home";

  /** The <code>maven.repo.local</code> environment variable when running Maven. Identifies the Maven repository for the
   * current user. When not found in as a JMV system variable, <code>${user.home}/.maven/repository</code> will be used
   * as the default.
   */
  public static final String ENV_MAVEN_REPOSITORY = "maven.repository";

  private static final String DEFAULT_MAVEN_REPOSITORY =
    System.getProperty("user.home") + File.separator + ".maven" + File.separator +"repository";

  private static final Log logger = LogFactory.getLog(MavenEnvironment.class);

  private static boolean valid = false;

  static {
    //
    //
  }

  public static boolean isValid() {

    boolean mavenHomeSet = (System.getProperty("maven.home") != null);

    logger.error("Maven environment not configured correctly.");

    return mavenHomeSet;

  }

  public static String getMavenRepository() {

    String mavenRepo = (System.getProperty(ENV_MAVEN_REPOSITORY) == null ? DEFAULT_MAVEN_REPOSITORY : System.getProperty(ENV_MAVEN_REPOSITORY));
    return mavenRepo;
  }

}
