package nl.toolforge.karma.core;

import nl.toolforge.karma.core.scm.DependencyReader;
import nl.toolforge.karma.core.scm.SourceModuleDependencyReader;
import nl.toolforge.karma.core.scm.MavenDependencyReader;
import nl.toolforge.karma.core.scm.DependencyReader;
import nl.toolforge.karma.core.scm.MavenDependencyReader;

import java.io.File;
import java.util.List;

public class MavenModule extends SourceModule {

  public MavenModule(MavenModuleDescriptor descriptor, File manifestDirectory) throws ManifestException {
    super(descriptor, manifestDirectory);
  }

  /**
   * Overrides {@link SourceModule#getDependencies()}. Dependencies are determined based on the <code>project.xml</code>
   * file.
   *
   * @throws KarmaException
   */
  public List getDependencies() throws KarmaException {

    if (dependencies == null) {

      DependencyReader reader = new MavenDependencyReader();
      dependencies = reader.parse(new File(getModuleDirectory(), "project.xml"));
    }
    return dependencies;
  }
}
