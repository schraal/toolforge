package nl.toolforge.karma.core;

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.DevelopmentLine;

/**
 * Object representation of a <code>&lt;mavenmodule&gt;</code>-element in a manifest file.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class MavenModuleDescriptor extends SourceModuleDescriptor {

	/** Element name for a maven module in a manifest XML file */
	public static String ELEMENT_NAME = "mavenmodule";

  public MavenModuleDescriptor(String name, Location location) throws ManifestException {
    super(name, location);
  }

  public MavenModuleDescriptor(String name, Location location, Version version) throws ManifestException {
    super(name, location, version);
  }

  public MavenModuleDescriptor(String name, Location location, Version version, DevelopmentLine line) throws ManifestException {
    super(name, location, version, line);
  }

  public MavenModuleDescriptor(String name, Location location, DevelopmentLine line) throws ManifestException {
    super(name, location, line);
  }
}


