package nl.toolforge.karma.core.manifest;

import java.util.Set;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.util.LibModuleLayoutTemplate;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;

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

  /**
   * Returns an {@link LibModuleLayoutTemplate} instance.
   *
   * @return An {@link LibModuleLayoutTemplate} instance.
   */
  public ModuleLayoutTemplate getLayoutTemplate() {
    return new LibModuleLayoutTemplate();
  }

  public LibModule(String name, Location location, Version version) {
    super(name, location, version);
  }

  public Set getLibraries() {
    throw new KarmaRuntimeException("to be implemented ...");
  }

}
