package nl.toolforge.karma.core;

import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.ModuleDescriptor;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.DevelopmentLine;

/**
 * Object representation of a <code>&lt;sourcemodule&gt;</code>-element in a manifest file.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class SourceModuleDescriptor extends ModuleDescriptor {

	/** Element name for a source module in a manifest XML file */
	public static final String ELEMENT_NAME = "sourcemodule";

	/** The <code>version</code>-attribute for a module. */
	public static final String VERSION_ATTRIBUTE = "version";

	/** The <code>branch</code>-attribute for a module. */
	public static final String DEVELOPMENT_LINE_ATTRIBUTE = "development-line";

  private DevelopmentLine developmentLine = null;

  public SourceModuleDescriptor(String name, Location location) throws ManifestException {
    super(name, location);
  }

  public SourceModuleDescriptor(String name, Location location, Version version) throws ManifestException {
    super(name, location, version);
  }

  public SourceModuleDescriptor(String name, Location location, Version version, DevelopmentLine line) throws ManifestException {
    super(name, location, version);
    this.developmentLine = line;
  }

  public SourceModuleDescriptor(String name, Location location, DevelopmentLine line) throws ManifestException {
    super(name, location);
    this.developmentLine = line;
  }

  public final DevelopmentLine getDevelopmentLine() {
    return developmentLine;
  }
}


