package nl.toolforge.karma.core.scm;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;

import java.util.Iterator;
import java.util.Map;

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
   */
  public String getDependencies() {

    StringBuffer buf = new StringBuffer();

    Map modules = manifest.getAllModules();

    for (Iterator i = modules.values().iterator(); i.hasNext();) {
      Module module = (Module) i.next();
      buf.append(((SourceModule) module).getDependencies());
    }
    return buf.toString();
  }
}
