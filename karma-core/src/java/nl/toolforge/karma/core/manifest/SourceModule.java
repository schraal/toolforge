package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.scm.ModuleDependency;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.PatchLine;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  private State state = null;
  private Version version = null;
  private boolean patchLine = false;
  private File baseDir = null;
  protected static List dependencies = null; // Lazy loading, the first time it is initialized and cached.

  /**
   * Constructs a SourceModule; this module variant has not <code>&lt;version&gt;</code>- or
   * <code>&lt;development-line&gt;</code>-attribute assigned.
   *
   * @param name Mandatory parameter; name of the module.
   * @param location Mandatory parameter; location of the module.
   */
  public SourceModule(String name, Location location) {
    this(name, location, null);
  }

  /**
   *
   */
  public SourceModule(String name, Location location, Version version) {

    super(name, location);
    this.version = version;
  }

  /**
   * @return <code>null</code> if a PatchLine does not exist for this module, otherwise the <code>PatchLine</code>
   *   instance for this module.
   */
  public final PatchLine getPatchLine() {
    if (patchLine) {
      return new PatchLine(PatchLine.NAME_PREFIX + "_" + getVersionAsString());
    }
    return null;
  }

  public final void setPatchLine() {
    patchLine = true;
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
   * Checks if this module has been patched (and is thus part of a <code>ReleaseManifest</code>).
   *
   * @return <code>true</code> when this module has a patch-line attached to it, <code>false</code> if it isn't.
   */
  public boolean hasPatchLine() {
    if (hasVersion()) {
      // todo check in cvs (or rather, has to be done upon loading the manifest).
      return patchLine;
    }
    return false;
  }

  /**
   * When initialized by <code>AbstractManifest</code>, a module is assigned its base directory, relative to the manifest. The
   * base directory is used internally for base-directory-aware methods.
   *
   * @param baseDir
   */
  public final void setBaseDir(File baseDir) {

    if (baseDir == null) {
      throw new IllegalArgumentException("If you use it, initialize it with a valid 'File' instance ...");
    }
    this.baseDir = baseDir;
  }

  public File getBaseDir() {

    if (baseDir == null) {
      throw new KarmaRuntimeException("Basedir not set.");
    }
    return baseDir;
  }

  /**
   * Converts a modules dependencies XML-tree to a <code>Set</code> of
   * {@link nl.toolforge.karma.core.scm.ModuleDependency} instances. This method merely transforms the deps for a
   * module. It doesn't do any validation (like, does the dep actually exists locally).
   *
   * @return A <code>Set</code> containing {@link nl.toolforge.karma.core.scm.ModuleDependency} instances.
   */
  public Set getDependencies() throws ManifestException {

    // For now, we assume each module has a project.xml, modelled as per the Maven definition.
    //
    Set deps = new HashSet();

    // Read in the base dependency structure of a Maven project.xml file
    //
    Digester digester = new Digester();

    digester.addObjectCreate("*/dependencies", "java.util.HashSet");
    digester.addObjectCreate("*/dependency", "nl.toolforge.karma.core.scm.ModuleDependency");
    digester.addSetProperties("*/dependency"); // new String[]{"id", "groupId", "artifactId", "version", "module", "jar"};
    digester.addSetNext("*/dependency", "add");

    try {
      // Load 'dependencies.xml'
      //
      deps = (Set) digester.parse(new File(getBaseDir(), "dependencies.xml"));
    } catch (IOException e) {
//      if (e instanceof FileNotFoundException) {
//        throw new ManifestException(e, ManifestException.DEPENDENCY_FILE_NOT_FOUND, new Object[]{getName()});
//      }
//      throw new ManifestException(e, ManifestException.DEPENDENCY_FILE_NOT_FOUND, new Object[]{getName()});

      logger.info("No dependencies found for module : " + getName());

    } catch (SAXException e) {
      throw new ManifestException(e, ManifestException.DEPENDENCY_FILE_LOAD_ERROR, new Object[]{getName()});
    }

    return deps;
  }

  public void setState(State state) {

    if (state == null) {
      throw new IllegalArgumentException("Parameter state cannot be null.");
    }
    this.state = state;
  }

  /**
   * Gets the modules' state. State is identified by the manifest that loaded the module.
   *
   * @return The state of the module. See {@link Module}.
   */
  public final State getState() {
    return state;
  }

  public final String getStateAsString() {
    return (state == null ? "N/A" : state.toString());
  }
}
