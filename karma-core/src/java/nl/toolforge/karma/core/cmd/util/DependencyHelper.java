package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.scm.ModuleDependency;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.Utils;
import nl.toolforge.core.util.collection.CollectionUtil;
import org.apache.tools.ant.BuildException;

import java.io.File;
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
  public String getClassPath(Module module) throws KarmaBuildException {

    Set moduleDeps = getModuleDependencies(module);
    Set jarDeps = getModuleDependencies(module);

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
   * Gets a <code>Set</code> of <code>String</code>s, each one identifying the path to a module dependency (a
   * dependency of <code>module</code> to another <code>Module</code>. The paths returned as <code>String</code>s in
   * the set returned are relative to relative to {@link BuildEnvironment#getBuildRootDirectory()}
   *
   * @param module The module for which a dependency-path should be determined.
   * @return See method description.
   */
  public Set getModuleDependencies(Module module) throws KarmaBuildException {

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
          dependencyJar = new File(env.getModuleBuildDirectory() + File.separator + resolveArchiveName(manifest.getModule(dep.getModule())));
        } catch (ManifestException e) {
          throw new KarmaBuildException(e);
        }

        if (!dependencyJar.exists()) {
          throw new KarmaBuildException(CommandException.DEPENDENCY_DOES_NOT_EXIST);
        }

        s.add(dependencyJar.getPath());
      }
    }
    return s;
  }

  /**
   * Gets a <code>Set</code> of <code>String</code>s, each one identifying the path to the
   * <code>jar</code>-file relative to {@link WorkingContext#getLocalRepository()}.
   *
   * @param module
   * @return
   * @throws BuildException
   */
  public Set getJarDependencies(Module module) throws KarmaBuildException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    Set s = new HashSet();

    for (Iterator iterator = module.getDependencies().iterator(); iterator.hasNext();) {

      ModuleDependency dep = (ModuleDependency) iterator.next();

      if (!dep.isModuleDependency()) {
        if (!new File(dep.getJarDependency()).exists()) {
          // todo has to be localized.
          //
          // todo this bit could have to download the dependency, like maven does.
          throw new KarmaBuildException("Dependency " + dep.getJarDependency() + " does not exist.");
        }

        s.add(dep.getJarDependency());
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
  public String resolveArchiveName(Module module) throws KarmaBuildException {

    String jar = module.getName() + "_";

    // todo introduce a method to determine if a module is webapp-module; maybe its own class.
    //
    String extension;
    if (module.getDeploymentType().equals(Module.WEBAPP)) {
      extension = ".war";
    } else if (module.getDeploymentType().equals(Module.EAPP)) {
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
      throw new KarmaBuildException(v.getErrorCode());
//      throw new KarmaBuildException(Exception(v.getErrorCode(), v.getMessageArguments());
//      throw new KarmaBuildException(v);
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
        throw new DependencyException();
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
