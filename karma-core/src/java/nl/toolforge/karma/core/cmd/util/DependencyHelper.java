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
package nl.toolforge.karma.core.cmd.util;

import net.sf.sillyexceptions.OutOfTheBlueException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.module.ModuleTypeException;
import nl.toolforge.karma.core.scm.ModuleDependency;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvsimpl.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Dependency management is heavily used by Karma. This helper class provides methods to resolve dependencies, check
 * them, etc.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class DependencyHelper {

  public final static String MODULE_DEPENDENCIES_PROPERTIES = "module-dependencies.properties";

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

    Set deps = getAllDependencies(module, false);
    return DependencyPath.concat(deps, false, ';');

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

  public Set getAllDependencies(Module module, boolean doPackage) throws ModuleTypeException, DependencyException {
    Set all = new LinkedHashSet();
    all.addAll(getModuleDependencies(module, doPackage));
    all.addAll(getJarDependencies(module, doPackage));
    return all;
  }

  /**
   * Gets a <code>Set</code> of {@link DependencyPath}s, each one identifying the path to a module dependency (a
   * dependency of <code>module</code> to another <code>Module</code>).
   *
   * @param module      The module for which a dependency-path should be determined.
   * @param doPackage   Whether to include only the deps that are to be packaged or all deps.
   * @param moduleType  Only return modules of the specified type. Return all types when null.
   *
   * @return See method description.
   * @throws DependencyException  When a dependency for a module is not available.
   */
  public Set getModuleDependencies(Module module, boolean doPackage, Module.Type moduleType) throws ModuleTypeException, DependencyException {
    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    Set s = new LinkedHashSet();

    for (Iterator iterator = module.getDependencies().iterator(); iterator.hasNext();) {
      ModuleDependency dep = (ModuleDependency) iterator.next();
      if (dep.isModuleDependency()) {

        try {
          Module depModule = manifest.getModule(dep.getModule());
          DependencyPath path;

          //when packaging we want to have the archive
          //when we are not packaging, i.e. building or testing, then we want the classes.
          if (doPackage) {
            path = new DependencyPath(manifest.getBuildBaseDirectory(), new File(dep.getModule(), resolveArchiveName(depModule)));
          } else {
//todo: in case of tests we need the resources as well.
            path = new DependencyPath(manifest.getBuildBaseDirectory(), new File(dep.getModule(), "build"));
          }
          System.out.println(path);
          System.out.println(path.exists());
          if (!path.exists()) {
            throw new DependencyException(DependencyException.DEPENDENCY_NOT_FOUND, new Object[]{dep.getModule()});
          }
          if ((!doPackage || dep.doPackage()) &&
              (moduleType == null || moduleType.equals(depModule.getType())) ) {
            s.add(path);
          }
        } catch (ManifestException me) {
          if (me.getErrorCode().equals(ManifestException.MODULE_NOT_FOUND)) {
            throw new DependencyException(DependencyException.MODULE_NOT_IN_MANIFEST, me.getMessageArguments());
          } else {
            throw new DependencyException(me.getErrorCode(), me.getMessageArguments());
          }
        }
      }
    }
    return s;
  }

  /**
   * Gets a <code>Set</code> of {@link DependencyPath}s, each one identifying the path to a module dependency (a
   * dependency of <code>module</code> to another <code>Module</code>).
   *
   * @param module     The module for which a dependency-path should be determined.
   * @param doPackage  Whether to include only the deps that are to be packaged or all deps.
   *
   * @return See method description.
   * @throws DependencyException  When a dependency for a module is not available.
   */
  public Set getModuleDependencies(Module module, boolean doPackage) throws ModuleTypeException, DependencyException {
    return getModuleDependencies(module, doPackage, null);
  }

  /**
   * Create a properties file that contains mappings from module name to
   * module name plus version. E.g. karma-core -> karma-core_0-1.
   * <p>
   * The properties file is called 'module-dependencies.properties' and is
   * stored in the build directory of the given module.
   * </p>
   */
  public void createModuleDependenciesFilter(Module module) throws DependencyException {
    BuildEnvironment env = new BuildEnvironment(manifest, module);

    FileWriter write1 = null;
    try {
      Set moduleDeps = module.getDependencies();
      Iterator it = moduleDeps.iterator();

      File moduleBuildDir = env.getModuleBuildDirectory();
      moduleBuildDir.mkdirs();
      File archivesProperties = new File(moduleBuildDir, MODULE_DEPENDENCIES_PROPERTIES);
      archivesProperties.createNewFile();
      write1 = new FileWriter(archivesProperties);

      while (it.hasNext()) {
        ModuleDependency dep = (ModuleDependency) it.next();
        if (dep.isModuleDependency()) {
          Module mod = manifest.getModule(dep.getModule());

          write1.write(mod.getName()+"="+resolveArtifactName(mod)+"\n");
        }
      }
    } catch (ManifestException me) {
      me.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      try {
        write1.close();
      } catch (Exception e) {
        throw new OutOfTheBlueException("Unexpected exception when closing file writer.", e);
      }
    }
  }

  /**
   * Check whether a certain module has an other module as a dependency.
   *
   * @param module      The module for which is checked whether it has <code>dependency</code> as a dependency.
   * @param dependency  The module for which to check whether it is a dependency of the current module.
   * @param doPackage   Whether to include only the deps that are to be packaged or all deps.
   * @return Whether the given module had the other given module as a dependency.
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
   * <p>Gets a <code>Set</code> of {@link DependencyPath}s, each one identifying a <code>jar</code>-file. Jar files are looked
   * up Maven-style (see {@link ModuleDependency}.
   *
   * @param module         The module for which jar dependencies should be determined.
   * @param doPackage      Indicate if the dependencies that are to be packaged (<code>&lt;package="true"&gt;</code>)
   *                       should be included (<code>true</code>) or all dependencies should be included
   *                       (<code>false</code>).
   *
   * @return               A <code>Set</code> containing {@link DependencyPath}s
   *
   * @throws DependencyException
   *   When a jar dependency is not phsyically available on disk. A check is performed on the
   *   existence of the jar file in either the local jar repository ({@link WorkingContext#getLocalRepository()}) or in
   *   the lib module that is specified as being part of the manifest.
   */
  public Set getJarDependencies(Module module, boolean doPackage) throws DependencyException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    Set s = new LinkedHashSet();

    for (Iterator iterator = module.getDependencies().iterator(); iterator.hasNext();) {

      ModuleDependency dep = (ModuleDependency) iterator.next();

      if (dep.isLibModuleDependency() || !dep.isModuleDependency()) {
        DependencyPath path;
        if (dep.isLibModuleDependency()) {
          //dep on jar in lib module. This one is relative to the base dir of the manifest.
          path = new DependencyPath(manifest.getModuleBaseDirectory(), new File(dep.getJarDependency()));
        } else {
          //dep on jar in Maven-style repo.
          path = new DependencyPath(WorkingContext.getLocalRepository(), new File(dep.getJarDependency()));
        }
        if (!path.exists()) {
          // todo this bit could have to download the dependency, like maven does.
          throw new DependencyException(DependencyException.DEPENDENCY_NOT_FOUND, new Object[]{dep.getJarDependency()});
        }

        if (!doPackage || dep.doPackage()) {
          s.add(path);
        }
      }
    }
    return s;
  }

  /**
   * Determines the correct artifact name for <code>module</code>.
   * The artifact-name is determined as follows:
   *
   * <ul>
   *   <li/>If the state of the module is <code>WORKING</code>, the artifact-name is
   *        <code>&lt;module-name&gt;-WORKING</code>.
   *   <li/>If the state of the module is <code>DYNAMIC</code>, the artifact-name is
   *        <code>&lt;module-name&gt;-&lt;latest-versions&gt;</code>.
   *   <li/>If the state of the module is <code>STATIC</code>, the artifact-name is
   *        <code>&lt;module-name&gt;-&lt;version&gt;</code>.
   * </ul>
   *
   * @param module  The module for which to determine the artifact name.
   * @return The artifact name
   */
  public String resolveArtifactName(Module module) throws DependencyException {

    String artifact = module.getName() + "-";

    String version = "";
    try {
      if (manifest.getState(module).equals(Module.WORKING)) {
        version = Module.WORKING.toString();
      } else if (manifest.getState(module).equals(Module.DYNAMIC)) {
        version = Utils.getLocalVersion(module).toString();
      } else { // STATIC module
        version = ((Module) module).getVersionAsString();
      }
      version = version.replaceAll(Version.VERSION_SEPARATOR_CHAR, ".");
    } catch (VersionControlException v) {
      throw new DependencyException(v.getErrorCode(), v.getMessageArguments());
    }
    artifact += version;

    return artifact;
  }

  /**
   * <p>Determines the correct archive name for <code>module</code>. The archive
   * name is determined as follows:
   *
   * <ul>
   *   <li/>If the state of the module is <code>WORKING</code>, the archive-name is
   *        <code>&lt;module-name&gt;_WORKING.jar</code>.
   *   <li/>If the state of the module is <code>DYNAMIC</code>, the archive-name is
   *        <code>&lt;module-name&gt;_&lt;latest-versions&gt;.jar</code>.
   *   <li/>If the state of the module is <code>STATIC</code>, the archive-name is
   *        <code>&lt;module-name&gt;_&lt;version&gt;.jar</code>.
   * </ul>
   *
   * <p>The extension is <code>.war</code> if the module is a
   * <code>webapp</code>-module and <code>.ear</code> if the module is an
   * <code>eapp</code>-module
   *
   * @param module A <code>SourceModule</code> instance.
   * @return The archive-name as determined the way as described above.
   */
  public String resolveArchiveName(Module module) throws ModuleTypeException, DependencyException {

    // todo introduce a method to determine if a module is webapp-module; maybe its own class.
    //
    String extension;
    if (module.getType().equals(Module.JAVA_WEB_APPLICATION)) {
      extension = ".war";
    } else if (module.getType().equals(Module.JAVA_ENTERPRISE_APPLICATION)) {
      extension = ".ear";
    } else if (module.getType().equals(Module.JAVA_SOURCE_MODULE)) {
      extension = ".jar";
    } else if (module.getType().equals(Module.OTHER_MODULE)) {
      extension = ".zip";
    } else {
      extension = "";
    }
    return resolveArtifactName(module) + extension;
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
//        todo ???????????????????????????????????????????????
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
