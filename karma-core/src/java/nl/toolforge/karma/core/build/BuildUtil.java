package nl.toolforge.karma.core.build;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.scm.ModuleDependency;
import nl.toolforge.core.util.file.MyFileUtils;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.helper.ProjectHelperImpl;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

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

  /**
   * Gets an Ant <code>Project</code> for a module.
   * 
   * @param module
   * @return
   * @throws CommandException
   */
  public Project getAntProject(Module module) throws CommandException {


    DefaultLogger logger = new DefaultLogger();
    // todo hmm, this mechanism doesn't integrate with the commandresponse mechanism
    //
    logger.setErrorPrintStream(System.out);

    // Configure underlying ant to run a command.
    //
    Project project = new Project();
    project.addBuildListener(logger);
    project.init();

    // Read in the build.xml file
    //
    ProjectHelper helper = new ProjectHelperImpl();
    File tmp = null;
    try {
      tmp = getBuildFile("build-module.xml");
      helper.parse(project, tmp);
    } catch (IOException e) {
      throw new CommandException(e, CommandException.BUILD_FAILED, new Object[] {module.getName()});
    } finally {
      try {
        FileUtils.deleteDirectory(tmp.getParentFile());
      } catch (IOException e) {
        throw new CommandException(e, CommandException.BUILD_FAILED, new Object[] {module.getName()});
      }
    }

    return project;
  }

  private File getBuildFile(String buildFile) throws IOException {

    File tmp = null;

    tmp = MyFileUtils.createTempDirectory();

    BufferedReader in =
        new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(buildFile)));
    BufferedWriter out =
        new BufferedWriter(new FileWriter(new File(tmp, buildFile)));

    String str;
    while ((str = in.readLine()) != null) {
      out.write(str);
    }
    out.close();
    in.close();

    // Return a temp reference to the file
    //
    return new File(tmp, buildFile);
  }

}
