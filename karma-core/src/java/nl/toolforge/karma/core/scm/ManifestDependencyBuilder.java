package nl.toolforge.karma.core.scm;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Manifest;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.ModuleMap;

import java.util.Iterator;

public final class ManifestDependencyBuilder implements DependencyBuilder {

  private Manifest manifest = null;

  /**
   * Creates a builder for the manifest supplied.
   *
   * @param manifest
   */
  public ManifestDependencyBuilder(Manifest manifest) {
    this.manifest = manifest;
  }

  /**
   * Builds a dependency list for all modules contained in the manifest.
   *
   * @return A ':' separated string with all dependencies for this manifest.
   *
   * @throws KarmaException
   */
  public String getDependencies() throws KarmaException {

    StringBuffer buf = new StringBuffer();

    ModuleMap modules = manifest.getModules();

    for (Iterator i = modules.values().iterator(); i.hasNext();) {
      ModuleDependencyBuilder builder = new ModuleDependencyBuilder((Module) i.next());
      buf.append(builder.getDependencies());
    }
    return buf.toString();
  }
}
