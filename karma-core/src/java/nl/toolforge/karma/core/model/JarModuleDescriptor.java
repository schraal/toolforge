package nl.toolforge.karma.core.model;

import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;

/**
 * Object representation of a <code>&lt;jarmodule&gt;</code>-element in a manifest file.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class JarModuleDescriptor extends ModuleDescriptor {

  public static final String ELEMENT_NAME = "jarmodule";

  public JarModuleDescriptor(String name, Location location, Version version) throws ManifestException {
    super(name, location, version);
  }
}


