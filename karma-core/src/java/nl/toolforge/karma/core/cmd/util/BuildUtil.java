package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Validates a modules' dependencies by checking if the actual artifacts already exists on a local disk.
 *
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class BuildUtil {

  // todo MOVE TO AbstractBuildCommand after ample consideration ...

//  private static final String DEFAULT_BUILD_DIR = "build";

  private Manifest currentManifest = null;

  public BuildUtil(Manifest currentManifest) {
    this.currentManifest = currentManifest;
  }

  /**
   * Cleans a modules' dependencies, by (recursively) traversing all modules that depend on <code>module</code> and
   * cleaning their <code>build</code>-directories.
   *
   * @param module The (root)-module for which dependencies should be cleaned.
   * //@param modules All modules that have a dependency on <code>module</code>.
   */
  public void cleanDependencies(Module module) throws ManifestException {

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

      } catch (IOException e) {
        throw new KarmaRuntimeException(e.getMessage());
      }
    }
  }

}
