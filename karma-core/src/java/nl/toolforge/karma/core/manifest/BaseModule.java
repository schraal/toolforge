package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.location.Location;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.regex.PatternSyntaxException;

/**
 * The name says it all. This class is the base (template) for a module.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class BaseModule implements Module {

  protected static Log logger = LogFactory.getLog(BaseModule.class);

  private Location location = null;
  private String name = null;

  private Module.DeploymentType deploymentType = null;

  public BaseModule(String name, Location location) {

    if (!name.matches(ModuleDescriptor.NAME_PATTERN_STRING)) {
      throw new PatternSyntaxException(
          "Pattern mismatch for 'name'. Should match " + ModuleDescriptor.NAME_PATTERN_STRING, name, -1);
    }
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null.");
    }

    this.name = name;
    setDeploymentType(name);
    this.location = location;
  }

  /**
   * Gets the modules' name.
   *
   * @see Module#getName
   */
  public final String getName() {
    return name;
  }

  private void setDeploymentType(String moduleName) {
    deploymentType = new Module.DeploymentType(moduleName);
  }

  public final DeploymentType getDeploymentType() {
    return deploymentType;
  }

  /**
   * Gets the modules' location.
   *
   * @return See {@link nl.toolforge.karma.core.location.Location}, and all implementing classes.
   */
  public final Location getLocation() {
    return location;
  }


//  public abstract String getDependencyName();

  public boolean equals(Object obj) {

    if (obj instanceof BaseModule) {
      if (((BaseModule) obj).getName().equals(getName()) &&
          ((BaseModule) obj).getLocation().equals(getLocation())) {
        return true;
      }
    }
    return false;
  }

  public int hashCode() {
    return getName().hashCode() + getLocation().hashCode();
  }
}
