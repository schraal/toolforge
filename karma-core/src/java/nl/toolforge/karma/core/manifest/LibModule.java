package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;

import java.util.Set;

/**
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

  /**
   * Returns the <code>lib</code> SourceType.
   *
   * @return
   */
  public SourceType getSourceType() {
    return new Module.SourceType("lib");
  }

  public Set getDependencies() {
    throw new KarmaRuntimeException("to be implemented ...");
  }

}
