package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;

import java.util.Set;

/**
 * Module type containing libraries. This release only supports the Karma Java Edition, which means that the libs
 * (jar-files) need to be stored Maven-style, as this is how they will be looked up in the module.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class LibModule extends BaseModule {

  public LibModule(String name, Location location) {
    this(name, location, null);
  }

  public LibModule(String name, Location location, Version version) {
    super(name, location, version);
  }

//  /**
//   * Returns the <code>lib</code> SourceType.
//   *
//   * @return
//   */
//  public SourceType getSourceType() {
//    return new Module.SourceType("lib");
//  }

  /**
   * Not implemented. Will throw a <code>KarmaRuntimeException</code>.
   * 
   * @return
   */
  public Set getDependencies() {
    throw new KarmaRuntimeException(
        "Lib module cannot have dependencies themselves. If you intended to retrieve " +
        "the libraries contained in this module, use the 'getLibraries()'-method.");
  }

  public Set getLibraries() {
    throw new KarmaRuntimeException("to be implemented ...");
  }

}
