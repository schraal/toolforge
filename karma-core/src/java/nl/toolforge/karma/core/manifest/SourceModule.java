/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.PatchLine;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>A <code>SourceModule</code> represents a module for which the developer wants to have the sources available to
 * on the local harddisk.
 *
 * @author D.A. Smedes
 * @version $Id$
 * @see Module
 */
public class SourceModule extends BaseModule {

//  private State state = null;
  private Version version = null;
  private boolean patchLine = false;
  private boolean developmentLine = false;
  private File baseDir = null;

  /**
   * Constructs a <code>SourceModule</code> with a <code>name</code> and <code>location</code>.
   *
   * @param name Mandatory parameter; name of the module.
   * @param location Mandatory parameter; location of the module.
   */
  public SourceModule(String name, Location location) {
    this(name, location, null);
  }

  /**
   * Constructs a <code>SourceModule</code> with a <code>name</code>, <code>location</code> and <code>version</code>.
   */
  public SourceModule(String name, Location location, Version version) {

    super(name, location);
    this.version = version;
  }

  /**
   * Future functionality. Not yet supported. Returns <code>false</code>.
   *
   * @return <code>false</code>.
   */
  public boolean hasDevelopmentLine() {
    return false;
  }

  public final void markDevelopmentLine(boolean mark) {
    developmentLine = mark;
  }

  public final DevelopmentLine getPatchLine() {
    return new PatchLine(getVersion());
  }

  public final void markPatchLine(boolean mark) {
    patchLine = true;
  }

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

  public boolean hasVersion() {
    return version != null;
  }

  /**
   * Checks if this module has been patched (and is thus part of a <code>ReleaseManifest</code>).
   *
   * @return <code>true</code> when this module has a <code>PatchLine</code> attached to it, <code>false</code> if it
   *         hasn't.
   */
  public boolean hasPatchLine() {
    return patchLine;
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

  public SourceType getSourceType() {
    return new Module.SourceType("src");
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
   * module. It doesn't do any validation (like, does the dep actually exist locally).
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
    digester.addFactoryCreate("*/dependency", "nl.toolforge.karma.core.scm.digester.ModuleDependencyCreationFactory");
    digester.addSetNext("*/dependency", "add");

    try {
      // Load 'dependencies.xml'
      //
      deps = (Set) digester.parse(new File(getBaseDir(), "dependencies.xml"));
    } catch (IOException e) {

      logger.info("No dependencies found for module : " + getName());

    } catch (SAXException e) {
      throw new ManifestException(e, ManifestException.DEPENDENCY_FILE_LOAD_ERROR, new Object[]{getName()});
    }

    return deps;
  }
}
