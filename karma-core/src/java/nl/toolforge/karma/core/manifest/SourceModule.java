package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.DevelopmentLine;

import java.util.List;

/**
 * <p>A <code>SourceModule</code> represents a module for which the developer wants to have the sources available to
 * on the local harddisk.
 * <p/>
 * <p>TODO Validation checks on setVersion and setDevelopmentLine
 *
 * @author D.A. Smedes
 * @version $Id$
 * @see Module
 */
public class SourceModule extends BaseModule {

  /**
   * The name of the mandatory file in a source module. A file with this name is created by Karma or should be created
   * manually and contain all data (symbolic names) that should be available for existing manifests.
   */
  public static final String MODULE_INFO = "module.info";

  private Version version = null;
  private DevelopmentLine developmentLine = null;
  protected static List dependencies = null; // Lazy loading, the first time it is initialized and cached.

  /**
   * Constructs a SourceModule; this module variant has not <code>&lt;version&gt;</code>- or
   * <code>&lt;development-line&gt;</code>-attribute assigned.
   *
   * @param name Mandatory parameter; name of the module.
   * @param location Mandatory parameter; location of the module.
   */
  public SourceModule(String name, Location location) {
    super(name, location);
  }

  /**
   *
   */
  public SourceModule(String name, Location location, Version version) {
    this(name, location, version, null);
  }

  public SourceModule(String name, Location location, DevelopmentLine line) {
    this(name, location, null, line);
  }

  public SourceModule(String name, Location location, Version version, DevelopmentLine line) {
    
    this(name, location);

    this.version = version;
    this.developmentLine = line;
  }

  public final DevelopmentLine getDevelopmentLine() {
    return developmentLine;
  }

  /**
   * If the module element in the manifest contains a <code>version</code> attribute, this method will return the
   * value of that attribute.
   *
   * @return The module version, or N/A, when no version number exists.
   */
  public final Version getVersion() {
    return version;
  }

  /**
   * If the module element in the manifest contains a <code>version</code> attribute, this method will return the
   * value of that attribute.
   *
   * @return The module version, or <code>N/A</code>, when no version number exists.
   */
  public final String getVersionAsString() {
    return (version == null ? "N/A" : version.getVersionNumber());
  }

  /**
   * Checks if this module has a version number.
   *
   * @return <code>true</code> when this module has a version number, <code>false</code> if it hasn't.
   */
  public boolean hasVersion() {
    return version != null;
  }

  /**
   * Checks if this module is developed on a development line, other than the
   * {@link nl.toolforge.karma.core.vc.model.MainLine}.
   *
   * @return <code>true</code> when this module is developed on a development line, <code>false</code> if it isn't.
   */
  public boolean hasDevelopmentLine() {
    return developmentLine != null;
  }

  public String getDependencyName() {

    if (getVersion() != null) {
      return getName() + "_" + getVersionAsString() + ".jar";
    } else
      return getName() + "_" + WORKING;
  }

  public String getDependencies() {
    throw new RuntimeException("Not implemented yet.");
  }
}
