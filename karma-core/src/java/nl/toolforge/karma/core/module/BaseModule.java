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
package nl.toolforge.karma.core.module;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import net.sf.sillyexceptions.OutOfTheBlueException;
import org.apache.commons.digester.Digester;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.history.ModuleHistory;
import nl.toolforge.karma.core.history.ModuleHistoryEvent;
import nl.toolforge.karma.core.history.ModuleHistoryException;
import nl.toolforge.karma.core.history.ModuleHistoryFactory;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.module.template.ModuleLayoutTemplate;
import nl.toolforge.karma.core.scm.digester.ModuleDependencyCreationFactory;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.Authenticators;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.PatchLine;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvsimpl.CVSRunner;

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

  public abstract ModuleLayoutTemplate getLayoutTemplate();

  /**
   *
   * @param createComment
   * @throws VersionControlException
   * @throws AuthenticationException
   */
  public final void createRemote(Authenticator authenticator, String createComment) throws AuthenticationException, VersionControlException {

    // Create the layout and return its location.
    //
    File tmpDir = null;
    try {
      tmpDir = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      throw new KarmaRuntimeException("Could not create temporary directory.");
    }

    File moduleDir = new File(tmpDir, getName());
    moduleDir.mkdir();

    // Create the modules' layout
    //
    try {
      getLayoutTemplate().createLayout(moduleDir);
    } catch (IOException e) {
      // todo
      e.printStackTrace();
    }

    logger.debug("Created layout for module `" + getName() + "`");

    // Add the module to the version control system
    //
    setBaseDir(moduleDir);

    ModuleDescriptor descriptor = new ModuleDescriptor(this);
    try {
      descriptor.createFile(moduleDir);
    } catch (IOException e) {
      // todo
      e.printStackTrace();
    }

    // Prepare the module history
    //
    ModuleHistory history = null;
    try {
      history = ModuleHistoryFactory.getInstance(getBaseDir()).getModuleHistory(this);
    } catch (ModuleHistoryException e) {
      throw new OutOfTheBlueException("Module history does not yet exist, so this is impossible.");
    }

    ModuleHistoryEvent event = new ModuleHistoryEvent();
    event.setType(ModuleHistoryEvent.CREATE_MODULE_EVENT);
    event.setVersion(Version.INITIAL_VERSION);
    event.setDatetime(new Date());

    // Is a requirement.
    //
    Authenticator a = Authenticators.getAuthenticator(authenticator);

    event.setAuthor(a.getUsername());
    event.setComment(createComment);
    history.addEvent(event);
    try {
      history.save();
    } catch (ModuleHistoryException mhe) {
      logger.error("Troubles when saving the module history.", mhe);
    }

    CVSRunner runner = (CVSRunner) RunnerFactory.getRunner(getLocation());
    runner.addModule(this, createComment);

    try {
      FileUtils.deleteDirectory(tmpDir);
    } catch (IOException e) {
      logger.warn("Could not remove temporary directory. It is probably still locked by the OS.");
    }
  }

  /**
   * Reads <code>module-descriptor.xml</code>-file from the module base directory. If the base directory does not exist,
   * <code>Module.UNKNOWN</code> is returned.
   *
   * @return The module type.
   * @throws nl.toolforge.karma.core.module.ModuleTypeException When <code>module-descriptor</code> is non-existing. This is possible when the
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
      return (Type) digester.parse(new File(getBaseDir(), Module.MODULE_DESCRIPTOR).getPath());
    } catch (IOException e) {
      throw new ModuleTypeException(ModuleTypeException.INVALID_MODULE_DESCRIPTOR);
    } catch (SAXException e) {
      throw new ModuleTypeException(ModuleTypeException.INVALID_MODULE_DESCRIPTOR);
    }
  }

  /**
   * See {@link Module#getDependencies}. This implementation throws a <code>KarmaRuntimeException</code> when the
   *  modules' <code>dependencies.xml</code> could not be parsed properly. When no dependencies have been specified, or
   * when the file does not exist, the method returns an empty <code>Set</code>.
   *
   * @return A <code>Set</code> containing {@link nl.toolforge.karma.core.scm.ModuleDependency} instances.
   */
  public final Set getDependencies() {

    Set dependencies = new HashSet();

    // Read in the base dependency structure of a Maven project.xml file
    //
    Digester digester = new Digester();

    digester.addObjectCreate("*/dependencies", HashSet.class);
    digester.addFactoryCreate("*/dependency", ModuleDependencyCreationFactory.class);
    digester.addSetNext("*/dependency", "add");

    try {

      dependencies = (Set) digester.parse(new File(getBaseDir(), "dependencies.xml"));

    } catch (IOException e) {
      return new HashSet();
    } catch (SAXException e) {
      throw new KarmaRuntimeException(ManifestException.DEPENDENCY_FILE_LOAD_ERROR, new Object[]{getName()});
    }
    return dependencies;
  }

  /**
   * Returns the module name.
   *
   * @return
   */
  public String toString() {
    return getName();
  }

}
