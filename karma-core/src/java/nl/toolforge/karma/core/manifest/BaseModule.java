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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.regex.PatternSyntaxException;

/**
 * The name says it all. This class is the base (template) for a module.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class BaseModule implements Module {

  protected static Log logger = LogFactory.getLog(BaseModule.class);

  private Location location = null;
  private String name = null;
//  private Manifest manifest = null;

  private File baseDir = null;

  private Version version = null;
  private boolean patchLine = false;
  private boolean developmentLine = false;


  private Module.DeploymentType deploymentType = null;

  public BaseModule(String name, Location location, Version version) {
    this(name, location);
    this.version = version;
  }

  public BaseModule(String name, Location location) {

    if (!name.matches(ModuleDescriptor.NAME_PATTERN_STRING)) {
      throw new PatternSyntaxException(
          "Pattern mismatch for 'name'. Should match " + ModuleDescriptor.NAME_PATTERN_STRING, name, -1);
    }
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null.");
    }

    this.name = name;
    setDeploymentType(name);

    this.location = location;
  }

  /**
   * Gets the modules' name.
   *
   * @see Module#getName
   */
  public final String getName() {
    return name;
  }

  private void setDeploymentType(String moduleName) {
    deploymentType = new Module.DeploymentType(moduleName);
  }

  public final DeploymentType getDeploymentType() {
    return deploymentType;
  }

  /**
   * Gets the modules' location.
   *
   * @return See {@link nl.toolforge.karma.core.location.Location}, and all implementing classes.
   */
  public final Location getLocation() {
    return location;
  }

  public boolean equals(Object obj) {

    if (obj instanceof BaseModule) {
      if (((BaseModule) obj).getName().equals(getName()) &&
          ((BaseModule) obj).getLocation().equals(getLocation())) {
        return true;
      }
    }
    return false;
  }

  public int hashCode() {
    return getName().hashCode() + getLocation().hashCode();
  }


  /**
   * Future functionality. Not yet supported. Returns <code>false</code>.
   *
   * @return <code>false</code>.
   */
  public final boolean hasDevelopmentLine() {
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

  public final boolean hasVersion() {
    return version != null;
  }

  /**
   * Checks if this module has been patched (and is thus part of a <code>ReleaseManifest</code>).
   *
   * @return <code>true</code> when this module has a <code>PatchLine</code> attached to it, <code>false</code> if it
   *         hasn't.
   */
  public final boolean hasPatchLine() {
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



  public final File getBaseDir() {

    if (baseDir == null) {
      throw new KarmaRuntimeException("Basedir not set.");
    }
    return baseDir;
  }

}
