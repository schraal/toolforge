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

import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.scm.ModuleDependency;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.threads.ParallelRunner;
import nl.toolforge.karma.core.vc.cvs.AdminHandler;
import nl.toolforge.karma.core.vc.cvs.Utils;
import nl.toolforge.karma.core.vc.cvs.threads.CVSLogThread;
import nl.toolforge.karma.core.vc.cvs.threads.PatchLineThread;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;
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

  protected ModuleFactory moduleFactory = ModuleFactory.getInstance();

  private static Collection duplicates = null; // To detect duplicate included manifests.

  private Collection childManifests = new ArrayList();

  private Map moduleCache = new HashMap();

  private String name = null;
  private String version = null;
  private String description = null;

  private Map modules = null;

  /**
   * Constructs a manifest instance; <code>name</code> is mandatory.
   */
  public AbstractManifest(String name) {

    if ("".equals(name) || name == null) {
      throw new IllegalArgumentException("Manifest name cannot be empty or null.");
    }
    this.name = name;

    modules = new Hashtable();
  }

  /**
   * Gets a manifests' name (the &lt;name&gt;-attribute) from the manifest XML file.
   *
   * @return The manifests' name.
   */
  public final String getName() {
    return name;
  }

  public final void setType(String type) {
    // Just here for compatibility.
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
   * @param version The manifests' version (&lt;version&gt;-attribute); may be <code>null</code>.
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
   * Loads the manifest from disk. It uses a
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a> to parse the manifest XML.
   *
   * @throws ManifestException When loading the manifest failed.
   */
  public synchronized void load() throws ManifestException {

    moduleCache.clear();
    modules.clear();

    (duplicates = new ArrayList()).add(getName()); // Add the root manifest.

    ManifestFactory factory = ManifestFactory.getInstance();
    Manifest manifest = factory.parse(getName());

    if (manifest instanceof ReleaseManifest) {
      checkForPatchLines(manifest);
    }

    copyToThis((AbstractManifest) manifest);

    // todo As a final step, check all modules in the manifest and remove modules that are not in the manifest anymore
    // but still on disk.

    // todo should move all modules to a 'modules' sub-directory, for better scanning purposes.
  }

  /**
   * Checks (in parallel) if modules have a PatchLine associated.
   *
   * @param manifest
   */
  private void checkForPatchLines(Manifest manifest) {

    ParallelRunner runner = new ParallelRunner(manifest, PatchLineThread.class);
    runner.execute();

    // todo timing issue ... COULD last forever.
    //
    while (!runner.finished()) {

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Makes a 'deep' copy of <code>manifest</code> into <code>this</code>. <code>manifest</code> was generated by
   * Digester, but they have to be copied to <code>this</code>, because that's the <code>AbstractManifest</code> instance that
   * is returned by {@link #load()}.
   */
  private void copyToThis(AbstractManifest manifest) {

    // Copy all loaded data into this instance
    //
    setDescription(manifest.getDescription());
    setVersion(manifest.getVersion());
    copyIncludes(manifest.getIncludes());
    copyModules(manifest.getModulesForManifest());

  }

  private void copyIncludes(Collection includedManifests) {
    for (Iterator i = includedManifests.iterator(); i.hasNext();) {
      childManifests.add((Manifest) i.next());
    }
  }

  private void copyModules(Map newModules) {
    getModulesForManifest().putAll(newModules);
  }

  /**
   * Includes another manifest in the manifest and links them as 'parent-child'. This method is called by
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a>
   *
   * @param child
   * @throws ManifestException
   */
  public final void includeManifest(ManifestDescriptor child) throws ManifestException {

    if (duplicates.contains(child.getName())) {
      throw new ManifestException(ManifestException.MANIFEST_NAME_RECURSION, new Object[]{child.getName()});
    }
    duplicates.add(child.getName());

    ManifestFactory factory = ManifestFactory.getInstance();
    Manifest manifest = factory.parse(child.getName());

    // If we correctly parsed the manifest, we can link it to the current manifest.
    //
    childManifests.add(manifest);
  }

  /**
   * Adds a module to this manifest. This method is called by
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a> during the {@link #load()}-process.
   *
   * @param descriptor The object representing a &lt;module&gt;-elemeent from a manifest XML file.
   * @throws LocationException When an invalid location was passed with <code>descriptor</code>. This occurs when no
   *   location-id has been identified in the <code>locations.xml</code>-file in the manifest-store.
   * @throws ManifestException
   */
  public abstract void addModule(ModuleDescriptor descriptor) throws LocationException, ManifestException;

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

    if (moduleCache == null || moduleCache.size() == 0) {

      moduleCache = new HashMap();

      // If there is nothing in the cache, there is a chance that we have not yet

      for (Iterator i = childManifests.iterator(); i.hasNext();) {
        moduleCache.putAll(((Manifest) i.next()).getAllModules());
      }
      moduleCache.putAll(getModulesForManifest());
    }
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
    return new File(getDirectory(), module.getName()).exists();
  }

  public final File getDirectory() {
    return new File(LocalEnvironment.getDevelopmentHome(), getName());
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

//  public abstract Module.State getLocalState(Module module);

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
   * <p>Determines the correct artifact name for <code>module</code>. The artifact-name is determined as follows:
   *
   * <ul>
   *   <li/>If the state of the module is <code>WORKING</code>, the artifact-name is
   *        <code>&lt;module-name&gt;_WORKING.jar</code>.
   *   <li/>If the state of the module is <code>DYNAMIC</code>, the artifact-name is
   *        <code>&lt;module-name&gt;_&lt;latest-versions&gt;.jar</code>.
   *   <li/>If the state of the module is <code>STATIC</code>, the artifact-name is
   *        <code>&lt;module-name&gt;_&lt;version&gt;.jar</code>.
   * </ul>
   *
   * <p>The extension if <code>.war</code> if the module is a <code>webapp</code>-module.
   *
   * @param module A <code>SourceModule</code> instance.
   * @return The artifact-name as determined the way as described above.
   * @throws ManifestException
   */
  public final String resolveArchiveName(Module module) throws ManifestException {

    String jar = module.getName() + "_";

    // todo introduce a method to determine if a module is webapp-module; maybe its own class.
    //
    String extension;
    if (module.getDeploymentType().equals(Module.WEBAPP)) {
      extension = ".war";
    } else if (module.getDeploymentType().equals(Module.WEBAPP)) {
      extension = ".ear";
    } else {
      extension = ".jar";
    }

    try {
      if (getState(module).equals(Module.WORKING)) {
        jar += Module.WORKING.toString();
      } else if (getState(module).equals(Module.DYNAMIC)) {
        jar += (Utils.getLocalVersion(module));
      } else { // STATIC module
        jar += ((SourceModule) module).getVersionAsString();
      }
      jar += extension;
    } catch (VersionControlException v) {
      throw new ManifestException(v.getErrorCode(), v.getMessageArguments());
    }

    return jar;
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
        moduleDependencies = ((SourceModule) module).getDependencies();

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
  protected boolean removeLocal(Module module) {

    // todo this method is not abstract ! handles CVS only.

    AdminHandler handler = new AdminHandler();
    if (!handler.isEqualLocation(module)) {
      try {
        FileUtils.deleteDirectory(module.getBaseDir());
      } catch (IOException e) {
        return false;
      }
    }

    return true;
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
      throw new KarmaRuntimeException(e.getMessage());
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
      return Module.STATIC;
    }

    if (stateFiles.length > 0 && ".WORKING".equals(stateFiles[0])) {
      return Module.WORKING;
    }

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
