package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.DevelopmentLine;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.maven.project.Dependency;
import org.xml.sax.SAXException;

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

  private State state = null;
  private Version version = null;
  private DevelopmentLine developmentLine = null;
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

  /**
   * When initialized by <code>Manifest</code>, a module is assigned its base directory, relative to the manifest. The
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

  private File getBaseDir() {
    return baseDir;
  }

  public String getDependencies() throws ManifestException {

    // For now, we assume each module has a project.xml, modelled as per the Maven definition.
    //
    Set deps = new HashSet();

//    URL rules = this.getClass().getClassLoader().getResource("maven-project-rules.xml");
//    Digester digester = DigesterLoader.createDigester(rules);

    // Read in the base dependency structure of a Maven project.xml file
    //
    Digester digester = new Digester();

    digester.addObjectCreate("*/dependencies", "java.util.HashSet");

    digester.addObjectCreate("*/dependency", "org.apache.maven.project.Dependency");

    digester.addCallMethod("*/dependency/groupId", "setGroupId", 1);
    digester.addCallParam("*/dependency/groupId", 0);

    digester.addCallMethod("*/dependency/id", "setId", 1);
    digester.addCallParam("*/dependency/id", 0);

    digester.addCallMethod("*/dependency/version", "setVersion", 1);
    digester.addCallParam("*/dependency/version", 0);

    digester.addCallMethod("*/dependency/artifactId", "setArtifactId", 1);
    digester.addCallParam("*/dependency/artifactId", 0);

    digester.addSetNext("*/dependency", "add", "org.apache.maven.project.Dependency");

    try {
      // Load 'project.xml'
      //
      deps = (Set) digester.parse(new File(getBaseDir(), "project.xml"));
    } catch (IOException e) {
      if (e instanceof FileNotFoundException) {
        throw new ManifestException(e, ManifestException.DEPENDENCY_FILE_NOT_FOUND, new Object[]{getName()});
      }
      throw new ManifestException(e, ManifestException.DEPENDENCY_FILE_NOT_FOUND, new Object[]{getName()});
    } catch (SAXException e) {
      if (e.getException() instanceof ManifestException) {
        // It was already a ManifestException
        //
//        throw new ManifestException(((ManifestException) e.getException()).getErrorCode());
        throw (ManifestException) e.getException();
      }
      throw new ManifestException(e, ManifestException.DEPENDENCY_FILE_NOT_FOUND, new Object[]{getName()});
    }

    StringBuffer buffer = new StringBuffer();
    String userHome = System.getProperty("user.home");

    for (Iterator iterator = deps.iterator(); iterator.hasNext();) {
      Dependency dep = (Dependency) iterator.next();

      String jar = userHome + File.separator + ".maven" + File.separator + "repository" + File.separator;

      jar += dep.getArtifactId() + "-" + dep.getVersion() + ".jar";

      buffer.append(jar);
      if (iterator.hasNext()) {
        buffer.append(";");
      }
    }

    return buffer.toString();
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
