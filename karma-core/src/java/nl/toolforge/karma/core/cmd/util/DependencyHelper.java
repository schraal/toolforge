package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.core.util.collection.CollectionUtil;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ModuleTypeException;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.scm.ModuleDependency;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Dependency management is heavily used by Karma. This helper class provides methods to resolve dependencies, check
 * them, etc.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class DependencyHelper {

  private Manifest manifest = null;

  public DependencyHelper(Manifest currentManifest) {

    if (currentManifest == null) {
      throw new IllegalArgumentException("Manifest cannot be null.");
    }

    this.manifest = currentManifest;
  }

  /**
   * Returns the classpath for <code>module</code>, or an empty <code>String</code> if no dependencies exist.
   *
   * @param module The module for which a classpath should be determined.
   * @return See method description.
   */
  public String getClassPath(Module module) throws ModuleTypeException, DependencyException {

    Set moduleDeps = getModuleDependencies(module);
    Set jarDeps = getJarDependencies(module);

    if (jarDeps.size() == 0) {
      if (moduleDeps.size() == 0) {
        return "";
      } else {
        return CollectionUtil.concat(moduleDeps, ';');
      }
    } else {
      if (moduleDeps.size() == 0) {
        return CollectionUtil.concat(jarDeps, ';');
      } else {
        return CollectionUtil.concat(jarDeps, ';') + ";" + CollectionUtil.concat(moduleDeps, ';');
      }
    }
  }

  /**
   * classpath required for testing.
   *
   * @param module
   * @return
   */
  public String getTestClassPath(Module module) {
    return null;
  }

  /**
   * Gets a <code>Set</code> of <code>String</code>s, each one identifying the absolute path to a module dependency.
   * All defined module dependencies are returned.
   * See {@link DependencyHelper#getModuleDependencies(Module, boolean, boolean)}
   *
   * @param module The module for which a dependency-path should be determined.
   * @return See method description.
   * @throws DependencyException When a dependency for a module is not available.
   */
  public Set getModuleDependencies(Module module) throws ModuleTypeException, DependencyException {
    return getModuleDependencies(module, false, false);
  }

  /**
   * Gets a <code>Set</code> of <code>String</code>s, each one identifying the path to a module dependency (a
   * dependency of <code>module</code> to another <code>Module</code>. The paths returned as <code>String</code>s in
   * the set returned are relative to {@link BuildEnvironment#getBuildRootDirectory()} or are absolute paths,
   * depending on the <code>relative</code> parameter.
   *
   * @param module The module for which a dependency-path should be determined.
   * @param relative  Whether or not the paths are relative
   * @param doPackage  Whether to include only the deps that are to be packaged or all deps.
   *
   * @return See method description.
   * @throws DependencyException When a dependency for a module is not available.
   */
  public Set getModuleDependencies(Module module, boolean relative, boolean doPackage) throws ModuleTypeException, DependencyException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    BuildEnvironment env = new BuildEnvironment(manifest, module);

    Set s = new HashSet();

    for (Iterator iterator = module.getDependencies().iterator(); iterator.hasNext();) {
      ModuleDependency dep = (ModuleDependency) iterator.next();

      if (dep.isModuleDependency()) {

        File dependencyJar = null;
        try {
          dependencyJar = new File(dep.getModule(), resolveArchiveName(manifest.getModule(dep.getModule())));
        } catch (ManifestException e) {
          throw new DependencyException(e.getErrorCode(), e.getMessageArguments());
        }

        File dependencyJarLocation = new File(env.getBuildRootDirectory(), dependencyJar.getPath());
        if (!dependencyJarLocation.exists()) {
          throw new DependencyException(DependencyException.DEPENDENCY_NOT_FOUND, new Object[]{dep.getModule()});
        }
        if (!doPackage || dep.doPackage()) {
          if (relative) {
            s.add(dependencyJar.getPath());
          } else {
            s.add(dependencyJarLocation.getPath());
          }
        }

      }
    }
    return s;
  }


  /**
   *
   * @param module      The module for which is checked whether it has <code>dependency</code> as a dependency.
   * @param dependency  The module for which to check whether it is a dependency of the current module.
   * @param doPackage   Whether to include only the deps that are to be packaged or all deps.
   * @return
   */
  public boolean hasModuleDependency(Module module, Module dependency, boolean doPackage) {
    Iterator it = module.getDependencies().iterator();
    ModuleDependency dep;
    boolean found = false;

    while (it.hasNext() && !found) {
      dep = (ModuleDependency) it.next();
      if (dep.isModuleDependency()) {
        if (dep.getModule().equals(dependency.getName())) {
          found = (!doPackage || dep.doPackage());
        }
      }
    }

    return found;
  }


  /**
   * See {@link #getJarDependencies(Module, boolean, boolean)}. This method returns a set with the absolute pathnames.
   * All defined jar dependencies are returned.
   */
  public Set getJarDependencies(Module module) throws DependencyException {
    return getJarDependencies(module, false, false);
  }


  /**
   * <p>Gets a <code>Set</code> of <code>String</code>s, each one identifying a <code>jar</code>-file. Jar files are looked
   * up Maven-style (see {@link ModuleDependency}. The result set contains strings like ;
   * <code>&lt;groupId&gt;/jars/&lt;artifactId&gt;-&lt;version&gt;.jar</code>.
   *
   * <p>The <code>relative</code> parameters
   *
   * @param module The module for which jar dependencies should be determined.
   * @param relative  Whether or not the path of the jars is relative or absolute.
   * @param doPackage  Whether to include only the deps that are to be packaged or all deps.
   *
   * @return A <code>Set</code> containing <code>String</code>s, each one representing the relative path (Maven-style)
   *   to the jar dependency.
   *
   * @throws DependencyException When a jar dependency is not phsyically available on disk. A check is performed on the
   *   existence of the jar file in either the local jar repository ({@link WorkingContext#getLocalRepository()}) or in
   *   the lib module that is specified as being part of the manifest.
   */
  public Set getJarDependencies(Module module, boolean relative, boolean doPackage) throws DependencyException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    Set s = new HashSet();

    for (Iterator iterator = module.getDependencies().iterator(); iterator.hasNext();) {

      ModuleDependency dep = (ModuleDependency) iterator.next();
      if (!dep.isModuleDependency()) {
        try {
          if (!new File(WorkingContext.getLocalRepository(), dep.getJarDependency()).exists()) {
            // todo has to be localized.
            //
            // todo this bit could have to download the dependency, like maven does.
            throw new DependencyException(DependencyException.DEPENDENCY_NOT_FOUND, new Object[]{dep.getJarDependency()});
          }

          if (!doPackage || dep.doPackage()) {
            if (relative) {
              s.add(dep.getJarDependency());
            } else {
              s.add(WorkingContext.getLocalRepository().getPath() + File.separator +dep.getJarDependency());
            }
          }
        } catch (IOException e) {
          throw new DependencyException(e, DependencyException.DEPENDENCY_NOT_FOUND, new Object[]{dep.getJarDependency()});
        }

      } else {
        // todo Implement stuff for lib modules ...
      }
    }
    return s;
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
   */
  public String resolveArchiveName(Module module) throws ModuleTypeException, DependencyException {

    String jar = module.getName() + "_";

    // todo introduce a method to determine if a module is webapp-module; maybe its own class.
    //
    String extension;
    if (module.getType().equals(Module.JAVA_WEB_APPLICATION)) {
      extension = ".war";
    } else if (module.getType().equals(Module.JAVA_ENTERPRISE_APPLICATION)) {
      extension = ".ear";
    } else {
      extension = ".jar";
    }

    try {
      if (manifest.getState(module).equals(Module.WORKING)) {
        jar += Module.WORKING.toString();
      } else if (manifest.getState(module).equals(Module.DYNAMIC)) {
        jar += (Utils.getLocalVersion(module));
      } else { // STATIC module
        jar += ((SourceModule) module).getVersionAsString();
      }
      jar += extension;
    } catch (VersionControlException v) {
      throw new DependencyException(v.getErrorCode(), v.getMessageArguments());
    }

    return jar;
  }


  /**
   * Traverses the modules' dependencies, and traverses all module dependencies as well (recursively), calculating
   * the set of dependencies that are unique.  A dependency is not unique if the artifact-name already exists in the
   * set but with another version. This will result in a DependencyException.
   *
   * @param module
   *
   * @return A <code>Set</code> containing all dependencies, all the way down to the lowest
   */
  public Set getAllLevels(Module module) throws ManifestException, DependencyException {
    return getLevels(module, null);
  }

  // Method that is called recursively to dive into a modules' dependency tree.
  //
  private Set getLevels(Module module, Set currentSet) throws ManifestException, DependencyException{

    if (currentSet == null) {
      currentSet = new HashSet();
    }

    Set moduleDeps = module.getDependencies();

    Iterator i = moduleDeps.iterator();
    while (i.hasNext()) {

      ModuleDependency dep = (ModuleDependency) i.next();

      if (!currentSet.add(dep)) {
//        ???????????????????????????????????????????????
//        throw new DependencyException(DependencyException.DUPLICATE_ARTIFACT_VERSION);
      } else {
        if (dep.isModuleDependency()) {
          Module moduleDep = manifest.getModule(dep.getModule());
          currentSet.addAll(getLevels(moduleDep, currentSet));
        }
      }
    }
    return currentSet;
  }

}
