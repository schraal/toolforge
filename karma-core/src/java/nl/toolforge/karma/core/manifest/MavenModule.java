package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.DevelopmentLine;

/**
 * <p>This type of module represents a module from a Maven project.
 *
 * <p>Maven versions supported: <code>maven-1.0-rc2</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class MavenModule extends SourceModule {

  public MavenModule(String name, Location location) {
    super(name, location);
  }

  public MavenModule(String name, Location location, Version version) {
    super(name, location, version);
  }

  public MavenModule(String name, Location location, DevelopmentLine line) {
    super(name, location, line);
  }

  public MavenModule(String name, Location location, Version version, DevelopmentLine line) {
    super(name, location, version, line);
  }

//  public String getDependencies() {
//
//    // 1. Get the correct module type. If type == maven, get project.xml, else get module.xml.
//    //
//    StringBuffer buf = new StringBuffer();
//
//    DependencyReader reader = new MavenDependencyReader();
//    dependencies = reader.parse(new File(getModuleDirectory(), "project.xml"));
//
//    for (Iterator i = dependencies.iterator(); i.hasNext();) {
//      Dependency dep = (Dependency) i.next();
//      String jarDir =
//        MavenEnvironment.getMavenRepository() + File.separator +
//        dep.getArtifactDirectory() + File.separator +
//        "jars" + File.separator +
//        dep.getArtifactId();
//      if (dep.getVersion() != null) {
//        jarDir += "-" + dep.getVersion();
//      }
//      jarDir += ".jar";
//
//      buf.append(jarDir);
//      if (i.hasNext()) {
//        // Separator char for classpath parts
//        //
//        buf.append(":");
//      }
//    }
//
//    return buf.toString();
//  }



}
