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
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.scm.ModuleDependency;
import nl.toolforge.karma.core.vc.VersionControlSystem;
import nl.toolforge.karma.core.vc.cvsimpl.AdminHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p>General stuff for a manifest.</p>
 *
 * <p>Check the <a href="package-summary.html">package documentation</a> for more information on the concepts behind
 * Karma.</p>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class AbstractManifest implements Manifest {

  private Collection childManifests = new ArrayList();

  private String name = null;
  private String version = null;
  private String description = null;

  private Map modules = null;

  private WorkingContext workingContext = null;
  private ManifestStructure manifestStructure = null;

  private File manifestBaseDirectory = null;
  private File manifestTempDirectory = null;
  private Map moduleCache = null;

  /**
   * Constructs a manifest instance; <code>name</code> is mandatory.
   */
  public AbstractManifest(WorkingContext workingContext, String name) throws ManifestException, LocationException {

    if ("".equals(name) || name == null) {
      throw new IllegalArgumentException("Manifest name cannot be empty or null.");
    }
    this.name = name;

    ManifestLoader loader = new ManifestLoader(workingContext);
    this.manifestStructure = loader.load(name);

    init();
  }

  /**
   * A manifest is created based on its <code>ManifestStructure</code>, which can be loaded by the
   * <code>ManifestLoader</code>. The <code>ManifestStructure</code> is the basis for the Manifest; a number of checks
   * are applied to it, including a linking of the manifest to the {@link WorkingContext}.
   *
   * @param workingContext The current working context.
   * @param structure The ManifestStructure, which is the basis for the manifest.
   */
  public AbstractManifest(WorkingContext workingContext, ManifestStructure structure) throws LocationException {

    this.workingContext = workingContext;
    this.manifestStructure = structure;

    this.name = structure.getName();

    init();
  }

  private void init() throws LocationException {

    // Recursively load all modules from the root manifest and all includes to have them available quickly as one
    // list.
    //
    copyStructure();

    // Apply the current working context to this manifest.
    //
    applyWorkingContext();
  }

  //
  //
  //
  private void copyStructure() throws LocationException {

    // Step 1
    //
    modules = new Hashtable();

    ModuleFactory moduleFactory = new ModuleFactory(workingContext);

    for (Iterator i = manifestStructure.getModules().iterator(); i.hasNext();) {
      Module module = moduleFactory.create((ModuleDigester) i.next(), Module.UNKNOWN);
      modules.put(module.getName(), module);
    }

    // Step 2
    //
    moduleCache = new HashMap();

    // If there is nothing in the cache, there is a chance that we have not yet

    ManifestFactory factory = new ManifestFactory();

    for (Iterator i = manifestStructure.getChilds().values().iterator(); i.hasNext();) {

      ManifestStructure childStructure = (ManifestStructure) i.next();
      Manifest manifest = factory.create(workingContext, childStructure);

      childManifests.add(manifest);
      moduleCache.putAll(manifest.getAllModules());
    }
    moduleCache.putAll(getModulesForManifest());
  }

  //
  //
  //
  private void applyWorkingContext() {

    manifestBaseDirectory = new File(workingContext.getProjectBaseDirectory(), getName());
    manifestTempDirectory = new File(getBaseDirectory(), "tmp");

    for (Iterator i = moduleCache.values().iterator(); i.hasNext();) {

      Module module = (Module) i.next();
      setModuleBaseDir(module);
      applyWorkingContext(workingContext, module);
      removeLocal(module);
    }
  }

  /**
   * A specific <code>Manifest</code> implementation may have to apply specific actions to modules per working context.
   * Each implementation should therefor implement this method and do what it has to do.
   *
   * @param context The current {@link WorkingContext}.
   * @param module The module to which <code>context</code> should be applied.
   */
  protected abstract void applyWorkingContext(WorkingContext context, Module module);

  public final File getBaseDirectory() {

    if (!manifestBaseDirectory.exists()) {
      manifestBaseDirectory.mkdir();
    }
    return manifestBaseDirectory;
  }

  public File getBuildBaseDirectory() {
    
    File f = new File(getBaseDirectory(), "build");

    if (!f.exists()) {
      f.mkdir();
    }
    return f;
  }

  public File getReportsBaseDirectory() {
    
    File f = new File(getBaseDirectory(), "reports");

    if (!f.exists()) {
      f.mkdir();
    }
    return f;
  }

  public File getModuleBaseDirectory() {

    File f = new File(getBaseDirectory(), "modules");

    if (!f.exists()) {
      f.mkdir();
    }
    return f;
  }

  public final File getTempDirectory() {

    if (!manifestTempDirectory.exists()) {
      manifestTempDirectory.mkdir();
    }
    return manifestTempDirectory;
  }

  /**
   * Gets a manifests' name (the &lt;name&gt;-attribute) from the manifest XML file.
   *
   * @return The manifests' name.
   */
  public final String getName() {
    return name;
  }

  public abstract String getType();

  /**
   * Gets a manifests' version (the &lt;version&gt;-attribute) from the manifest XML file.
   *
   * @return The manifests' version.
   */
  public final String getVersion() {
    return version;
  }

  /**
   * Sets the manifests' version. This method is called by
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a> while parsing the manifest XML file.
   *
   * @param version The manifests' version (<code>&lt;version&gt;</code>-attribute); may be <code>null</code>.
   */
  public final void setVersion(String version) {
    this.version = version;
  }

  public final String getDescription() {
    return description;
  }

  public final void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets all modules defined in this manifest (excluding includedManifests).
   *
   * @see #getAllModules()
   *
   * @return A <code>Map</code> with {@link Module} instances.
   */
  public final Map getModulesForManifest() {
    return modules;
  }

  /**
   * Gets all modules defined in this manifest including all modules for all child manifests.
   *
   * @see #getModulesForManifest()
   *
   * @return A <code>Map</code> with {@link Module} instances.
   */
  public final Map getAllModules() {
    return moduleCache;
  }

  /**
   * Counts all modules for a manifest, also counting all modules of all included manifests. This method thus counts
   * all child manifests as well.
   *
   * @return The total number of modules in this manifest (inlcuding all included manifests).
   */
  public final int size() {

    int total = getModulesForManifest().size();

    for (Iterator i = childManifests.iterator(); i.hasNext();) {
      total += ((AbstractManifest) i.next()).size();
    }

    return total;
  }


  /**
   *
   * @param moduleName
   * @return
   * @throws ManifestException
   */
  public final Module getModule(String moduleName) throws ManifestException {

    Map allModules = getAllModules();

    if (allModules.containsKey(moduleName)) {
      return (Module) allModules.get(moduleName);
    } else {
      throw new ManifestException(ManifestException.MODULE_NOT_FOUND, new Object[]{moduleName});
    }
  }

  public final boolean isLocal() {

    for (Iterator i = getAllModules().values().iterator(); i.hasNext();) {

      Module m = (Module) i.next();

      // If we stumble upon a non local module, return false
      if (!isLocal(m)) {
        return false;
      }
    }

    return true;
  }

  public final boolean isLocal(Module module) {
    return module.getBaseDir().exists();
  }

  /**
   * Retrieves all included manifests.
   *
   * @return A <code>Collection</code> of <code>AbstractManifest</code> instances, or an empty collection if no included
   *   manifests are available.
   */
  public final Collection getIncludes() {
    return childManifests;
  }

  /**
   * Saves the manifest to disk, including all its included manifests.
   */
  public void save() throws ManifestException {
    // todo this requires a manifest to maintain a map of which module belongs to which manifest ...
  }

  /**
   * A manifest is equal to another manifest if their names are equal.
   *
   * @param o A <code>AbstractManifest</code> instance.
   */
  public final boolean equals(Object o) {

    if (o instanceof Manifest) {
      if (getName().equals(((Manifest) o).getName())) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public int hashCode() {
    return name.hashCode();
  }

  /**
   *
   *
   * @param module
   * @return Interdependencies for <code>module</code> or an empty <code>Collection</code>.
   */
  public final Collection getModuleInterdependencies(Module module) throws ManifestException {

    Collection deps = (Collection) getInterdependencies().get(module.getName());

    return (deps == null ? new HashSet() : deps);
  }

  /**
   * <p>Calculates interdepencies between modules in the manifest; interdependencies are inverse relationships
   * between a module and other modules (being <code>SourceModule</code> instances).
   *
   * <p>If a module <code>B</code> has a dependency on module <code>A</code>, then this method will return a map, with
   * a key <code>A</code> and its value a <code>Collection</code> of interdependencies (in this case, <code>B</code>).
   *
   * @return
   */
  public final Map getInterdependencies() throws ManifestException {

    Map interDependencies = new Hashtable();

    // Interdependencies can only be determined if the module has been checked out locally ...
    //

    Map allModules = getAllModules();

    for (Iterator i = allModules.keySet().iterator(); i.hasNext();) {

      Module module = (Module) allModules.get((String) i.next());

      if (isLocal(module)) {

        // Does the module have 'module'-deps ?
        //

        Set moduleDependencies = null;
        moduleDependencies = ((BaseModule) module).getDependencies();

        // Iterate over all dependencies. If it is a module dep, check if we already have an
        // entry in the interdep-collection; create one when necessary.
        //
        for (Iterator j = moduleDependencies.iterator(); j.hasNext();) {
          ModuleDependency moduleDependency = (ModuleDependency) j.next();
          if (moduleDependency.isModuleDependency()) {

            // Check if a key for the module dep already exists.
            //
            if (interDependencies.containsKey(moduleDependency.getModule())) {

              // If so, get the corresponding collection and add the module to the collection.
              //
              Collection col = (Collection) interDependencies.get(moduleDependency.getModule());
              col.add(module);
            } else {
              // For the dependency, no entry exists, so we create one.
              //
              Collection col = new HashSet();
              col.add(module);
              // todo TEST (!) if the mechanism works for 'duplicate' keys.
              interDependencies.put(moduleDependency.getModule(), col);
            }
          }
        }
      } else {
        // todo else what ???
      }
    }

    return interDependencies;
  }

  /**
   * <p>Checks is <code>module</code> should be removed locally. This can - e.g. - happen if the module was checked out
   * from a location elsewhere and the module with the same name but with a different location has been defined in the
   * manifest, before the module was cleaned locally.
   *
   * <p>This method only supports CVS.
   *
   * @param module
   * @return <code>true</code> if a local version has been removed or <code>false</code> if nothing has been removed.
   */
  private boolean removeLocal(Module module) {

    // todo this method is not abstract ! handles CVS only.

    AdminHandler handler = new AdminHandler(module);
    if (!handler.isEqualLocation()) {
      try {
        FileUtils.deleteDirectory(module.getBaseDir());
      } catch (IOException e) {
        return false;
      }
    }

    return true;
  }

  private void setModuleBaseDir(Module module) {

    try {
      module.setBaseDir(new File(getModuleBaseDirectory(), module.getName()));

//      if (((VersionControlSystem)module.getLocation()).getModuleOffset() == null) {
//        module.setBaseDir(new File(getModuleBaseDirectory(), module.getName()));
//      } else {
//        module.setBaseDir(new File(new File(getModuleBaseDirectory(), ((VersionControlSystem)module.getLocation()).getModuleOffset()), module.getName()));
//      }
//      module.setCheckoutDir(getModuleBaseDirectory());
    } catch(Exception e) {
      // Basically, if we can't do this, we have nothing ... really a RuntimeException
      //
      throw new KarmaRuntimeException("Could not set base directory for module " + module.getName());
    }
  }

  /**
   * Sets a modules' state when the module is locally available.
   *
   * @param module
   * @param state
   */
  public final void setState(Module module, Module.State state) {
    if (state == null) {
      throw new IllegalArgumentException("Parameter state cannot be null.");
    }

    if (!isLocal(module)) {
      return;
    }

    try {

      // Remove old state files ...
      //
      FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          if ((name != null) && ((".WORKING".equals(name)) || (".STATIC".equals(name)) || (".DYNAMIC".equals(name)))) {
            return true;
          } else {
            return false;
          }
        }
      };

      String[] stateFiles = module.getBaseDir().list(filter);

      if (stateFiles != null) {
        for (int i = 0; i < stateFiles.length; i++) {
          new File(module.getBaseDir(), stateFiles[i]).delete();
        }
      }

      File stateFile = new File(module.getBaseDir(), state.getHiddenFileName());
      stateFile.createNewFile();

    } catch (Exception e) {
      throw new KarmaRuntimeException(e);
    }
  }

  public final Module.State getState(Module module) {

    if (!isLocal(module)) {
      if (module.hasVersion() || this instanceof ReleaseManifest) {
        return Module.STATIC;
      } else {
        return Module.DYNAMIC;
      }
    }

    FilenameFilter filter = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        if ((name != null) && name.matches(".WORKING|.STATIC|.DYNAMIC")) {
          return true;
        } else {
          return false;
        }
      }
    };

    String[] stateFiles = module.getBaseDir().list(filter);

    if ((stateFiles == null || stateFiles.length == 0)) {
      if (module.hasVersion()) {
        return Module.STATIC;
      } else {
        return Module.DYNAMIC;
      }
    } else {

      // We have state files, meaning that the module was local.
      //
      if (".WORKING".equals(stateFiles[0])) {
        return Module.WORKING;
      } else {

        // The module is not working.
        //
        if (this instanceof ReleaseManifest) {
          return Module.STATIC;
        } else {
          if (module.hasVersion()) {
            return Module.STATIC;
          }
        }
        return Module.DYNAMIC;
      }
    }
  }

  public final String toString() {
    return getName();
  }
}
