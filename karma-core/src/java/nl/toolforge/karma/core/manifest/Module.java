package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.location.Location;

import java.io.File;
import java.util.regex.PatternSyntaxException;


/**
 * <p>A module is a collection of files, representing some block of functionality. This definition is probably highly
 * subjective, but for Karma, that's what it is. A module is part of a container, called a
 * <code>Manifest</code>. System's theory tells us that a system is separated into subsystems. Well, that's what we
 * do in the Karma context as well. An application system consists of one or more (generally more) modules.</p>
 *
 * <p>Karma <code>Module</code>s are maintained in a version management system and grouped together in a
 * <code>Manifest</code>. The manifest is managing the modules.</p>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Module {

  public static final State WORKING = new State("WORKING");
  public static final State DYNAMIC = new State("DYNAMIC");
  public static final State STATIC = new State("STATIC");

  public static final DeploymentType JAR = new DeploymentType("jar");

  /**
   * <p>Modules that contain application server specific configuration, must have this prefix.
   *
   * <p>Check the Karma User Manual for background information about this type of module.
   */
  public static final DeploymentType CONFIG_APPSERVER = new DeploymentType("config-appserver");

  /**
   * <p>Modules that contain application server software (or the applicatin server as a whole), must have this prefix.
   *
   * <p>Check the Karma User Manual for background information about this type of module.
   */
  public static final DeploymentType APPSERVER = new DeploymentType("appserver");

  /**
   * <p>Modules containing a web application (resulting in the creation of a web application archive - WAR -, must have
   * this prefix. Webapp-modules have specific requirements wrt to their internal structure. The
   *
   * <p>Check the Karma User Manual for background information about this type of module.
   */
  public static final DeploymentType WEBAPP = new DeploymentType("webapp");

  /**
   * <p>Modules containing an enterprise application (resulting in the creation of a enterprise application archive - EAR -, must have
   * this prefix. Eapp-modules have specific requirements wrt to their internal structure. The 
   *
   * <p>Check the Karma User Manual for background information about this type of module.
   */
  public static final DeploymentType EAPP = new DeploymentType("eapp");

  /**
   * Modules that should create an <code>EAR</code>-file as a means of distribution, should have a module with
   * a <code>deploy-config</code>-prefix. Each of these modules is scanned for the <code>application.xml</code> file. 
   */
  public static final DeploymentType DEPLOY_CONFIG = new DeploymentType("deploy-config");

  /**
   * The name of the mandatory file in a module. A file with this name is created by Karma or should be created
   * manually and contain all data (symbolic names) that should be available for existing manifests.
   */
  public static final String MODULE_INFO = "module.info";

  /**
   * Retrieves a modules' name, the <code>name</code> attribute of the module in the manifest XML file.
   *
   * @return The modules' name.
   */
  public String getName();

  public SourceType getSourceType();

  public DeploymentType getDeploymentType();

  /**
   * Returns the <code>Location</code> instance, which is derived from the <code>location</code>-attribute.
   */
  public Location getLocation();

  /**
   * A module has a base directory, which is relative to the manifest that requires it. When the manifest is loaded, the
   * base directory can be set.
   *
   * @param baseDir The base directory of the module relative to the active manifest.
   */
  public void setBaseDir(File baseDir);

  /**
   * The base directory of the module relative to the active manifest.
   *
   * @return The base directory of the module relative to the active manifest.
   */
  public File getBaseDir();

  /**
   * <p>Sets a modules' state.</p>
   *
   * <p>This method currently only applies to SourceModule implementations, but has been included here to allow for
   * easier future enhancement to Karma.</p>
   *
   * @param state
   */
  public void setState(State state);

  /**
   * <p>This method currently only applies to SourceModule implementations, but has been included here to allow for
   * easier future enhancement to Karma.</p>
   *
   * @return
   */
  public String getStateAsString();

  /**
   * Types a module in the source hierarchy.
   */
  final class SourceType {

    private String sourceType = null;

    public SourceType(String sourceType) {

      if (sourceType == null || !sourceType.matches(ModuleDescriptor.SOURCE_TYPE_PATTERN_STRING)) {
        throw new PatternSyntaxException(
            "Pattern mismatch for type-attribute. Should match " + ModuleDescriptor.SOURCE_TYPE_PATTERN_STRING, sourceType, -1);
      }
      this.sourceType = sourceType;
    }

    public boolean equals(Object o) {

      if (o instanceof SourceType) {
        if (((SourceType) o).getSourceType().equals(this.getSourceType())) {
          return true;
        } else {
          return false;
        }
      }
      return false;
    }



    public String getSourceType() {
      return this.sourceType;
    }

    public int hashCode() {
      return sourceType.hashCode();
    }
  }

  /**
   * Types a module in the deployment hierarchy.
   */
  final class DeploymentType {

    private static final String CONFIG_APPSERVER_PREFIX = "config-appserver";
    private static final String APPSERVER_PREFIX = "appserver";
    private static final String WEBAPP_PREFIX = "webapp";
    private static final String EAPP_PREFIX = "eapp";
    private static final String DEPLOY_CONFIG_PREFIX = "deploy-config";

    private static final String DEPLOYMENT_TYPE_PATTERN_STRING =
        CONFIG_APPSERVER_PREFIX + "|" +
        APPSERVER_PREFIX + "|" +
        WEBAPP_PREFIX + "|" +
        EAPP_PREFIX + "|" +
        DEPLOY_CONFIG_PREFIX;

    private String deploymentType = null;

    public DeploymentType(String moduleName) {

      if (moduleName == null) {
        throw new IllegalArgumentException("");
      }

      if (moduleName.startsWith(CONFIG_APPSERVER_PREFIX)) {
        deploymentType = CONFIG_APPSERVER_PREFIX;
      } else if (moduleName.startsWith(APPSERVER_PREFIX)) {
        deploymentType = APPSERVER_PREFIX;
      } else if (moduleName.startsWith(WEBAPP_PREFIX)) {
        deploymentType = WEBAPP_PREFIX;
      } else if (moduleName.startsWith(EAPP_PREFIX)) {
        deploymentType = EAPP_PREFIX;
      } else if (moduleName.startsWith(DEPLOY_CONFIG_PREFIX)) {
        deploymentType = DEPLOY_CONFIG_PREFIX;
      } else {
        deploymentType = "jar"; // This is a JAR;
      }
    }

    public boolean equals(Object o) {

      if (o instanceof DeploymentType) {
        if (((DeploymentType) o).getDeploymentType().equals(this.getDeploymentType())) {
          return true;
        } else {
          return false;
        }
      }
      return false;
    }

    public String getDeploymentType() {
      return this.deploymentType;
    }

    public int hashCode() {
      return deploymentType.hashCode();
    }

    public String getPrefix() {

      if (getDeploymentType().equals("jar")) {
        return ""; // No prefix
      } else {
        return deploymentType;
      }
    }
  }

  /**
   * <p>Inner class representing the 'state' of a module. Three states exist at the moment : <code>WORKING</code>,
   * <code>STATIC</code> and <code>DYNAMIC</code>.
   * <p/>
   * <ul>
   * <li/><code>WORKING</code> means that a developer wants to develop on the module; add code, remove code etc. The
   * local copy of the module will be updated to the reflect the latest versions of files in a particular
   * branch. <code>WORKING</code> state also implies that a developer can promote a module so that manifests
   * that have the module in a <code>DYNAMIC</code> state, can
   */
  final class State {

    // todo unit test should be written

    private String state = null;

    /**
     * Constructor. Initializes the <code>State</code> instance with the correct state string.
     *
     * @param stateString
     */
    public State(String stateString) {

      if (!stateString.matches("WORKING|DYNAMIC|STATIC")) {
        throw new PatternSyntaxException(
            "Pattern mismatch for 'state'; pattern must match 'WORKING|DYNAMIC|STATIC'", stateString, -1);
      }
      this.state = stateString;
    }

    /**
     * Gets the string representation of this state object.
     *
     * @return A <code>String</code> representation of this state object.
     */
    public String toString() {
      return state;
    }

    public int hashCode() {
      return state.hashCode();
    }

    /**
     * Checks equality of one <code>State</code> instance to this <code>State</code> instance. Instances are equal
     * when their state strings are equal.
     *
     * @param o An object instance that must be checked for equality with this <code>State</code> instance.
     * @return <code>true</code> if this <code>State</code> instance equals <code>o</code>, otherwise
     *         <code>false</code>.
     */
    public boolean equals(Object o) {

      if (o instanceof State) {
        if (o.toString().equals(this.toString())) {
          return true;
        } else {
          return false;
        }
      }
      return false;
    }

    /**
     * Returns the filename for this state on disk (generally something like <code>.WORKING</code> or
     * <code>.STATIC</code>.
     *
     * @return
     */
    public String getHiddenFileName() {
      return "." + state;
    }
  }
}
