package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.scm.ModuleDependency;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.helper.ProjectHelperImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;

/**
 * Superclass for all commands dealing with building modules. This class provides all basic property mappers and methods
 * that are required to use the <code>build-module.xml</code> properly.
 *
 * @author D.A. Smedes
 * @author W.H. Schraal
 * @version $Id$
 */
public abstract class AbstractBuildCommand extends DefaultCommand {

  private static final Log logger = LogFactory.getLog(AbstractBuildCommand.class);

  protected static final char DEPENDENCY_SEPARATOR_CHAR = ',';
  protected static final char CLASSPATH_SEPARATOR_CHAR = ';';

  protected static final String DEFAULT_BUILD_DIR = "build";
  protected final static String DEFAULT_TEST_BUILD_DIRECTORY = "test";
  protected final static String DEFAULT_PACKAGE_DIRECTORY = "package";

  /**
   * Mapper to the target in <code>build-module.xml</code> to build a module.
   */
  protected static final String BUILD_MODULE_TARGET = "build-module";

  /**
   * Mapper to the target in <code>build-module.xml</code> to test a module.
   */
  protected static final String TEST_MODULE_TARGET = "test-module";

  /**
   * Mapper to the target in <code>build-module.xml</code> to clean a module.
   */
  protected static final String CLEAN_MODULE_TARGET = "clean-module";

  /**
   * Mapper to the target in <code>build-module.xml</code> to clean all modules.
   */
  protected static final String CLEAN_ALL_TARGET = "clean-all";

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
   * to <code>&lt;development-home&gt/&lt;manifest-name&gt/build/&lt;module-name&gt</code>.
   */
  protected static final String MODULE_BUILD_DIR_PROPERTY = "module-build-dir";

  /**
   * Property describing the <strong>relative</strong> path-name (to {@link #MODULE_BUILD_DIR_PROPERTY} to the directory
   * where compiled classes will be stored.
   */
  protected static final String MODULE_COMPILE_DIR_PROPERTY = "module-compile-dir";

  /**
   * Property describing the <strong>relative</strong> path-name (to {@link #MODULE_BUILD_DIR_PROPERTY} to the directory
   * where compiled tests and the results of the tests will be stored.
   */
  protected static final String MODULE_TEST_DIR_PROPERTY = "module-test-dir";

  /**
   * Property describing the <strong>relative</strong> path-name (to {@link #MODULE_BUILD_DIR_PROPERTY} to the directory
   * where the exploded package will be stored.
   */
  protected static final String MODULE_PACKAGE_DIR_PROPERTY = "module-package-dir";

  /**
   * Property containing the compile classpath while building a module.
   */
  protected static final String MODULE_CLASSPATH_PROPERTY = "module-classpath";

  /**
   * The modules' base directory (<code>&lt;development-home&gt/&lt;manifest-name&gt/&lt;module-name&gt;).
   */
  protected static final String MODULE_BASEDIR_PROPERTY = "module-base-dir";

  /**
   * The modules' 'module'-dependencies, relative from a manifests' build-directory.
   */
  protected static final String MODULE_MODULE_DEPENDENCIES_PROPERTY = "module-module-dependencies";

  /**
   * The modules' 'module'-dependencies, relative from a manifests' build-directory.
   */
  protected static final String MODULE_JAR_DEPENDENCIES_PROPERTY = "module-jar-dependencies";

  /**
   * The base location for jar dependencies; follows the <code>Maven</code>-conventions. Would generally
   * be equal to <code>maven.repo.local</code>.
   */
  protected static final String KARMA_JAR_REPOSITORY_PROPERTY = "karma-jar-repository";

  /**
   * The manifests' build directory.
   */
  protected static final String MANIFEST_BUILD_DIR = "manifest-build-dir";

  protected Module module = null;
  private Manifest currentManifest = null;

