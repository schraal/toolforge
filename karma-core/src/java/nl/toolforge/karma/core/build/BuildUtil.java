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
import java.io.File;

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
  private SourceModule module = null;

  public BuildUtil(Manifest currentManifest) {
    this.currentManifest = currentManifest;
    this.module = module;
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

        // Use BuildUtil again to determine the correct jar name.
//        BuildUtil util = new BuildUtil(currentManifest);
        String dependencyJarName = currentManifest.resolveJarName(currentManifest.getModule(dep.getModule()));

        File dependencyJar = new File(moduleBuildDir + File.separator + dependencyJarName);

        try {
          if (!dependencyJar.exists()) {
            throw new CommandException(CommandException.DEPENDENCY_DOES_NOT_EXIST, new Object[] {jar});
          }
        } catch(Exception e) {
          throw new CommandException(e, CommandException.BUILD_FAILED);
        }
      }

      buffer.append(jar);
      if (iterator.hasNext()) {
        buffer.append(";");
      }
    }

    return buffer.toString();
  }


}
