package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.scm.ModuleDependency;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.vc.cvs.Utils;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.core.util.file.MyFileUtils;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.helper.ProjectHelperImpl;

/**
 * Superclass for all commands dealing with building modules. This class provides all basic property mappers and methods
 * that are required to use the <code>build-module.xml</code> properly.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class AbstractBuildCommand extends DefaultCommand {

  private static final String DEFAULT_SRC_PATH = "src/java";
  private static final String DEFAULT_BUILD_DIR = "build";

  /**
   * Mapper to the target in <code>build-module.xml</code> to build a module.
   */
  protected static final String BUILD_MODULE_TARGET = "build-module";

  /**
   * Mapper to the target in <code>build-module.xml</code> to package a module as a <code>jar</code>-file.
   */
  protected static final String BUILD_TARGET_JAR = "package-module-as-jar";

  /**
   * Mapper to the target in <code>build-module.xml</code> to package a module as a <code>war</code>-file.
   */
  protected static final String BUILD_TARGET_WAR = "package-module-as-war";

  /**
   * Mapper to the target in <code>build-module.xml</code> to package a module as an <code>ear</code>-file.
   */
  protected static final String BUILD_TARGET_EAR = "package-module-as-ear";

  /**
   * Property describing the full path-name to the modules' <code>src/java</code> directory.
   */
  protected static final String MODULE_SOURCE_DIR_PROPERTY = "module-source-dir";

  /**
   * Property describing the full path-name to the modules' <code>build</code> directory; this is generally pointing
   * to <code>&lt;development-home&gt/&lt;manifest-name&gt/build</code>.
   */
  protected static final String MODULE_BUILD_DIR_PROPERTY = "module-build-dir";

  /**
   * Property describing the <strong>relative</strong> path-name (to {@link #MODULE_BUILD_DIR_PROPERTY} to the directory
   * where compiled classes will be stored.
   */
  protected static final String MODULE_COMPILE_DIR_PROPERTY = "module-compile-dir";

  /**
   * Property containing the compile classpath while building a module.
   */
  protected static final String MODULE_CLASSPATH_PROPERTY = "module-classpath";

  /**
   * The modules' base directory (<code>&lt;development-home&gt/&lt;manifest-name&gt/&lt;module-name&gt;).
   */
  protected static final String MODULE_BASEDIR_PROPERTY = "module-base-dir";

  private Module module = null;
  private Manifest currentManifest = null;

  /**
   * Creates a command by initializing the command through its <code>CommandDescriptor</code>.
   *
   * @param descriptor The command descriptor instance containing the basic information for this command
   */
  public AbstractBuildCommand(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }

    String moduleName = getCommandLine().getOptionValue("m");

    try {
      // todo move this bit to aspect-code.
      //
      currentManifest = getContext().getCurrentManifest();

      module = currentManifest.getModule(moduleName);
      if (!currentManifest.isLocal(module)) {
//      if (!currentManifest.isLocal(module) && Utils.existsInRepository(module)) {
        throw new CommandException(ManifestException.MODULE_NOT_LOCAL, new Object[]{module.getName()});
      }

    } catch (ManifestException m) {
      throw new CommandException(m.getErrorCode(), m.getMessageArguments());
//    } catch (VersionControlException e) {
//      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }
  }

  /**
   * Helper method to get to the current module.
   *
   * @return
   */
  protected SourceModule getCurrentModule() {

    if (module == null) {
      throw new KarmaRuntimeException("Module is null. Execute method has not been called by subclass.");
    }
    return (SourceModule) module;
  }

  /**
   * Helper method to get to the current manifest.
   *
   * @return
   */
  protected Manifest getCurrentManifest() {

    if (currentManifest == null) {
      throw new KarmaRuntimeException("Manifest is null. Execute method has not been called by subclass.");
    }
    return currentManifest;
  }

  /**
   * Returns the build directory for a module.
   *
   * @return
   * @throws ManifestException
   */
  protected File getBuildDirectory() throws ManifestException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    // the rest, for the time being.
    //
    return new File(new File(getCurrentManifest().getDirectory(), "build"), getCurrentModule().getName());
  }

  /**
   * Returns the compile directory for a module.
   *
   * @return
   * @throws ManifestException
   */
  protected File getCompileDirectory() throws ManifestException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    if (module.getName().startsWith(Module.WEBAPP_PREFIX)) {
      return new File("WEB-INF/classes");
    } else {
      return new File("");
    }
  }

  protected File getSourceDirectory() throws ManifestException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }
    return new File(new File(getCurrentManifest().getDirectory(), getCurrentModule().getName()), DEFAULT_SRC_PATH);
  }

  protected String getDependencies(Set dependencies) throws ManifestException, CommandException {

    // todo kan dit manifest niet anders (als parameters ??)

    StringBuffer buffer = new StringBuffer();
    String userHome = System.getProperty("user.home");

    File baseDir = getCurrentManifest().getDirectory();

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
            new File(moduleBuildDir + File.separator + getCurrentManifest().resolveArchiveName(getCurrentManifest().getModule(dep.getModule())));

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
   * Gets an Ant <code>Project</code> for a module.
   *
   * @return
   * @throws CommandException
   */
  protected Project getAntProject() throws CommandException {


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