  private File tempBuildFileLocation = null; // Maintains a hook to a temp location for the Ant build file.

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
        throw new CommandException(ManifestException.MODULE_NOT_LOCAL, new Object[]{module.getName()});
      }

    } catch (ManifestException m) {
      throw new CommandException(m.getErrorCode(), m.getMessageArguments());
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
  protected final File getBuildDirectory() throws ManifestException {

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
  protected final File getCompileDirectory() throws ManifestException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    if (module.getDeploymentType().equals(Module.WEBAPP)) {
      return new File("build/WEB-INF/classes");
    } else {
      return new File("build");
    }
  }

  /**
   * Returns the test directory for a module.
   *
   * @return
   * @throws ManifestException
   */
  protected final File getTestDirectory() throws ManifestException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    return new File(DEFAULT_TEST_BUILD_DIRECTORY);
  }

  /**
   * Returns the package directory for a module.
   *
   * @return
   * @throws ManifestException
   */
  protected final File getPackageDirectory() throws ManifestException {

    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null.");
    }

    return new File(DEFAULT_PACKAGE_DIRECTORY);
  }

  /**
   * Returns the source directory for a module.
   *
   * @return
   * @throws ManifestException
   */
  protected abstract File getSourceDirectory() throws ManifestException;

  
  protected String getJarDependencies(Set dependencies, boolean relative, char separator) throws ManifestException, CommandException {

    StringBuffer buffer = new StringBuffer();

    for (Iterator iterator = dependencies.iterator(); iterator.hasNext();) {
      ModuleDependency dep = (ModuleDependency) iterator.next();

      String jar = null;

      if (!dep.isModuleDependency()) {

        if (relative) {
          jar = dep.getJarDependency();
        } else {
          jar = LocalEnvironment.getLocalRepository().getPath() + File.separator + dep.getJarDependency();
        }

        buffer.append(jar);
        if (iterator.hasNext()) {
          buffer.append(separator);
        }
      }
    }
    return buffer.toString();
  }

  protected String getModuleDependencies(Set dependencies, boolean relative, char separator) throws ManifestException, CommandException {

    StringBuffer buffer = new StringBuffer();

    File baseDir = getCurrentManifest().getDirectory();

    for (Iterator iterator = dependencies.iterator(); iterator.hasNext();) {
      ModuleDependency dep = (ModuleDependency) iterator.next();

      String jar = null;

      if (dep.isModuleDependency()) {

        // The module depends on another SourceModule.
        //
        File moduleBuildDir = new File(new File(baseDir, DEFAULT_BUILD_DIR), dep.getModule());

        File dependencyJar =
            new File(moduleBuildDir + File.separator + getCurrentManifest().resolveArchiveName(getCurrentManifest().getModule(dep.getModule())));

        if (!dependencyJar.exists()) {
          throw new CommandException(CommandException.DEPENDENCY_DOES_NOT_EXIST, new Object[] {dep.getModule(), getCurrentModule().getName()});
        }

        jar = dependencyJar.getPath();
        if (relative) {
          // Subtract the first bit.
          //
          File relativePart = new File(new File("", DEFAULT_BUILD_DIR), dep.getModule());
          jar = jar.substring(jar.indexOf(relativePart.getPath()) + DEFAULT_BUILD_DIR.length() + 2);
        }

        buffer.append(jar);
        if (iterator.hasNext()) {
          buffer.append(separator);
        }
      }
    }
    return buffer.toString();
  }

  protected String getDependencies(Set dependencies, boolean relative) throws ManifestException, CommandException {
    return getDependencies(dependencies, relative, DEPENDENCY_SEPARATOR_CHAR);
  }

  protected String getDependencies(Set dependencies, boolean relative, char separator) throws ManifestException, CommandException {

    String moduleDeps = getModuleDependencies(dependencies, relative, separator);
    String jarDeps = getJarDependencies(dependencies, relative, separator);

    if (!"".equals(jarDeps) && !"".equals(moduleDeps)) {
      moduleDeps += separator;
    }

    logger.debug("Dependencies: "+moduleDeps + jarDeps);
    return moduleDeps + jarDeps;
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
    logger.setOutputPrintStream(System.out);
    logger.setErrorPrintStream(System.out);

    logger.setMessageOutputLevel(Project.MSG_INFO); // Always handy ...
//    logger.setMessageOutputLevel(Project.MSG_VERBOSE); // Always handy ...
//    logger.setMessageOutputLevel(Project.MSG_DEBUG); // Always handy ...

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
    }

    setBuildFileLocation(tmp);


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

  private void setBuildFileLocation(File tmpBuildFileLocation) {
    this.tempBuildFileLocation = tmpBuildFileLocation;
  }

  /**
   * Called by {@link nl.toolforge.karma.core.cmd.CommandContext} after executing a command.
   */
  public final void cleanUp() {

    try {
      FileUtils.deleteDirectory(tempBuildFileLocation.getParentFile());
    } catch (IOException e) {
      logger.warn("Could not remove temporary directory for Ant build file.");
    }
  }

}
