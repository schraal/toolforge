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

}
