package nl.toolforge.karma.core;

import nl.toolforge.karma.core.scm.DependencyReader;
import nl.toolforge.karma.core.scm.maven.MavenDependencyReader;
import org.apache.maven.project.Dependency;

import java.io.File;
import java.util.Iterator;

public class MavenModule extends SourceModule {

  public MavenModule(MavenModuleDescriptor descriptor, File manifestDirectory) throws ManifestException {
    super(descriptor, manifestDirectory);
  }

  public String getDependencies() throws KarmaException {

    // 1. Get the correct module type. If type == maven, get project.xml, else get module.xml.
    //
    StringBuffer buf = new StringBuffer();

    DependencyReader reader = new MavenDependencyReader();
    dependencies = reader.parse(new File(getModuleDirectory(), "project.xml"));

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

    return buf.toString();
  }



}
