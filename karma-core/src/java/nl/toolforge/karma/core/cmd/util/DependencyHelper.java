package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.scm.ModuleDependency;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

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
    this.manifest = currentManifest;
  }

  /**
   * Determines the classpath for a module and returns a comma-separated <code>String</code>, containing a modules'
   * dependencies. All classpath elements are returned with their absolute pathnames.
   *
   * @param module The module for which a classpath should be determined.
   * @return See method description.
   */
  public String getClassPath(Module module) {
    return null;
  }

  /**
   * Returns a ";"-separated <code>String</code> of a modules' module-dependencies, with their absolute pathnames.
   *
   * @param module The module for which a dependency-path should be determined.
   * @return See method description.
   */
  public String getModuleDependencies(Module module) {
    return null;
  }

  /**
   * Returns a ";"-separated <code>String</code> of a modules' dependencies, with their absolute pathnames.
   *
   * @param module The module for which a dependency-path should be determined.
   * @return See method description.
   */
  public String getAllDependencies(Module module) {
    return null;
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
