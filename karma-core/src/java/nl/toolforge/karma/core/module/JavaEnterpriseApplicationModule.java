package nl.toolforge.karma.core.module;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.BaseModule;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.util.EappModuleLayoutTemplate;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;
import nl.toolforge.karma.core.scm.digester.ModuleDependencyCreationFactory;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Module type containing libraries. This release only supports the Karma Java Edition, which means that the libs
 * (jar-files) need to be stored Maven-style, as this is how they will be looked up in the module.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class JavaEnterpriseApplicationModule extends BaseModule {

  public JavaEnterpriseApplicationModule(String name, Location location) {
    this(name, location, null);
  }

  /**
   * Returns an {@link EappModuleLayoutTemplate} instance.
   *
   * @return An {@link EappModuleLayoutTemplate} instance.
   */
  public ModuleLayoutTemplate getLayoutTemplate() {
    return new EappModuleLayoutTemplate();
  }

  public JavaEnterpriseApplicationModule(String name, Location location, Version version) {
    super(name, location, version);
  }
}
