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

  private State state = null;
  private Location location = null;
  private String name = null;

  public BaseModule(String name, Location location) {

    if (!name.matches(ModuleDescriptor.NAME_PATTERN_STRING)) {
      throw new PatternSyntaxException(
          "Pattern mismatch for 'name'. Should match " + ModuleDescriptor.NAME_PATTERN_STRING, name, -1);
    }
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null.");
    }

    this.name = name;
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

  /**
   * Gets the modules' location.
   *
   * @return See {@link nl.toolforge.karma.core.location.Location}, and all implementing classes.
   */
  public final Location getLocation() {
    return location;
  }

  /**
   * A <code>SourceModule</code> can be in the three different states as defined in {@link nl.toolforge.karma.core.Module}.
   *
   * @param state The (new) state of the module.
   */
  public final void setState(State state) {

    // TODO : this one should probably update the file on disk
    //
    this.state = state;
  }

  /**
   * Gets the modules' current state.
   *
   * @return The current state of the module.
   */
  public final State getState() {
    return state;
  }

  public final String getStateAsString() {
    return (state == null ? "N/A" : state.toString());
  }

  // todo getDependencies should throw some exception.

  public abstract String getDependencies();

  public abstract String getDependencyName();

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
