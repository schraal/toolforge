package nl.toolforge.karma.core;

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.model.ModuleDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/**
 * The name says it all. This class is the base (template) for a module.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public abstract class BaseModule implements Module {

  protected static Log logger = LogFactory.getLog(BaseModule.class);

  private State state = null;
  private Location location = null;
  private String name = null;
  private File manifestDirectory = null;

  /**
   * Constructs a module and stores a reference to it relative to the manifest on disk.
   *
   * @param descriptor Module definition object; cannot be <code>null</code>.
   * @param manifestDirectory The local directory where the module is located. Can be <code>null</code> when a
   * non-existing module is created (e.g. when a module doesn't yet exist in a repository.
   *
   * @throws ManifestException When input parameters don't match their respective patterns
   */
  public BaseModule(ModuleDescriptor descriptor, File manifestDirectory) throws ManifestException {

    if (descriptor == null) {
      throw new IllegalArgumentException("Descriptor should not be null.");
    }

//
//		if (location == null) {
//			throw new IllegalArgumentException("Location cannot be null.");
//		}
//
//		Pattern pattern = Expressions.getPattern("MODULE_NAME");
//
//    // TODO re-introduce the following piece of code.
//    //
//
////		if (pattern.matcher(moduleName).matches()) {
////			this.name = moduleName;
////		} else {
////			throw new KarmaException(KarmaException.DATAFORMAT_ERROR);
////		}

    this.name = descriptor.getName();
    this.location = descriptor.getLocation();
    setManifestDirectory(manifestDirectory);
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
   * @return See {@link Location}, and all implementing classes.
   */
  public final Location getLocation() {
    return this.location;
  }

  /**
   * A <code>SourceModule</code> can be in the three different states as defined in {@link Module}.
   *
   * @param state The (new) state of the module.
   */
  public void setState(State state) {

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

  private void setManifestDirectory(File manifestDirectory) {
    this.manifestDirectory = manifestDirectory;
  }

  /**
   * Gets the reference to the manifest directory where this module is a part of.
   *
   * @return
   */
  protected final File getManifestDirectory() {
    return this.manifestDirectory;
  }
}
