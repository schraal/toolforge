package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.module.ModuleDescriptor;
import nl.toolforge.karma.core.manifest.util.WebappModuleLayoutTemplate;
import nl.toolforge.karma.core.manifest.util.EappModuleLayoutTemplate;
import nl.toolforge.karma.core.manifest.util.LibModuleLayoutTemplate;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;
import nl.toolforge.karma.core.vc.VersionControlSystem;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.cvsimpl.CVSRunner;
import nl.toolforge.karma.core.location.Location;

import java.util.Set;
import java.io.IOException;
import java.io.File;

import org.apache.commons.io.FileUtils;

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

//  /**
//   * Creates a <code>JavaWebApplicationModule</code> using a {@link nl.toolforge.karma.core.manifest.util.WebappModuleLayoutTemplate} as the layout
//   * template.
//   *
//   * @throws java.io.IOException When the module (layout) could not be created.
//   */
//  public void create() throws IOException {
//
//    File tmp = create(new LibModuleLayoutTemplate());
//
//    try {
//      FileUtils.deleteDirectory(tmp);
//    } catch (IOException e) {
//      throw new KarmaRuntimeException("Could not remove temporary directory.");
//    }
//
//    ModuleDescriptor descriptor = new ModuleDescriptor(Module.LIBRARY_MODULE);
//    descriptor.createFile(getBaseDir());
//  }

//    public Type getType() {
//    return Module.LIBRARY_MODULE;
//  }

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
