package nl.toolforge.karma.core.boot;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.BaseModule;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;

import java.io.File;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class LocationStore extends AdminStore {

  public LocationStore(WorkingContext workingContext) {
    super(workingContext);
  }

  public final Module getModule() {

    if (module != null) {
      return module;
    }

    if (getModuleName() == null || "".equals(getModuleName())) {
      throw new KarmaRuntimeException("Module name for location store has not been set (correctly).");
    }

    // Names for stores can contain an offset.
    //
    String name = getModuleName();
    while (name.endsWith(File.separator)) {
      name.substring(0, name.length());
    }
    if (name.lastIndexOf(File.separator) > 0) {
      name = name.substring(name.lastIndexOf(File.separator) + 1);
    }
    
    module = new LocationModule(name, getLocation());
    module.setBaseDir(new File(getWorkingContext().getLocationStoreBasedir(), getModuleName()));

    return module;
  }

  protected class LocationModule extends BaseModule {

    public LocationModule(String name, Location location) {
      super(name, location);
    }

    public ModuleLayoutTemplate getLayoutTemplate() {
      return null;
    }
  }
}
