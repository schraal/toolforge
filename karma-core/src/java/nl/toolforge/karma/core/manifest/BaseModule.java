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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
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

  private File baseDir = null;
  private File checkoutDir = null;

  private Version version = null;
  private boolean patchLine = false;
  private boolean developmentLine = false;

  public BaseModule(String name, Location location, Version version) {
    this(name, location);
    this.version = version;
  }

  public BaseModule(String name, Location location) {

    if (!name.matches(ModuleDigester.NAME_PATTERN_STRING)) {
      throw new PatternSyntaxException(
          "Pattern mismatch for 'name'. Should match " + ModuleDigester.NAME_PATTERN_STRING, name, -1);
    }
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null.");
    }

    this.name = name;
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

  public final void setCheckoutDir(File checkoutDir) {

    if (checkoutDir == null) {
      throw new IllegalArgumentException("If you use it, initialize it with a valid 'File' instance ...");
    }
    this.checkoutDir = checkoutDir;
  }

  /**
   * Warning, as for {@link #getBaseDir}, this method will throw a KarmaRuntimeException when the checkoutdir has not
   * been set.
   *
   * @return The directory where a version control system should check out the module.
   */
  public final File getCheckoutDir() {
    if (checkoutDir == null) {
      throw new KarmaRuntimeException("CheckoutDir not set.");
    }
    return checkoutDir;
  }

  /**
   * Reads <code>module-descriptor</code> from the module base directory. If the base directory does not exist,
   * <code>Module.UNKNOWN</code> is returned.
   *
   * @return The module type.
   * @throws ModuleTypeException When <code>module-descriptor</code> is non-existing. This is possible when the
   *   module is not locally available.
   */
  public final Type getType() throws ModuleTypeException {

    try {
      getBaseDir();
    } catch (KarmaRuntimeException k) {
      return Module.UNKNOWN;
    }

    if (!new File(getBaseDir(), Module.MODULE_DESCRIPTOR).exists()) {
      throw new ModuleTypeException(ModuleTypeException.MISSING_MODULE_DESCRIPTOR);
    }

    Digester digester = new Digester();

    digester.addObjectCreate("module-descriptor", Module.Type.class);
    digester.addCallMethod("module-descriptor/type", "setType", 0);

    try {
      Type t = (Type) digester.parse(new File(getBaseDir(), Module.MODULE_DESCRIPTOR).getPath());
      return (Type) digester.parse(new File(getBaseDir(), Module.MODULE_DESCRIPTOR).getPath());
    } catch (IOException e) {
      throw new KarmaRuntimeException(e.getMessage());
    } catch (SAXException e) {
      e.printStackTrace();
      throw new KarmaRuntimeException(e.getMessage());
    }
  }

}
