package nl.toolforge.karma.core;

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.location.LocationException;

/**
 * Factory class to create modules. We're talking about <b>new</b> modules, <b>not</b> existing modules in a
 * manifest; these are read by the {@link ManifestLoader} when required.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ModuleFactory {

  private static ModuleFactory instance;

  /**
   * Get the singleton instance of this factory.
   *
   * @return The factory to create modules.
   */
  public synchronized static ModuleFactory getInstance() {
    if (instance == null) {
      instance = new ModuleFactory();
    }
    return instance;
  }

  private ModuleFactory() {
  }

  public Module createModule(String moduleName, String locationAlias) throws LocationException, ManifestException {
    Location location = LocationFactory.getInstance().get(locationAlias);
    return new SourceModule(new SourceModuleDescriptor(moduleName, location), null);
  }
}

