package nl.toolforge.karma.core.scm;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.MavenEnvironment;
import nl.toolforge.karma.core.MavenModule;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.SourceModule;
import org.apache.maven.project.Dependency;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public final class ModuleDependencyBuilder implements DependencyBuilder {

  private Module module = null;

  /**
   * Creates a builder for the module supplied.
   *
   * @param module
   */
  public ModuleDependencyBuilder(Module module) {
    this.module = module;
  }

  public String getDependencies() throws KarmaException {

    // 1. Get the correct module type. If type == maven, get project.xml, else get module.xml.
    //
    StringBuffer buf = new StringBuffer();

    if (this.module instanceof MavenModule) {

      List dependencies =
        ((MavenModule) this.module).getDependencies();

      for (Iterator i = dependencies.iterator(); i.hasNext();) {
        Dependency dep = (Dependency) i.next();
        String jarDir =
          MavenEnvironment.getMavenRepository() + File.separator +
          dep.getArtifactDirectory() + File.separator +
          "jars" + File.separator +
          dep.getArtifactId();
        if (dep.getVersion() != null) {
          jarDir += "-" + dep.getVersion();
        }
        jarDir += ".jar";

        buf.append(jarDir);
        if (i.hasNext()) {
          // Separator char for classpath parts
          //
          buf.append(":");
        }
      }

    } else if (this.module instanceof SourceModule) {

      // Read in deps from module.xml
      //
    }
    return buf.toString();
  }
}
