package nl.toolforge.karma.core.boot;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.BaseModule;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the manifest store for a working context. A manifest store contains module(s) which - in turn -
 * contain manifest files (albeit in
 *
 * @author D.A. Smedes
 * @version $Id$
 *
 * @since Karma 1.0
 */
public final class ManifestStore extends AdminStore {

  public ManifestStore(WorkingContext workingContext) {
    super(workingContext);
  }

  public ManifestStore(WorkingContext workingContext, String moduleName, Location location) {
    super(workingContext, moduleName, location);
  }

  /**
   *
   * @return
   * @throws IOException
   */
  public List getManifestFiles() throws IOException {
    return new ArrayList();
  }

  public final Module getModule() {

    if (module != null) {
      return module;
    }

    if (getModuleName() == null || "".equals(getModuleName())) {
      throw new KarmaRuntimeException("Module name for manifest store has not been set (correctly).");
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
    
    module = new ManifestModule(name, getLocation());
    module.setBaseDir(new File(getWorkingContext().getManifestStoreBasedir(), getModuleName()));
//    module.setCheckoutDir(getWorkingContext().getManifestStoreBasedir());

    return module;
  }

  protected class ManifestModule extends BaseModule {

    public ManifestModule(String name, Location location) {
      super(name, location);
    }

    public ModuleLayoutTemplate getLayoutTemplate() {
      return null;
    }
  }

}
