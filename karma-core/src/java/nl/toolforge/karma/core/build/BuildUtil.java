package nl.toolforge.karma.core.build;

import nl.toolforge.karma.core.scm.ModuleDependency;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.vc.cvs.CVSVersionExtractor;
import nl.toolforge.karma.core.vc.VersionControlException;

import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Validates a modules' dependencies by checking if the actual artifacts already exists on a local disk.
 *
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class BuildUtil {

  private static final String DEFAULT_BUILD_DIR = "build";

  private Manifest currentManifest = null;
//  private SourceModule module = null;

  public BuildUtil(Manifest currentManifest) {
    this.currentManifest = currentManifest;
//    this.module = module;
  }

  public String getDependencies(Set dependencies) throws ManifestException, CommandException {

    StringBuffer buffer = new StringBuffer();
    String userHome = System.getProperty("user.home");

    File baseDir = currentManifest.getDirectory();

    for (Iterator iterator = dependencies.iterator(); iterator.hasNext();) {
      ModuleDependency dep = (ModuleDependency) iterator.next();

      String jar = null;

      if (!dep.isModuleDependency()) {
        jar = userHome + File.separator + ".maven" + File.separator + "repository" + File.separator;
        jar += dep.getJarDependency();

      } else {

        // The module depends on another SourceModule.
        //

        // todo consider using a reference to the manifest in a module, this implies that a module exists when a manifest is active ..

        File moduleBuildDir = new File(new File(baseDir, DEFAULT_BUILD_DIR), dep.getModule());

        File dependencyJar =
            new File(moduleBuildDir + File.separator + currentManifest.resolveJarName(currentManifest.getModule(dep.getModule())));

        if (!dependencyJar.exists()) {
          throw new CommandException(CommandException.DEPENDENCY_DOES_NOT_EXIST, new Object[] {jar, dep.getModule()});
        }
        jar = dependencyJar.getPath();
      }

      buffer.append(jar);
      if (iterator.hasNext()) {
        buffer.append(";");
      }
    }

    return buffer.toString();
  }

  /**
   * Cleans a modules' dependencies, by (recursively) traversing all modules that depend on <code>module</code> and
   * cleaning their <code>build</code>-directories.
   *
   * @param module The (root)-module for which dependencies should be cleaned.
   * //@param modules All modules that have a dependency on <code>module</code>.
   */
  public void cleanDependencies(Module module) {

    // todo proper exception handling --> CommandException ????

    // Get all modules that depend on this module.
    //
    Collection interDeps = currentManifest.getModuleInterdependencies(module);

    for (Iterator i = interDeps.iterator(); i.hasNext();) {

      Module dep = (Module) i.next();

      if (currentManifest.getInterdependencies().containsKey(dep.getName())) {
        cleanDependencies(dep);
      }
      // No interdependencies found for the dependency, so we remove its build-directory.
      //
      try {
        File buildDir = new File(new File(currentManifest.getDirectory(), "build"), dep.getName());

        FileUtils.deleteDirectory(buildDir);

      } catch (ManifestException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


}
