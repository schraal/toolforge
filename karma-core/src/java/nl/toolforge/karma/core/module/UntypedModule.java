package nl.toolforge.karma.core.module;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.BaseModule;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;

import java.io.IOException;
import java.util.Set;

/**
 * This type of module is used when a Module instance is required for remote modules. At this point, the type is not yet
 * known.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class UntypedModule extends BaseModule {

  public UntypedModule(String name, Location location) {
    this(name, location, null);
  }

  /**
   * Creates a <code>JavaEnterpriseApplicationModule</code> using a {@link nl.toolforge.karma.core.manifest.util.EappModuleLayoutTemplate} as the layout
   * template.
   *
   * @throws java.io.IOException When the module (layout) could not be created.
   */
  public void create() throws IOException {
    throw new KarmaRuntimeException("Untyped modules cannot be created.");
  }

  /**
   * Throws a <code>KarmaRuntimeException</code>, because these modules cannot be created.
   */
  public ModuleLayoutTemplate getLayoutTemplate() {
    throw new KarmaRuntimeException("Untyped modules have no layout template.");
  }

  public UntypedModule(String name, Location location, Version version) {
    super(name, location, version);
  }
}
